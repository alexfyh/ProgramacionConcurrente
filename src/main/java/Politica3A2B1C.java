import java.util.Map;

/**
 * Created by YepezHinostroza on 8/11/2017.
 */
public class Politica3A2B1C extends Politica {
    public Politica3A2B1C(Map<Integer, Hilo> mapa, LectorPipe lectorPipe) {
        super(mapa, lectorPipe);
    }

    public Integer getLock(Matriz VectorAnd) {
        for (int i = 0; i < this.secuencia.length; i++) {
            if (VectorAnd.getMatriz()[0][secuencia[i]] != 0) {
                return getInteger(secuencia[i]);
            }
        }
        System.err.println("No se ha seleccionado ninguno hilo del vector AND");
        return getInteger(0);
    }
}
