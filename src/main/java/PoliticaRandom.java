import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by YepezHinostroza on 14/11/2017.
 */
public class PoliticaRandom extends Politica {

    public PoliticaRandom(Map<Integer, Hilo> mapa, LectorPipe lectorPipe) {
        super(mapa, lectorPipe);
        this.mapa = mapa;
        this.v.setVisible(false);
    }

    public Integer getLock(Matriz And) {
        List<Integer> enteros = new ArrayList<>();
        for (int i = 0; i < And.getN(); i++) {
            if (And.getMatriz()[0][i] == 1) {
                enteros.add(i);
            }
        }
        return getInteger(enteros.get((int) (Math.random() * enteros.size())));
    }

    public boolean hayAlguienParaDespertar(Matriz And) {
        if (And.cantidadDeUnos() > 0) {
            return true;
        } else {
            return false;
        }
    }
}
