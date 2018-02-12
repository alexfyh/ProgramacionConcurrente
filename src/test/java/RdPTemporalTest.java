import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class RdPTemporalTest {
    RdP petri;

    @Before
    public void setUp() {
        petri = new RdP();
    }

    @Test
    public void disparoSinSleep() {
        try {
            assertTrue(petri.disparar(0, petri.getTimeStamp()[0] + petri.getAlfa()[0] * petri.unidadTiempo, "Hilo"));
            assertEquals(EnumLog.MotivoDisparadoSinSleep, petri.getMotivo());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void disparoNoSensibilizado() {
        try {
            assertFalse(petri.disparar(1, petri.getTimeStamp()[1] + petri.getAlfa()[1], "Hilo"));
            assertEquals(EnumLog.MotivoNoSensibilizado, petri.getMotivo());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void disparoNoSensibilizadoYAntes() {
        try {
            assertFalse(petri.disparar(1, petri.getTimeStamp()[1] + petri.getAlfa()[1] * petri.unidadTiempo - 5, "Hilo"));
            assertEquals(EnumLog.MotivoNoSensibilizado, petri.getMotivo());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void disparoDespuesDeVentana() {
        try {
            petri.disparar(0, petri.getTimeStamp()[0] + petri.getAlfa()[0] * petri.unidadTiempo, "Hilo");
            petri.disparar(1, petri.getTimeStamp()[1] + petri.getAlfa()[1] * petri.unidadTiempo, "Hilo");
            assertFalse(petri.disparar(3, petri.getTimeStamp()[3] + petri.getBeta()[3] * petri.unidadTiempo + 5L, "Hilo"));
            assertEquals(EnumLog.MotivoDespuesDeVentana, petri.getMotivo());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void disparoAntesDeVentanaYPrimero() {
        try {
            petri.disparar(0, petri.getTimeStamp()[0] + petri.getAlfa()[0] * petri.unidadTiempo, "Hilo");
            petri.disparar(1, petri.getTimeStamp()[1] + petri.getAlfa()[1] * petri.unidadTiempo, "Hilo");
            assertFalse(petri.disparar(3, petri.getTimeStamp()[3] + petri.getAlfa()[3] * petri.unidadTiempo - 5L, "Hilo"));
            assertEquals(EnumLog.MotivoAntesDeVentana, petri.getMotivo());
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    @Test
    public void disparoAntesDeVentanaYSegundo() {
        try {
            petri.disparar(0, petri.getTimeStamp()[0] + petri.getAlfa()[0] * petri.unidadTiempo, "Hilo");
            petri.disparar(1, petri.getTimeStamp()[1] + petri.getAlfa()[1] * petri.unidadTiempo, "Hilo");
            petri.disparar(3, petri.getTimeStamp()[3] + petri.getAlfa()[3] * petri.unidadTiempo - 5L, "Primer Hilo");
            assertFalse(petri.disparar(3, petri.getTimeStamp()[3] + petri.getAlfa()[3] * petri.unidadTiempo - 2L, "Segundo Hilo"));
            assertEquals(EnumLog.MotivoNoAutorizado, petri.getMotivo());
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    @Test
    public void disparoEnVentanaYSegundo() {
        try {
            petri.disparar(0, petri.getTimeStamp()[0] + petri.getAlfa()[0] * petri.unidadTiempo, "Hilo");
            petri.disparar(1, petri.getTimeStamp()[1] + petri.getAlfa()[1] * petri.unidadTiempo, "Hilo");
            petri.disparar(3, petri.getTimeStamp()[3] + petri.getAlfa()[3] * petri.unidadTiempo - 5L, "Primer Hilo");
            assertFalse(petri.disparar(3, petri.getTimeStamp()[3] + petri.getAlfa()[3] * petri.unidadTiempo, "Segundo Hilo"));
            assertEquals(EnumLog.MotivoNoAutorizado, petri.getMotivo());
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    @Test
    public void disparoConSleep() {
        try {
            petri.disparar(0, petri.getTimeStamp()[0] + petri.getAlfa()[0] * petri.unidadTiempo, "Hilo");
            petri.disparar(1, petri.getTimeStamp()[1] + petri.getAlfa()[1] * petri.unidadTiempo, "Hilo");
            petri.disparar(3, petri.getTimeStamp()[3] + petri.getAlfa()[3] * petri.unidadTiempo - 5L, "Primer Hilo");
            assertTrue(petri.disparar(3, petri.getTimeStamp()[3] + petri.getAlfa()[3] * petri.unidadTiempo, "Primer Hilo"));
            assertEquals(EnumLog.MotivoDisparadoConSleep, petri.getMotivo());
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}