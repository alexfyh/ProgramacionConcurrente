import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RdPTest {

    private RdP petri;
    private Log log;
    private LectorPipe lectorPipe;

    private List<Matriz> historialMarcados;
    private List<Integer> historialDisparos;
    private List<Boolean> historialEstadoDisparos;
    private List<String> historialContador;
    private List<String> historialHilos;
    private List<String> historialSolicitudes;
    private List<String> historialMotivos;
    private List<List<String>> historialTranicionesPorDisparar;

    @Before
    public void setUp() throws Exception {
        this.lectorPipe = new LectorPipe();
        final String path = (new File(".")).getCanonicalPath().toString();
        final String invariantesregistro = "/registro.txt";
        this.log = new Log(path + invariantesregistro, lectorPipe);
        log.leerLineas();
        petri = new RdP();

        this.historialMarcados = log.getHistorialMarcados();
        this.historialDisparos = log.getHistorialDisparos();
        this.historialEstadoDisparos = log.getHistorialResultadoDisparo();
        this.historialContador = log.extraerLineas(EnumLog.Texto_ContadorDisparos.toString(), 0);
        this.historialHilos = log.getHistorialHiloEnMonitor();
        this.historialSolicitudes = log.extraerLineas(EnumLog.Texto_Solicitud.toString(), 0);
        this.historialMotivos = log.getHistorialMotivos();
        this.historialTranicionesPorDisparar = log.getHistorialListaTransicionesPorDisparar();
    }

    @Test
    public void verificarSensibilidadDisparo() {
        /*
        Verifica si el disparo debió o no dispararse de acuerdo al marcado previo.
         */
        try {
            Matriz marcadoPrevio = Matriz.obtenerFila(new Matriz(this.lectorPipe.getMarcados()), 0).transpuesta();
            Matriz incidenciaPrevia = new Matriz(lectorPipe.getIncidenciaPrevia());
            for (int i = 0; i < historialMarcados.size(); i++) {
                if (historialEstadoDisparos.get(i)) {
                    assertTrue(historialSolicitudes.get(i) + "\n" + "No se pudo haber disparado = " + log.traducirDisparo(historialDisparos.get(i)),
                            petri.transicionSensibilizada(historialDisparos.get(i), RdP.Sensibilizadas(incidenciaPrevia, marcadoPrevio))
                    ||historialTranicionesPorDisparar.get(i-1).contains(log.traducirDisparo(historialDisparos.get(i))));
                } else {
                    if (historialMotivos.get(i).equals(EnumLog.MotivoNoSensibilizado.toString()))
                    assertFalse(historialSolicitudes.get(i) + "\n" + "Si se pudo haber disparado = " + log.traducirDisparo(historialDisparos.get(i)),
                            petri.transicionSensibilizada(historialDisparos.get(i), RdP.Sensibilizadas(incidenciaPrevia, marcadoPrevio)));
                }
                marcadoPrevio = historialMarcados.get(i);
            }
        } catch (Exception e) { e.printStackTrace();}
    }

    @Test
    public void verificarMarcado() {
        /*
        Verifica que todos los marcados del historial se obtuvieron por el disparo de esa transición
        No tiene en cuenta si estaba sensibilizada o no, solo si se disparó.
         */
        try {
            Matriz marcadoPrevio = Matriz.obtenerFila(new Matriz(this.lectorPipe.getMarcados()), 0).transpuesta();
            for (int i = 0; i < historialMarcados.size(); i++) {
                if(historialEstadoDisparos.get(i)){
                    if(this.historialMotivos.get(i).equals(EnumLog.MotivoDisparadoSinSleep.toString())){
                        Matriz calculada = Matriz.suma(marcadoPrevio, Matriz.obtenerColumna(petri.getIncidencia(), historialDisparos.get(i)));
                        assertTrue(historialSolicitudes.get(i)+ "\n" + this.historialHilos.get(i) + "\n" + log.getMarcadosJustificados() + "\n" + calculada,
                                historialMarcados.get(i).esIgual(calculada));
                    }
                    else{
                        assertTrue(historialSolicitudes.get(i)+"Motivo debe ser con sleep",historialMotivos.get(i).equals(EnumLog.MotivoDisparadoConSleep.toString()));
                        Matriz calculada = Matriz.suma(marcadoPrevio, Matriz.obtenerColumna(petri.getIncidenciaPosterior(), historialDisparos.get(i)));
                        assertTrue(historialSolicitudes.get(i)+ "\n" + this.historialHilos.get(i) + "\n" + log.getMarcadosJustificados() + "\n" + calculada,
                                historialMarcados.get(i).esIgual(calculada));
                    }
                } else{
                    if(this.historialMotivos.get(i).equals(EnumLog.MotivoAntesDeVentana.toString())){
                        Matriz calculada = Matriz.suma(marcadoPrevio, Matriz.porEscalar(Matriz.obtenerColumna(petri.getIncidenciaPrevia(), historialDisparos.get(i)),-1));
                        assertTrue(historialSolicitudes.get(i)+ "\n" + this.historialHilos.get(i) + "\n" + log.getMarcadosJustificados() + "\n" + calculada,
                                historialMarcados.get(i).esIgual(calculada));
                    }
                    else{
                        assertTrue(historialMotivos.get(i).equals(EnumLog.MotivoNoSensibilizado.toString())||
                        historialMotivos.get(i).equals(EnumLog.MotivoDespuesDeVentana.toString())||
                        historialMotivos.get(i).equals(EnumLog.MotivoNoAutorizado.toString()));
                        assertTrue(historialSolicitudes.get(i) +"\n"+this.historialHilos.get(i)+"\n"+log.getMarcadosJustificados()+"\n"+marcadoPrevio,
                                historialMarcados.get(i).esIgual(marcadoPrevio));
                    }
                }
                marcadoPrevio=historialMarcados.get(i);
            }
        }
        catch (Exception e){e.printStackTrace();}
    }

    @Test
    public void pInvariantes() throws Exception {
        /*
        No está sacado como producto punto, sino como producto vectorial
         */
        // POR AHÍ CONVIENE LLAMARLO MATRIZ DE PINVARIANTES Y NO PINVARIANTE
        Matriz resultadoPInv= new Matriz(this.lectorPipe.getResultadoPInvariantes());
        Matriz PInvariantes = new Matriz((this.lectorPipe.getPInvariantes()));
        System.out.println("Matriz de resultado de los P Invariantes = ");
        System.out.println(resultadoPInv);
        for (int i = 0; i < historialMarcados.size(); i++) {
            //assertEquals(constantes.resultadoPInv,Matriz.multiplicacion(constantes.PInvariante,historialMarcados.get(i)));
            Matriz resultado = Matriz.multiplicacion(PInvariantes,historialMarcados.get(i));
            if (this.historialTranicionesPorDisparar.get(i).size()==0){
                assertTrue(historialSolicitudes.get(i)+"\n"+this.historialHilos.get(i)+"\n"+resultado,
                        resultado.esIgual(resultadoPInv));
            }
        }
    }

    @Test
    public void verificarTimeStamp(){

    }

    @Test
    public void verificarHilosSensibilizados() {

    }

}