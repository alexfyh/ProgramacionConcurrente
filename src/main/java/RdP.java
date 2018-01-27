public class RdP {
    private Matriz marcadoInicial;
    private Matriz marcadoActual;
    private Matriz incidencia;
    private Matriz incidenciaPrevia;
    private Matriz vectorSensibilizadas;
    private int contadorDisparos;
    private LectorPipe lectorPipe;

    private int [] alfa;
    private int [] beta;
    private long [] timeStamp;
    private long startTime;
    public final int unidadTiempo = 100;

    private String[] autorizados;

    public RdP() {
        try {
             this.lectorPipe = new LectorPipe();
            this.marcadoInicial = (Matriz.obtenerFila(new Matriz(lectorPipe.getMarcados()),0)).transpuesta();
            this.marcadoActual = this.marcadoInicial;
            this.incidenciaPrevia = new Matriz(lectorPipe.getIncidenciaPrevia());
            this.incidencia = new Matriz(lectorPipe.getIncidenciaCombinada());
            this.vectorSensibilizadas = Sensibilizadas(incidenciaPrevia, marcadoInicial);

            LectorTina lectorTina= new LectorTina(this.lectorPipe);
            this.alfa=lectorTina.getArregloAlfa();
            this.beta=lectorTina.getArregloBeta();
            this.startTime =System.currentTimeMillis();
            this.timeStamp = new long[this.alfa.length];
            for (int i = 0; i < timeStamp.length; i++) {
                timeStamp[i]= this.currentTime();
            }


            this.autorizados = new String[this.alfa.length];

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

    public int getContadorDisparos() {
        return contadorDisparos;
    }

    public boolean disparar(int x, long tiempo) throws Exception {
        try {
            if (x < 0 || x > this.incidencia.getMatriz()[0].length) {
                throw new Exception("Transicion no valida.");
            }
            Hilo hilo = (Hilo) Thread.currentThread();
            if (this.transicionSensibilizada(x,vectorSensibilizadas)&&estaDentroVentana(x,tiempo)&&estaAutorizado(hilo.getNombre(),x)) {
                this.marcadoActual = Matriz.suma(this.marcadoActual, Matriz.obtenerColumna(this.incidencia, x));
                Matriz sensibilizadosViejos = getVectorSensibilizadas();
                //int sensiPrevio = sensibilizadosViejos.getMatriz()[0][x];
                this.vectorSensibilizadas = Sensibilizadas(this.incidenciaPrevia, this.marcadoActual);
                actualizarTimeStamp(sensibilizadosViejos,vectorSensibilizadas,this.currentTime());
                //sensiPrevio tuvo que ser 1 porque se pudo disparar
                if(sensibilizadosViejos.getMatriz()[0][x]==vectorSensibilizadas.getMatriz()[0][x]){
                    timeStamp[x]=tiempo;
                }
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

    public LectorPipe getLectorPipe() {
        return this.lectorPipe;
    }

    public void actualizarTimeStamp(Matriz vectorSensibilizadasPrevia, Matriz vectorSensibilizadasNuevo,long tiempo){
        int [][] previa = vectorSensibilizadasPrevia.getMatriz();
        int [][] nuevo = vectorSensibilizadasNuevo.getMatriz();
        for (int i = 0; i < nuevo[0].length; i++) {
            if(nuevo[0][i]==0) {
                this.timeStamp[i]=-1L;
            }
            else{
                if(previa[0][i]==0){
                    this.timeStamp[i] = tiempo;
                }
            }
        }
    }
    public long [] getTimeStamp(){
        return this.timeStamp;
    }
    public long currentTime(){
        return (System.currentTimeMillis()-this.startTime);
    }

    public boolean transicionSensibilizada(int transición, Matriz VectorSensi) {
        if (VectorSensi.getMatriz()[0][transición] == 1) {
            return true;
        } else {
            return false;
        }
    }


    public boolean estaDentroVentana(int x, long tiempo){
        if((this.timeStamp[x]+this.alfa[x]*unidadTiempo)<=tiempo&&
        (tiempo<=this.timeStamp[x]+this.beta[x]*unidadTiempo)){
            return true;
        }
        else{
            // tuve que cambiar el valor de beta a un numero mas chico porque sino saltaba cualquier cosa
            /*
            System.out.println((this.timeStamp[x]+this.alfa[x]*unidadTiempo));
            System.out.println(this.currentTime());
            System.out.println(this.timeStamp[x]+this.beta[x]*unidadTiempo);
            */
            return false;
        }
    }

    public boolean estaAutorizado(String hilo,int transicion){
        if(autorizados[transicion]==null||autorizados[transicion].equals(hilo.trim())){
            return true;
        }
        else{
            return false;
        }
    }

    public void setAutorizado(String hilo, int transicion){

    }

    public int[] getAlfa() {
        return alfa;
    }

    public int[] getBeta() {
        return beta;
    }
}
