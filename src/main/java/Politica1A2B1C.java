/**
 * Created by YepezHinostroza on 25/10/2017.
 */
public class Politica1A2B1C extends Politica {
    public Politica1A2B1C(LectorPipe lectorPipe) {
        super(lectorPipe);
    }

    public Integer getLock(Matriz VectorAnd) {
        if ((this.lineaDeProduccion[2] / 50) < (this.lineaDeProduccion[3] / 50) * 2) {
            this.secuencia = this.preferenciaB;
        } else {
            this.secuencia = this.equilibrio;
        }
        for (int i = 0; i < this.secuencia.size(); i++) {
            if (VectorAnd.getMatriz()[0][secuencia.get(i)] != 0) {
                return getInteger(secuencia.get(i));
            }
        }
        System.err.println("No se ha seleccionado ninguno hilo del vector AND");
        return getInteger(0);
    }
}
