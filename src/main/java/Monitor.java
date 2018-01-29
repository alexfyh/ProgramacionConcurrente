import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

public class Monitor {
    private static Monitor UniqueInstance;
    private Semaphore mutex;
    private boolean k;
    private RdP petri;
    private Map<Integer, Hilo> mapa;
    private Matriz VectorEncolados;
    private Matriz VectorAnd;
    private Log log;
    private Politica politica;
    private long tiempo;

    public static Monitor getUniqueInstance(int pol){
        if (Monitor.UniqueInstance==null){
            Monitor.UniqueInstance = new Monitor(pol);
            return  Monitor.UniqueInstance;
        }
        else{
            return Monitor.UniqueInstance;
        }
    }
    private Monitor(int pol) {
        try {
            mutex = new Semaphore(1, true);
            k = true;
            petri = new RdP();
            mapa = new HashMap<Integer, Hilo>();
            VectorEncolados = Matriz.matrizVacia(1, petri.getIncidenciaPrevia().getN());
            VectorAnd = Matriz.matrizVacia(1, getPetri().getIncidenciaPrevia().getN());
            if (pol == 1) {
                this.politica = new Politica1A2B1C(mapa, getPetri().getLectorPipe());
            } else {
                if (pol == 2) {
                    this.politica = new Politica3A2B1C(mapa, getPetri().getLectorPipe());
                } else {
                    this.politica = new PoliticaRandom(mapa, getPetri().getLectorPipe());
                }
            }
            final String path = (new File(".")).getCanonicalPath();
            final String invariantesregistro = "/registro.txt";
            this.log = new Log(path + invariantesregistro, getPetri().getLectorPipe());
            log.limpiar();
        } catch (Exception e) {
            System.err.println("No se ha podido inicializar el monitor");
        }
    }

    public void dispararTransicion(Integer transicion) {
        boolean volverADisparar;
        long diferencia = 0L;
        do {
            volverADisparar = false;
            try {
                this.log.escribir(((Hilo) (Thread.currentThread())).getNombre() + "  pide el mutex.", this.log.getRegistro());
                mutex.acquire();
                k = true;
                this.log.escribir(((Hilo) (Thread.currentThread())).getNombre() + "  obtiene el mutex.", this.log.getRegistro());
                this.log.escribir(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>", this.log.getRegistro());

                while (k == true) {
                    tiempo = getPetri().currentTime();
                    k = petri.disparar(transicion, tiempo,((Hilo) (Thread.currentThread())).getNombre());
                    if (k == true) {
                        this.politica.incrementarDisparoDeTransicion(transicion);
                        // Sin esta linea no se actualiza porque direcciona a un viejo VectorSensibilizada
                        //VectorSensibilizados = getPetri().getVectorSensibilizadas();
                        VectorAnd.and(this.getPetri().getVectorSensibilizadas(), VectorEncolados);
                        if (politica.hayAlguienParaDespertar(VectorAnd)) {
                            Integer locker = politica.getLock(VectorAnd);
                            int t = locker.intValue();
                            this.log.registrar(this, transicion, true, mapa.get(locker), tiempo,0);
                            VectorEncolados.getMatriz()[0][t] = 0;
                            while (mapa.get(locker).getState() != Thread.State.WAITING) {
                                Thread.currentThread().sleep(1);
                                System.err.println("Esperando que se duerma para despertarlo : " + mapa.get(locker).getNombre());
                            }
                            synchronized (locker) {
                                locker.notifyAll();
                                return;
                            }
                        } else {
                            this.log.registrar(this, transicion, true, null, tiempo,0);
                            k = false;
                        }
                    } else {
                        if (!getPetri().transicionSensibilizada(transicion, getPetri().getVectorSensibilizadas())) {
                            encolar(transicion,tiempo,1);
                        } else {
                            diferencia = tiempo - (getPetri().getTimeStamp()[transicion] + getPetri().getAlfa()[transicion] * getPetri().unidadTiempo);
                            if (diferencia < 0) {
                                getPetri().setAutorizado(((Hilo) (Thread.currentThread())).getNombre(), transicion);
                                this.log.registrar(this, transicion, false, null, tiempo,2);
                                Thread.currentThread().sleep(diferencia * -1);
                                volverADisparar = true;
                                //break;
                                k = false;
                            } else {
                                diferencia = tiempo - (getPetri().getTimeStamp()[transicion] + getPetri().getBeta()[transicion] * getPetri().unidadTiempo);
                                if (diferencia > 0) {
                                    encolar(transicion,tiempo,3);
                                } else {
                                    encolar(transicion,tiempo,4);
                                }
                            }
                        }
                    }
                }
                this.log.escribir(((Hilo) (Thread.currentThread())).getNombre() + "  devuelve el mutex", this.log.getRegistro());
                this.log.escribir("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<", this.log.getRegistro());
                mutex.release();
                if(volverADisparar){
                    this.log.escribir(((Hilo) (Thread.currentThread())).getNombre()+" procede a dormir", this.log.getRegistro());
                    Thread.currentThread().sleep(diferencia * -1);
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println(e.getMessage() + "----");
            }

        }
        while (volverADisparar);

    }

    public void mapeo(Hilo hilo) {
        for (Integer i : hilo.getTransiciones()) {
            this.mapa.put(i, hilo);
        }
        this.log.registrarHilo(hilo);
    }

    public RdP getPetri() {
        return this.petri;
    }

    public String printHilosDeVector(String inicio, Matriz Vector) {
        String cadena = inicio;
        for (int i = 0; i < Vector.getN(); i++) {
            if (Vector.getMatriz()[0][i] != 0) {
                cadena = cadena + mapa.get(i).getNombre();
                cadena = cadena + " || ";
            }
        }
        return cadena;
    }

    public Matriz getVectorEncolados() {
        return this.VectorEncolados;
    }

    public Matriz getVectorAnd() {
        return this.VectorAnd;
    }

    public void encolar(Integer transicion, long tiempo,int resultado) {
        this.VectorEncolados.getMatriz()[0][transicion] = 1;
        this.log.registrar(this, transicion, false, null, tiempo, resultado);
        synchronized (transicion) {
            this.log.escribir(((Hilo) (Thread.currentThread())).getNombre() + "  devuelve el mutex", this.log.getRegistro());
            this.log.escribir("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<", this.log.getRegistro());
            mutex.release();
            try {
                transicion.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public Politica getPolitica() {
        return politica;
    }
}
