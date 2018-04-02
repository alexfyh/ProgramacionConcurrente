import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by YepezHinostroza on 31/8/2017.
 */
public abstract class Politica {
    protected List<Integer> secuencia;
    protected List<Integer> secuencia2A1B;
    protected List<Integer> secuenciaBC;
    protected List<Integer> secuenciaAB;
    protected List<Integer> secuenciaAuxiliar;
    protected List<Integer> secuenciaAuxiliar2;
    protected List<Integer> secuenciaAuxiliar3;


    protected Vista v;
    protected int[][] arregloTInvariante;
    protected int[] DisparosPorTransicion;
    protected int[] lineaDeProduccion;
    protected final int factorCambio=20;


    public Politica(LectorPipe lectorPipe, LectorTina lectorTina) {
        List<Integer> invarianteA2 = lectorTina.getListaTInvariantes().get(0);
        List<Integer> invarianteA1 = lectorTina.getListaTInvariantes().get(1);
        List<Integer> invarianteB = lectorTina.getListaTInvariantes().get(2);
        List<Integer> invarianteC = lectorTina.getListaTInvariantes().get(3);

        secuenciaAB = concatenar(concatenar(concatenar(invarianteA2, invarianteA1), invarianteB), invarianteC);
        secuenciaBC = concatenar(concatenar(concatenar(invarianteC, invarianteB), invarianteA2), invarianteA1);
        secuenciaAuxiliar = concatenar(concatenar(concatenar(invarianteC, invarianteA1), invarianteA2), invarianteB);
        secuenciaAuxiliar2 = concatenar(concatenar(concatenar(invarianteA2, invarianteC), invarianteA1), invarianteB);
        secuenciaAuxiliar3 = concatenar(concatenar(concatenar(invarianteC, invarianteA2), invarianteB), invarianteA1);

        this.arregloTInvariante = lectorPipe.getTInvariantes();
        this.DisparosPorTransicion = new int[arregloTInvariante[0].length];
        this.lineaDeProduccion = new int[arregloTInvariante.length];
        try {
            Vista ventana = new Vista(this);
            this.v = ventana;
            ventana.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public abstract Integer getLock(Matriz VectorAnd);

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

    protected void actualizarVista() {
        v.repintar();
        v.repaint();
    }

    public boolean hayAlguienParaDespertar(Matriz And) {
        return And.cantidadDeUnos() > 0;
    }

    private List<Integer> concatenar(List<Integer> primera, List<Integer> segunda) {
        List<Integer> nueva = new ArrayList<>(primera);
        List<Integer> continuacion = new ArrayList<>(segunda);
        nueva.addAll(continuacion);
        return nueva;
    }
}
