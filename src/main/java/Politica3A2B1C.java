/**
 * Created by YepezHinostroza on 8/11/2017.
 */
public class Politica3A2B1C extends Politica {
    public Politica3A2B1C(LectorPipe lectorPipe) {
        super(lectorPipe);
    }

    public Integer getLock(Matriz VectorAnd) {
        this.secuencia=preferenciaA;

        for (int i = 0; i < this.secuencia.size(); i++) {
            if (VectorAnd.getMatriz()[0][secuencia.get(i)] != 0) {
                return getInteger(secuencia.get(i));
            }
        }

        System.err.println("No se ha seleccionado ninguno hilo del vector AND");
        return getInteger(0);
    }
}
