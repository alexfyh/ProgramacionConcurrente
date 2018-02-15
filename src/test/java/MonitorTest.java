import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class MonitorTest {
    List<String> Lineas;
    Log log;
    List<String> historialSolicitudes;
    List<Boolean> historialEstadoDisparos;
    List<String> historialMotivos;

    @Before
    public void setUp() throws Exception {
        LectorPipe lectorPipe = new LectorPipe();
        final String path = (new File(".")).getCanonicalPath().toString();
        final String invariantesregistro = "/registro.txt";
        this.log = new Log(path + invariantesregistro, lectorPipe);
        System.out.println();
        this.historialSolicitudes = log.extraerLineas("Solicitud =", 0);
        this.historialEstadoDisparos = log.getHistorialResultadoDisparo();
        this.historialMotivos = log.getHistorialMotivos();
        Lineas = log.leerLineas();

    }

    @Test
    public void estadosMonitor() {
        List<String> estados = log.getEstadosMonitor();
        MaquinaDeEstados maquina = new MaquinaDeEstados();
        for (String evento :
                estados) {
            if (evento.contains(EnumLog.Texto_ObtieneMutex.toString())) {
                String[] cast = evento.split(EnumLog.Texto_ObtieneMutex.toString());
                assertTrue("El monitor no estaba disponible.",maquina.bloquear(cast[0].trim()));
            } else {
                if (evento.contains(EnumLog.Texto_DevuelveMutex.toString())) {
                    String[] cast = evento.split(EnumLog.Texto_DevuelveMutex.toString());
                    assertTrue(cast[0].trim()+"El monitor estaba disponible " +
                            "o "+ cast[0].trim()+ " no pudo haber devuelto.",maquina.desbloquear(cast[0].trim()));
                } else {
                    String[] cast = evento.split("=");
                    assertTrue("El monitor no estaba disponible.",maquina.despertar(cast[1].trim()));
                }
            }
        }
    }

    @Test
    public void encolados() {
        /*  Analiza sobre la linea Hilos encolados en contraposición de los intento fallidos de disparo e hilo despertado.
         *   Botonea en el caso que haya un encolado que falta  como también que sobre un encolado
         * */

        // PUEDE NO DISPARAR Y NO ENCOLARSE EN EL CASO QUE EL MOTIVO SEA EL DE LLEGAR ANTES

        // NO ANALIZA LA ÚLTIMA MODIFICACIÓN POR IndexOutOfBoundsException

        List<String> actividadHilos = log.getHistorialHiloEnMonitor();
        List<Boolean> estadosDisparos = log.getHistorialResultadoDisparo();
        List<String> hilosDespertados = log.getHistorialHilosDespertados();
        List<List<String>> hilosEncolados = log.getHistorialHilosEncolados();
        List<String> encolados = new ArrayList<>();
        for (int i = 0; i < hilosEncolados.size() - 1; i++) {
            if (!estadosDisparos.get(i) && !historialMotivos.get(i).contains(EnumLog.MotivoAntesDeVentana.toString())) {
                encolados.add(actividadHilos.get(i));
            }
            assertTrue(this.historialSolicitudes.get(i) + "\n" + "Encolados correctos = " + encolados,
                    hilosIguales(encolados, hilosEncolados.get(i)));
            if (hilosDespertados.get(i).trim().length() != 0) {
                encolados.remove(hilosDespertados.get(i));
            }
        }
    }

    @Test
    public void autorizado() {
        /*Verifica que el próximo en intentar disparar sea el que obtuvo el mutex o el recien despertado.
        En sí, tambien verifico tambien que tenga prioridad sobre el mutex el recién despertado
         */
        List<String> hilosDisparando = log.getHistorialHiloEnMonitor();
        List<String> hilosPermitods = log.getHistorialHilosConMutex();

        for (int i = 0; i < hilosDisparando.size(); i++) {
            assertTrue(this.historialSolicitudes.get(i),
                    hilosDisparando.get(i).equals(hilosPermitods.get(i)));
        }
    }

    @Test
    public void hiloDespertadoEncoladoSensibilizado() {
        /*Verifica que sólo se haya despertado un hilo que estaba sensibilizado y encolado(que esté en ambas) .
         */
        List<List<String>> listaHilosSensibilizados = log.getHistorialHilosSensibilizados();
        List<List<String>> listaHilosEncolados = log.getHistorialHilosEncolados();
        List<String> hilosDespertados = log.getHistorialHilosDespertados();
        for (int i = 0; i < hilosDespertados.size(); i++) {
            if (hilosDespertados.get(i).length() != 0) {
                assertTrue(this.historialSolicitudes.get(i),
                        listaHilosEncolados.get(i).contains(hilosDespertados.get(i)) &&
                                listaHilosSensibilizados.get(i).contains(hilosDespertados.get(i)));
            }
        }
    }

    @Test
    public void hiloDespertadoDisparado() {
        /*Si se despertó un hilo, tiene que haber podido disparar
         */

        // AHORA CON TIEMPO PUEDE QUE TODAVIA NO ESTE EN LA VENTANA
        // SOLO TIENE QUE SER EL PROXIMO EN DISPARAR SIN IMPORTAR EL RESULTADO
        List<String> hilosDespertados = log.getHistorialHilosDespertados();
        for (int i = 0; i < hilosDespertados.size() - 1; i++) {
            if (hilosDespertados.get(i).length() != 0) {
                assertTrue(this.historialSolicitudes.get(i + 1),
                        this.historialEstadoDisparos.get(i + 1));
            }
        }
    }

    @Test
    public void hiloRepetido() {
        List<List<String>> listaHilosEncolados = log.getHistorialHilosEncolados();
        List<List<String>> listaHilosSensibilizados = log.getHistorialHilosSensibilizados();
        List<List<String>> listaHilosEnAmbas = log.getHistorialHilosEnAmbas();
        for (int i = 0; i < listaHilosEncolados.size(); i++) {
            assertFalse(this.historialSolicitudes.get(i) + "\n" + " Encolado Repetido"
                    , hiloRepetido(listaHilosEncolados.get(i)));
        }
        for (int i = 0; i < listaHilosSensibilizados.size(); i++) {
            assertFalse(this.historialSolicitudes.get(i) + "\n" + " Sensibilizado Repetido"
                    , hiloRepetido(listaHilosSensibilizados.get(i)));
        }
        for (int i = 0; i < listaHilosEnAmbas.size(); i++) {
            assertFalse(this.historialSolicitudes.get(i) + "\n" + " En ambas Repetido"
                    , hiloRepetido(listaHilosEnAmbas.get(i)));
        }
    }

    @Test
    public void bufferLimitado() {
        //Deberia hacer la forma que la cantidad maxima no sea hardcodeada...
        int MAXBUFFER = log.cantidadHilos();
        List<List<String>> listaHilosEncolados = log.getHistorialHilosEncolados();
        for (int i = 0; i < listaHilosEncolados.size(); i++) {
            assertTrue(this.historialSolicitudes.get(i),
                    listaHilosEncolados.get(i).size() < MAXBUFFER);
        }
    }

    private boolean hiloRepetido(List<String> lista) {
        Set<String> conjunto = new TreeSet<>(lista);
        return !(lista.size() == conjunto.size());
    }

    private boolean hilosIguales(List<String> hilo1, List<String> hilo2) {
        Set<String> conjunto = new TreeSet<>(hilo1);
        Set<String> conjunto2 = new TreeSet<>(hilo2);
        return (conjunto.equals(conjunto2));
    }
}