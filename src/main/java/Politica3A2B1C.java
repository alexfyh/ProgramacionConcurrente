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

    public boolean hayAlguienParaDespertar(Matriz And) {
        if (((lineaDeProduccion[0] + lineaDeProduccion[1]) / 50) > (lineaDeProduccion[3] / 50) * 3) {
            if (lineaDeProduccion[2] / 50 > (lineaDeProduccion[3] / 50) * 2) {
                this.secuencia = equilibrio;
            } else {
                this.secuencia = preferenciaB;
            }
        } else {
            if (((lineaDeProduccion[0] + lineaDeProduccion[1]) / 50) * 2 > (lineaDeProduccion[2] / 50) * 3) {
                this.secuencia = preferenciaB;
            } else {
                this.secuencia = preferenciaA;
            }
        }
        try {
            Matriz andMod = And.clonar();
            if (this.secuencia == preferenciaA) {
                andMod.getMatriz()[0][10] = 0;
                andMod.getMatriz()[0][14] = 0;
            }
            for (int i = 0; i < this.secuencia.length; i++) {
                if (andMod.getMatriz()[0][secuencia[i]] == 1) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error en hay alguien por despertar");
            return false;
        }
    }
}
