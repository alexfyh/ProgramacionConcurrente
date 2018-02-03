/**
 * Created by YepezHinostroza on 8/11/2017.
 */
public class Politica3A2B1C extends Politica {
    public Politica3A2B1C(LectorPipe lectorPipe) {
        super(lectorPipe);
    }

    public Integer getLock(Matriz VectorAnd) {
        /*
        *tr T10 P10 R1 -> P11
tr T11 M1 P11 -> P12 R1
tr T12 M3 P11 s1 -> P13 R1
tr T13 [2,w[ P12 R2 s2 -> M1 P14
tr T14 [2,w[ P13 R2 -> M3 P15
tr T15 M2 P14 -> P16 R2
tr T16 M4 P15 -> P17 R2
tr T17 [5,w[ P16 R3 -> M2 P18 s2
tr T18 [10,w[ P17 R3 -> M4 P18 s1
tr T19 P18 -> P10 R3
tr T21 P20 R2 s2 -> P21
tr T22 M2 P21 -> P22 R2
tr T23 [15,w[ P22 R2 -> M2 P23 s2
tr T24 P23 -> P20 R2
tr T31 P30 R3 s1 -> P31
tr T32 M4 P31 -> P32 R3
tr T33 [5,w[ P32 R2 -> M4 P33
tr T34 M3 P33 -> P34 R2
tr T35 [18,w[ P34 R1 -> M3 P35 s1
tr T36 P35 -> P30 R1
         */
        this.secuencia=this.equilibrio;
        for (int i = 0; i < this.secuencia.size(); i++) {
            if (VectorAnd.getMatriz()[0][secuencia.get(i)] != 0) {
                return getInteger(secuencia.get(i));
            }
        }
        System.err.println("No se ha seleccionado ninguno hilo del vector AND");
        return getInteger(0);

        /*this.secuencia=equilibrio;

        for (int i = 0; i < this.secuencia.size(); i++) {
            if (VectorAnd.getMatriz()[0][secuencia.get(i)] != 0) {
                return getInteger(secuencia.get(i));
            }
        }

        System.err.println("No se ha seleccionado ninguno hilo del vector AND");
        return getInteger(0);
        */
    }
}
