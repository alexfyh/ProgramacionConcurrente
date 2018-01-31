import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

/**
 * Created by Fabrizio_p on 31/01/2018.
 */
public class RdPTempTest {
    RdP petri;
    Log log;
    LectorTina tina;

    private int [] arregloAlpha;
    private int [] arregloBeta;
    @Before
    public void setUp() throws Exception {
        LectorPipe lectorPipe = new LectorPipe();
        tina =  new LectorTina(lectorPipe);
        arregloAlpha = tina.getArregloAlfa();
        arregloBeta = tina.getArregloBeta();
        final String path = (new File(".")).getCanonicalPath().toString();
        final String invariantesregistro = "/registro.txt";
        this.log = new Log(path + invariantesregistro, lectorPipe);
        petri = new RdP();

        log.leerLineas();
        System.out.print(tina.getArregloAlfa());
        System.out.print(tina.getArregloBeta());
    }

    @Test
    public void ProbarDispararAntes() throws Exception {
        assertTrue(petri.disparar(0, 100, "H1"));
        assertTrue(petri.disparar(1, 150, "H2"));
        assertTrue(petri.disparar(3, 450, "H3"));
    }
    @Test
    public void NoDisparaAntes() throws Exception {
        assertTrue(petri.disparar(0, 100, "H1"));
        assertTrue(petri.disparar(1, 150, "H2"));
        assertFalse(petri.disparar(3, 449, "H3"));
    }

}



