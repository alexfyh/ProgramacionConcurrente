import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by YepezHinostroza on 31/8/2017.
 */
public abstract class Politica {
    protected Map<Integer, Hilo> mapa;
    protected List<Integer> secuencia;
    protected List<Integer> equilibrio;
    protected List<Integer> preferenciaB;
    protected List<Integer> preferenciaA;

    protected Vista v;
    protected int[][] arregloTInvariante;
    protected int[] DisparosPorTransicion;
    protected int[] lineaDeProduccion;


    public Politica(LectorPipe lectorPipe) {
        LectorTina lectorTina = new LectorTina(lectorPipe);
        List<Integer> secuenciaA1 = lectorTina.getListaTInvariantes().get(0);
        List<Integer> secuenciaA2 = lectorTina.getListaTInvariantes().get(1);
        List<Integer> secuenciaB = lectorTina.getListaTInvariantes().get(2);
        List<Integer> secuenciaC = lectorTina.getListaTInvariantes().get(3);

        preferenciaA=concatenar(concatenar(concatenar(secuenciaA1,secuenciaA2),secuenciaB),secuenciaC);
        System.out.println(preferenciaA);
        preferenciaB=concatenar(concatenar(concatenar(secuenciaB,secuenciaC),secuenciaA2),secuenciaA1);
        System.out.println(preferenciaB);
        equilibrio=concatenar(concatenar(concatenar(secuenciaC,secuenciaB),secuenciaA1),secuenciaA2);
        System.out.println(equilibrio);
        secuencia=equilibrio;
        this.arregloTInvariante = lectorPipe.getTInvariantes();
        this.DisparosPorTransicion = new int[arregloTInvariante[0].length];
        this.lineaDeProduccion = new int[arregloTInvariante.length];
        this.mapa = new HashMap<Integer, Hilo>();
        try {
            Vista ventana = new Vista(this);
            this.v = ventana;
            ventana.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public abstract Integer getLock(Matriz VectorAnd);

    public Integer getInteger(int entero) {
        for (Integer i : mapa.keySet()) {
            if (i.intValue() == entero) {
                return i;
            }
        }
        //Deberpia tirar una excepción por si falla, pero me la soba a estas alturas
        return null;
    }

    public void incrementarDisparoDeTransicion(int transicion) {
        this.DisparosPorTransicion[transicion] = this.DisparosPorTransicion[transicion] + 1;
        for (int i = 0; i < this.arregloTInvariante.length; i++) {
            boolean lineaCompleta = true;
            for (int j = 0; j < this.arregloTInvariante[0].length; j++) {
                if (this.DisparosPorTransicion[j] - this.arregloTInvariante[i][j] < 0) {
                    lineaCompleta = false;
                    break;
                }
            }
            if (lineaCompleta) {
                lineaDeProduccion[i] = lineaDeProduccion[i] + 1;
                for (int indice = 0; indice < DisparosPorTransicion.length; indice++) {
                    this.DisparosPorTransicion[indice] = this.DisparosPorTransicion[indice] - this.arregloTInvariante[i][indice];
                }
                actualizarVista();
            }
        }
    }

    public int[] getLineaDeProduccion() {
        return lineaDeProduccion;
    }

    public void actualizarVista() {
        v.repintar();
        v.repaint();
    }

    public boolean hayAlguienParaDespertar(Matriz And) {
        if (And.cantidadDeUnos() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public void registrarHilo(Hilo hilo) {
        for (Integer i : hilo.getTransiciones()) {
            this.mapa.put(i, hilo);
        }
        Monitor.getUniqueInstance(0).getLog().registrarHilo(hilo.getNombre(), hilo.getTransiciones());
    }

    public Map<Integer, Hilo> getMapa() {
        return this.mapa;
    }

    public List<Integer> concatenar(List<Integer> primera, List<Integer> segunda) {
        List<Integer> nueva = new ArrayList<>(primera);
        List<Integer> continuacion = new ArrayList<>(segunda);
        nueva.addAll(continuacion);
        return nueva;
    }
}
