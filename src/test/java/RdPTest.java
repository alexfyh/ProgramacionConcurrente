import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RdPTest {

    RdP petri;
    Log log;
    LectorPipe lectorPipe;

    List<Matriz> historialMarcados;
    List<Integer> historialDisparos;
    List<Boolean> historialEstadoDisparos;
    List<String> historialContador;
    List<String> historialHilos;
    List<String> historialSolicitudes;

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
        this.historialEstadoDisparos = log.getHistorialEstadoDisparos();
        this.historialContador = log.extraerLineas("Contador de disparos :", 0);
        this.historialHilos = log.getHistorialActividadHilos();
        this.historialSolicitudes = log.extraerLineas("Solicitud =", 0);
    }

    @Test
    public void verificarSensibilidadDisparo() {
        /*
        Verifica si el disparo debió o no dispararse de acuerdo al marcado previo.
         */
        try {
            Matriz marcadoPrevio = Matriz.obtenerFila(new Matriz(this.lectorPipe.getMarcados()), 0);
            Matriz incidenciaPrevia = new Matriz(lectorPipe.getIncidenciaPrevia());
            for (int i = 0; i < historialMarcados.size(); i++) {

                if (historialEstadoDisparos.get(i)) {
                    assertTrue(historialSolicitudes.get(i) + "\n" + "No se pudo haber disparado = " + log.traducirDisparo(historialDisparos.get(i)),
                            petri.transicionSensibilizada(historialDisparos.get(i), RdP.Sensibilizadas(incidenciaPrevia, marcadoPrevio)));
                } else {
                    assertFalse(historialSolicitudes.get(i) + "\n" + "Si se pudo haber disparado = " + log.traducirDisparo(historialDisparos.get(i)),
                            petri.transicionSensibilizada(historialDisparos.get(i), RdP.Sensibilizadas(incidenciaPrevia, marcadoPrevio)));
                }
                marcadoPrevio = historialMarcados.get(i);
            }
        } catch (Exception e) { System.err.println("Error 404");}
    }

    @Test
    public void verificarMarcado() {
        /*
        Verifica que todos los marcados del historial se obtuvieron por el disparo de esa transición
        No tiene en cuenta si estaba sensibilizada o no, solo si se disparó.
         */
        try {
            Matriz marcadoPrevio = Matriz.obtenerFila(new Matriz(this.lectorPipe.getMarcados()), 0);
            for (int i = 0; i < historialMarcados.size(); i++) {
                if(historialEstadoDisparos.get(i)){
                    try{
                        Matriz calculada = Matriz.suma(marcadoPrevio,Matriz.obtenerColumna(petri.getIncidencia(),historialDisparos.get(i)));
                        assertTrue(historialContador.get(i)+"\n"+this.historialHilos.get(i)+"\n"+log.getMarcadosImprimibles()+"\n"+calculada,
                                historialMarcados.get(i).esIgual(calculada));
                    }
                    catch(Exception e){System.err.println("Error 404");}}
                else{
                    assertTrue(historialContador.get(i)+"\n"+this.historialHilos.get(i)+"\n"+log.getMarcadosImprimibles()+"\n"+marcadoPrevio,
                            historialMarcados.get(i).esIgual(marcadoPrevio));
                }
                marcadoPrevio=historialMarcados.get(i);
            }
        }
        catch (Exception e){System.err.println("Error 404");}
    }

    @Test
    public void verificarHilosSensibilizados() {

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

            assertTrue(historialContador.get(i)+"\n"+this.historialHilos.get(i)+"\n"+resultado,
                    resultado.esIgual(resultadoPInv));
        }
    }
}