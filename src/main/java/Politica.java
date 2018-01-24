import java.util.Map;

/**
 * Created by YepezHinostroza on 31/8/2017.
 */
public abstract class Politica {
    protected Map<Integer, Hilo> mapa;
    protected int[] secuencia;
    protected int[] equilibrio = {14, 15, 16, 17, 18, 19, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13};
    protected int[] preferenciaB = {10, 11, 12, 13, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 14, 15, 16, 17, 18, 19};
    protected int[] preferenciaA = {11, 12, 13, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 15, 16, 17, 18, 19};
    protected Vista v;
    protected int[][] arregloTInvariante;
    protected int[] DisparosPorTransicion;
    protected int[] lineaDeProduccion;


    public Politica(Map<Integer, Hilo> mapa, LectorPipe lectorPipe) {
        this.arregloTInvariante = lectorPipe.getTInvariantes();
        this.DisparosPorTransicion = new int[arregloTInvariante[0].length];
        this.lineaDeProduccion = new int[arregloTInvariante.length];
        this.mapa = mapa;
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

    public abstract boolean hayAlguienParaDespertar(Matriz And);

}
