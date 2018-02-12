public class RdP {
    private Matriz marcadoInicial;
    private Matriz marcadoActual;
    private Matriz incidencia;
    private Matriz incidenciaPrevia;
    private Matriz incidenciaPosterior;
    private Matriz vectorSensibilizadas;
    private int contadorDisparos;
    private int contadorSolicitud;
    private LectorPipe lectorPipe;

    private int[] alfa;
    private int[] beta;
    private long[] timeStamp;
    private long startTime;
    public final int unidadTiempo = 50;
    private EnumLog motivo;

    private String[] autorizados;

    public RdP() {
        try {
            this.lectorPipe = new LectorPipe();
            this.marcadoInicial = (Matriz.obtenerFila(new Matriz(lectorPipe.getMarcados()), 0)).transpuesta();
            this.marcadoActual = this.marcadoInicial;
            this.incidenciaPrevia = new Matriz(lectorPipe.getIncidenciaPrevia());
            this.incidenciaPosterior = new Matriz(lectorPipe.getIncidenciaPosterior());
            this.incidencia = new Matriz(lectorPipe.getIncidenciaCombinada());
            this.vectorSensibilizadas = Sensibilizadas(incidenciaPrevia, marcadoInicial);

            LectorTina lectorTina = new LectorTina(this.lectorPipe);
            this.alfa = lectorTina.getArregloAlfa();
            this.beta = lectorTina.getArregloBeta();
            this.startTime = System.currentTimeMillis();
            this.timeStamp = new long[this.alfa.length];
            for (int i = 0; i < timeStamp.length; i++) {
                timeStamp[i] = this.currentTime();
            }
            this.autorizados = new String[this.alfa.length];
            contadorDisparos = 0;
            contadorSolicitud = 0;
            motivo = null;
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

    public int getContadorDisparos() {
        return contadorDisparos;
    }

    public int getContadorSolicitud() {
        return this.contadorSolicitud;
    }

    public boolean disparar(int x, long tiempo, String nombre) throws Exception {
        try {
            if (x < 0 || x > this.incidencia.getMatriz()[0].length) {
                throw new Exception("Transicion no valida.");
            }
            contadorSolicitud++;
            if ((estaAutorizado(x, nombre)) || (this.transicionSensibilizada(x, vectorSensibilizadas) &&
                    estaDentroVentana(x, tiempo) && llegoPrimero(x))) {
                if (this.motivo == EnumLog.MotivoDisparadoSinSleep) {
                    disparoPrevio(x, tiempo, nombre);
                }
                disparoPosterior(x, tiempo);
                contadorDisparos++;
                System.out.println("Contador de Disparos =  " + contadorDisparos);
                return true;
            } else {
                if (this.motivo == EnumLog.MotivoAntesDeVentana && llegoPrimero(x)) {
                    this.motivo = EnumLog.MotivoAntesDeVentana;
                    disparoPrevio(x, tiempo, nombre);
                }
                return false;
            }
        } catch (Exception e) {
            if (this.motivo == EnumLog.MotivoNoAutorizado) {
                return false;
            }
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }

    private void disparoPrevio(int x, long tiempo, String nombre) {
        try {
            this.marcadoActual = Matriz.suma(this.marcadoActual, Matriz.porEscalar(Matriz.obtenerColumna(this.incidenciaPrevia, x), -1));
            Matriz sensibilizadosViejos = getVectorSensibilizadas();
            //int sensiPrevio = sensibilizadosViejos.getMatriz()[0][x];
            long tiempoPrevio = timeStamp[x];
            this.vectorSensibilizadas = Sensibilizadas(this.incidenciaPrevia, this.marcadoActual);
            actualizarTimeStamp(sensibilizadosViejos, vectorSensibilizadas, tiempo);
            //sensiPrevio tuvo que ser 1 porque se pudo disparar
            /*if (sensibilizadosViejos.getMatriz()[0][x] == vectorSensibilizadas.getMatriz()[0][x]) {
                timeStamp[x] = tiempo;
            }
            */
            timeStamp[x] = tiempoPrevio;
        } catch (Exception e) {
            System.err.println("Error en disparo previo");
        }
        autorizados[x] = nombre;
    }

    private void disparoPosterior(int x, long tiempo) {
        try {
            this.marcadoActual = Matriz.suma(this.marcadoActual, Matriz.obtenerColumna(this.incidenciaPosterior, x));
            Matriz sensibilizadosViejos = getVectorSensibilizadas();
            //int sensiPrevio = sensibilizadosViejos.getMatriz()[0][x];
            this.vectorSensibilizadas = Sensibilizadas(this.incidenciaPrevia, this.marcadoActual);
            actualizarTimeStamp(sensibilizadosViejos, vectorSensibilizadas, tiempo);
            //sensiPrevio tuvo que ser 1 porque se pudo disparar
            if (sensibilizadosViejos.getMatriz()[0][x] == vectorSensibilizadas.getMatriz()[0][x]) {
                timeStamp[x] = tiempo;
            }
        } catch (Exception e) {
            System.err.println("Error en disparo posterior");
        }
        autorizados[x] = null;
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

    public LectorPipe getLectorPipe() {
        return this.lectorPipe;
    }

    private void actualizarTimeStamp(Matriz vectorSensibilizadasPrevia, Matriz vectorSensibilizadasNuevo, long tiempo) {
        int[][] previa = vectorSensibilizadasPrevia.getMatriz();
        int[][] nuevo = vectorSensibilizadasNuevo.getMatriz();
        for (int i = 0; i < nuevo[0].length; i++) {
            if (nuevo[0][i] == 0) {
                this.timeStamp[i] = -1L;
            } else {
                if (previa[0][i] == 0) {
                    this.timeStamp[i] = tiempo;
                }
            }
        }
    }

    public long[] getTimeStamp() {
        return this.timeStamp;
    }

    public long currentTime() {
        return (System.currentTimeMillis() - this.startTime);
    }

    public boolean transicionSensibilizada(int transicion, Matriz VectorSensi) {
        if (VectorSensi.getMatriz()[0][transicion] == 1) {
            return true;
        } else {
            this.motivo = EnumLog.MotivoNoSensibilizado;
            return false;
        }
    }

    private boolean estaDentroVentana(int x, long tiempo) {
        return (!llegoDespues(x, tiempo) && !llegoAntes(x, tiempo));
    }

    private boolean llegoAntes(int x, long tiempo) {
        if ((this.timeStamp[x] + this.alfa[x] * unidadTiempo) <= tiempo) {
            return false;
        } else {
            this.motivo = EnumLog.MotivoAntesDeVentana;
            return true;
        }
    }

    private boolean llegoDespues(int x, long tiempo) {
        if ((tiempo <= this.timeStamp[x] + this.beta[x] * unidadTiempo)) {
            return false;
        } else {
            this.motivo = EnumLog.MotivoDespuesDeVentana;
            return true;
        }
    }

    private boolean llegoPrimero(int transicion) {
        if (autorizados[transicion] == null) {
            this.motivo = EnumLog.MotivoDisparadoSinSleep;
            return true;
        } else {
            this.motivo = EnumLog.MotivoNoAutorizado;
            return false;
        }
    }

    private boolean estaAutorizado(int transicion, String nombre) throws Exception {
        if (autorizados[transicion] == null) {
            return false;
        } else {
            if (autorizados[transicion].equals(nombre)) {
                this.motivo = EnumLog.MotivoDisparadoConSleep;
                return true;
            } else {
                this.motivo = EnumLog.MotivoNoAutorizado;
                throw new Exception(" Válido para salir del if");
            }
        }
    }

    public long getTiempoADormir(int transicion, long tiempo) throws Exception {
        long diferencia = timeStamp[transicion] + alfa[transicion] * unidadTiempo - tiempo;
        if (diferencia < 0)
            throw new Exception("No debio haberse dormido");
        return diferencia;
    }

    public int[] getAlfa() {
        return alfa;
    }

    public int[] getBeta() {
        return beta;
    }

    public EnumLog getMotivo() {
        return this.motivo;
    }
}
