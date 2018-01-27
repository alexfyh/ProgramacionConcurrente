import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

import static java.lang.Thread.sleep;

public class Monitor {
    public Semaphore mutex;
    private boolean k;
    private RdP petri;
    private Map<Integer, Hilo> mapa;
    private Matriz VectorSensibilizados;
    private Matriz VectorEncolados;
    private Matriz VectorAnd;
    private Log log;
    private Politica politica;

    private long tiempo;


    public Monitor(int pol) {
        try {
            mutex = new Semaphore(1, true);
            k = true;
            petri = new RdP();
            mapa = new HashMap<Integer, Hilo>();
            VectorSensibilizados = RdP.Sensibilizadas(petri.getIncidenciaPrevia(), getPetri().marcadoActual());
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
        try {
            this.log.escribir(((Hilo) (Thread.currentThread())).getNombre() + "  pide el mutex.", this.log.getRegistro());
            mutex.acquire();
            k = true;
            this.log.escribir(((Hilo) (Thread.currentThread())).getNombre() + "  obtiene el mutex.", this.log.getRegistro());
            this.log.escribir(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>", this.log.getRegistro());

            while (k == true) {
                tiempo = getPetri().currentTime();
                k = petri.disparar(transicion,tiempo);
                if (k == true) {
                    this.politica.incrementarDisparoDeTransicion(transicion);
                    // Sin esta linea no se actualiza porque direcciona a un viejo VectorSensibilizada
                    VectorSensibilizados = getPetri().getVectorSensibilizadas();

                    this.log.escribir("Cantidad de piezas producidas:  " + "A = " + (politica.getLineaDeProduccion()[0] + politica.getLineaDeProduccion()[1])
                            + "   B = " + politica.getLineaDeProduccion()[2] + "   C = " + politica.getLineaDeProduccion()[3], log.getRegistro());
                    politica.actualizarVista();
                    VectorAnd.and(VectorSensibilizados, VectorEncolados);

                    if (politica.hayAlguienParaDespertar(VectorAnd)) {
                        Integer locker = politica.getLock(VectorAnd);
                        int t = locker.intValue();
                        this.log.registrar(this, transicion, true, mapa.get(locker),tiempo);
                        VectorEncolados.getMatriz()[0][t] = 0;
                        //log.registrarEXtendido(this, VectorAnd, mapa.get(locker));
                        while (mapa.get(locker).getState() != Thread.State.WAITING) {
                            Thread.currentThread().sleep(1);
                            System.err.println("Esperando que se duerma para despertarlo : " + mapa.get(locker).getNombre());
                        }
                        synchronized (locker) {
                            locker.notifyAll();
                            return;
                        }
                    } else {
                        this.log.registrar(this, transicion, true, null,tiempo);
                        k = false;
                    }
                } else {
                    if(!getPetri().transicionSensibilizada(transicion,getPetri().getVectorSensibilizadas())){
                        VectorEncolados.getMatriz()[0][transicion] = 1;
                        this.log.registrar(this, transicion, false, null,tiempo);
                        synchronized (transicion) {
                            this.log.escribir("No estaba sensibilizado", this.log.getRegistro());
                            this.log.escribir(((Hilo) (Thread.currentThread())).getNombre() + "  devuelve el mutex", this.log.getRegistro());
                            this.log.escribir("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<", this.log.getRegistro());
                            mutex.release();
                            transicion.wait();
                        }
                    }
                    else{
                        long diferencia = tiempo-getPetri().getTimeStamp()[transicion]+getPetri().getAlfa()[transicion]*getPetri().unidadTiempo;
                        if(diferencia<0){
                            this.log.escribir("Esperando por disparar", this.log.getRegistro());
                            this.log.escribir(((Hilo) (Thread.currentThread())).getNombre() + "  devuelve el mutex", this.log.getRegistro());
                            this.log.escribir("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<", this.log.getRegistro());
                            mutex.release();
                            sleep(diferencia*-1);
                        }
                        else{
                            diferencia = tiempo-getPetri().getTimeStamp()[transicion]+getPetri().getBeta()[transicion]*getPetri().unidadTiempo;
                            if(diferencia>0){
                                VectorEncolados.getMatriz()[0][transicion] = 1;
                                this.log.registrar(this, transicion, false, null,tiempo);
                                synchronized (transicion) {
                                    this.log.escribir("Expiro el tiempo de sensibilidad", this.log.getRegistro());
                                    this.log.escribir(((Hilo) (Thread.currentThread())).getNombre() + "  devuelve el mutex", this.log.getRegistro());
                                    this.log.escribir("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<", this.log.getRegistro());
                                    mutex.release();
                                    transicion.wait();
                                }
                            }
                            else{
                                VectorEncolados.getMatriz()[0][transicion] = 1;
                                this.log.registrar(this, transicion, false, null,tiempo);
                                synchronized (transicion) {
                                    this.log.escribir("No esta autorizado para disparar", this.log.getRegistro());
                                    this.log.escribir(((Hilo) (Thread.currentThread())).getNombre() + "  devuelve el mutex", this.log.getRegistro());
                                    this.log.escribir("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<", this.log.getRegistro());
                                    mutex.release();
                                    transicion.wait();
                                }

                            }
                        }
                    }
                }
            }
            this.log.escribir(((Hilo) (Thread.currentThread())).getNombre() + "  devuelve el mutex", this.log.getRegistro());
            this.log.escribir("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<", this.log.getRegistro());
            mutex.release();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getMessage() + "----");
        }
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

}
