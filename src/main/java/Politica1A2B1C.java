/**
 * Created by YepezHinostroza on 25/10/2017.
 */
public class Politica1A2B1C extends Politica {
    public Politica1A2B1C(LectorPipe lectorPipe) {
        super(lectorPipe);
    }

    public Integer getLock(Matriz VectorAnd) {

        this.secuencia = this.secuencia1A2B1C;
        for (Integer aSecuencia : this.secuencia) {
            if (VectorAnd.getMatriz()[0][aSecuencia] != 0) {
                return getInteger(aSecuencia);
            }
        }
        System.err.println("No se ha seleccionado ninguno hilo del vector AND");
        return getInteger(0);
    }
}
