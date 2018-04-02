import java.util.ArrayList;

/**
 * Created by YepezHinostroza on 25/10/2017.
 */
public class Politica1A2B1C extends Politica {
    public Politica1A2B1C(LectorPipe lectorPipe,LectorTina lectorTina) {
        super(lectorPipe,lectorTina);
    }

    public Integer getLock(Matriz VectorAnd) {
        if ((lineaDeProduccion[0] + lineaDeProduccion[1]) / factorCambio < lineaDeProduccion[3] / factorCambio) {
            secuencia = secuenciaAB;
        } else {
            secuencia = secuenciaBC;
        }
        if(lineaDeProduccion[2]>lineaDeProduccion[3]*3+10){
            secuencia=new ArrayList<>(secuencia);
            secuencia.add(0,3);
            secuencia.add(0,4);
            //secuencia.add(0,16);
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
