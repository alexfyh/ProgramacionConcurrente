import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class HiloTest {
    RdP petri;
    Log log;

    private List<String> nombreDeHilos;
    private List<List<String>> nombreTransiciones;
    private List<String> historialSolicitudes;


    @Before
    public void setUp() throws Exception {
        LectorPipe lectorPipe = new LectorPipe();
        final String path = (new File(".")).getCanonicalPath().toString();
        final String invariantesregistro = "/registro.txt";
        this.log = new Log(path + invariantesregistro, lectorPipe);
        petri = new RdP();

        log.leerLineas();
        log.leerHilos();
        this.nombreDeHilos = log.getNombreHilos();
        this.nombreTransiciones = log.getTransicionesDeHilos();
        this.historialSolicitudes = log.extraerLineas("Solicitud =", 0);
    }

    //@Test
    public void showHilos() {


        for (int i = 0; i < log.getNombreHilos().size(); i++) {
            System.out.println("Hilo : "+ log.getNombreHilos().get(i));
            System.out.println("Transiciones : "+ log.getNombreTransiciones().get(i));
        }
        System.out.println("----------------");

        for (List l :
                log.getTransicionesDeHilos()) {
            System.out.println(l);
        }

    }

    @Test
    public void disparoEnSecuencia(){
        List<String> historialHilosEnMonitor = this.log.getHistorialActividadHilos();
        List<Boolean> historialResultadoDeSolicitud = this.log.getHistorialEstadoDisparos();
        List<String> historialDisparos = this.log.extraerDisparos();
        int[] contadoresDeDisparos = new int[this.nombreDeHilos.size()];
        for (int i = 0; i < historialHilosEnMonitor.size(); i++) {
            int indice = getIndice(historialHilosEnMonitor.get(i));
            System.out.println(this.nombreTransiciones.get(indice).get(contadoresDeDisparos[indice]%this.nombreTransiciones.get(indice).size()) +"  =   "+historialDisparos.get(i));

            String [] cast =  historialDisparos.get(i).split("la transicion  : ");
            String nombreTransicion = cast[1].trim();
            assertTrue(this.historialSolicitudes.get(i) ,
                    (this.nombreTransiciones.get(indice).get(contadoresDeDisparos[indice]%this.nombreTransiciones.get(indice).size())).equals(nombreTransicion));
            if (historialResultadoDeSolicitud.get(i)){
                contadoresDeDisparos[indice] = contadoresDeDisparos[indice]+1;
            }
        }
    }

    public int getIndice(String nombre){
        for (int i = 0; i < this.nombreDeHilos.size(); i++) {
            if(nombre.equals(nombreDeHilos.get(i).trim())){
                return i;
            }
        }
        //Espero que nunca llegue aca
        System.err.println("No se pudo encontrar el indice del hilo");
        return 0;
    }

}
