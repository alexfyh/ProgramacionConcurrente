import java.util.ArrayList;

/**
 * Created by YepezHinostroza on 8/11/2017.
 */
public class Politica3A2B1C extends Politica {

    public Politica3A2B1C(LectorPipe lectorPipe, LectorTina lectorTina) {
        super(lectorPipe,lectorTina);
    }

    public Integer getLock(Matriz VectorAnd) {

        if (lineaDeProduccion[2] / factorCambio < lineaDeProduccion[3]*2 / factorCambio) {
            secuencia = secuenciaAB;
        } else {
            this.secuencia=secuenciaAuxiliar;
        }
        for (Integer aSecuencia : this.secuencia) {
            if (VectorAnd.getMatriz()[0][aSecuencia] != 0) {
                return aSecuencia;
            }
        }
        System.err.println("No se ha seleccionado ninguno hilo del vector AND");
        return 0;
    }
}
