/**
 * Created by YepezHinostroza on 8/11/2017.
 */
public class Politica3A2B1C extends Politica {
    public Politica3A2B1C(LectorPipe lectorPipe) {
        super(lectorPipe);
    }

    public Integer getLock(Matriz VectorAnd) {
        if ((lineaDeProduccion[0] + lineaDeProduccion[1]) / 50 < lineaDeProduccion[2] / 50) {
            secuencia = secuenciaAB;
        } else {
            secuencia = secuenciaBC;
        }
        for (Integer aSecuencia : this.secuencia) {
            if (VectorAnd.getMatriz()[0][aSecuencia] != 0) {
                return getInteger(aSecuencia);
            }
        }
        System.err.println("No se ha seleccionado ninguno hilo del vector AND");
        return getInteger(0);
    }
}
