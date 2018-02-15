import java.io.File;
import java.util.concurrent.Semaphore;

public class Monitor {
    private static Monitor UniqueInstance;
    public static int pol=0;
    private Semaphore mutex;
    private boolean k;
    private RdP petri;
    private Matriz VectorEncolados;
    private Matriz VectorAnd;
    private Log log;
    private Politica politica;
    private long tiempo;
    private GestorDeColas gestorDeColas;

    public static Monitor getUniqueInstance() {
        if (Monitor.UniqueInstance == null) {
            Monitor.UniqueInstance = new Monitor(pol);
            return Monitor.UniqueInstance;
        } else {
            return Monitor.UniqueInstance;
        }
    }

    private Monitor(int pol) {
        try {
            mutex = new Semaphore(1, true);
            k = true;
            petri = new RdP();
            VectorEncolados = Matriz.matrizVacia(1, petri.getIncidenciaPrevia().getN());
            VectorAnd = Matriz.matrizVacia(1, getPetri().getIncidenciaPrevia().getN());
            this.gestorDeColas = new GestorDeColas();
            switch (pol){
                case 1: this.politica = new Politica1A2B1C(petri.getLectorPipe(),petri.getLectorTina());
                        break;
                case 2: this.politica = new Politica3A2B1C(petri.getLectorPipe(),petri.getLectorTina());
                        break;
                default: this.politica = new PoliticaRandom(petri.getLectorPipe(),petri.getLectorTina());
                        break;
            }
            final String path = (new File(".")).getCanonicalPath();
            final String registro = "/registro.txt";
            this.log = new Log(path + registro, getPetri().getLectorPipe());
            log.limpiar();
        } catch (Exception e) {
            System.err.println("No se ha podido inicializar el monitor");
        }
    }

    public void dispararTransicion(Integer transicion) {
        boolean volverAEntrar;
        do {
            volverAEntrar = false;
            try {
                this.log.escribir(((Hilo) (Thread.currentThread())).getNombre() +EnumLog.Texto_PideMutex.toString());
                mutex.acquire();
                k = true;
                this.log.escribir(((Hilo) (Thread.currentThread())).getNombre() +EnumLog.Texto_ObtieneMutex.toString());
                while (k) {
                    tiempo = getPetri().currentTime();
                    k = petri.disparar(transicion, tiempo, ((Hilo) (Thread.currentThread())).getNombre());
                    if (k) {
                        this.politica.incrementarDisparoDeTransicion(transicion);
                        VectorAnd.and(this.getPetri().getVectorSensibilizadas(), VectorEncolados);
                        if (politica.hayAlguienParaDespertar(VectorAnd)) {
                            Integer locker = politica.getLock(VectorAnd);
                            this.log.registrar(this, transicion, k, tiempo, petri.getMotivo(),gestorDeColas.desencolar(locker));
                            VectorEncolados.getMatriz()[0][locker] = 0;
                            synchronized (locker) {
                                locker.notify();
                                return;
                            }
                        } else {
                            this.log.registrar(this, transicion, k, tiempo, petri.getMotivo(),null);
                            k = false;
                        }
                    } else {
                        if (petri.getMotivo() == EnumLog.MotivoAntesDeVentana) {
                            volverAEntrar = true;
                            k = false;
                            this.log.registrar(this, transicion, k, tiempo, petri.getMotivo(),null);
                            this.log.escribir(((Hilo) (Thread.currentThread())).getNombre() +EnumLog.Texto_SeDormira.toString()+ petri.getTiempoADormir(transicion, tiempo) +EnumLog.Texto_ms.toString());
                        } else {
                            encolar(transicion, tiempo);
                        }
                    }
                }
                this.log.escribir(((Hilo) (Thread.currentThread())).getNombre() + EnumLog.Texto_DevuelveMutex.toString());
                mutex.release();
                if (volverAEntrar) {
                    Thread.currentThread().sleep(petri.getTiempoADormir(transicion, tiempo));
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println(e.getMessage() + "----");
            }
        }
        while (volverAEntrar);
    }

    public RdP getPetri() {
        return this.petri;
    }

    public Matriz getVectorEncolados() {
        return this.VectorEncolados;
    }

    public Matriz getVectorAnd() {
        return this.VectorAnd;
    }

    private void encolar(Integer transicion, long tiempo) {
        this.VectorEncolados.getMatriz()[0][transicion] = 1;
        this.log.registrar(this, transicion, k, tiempo, petri.getMotivo(),null);
        synchronized (transicion) {
            this.log.escribir(((Hilo) (Thread.currentThread())).getNombre() + EnumLog.Texto_DevuelveMutex.toString());
            mutex.release();
            try {
                gestorDeColas.encolar(transicion,((Hilo) (Thread.currentThread())));
                transicion.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public Politica getPolitica() {
        return politica;
    }

    public Log getLog() {
        return this.log;
    }
}
