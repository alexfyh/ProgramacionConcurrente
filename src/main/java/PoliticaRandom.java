import java.util.ArrayList;
import java.util.List;

/**
 * Created by YepezHinostroza on 14/11/2017.
 */
public class PoliticaRandom extends Politica {

    public PoliticaRandom(LectorPipe lectorPipe) {
        super(lectorPipe);
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
}
