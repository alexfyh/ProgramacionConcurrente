public class RdP {
    private Matriz marcadoInicial;
    private Matriz marcadoActual;
    private Matriz incidencia;
    private Matriz incidenciaPrevia;
    private Matriz vectorSensibilizadas;
    private int contadorDisparos;

    public RdP() {
        try {
            LectorPipe lp = new LectorPipe();

            this.marcadoInicial = (Matriz.obtenerFila(new Matriz(lp.getMarcados()),0)).transpuesta();
            this.marcadoActual = this.marcadoInicial;
            this.incidenciaPrevia = new Matriz(lp.getIncidenciaPrevia());
            this.incidencia = new Matriz(lp.getIncidenciaCombinada());
            this.vectorSensibilizadas = Sensibilizadas(incidenciaPrevia, marcadoInicial);
            contadorDisparos = 0;
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public Matriz getIncidencia() {
        return this.incidencia;
    }

    public Matriz getIncidenciaPrevia() {
        return this.incidenciaPrevia;
    }

    public Matriz marcadoInicial() {
        return this.marcadoInicial;
    }

    public Matriz marcadoActual() {
        return this.marcadoActual;
    }

    public boolean disparar(int x) throws Exception {
        try {
            if (x < 0 || x > this.incidencia.getMatriz()[0].length) {
                throw new Exception("Transicion no valida.");
            }
            if (this.vectorSensibilizadas.getMatriz()[0][x] != 0) {
                this.marcadoActual = Matriz.suma(this.marcadoActual, Matriz.obtenerColumna(this.incidencia, x));
                vectorSensibilizadas = Sensibilizadas(this.incidenciaPrevia, this.marcadoActual);
                contadorDisparos++;
                System.out.println("Contador de Disparos =  " + contadorDisparos);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public static Matriz Sensibilizadas(Matriz ip, Matriz marcado) throws Exception {
        try {
            if (marcado.getM() != ip.getM()) {
                throw new Exception("Matrices de distinto tamaño");
            }
            int[][] prev = ip.getMatriz();
            int[][] marc = marcado.getMatriz();
            int[][] sensibilizadas = new int[1][ip.getN()];
            for (int i = 0; i < ip.getN(); i++) {
                int j = 0;
                boolean sensible = true;
                while ((j < ip.getM()) && sensible) {
                    if (prev[j][i] > marc[j][0]) {
                        sensible = false;
                        sensibilizadas[0][i] = 0;
                    }
                    j = j + 1;
                    if ((j == ip.getM() - 1) && sensible) {
                        sensibilizadas[0][i] = 1;
                    }
                }
            }
            return new Matriz(sensibilizadas);
        } catch (Exception e) {
            throw new Exception("No se ha podido obtener las transiciones disponibles" + e.getMessage());
        }
    }

    public Matriz getVectorSensibilizadas() {
        return vectorSensibilizadas;
    }
    /*
    public boolean transicionSensibilizada(int transición, Matriz VectorSensi) {
        if (VectorSensi.getMatriz()[0][transición] == 1) {
            return true;
        } else {
            return false;
        }
    }
    */
}
