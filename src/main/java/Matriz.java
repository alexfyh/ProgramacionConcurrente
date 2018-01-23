public class Matriz {
    final int m, n;
    int[][] matriz;

    public Matriz(int[][] matriz) throws MatrizException {
        int m = matriz.length;
        int n = matriz[0].length;
        for (int[] i : matriz) {
            if (i.length != n) {
                throw new MatrizException("Matriz no rectangular");
            }
        }
        this.m = m;
        this.n = n;
        this.matriz = matriz;
        //System.out.println("Matriz de "+ m + "  filas y "+ n+"  columnas");
    }

    public int getM() {
        return this.m;
    }

    public int getN() {
        return this.n;
    }

    public int[][] getMatriz() {
        return this.matriz;
    }

    public void imprimir() {
        String espacio = " ";
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                String numero = String.valueOf(this.matriz[i][j]);
                while (numero.length() < 4) {
                    numero = espacio.concat(numero);
                }
                System.out.print(numero);
            }
            System.out.println("");
        }
    }

    public static Matriz suma(Matriz A, Matriz B) throws MatrizException {
        if (A.getM() != B.getM() || A.getN() != B.getN()) {
            throw new MatrizException("Suma : Matrices de tamaños incompatibles");
        }
        int[][] a = A.getMatriz();
        int[][] b = B.getMatriz();
        int m = A.getM();
        int n = A.getN();
        int[][] resultado = new int[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                resultado[i][j] = a[i][j] + b[i][j];
            }
        }
        return new Matriz(resultado);
    }

    public static Matriz multiplicacion(Matriz A, Matriz B) throws MatrizException {
        if (A.getN() != B.getM()) {
            throw new MatrizException("Multiplicacion : Dimensiones incompatibles ");
        }
        int m = A.getM();
        int n = B.getN();
        int[][] a = A.getMatriz();
        int[][] b = B.getMatriz();
        int[][] resultado = new int[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                int suma = 0;
                for (int indice = 0; indice < A.getN(); indice++) {
                    suma = suma + a[i][indice] * b[indice][j];
                }
                resultado[i][j] = suma;
            }
        }
        return new Matriz(resultado);
    }

    public static Matriz porEscalar(Matriz matriz, int escalar) throws MatrizException {
        int[][] array = matriz.getMatriz();
        int m = matriz.getM();
        int n = matriz.getN();
        int[][] resultado = new int[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                resultado[i][j] = array[i][j] * escalar;
            }
        }
        return new Matriz(resultado);
    }

    public static Matriz obtenerColumna(Matriz matriz, int x) throws MatrizException {
        if (x < 0 || x > matriz.getN()) {
            throw new MatrizException("Columna no disponible");
        }
        int[][] array = new int[matriz.getN()][1];
        array[x][0] = 1;
        Matriz resultado = Matriz.multiplicacion(matriz, new Matriz(array));
        return resultado;
    }

    public static Matriz obtenerFila(Matriz matriz, int x) throws MatrizException {
        if (x < 0 || x > matriz.getM()) {
            throw new MatrizException("Fila no disponible");
        }
        int[][] array = new int[1][matriz.getM()];
        array[0][x] = 1;
        Matriz resultado = Matriz.multiplicacion(new Matriz(array), matriz);
        return resultado;
    }

    public Matriz transpuesta() {
        int m = this.getM();
        int n = this.getN();
        int[][] original = this.getMatriz();
        int[][] transpuesto = new int[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                transpuesto[i][j] = original[j][i];
            }
        }
        try {
            return new Matriz(transpuesto);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean esIgual(Matriz B) {
        if (B == null) {
            return false;
        }
        if ((this.getM() != B.getM()) || (this.getN() != B.getN())) {
            return false;
        } else {
            int[][] a = this.getMatriz();
            int[][] b = B.getMatriz();
            for (int i = 0; i < this.getM(); i++) {
                for (int j = 0; j < this.getN(); j++) {
                    if (a[i][j] != b[i][j]) {
                        return false;
                    }
                }
            }
            return true;
        }
    }

    public String toString() {
        String matriz = "";
        Matriz transpuesta = this.transpuesta();
        int[][] arreglo = transpuesta.getMatriz();
        for (int i = 0; i < transpuesta.getM(); i++) {
            for (int j = 0; j < transpuesta.getN(); j++) {
                String numero = Integer.toString(arreglo[i][j]);
                while (numero.length() < 4) {
                    numero = " " + numero;
                }
                matriz = matriz + numero;
            }
            matriz = matriz + "\n";
        }
        return matriz;
    }

    public Matriz clonar() throws MatrizException {
        try {
            int[][] arreglo = new int[this.getM()][this.getN()];
            for (int i = 0; i < this.getM(); i++) {
                for (int j = 0; j < this.getN(); j++) {
                    arreglo[i][j] = this.getMatriz()[i][j];
                }
            }
            return new Matriz(arreglo);
        } catch (Exception e) {
            throw e;
        }
    }

    public static Matriz matrizVacia(int m, int n) throws Exception {
        try {
            int[][] vacia = new int[m][n];
            return new Matriz(vacia);
        } catch (Exception e) {
            throw new Exception("No se pudo crear la matriz de ceros");
        }
    }

    public void and(Matriz A, Matriz B) throws Exception {
        if (A.getM() != B.getM() || A.getN() != B.getN()) {
            throw new Exception("Matrices de dimensiones incompatibles");
        }
        int[][] a = A.getMatriz();
        int[][] b = B.getMatriz();
        int[][] c = this.getMatriz();

        for (int i = 0; i < A.getM(); i++) {
            for (int j = 0; j < A.getN(); j++) {
                c[i][j] = a[i][j] * b[i][j];
            }
        }
    }

    public void or(Matriz A, Matriz B) throws Exception {
        if (A.getM() != B.getM() || A.getN() != B.getN()) {
            throw new Exception("Matrices de dimensiones incompatibles");
        }
        int[][] a = A.getMatriz();
        int[][] b = B.getMatriz();
        int[][] c = this.getMatriz();

        for (int i = 0; i < A.getM(); i++) {
            for (int j = 0; j < A.getN(); j++) {
                if (a[i][j] == 1 || b[i][j] == 1) {
                    c[i][j] = 1;
                } else {
                    c[i][j] = 0;
                }
            }
        }
    }

    public int cantidadDeUnos() {
        int cantidad = 0;
        int[][] arreglo = this.getMatriz();
        for (int i = 0; i < this.getN(); i++) {
            if (arreglo[0][i] != 0) {
                cantidad++;
            }
        }
        return cantidad;
    }
}
