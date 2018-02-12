import java.io.File;
import java.util.concurrent.Semaphore;

public class Monitor {
    private static Monitor UniqueInstance;
    private Semaphore mutex;
    private boolean k;
    private RdP petri;
    private Matriz VectorEncolados;
    private Matriz VectorAnd;
    private Log log;
    private Politica politica;
    private long tiempo;

    public static Monitor getUniqueInstance(int pol) {
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
            if (pol == 1) {
                this.politica = new Politica1A2B1C(getPetri().getLectorPipe());
            } else {
                if (pol == 2) {
                    this.politica = new Politica3A2B1C(getPetri().getLectorPipe());
                } else {
                    this.politica = new PoliticaRandom(getPetri().getLectorPipe());
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
        boolean volverAEntrar;
        do {
            volverAEntrar = false;
            try {
                this.log.escribir(((Hilo) (Thread.currentThread())).getNombre() + "  pide el mutex.", this.log.getRegistro());
                mutex.acquire();
                k = true;
                this.log.escribir(((Hilo) (Thread.currentThread())).getNombre() + "  obtiene el mutex.", this.log.getRegistro());
                this.log.escribir(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>", this.log.getRegistro());
                while (k) {
                    tiempo = getPetri().currentTime();
                    k = petri.disparar(transicion, tiempo, ((Hilo) (Thread.currentThread())).getNombre());
                    if (k) {
                        this.politica.incrementarDisparoDeTransicion(transicion);
                        VectorAnd.and(this.getPetri().getVectorSensibilizadas(), VectorEncolados);
                        if (politica.hayAlguienParaDespertar(VectorAnd)) {
                            Integer locker = politica.getLock(VectorAnd);
                            int t = locker;
                            this.log.registrar(this, transicion, true, politica.getMapa().get(locker), tiempo, petri.getMotivo());
                            VectorEncolados.getMatriz()[0][t] = 0;
                            while (politica.getMapa().get(locker).getState() != Thread.State.WAITING) {
                                Thread.currentThread().sleep(1);
                                System.err.println("Esperando que se duerma para despertarlo : " + politica.getMapa().get(locker).getNombre());
                            }
                            synchronized (locker) {
                                locker.notifyAll();
                                return;
                            }
                        } else {
                            this.log.registrar(this, transicion, true, null, tiempo, petri.getMotivo());
                            k = false;
                        }
                    } else {
                        if (petri.getMotivo() == EnumLog.MotivoAntesDeVentana) {
                            volverAEntrar = true;
                            k = false;
                            this.log.registrar(this, transicion, false, null, tiempo, petri.getMotivo());
                            this.log.escribir(((Hilo) (Thread.currentThread())).getNombre() + " se dormira " + petri.getTiempoADormir(transicion, tiempo) + " ms", this.log.getRegistro());
                        } else {
                            encolar(transicion, tiempo, petri.getMotivo());
                        }
                    }
                }
                this.log.escribir(((Hilo) (Thread.currentThread())).getNombre() + "  devuelve el mutex", this.log.getRegistro());
                this.log.escribir("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<", this.log.getRegistro());
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

    public void encolar(Integer transicion, long tiempo, EnumLog motivo) {
        this.VectorEncolados.getMatriz()[0][transicion] = 1;
        this.log.registrar(this, transicion, false, null, tiempo, motivo);
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

    public Log getLog() {
        return this.log;
    }
}
