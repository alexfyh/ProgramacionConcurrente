/**
 * Created by Fabrizio_p on 30/08/2017.
 */

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Log {
    private File registro;
    final String direccionRegistro;
    private BufferedReader br;
    private BufferedWriter bw;
    private String EncabezadoMarcados;
    private List<String> nombreHilos;
    private List<String> nombreTransiciones;
    private LectorPipe lectorPipe;

    public Log(final String registro, LectorPipe lectorPipe) {
        this.direccionRegistro = registro;
        this.registro = new File(registro);
        this.lectorPipe = lectorPipe;
        this.EncabezadoMarcados = getMarcadosImprimibles();

    }

    public List<String> leerLineas() {
        List<String> lineasLeidas;
        lineasLeidas = new ArrayList<String>();
        try {
            FileReader fr = new FileReader(this.registro);
            br = new BufferedReader(fr);
            String linea;
            while ((linea = br.readLine()) != null) {
                // Me agregaba una línea con un string vacio sino
                if (linea.length() != 0) {
                    lineasLeidas.add(linea);
                }
            }
            return lineasLeidas;
        } catch (Exception e) {
            System.err.println(e.getMessage());
        } finally {
            try {
                br.close();

            } catch (Exception e) {

            }
            return lineasLeidas;
        }
    }

    public synchronized void escribir(final String linea, final File destino) {
        try {
            FileWriter fw = new FileWriter(destino, true);
            //fw.write(linea);
            //pw = new PrintWriter(fw);
            //pw.println(linea);
            bw = new BufferedWriter(fw);
            bw.write(linea);
            //bw.write("\n");
            bw.newLine();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        } finally {
            try {
                bw.close();
                //pw.close();
            } catch (Exception e) {
                return;
            }
        }
    }

    public File getRegistro() {
        return this.registro;
    }

    public void limpiar() {
        if (this.registro.exists())
            this.registro.delete();
        this.registro = new File(direccionRegistro);
    }

    public synchronized void registrarHilo(Hilo h){
        this.escribir("Nombre de Hilo = "+ h.getNombre(),this.registro);
        String transiciones = "";
        for (Integer i:
             h.getTransiciones()) {
            transiciones = transiciones+traducirDisparo(i)+" - ";
        }
        this.escribir(transiciones,this.registro);
    }

    public String traducirDisparo(int i) {
        String transicion = lectorPipe.nombreTransiciones().get(i);
        return transicion;
    }

    public int traducirTransicion(String transicion) throws Exception {
        for (int i = 0; i < lectorPipe.nombreTransiciones().size(); i++) {
            if (transicion.equals(lectorPipe.nombreTransiciones().get(i).trim())) {
                return i;
            }
        }
        throw new Exception("No se ha encontrado dicho nombre de transicion" + transicion);
    }

    public static Boolean esNumero(final String nume) {
        try {
            Integer.parseInt(nume);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public synchronized void registrarBasico(Monitor m, int transicion, boolean bool) {
        //escribir("------------------------------------------------------------------------------------------------------------------", this.getRegistro());
        escribir("\n"+"Tiempo = "+ m.currentTime(), this.getRegistro());
        //escribir("\n", this.getRegistro());
        escribir("Contador de disparos : " + m.getPetri().getContadorDisparos(), this.getRegistro());
        escribir("\n", this.getRegistro());
        String cadena;
        if (bool) {
            cadena = "  ha disparado la transicion  : ";
        } else {
            cadena = "  no ha podido disparar la transicion  : ";
        }
        escribir(((Hilo) (Thread.currentThread())).getNombre() + cadena + traducirDisparo(transicion), this.getRegistro());
    }

    public synchronized void registrarBasico2(Monitor m, Matriz sensi, Matriz enco) {
        escribir("\n", this.getRegistro());
        escribir("Marcado Actual : ", this.getRegistro());
        escribir(this.getMarcadosImprimibles(), this.getRegistro());
        //escribir("  M1  M2  M3  M4 P10 P11 P12 P13 P14 P15 P16 P17 P18 P20 P21 P22 P23 P30 P31 P32 P33 P34 P35  R1  R2  R3  s1  s2", this.getRegistro());
        escribir(m.getPetri().marcadoActual().toString() + "\n", this.getRegistro());
        // this.log.escribir("----------------------------------------------------------------------",log.getRegistro());
        escribir("\n", this.getRegistro());
        escribir(m.printHilosDeVector("Hilos Sensibilizados  =  ", sensi), this.getRegistro());
        escribir("\n", this.getRegistro());
        escribir(m.printHilosDeVector("Hilos Encolados  =  ", enco), this.getRegistro());
        escribir("\n", this.getRegistro());
    }

    public synchronized void registrarEXtendido(Monitor m, Matriz and, Hilo h) {
        escribir(m.printHilosDeVector("Hilos en ambas  =  ", and), this.getRegistro());
        escribir("\n", this.getRegistro());
        if (h != null) {
            escribir("Hilo despertado  =  " + h.getNombre(), this.getRegistro());
        } else {
            escribir("Hilo despertado  =  " + "", this.getRegistro());
        }
        escribir("\n", this.getRegistro());
    }

    public synchronized void registrar(Monitor m, int transicion, boolean bool, Hilo h) {
        registrarBasico(m, transicion, bool);
        registrarBasico2(m, m.getPetri().getVectorSensibilizadas(), m.getVectorEncolados());
        registrarEXtendido(m, m.getVectorAnd(), h);
    }

    public List<String> extraerLineas(String coincidencia, int desfasaje) {
        List<String> lineas = new ArrayList<String>();
        List<String> lineasALeer = leerLineas();
        for (int i = 0; i < lineasALeer.size(); i++) {
            if (lineasALeer.get(i).contains(coincidencia)) {
                lineas.add(lineasALeer.get(i + desfasaje));
            }
        }
        return lineas;
    }

    public List<String> extraerMarcados() {
        return extraerLineas(this.getEncabezadoMarcados(), 1);
    }

    public Matriz convertirMarcado(String Linea) throws Exception {
        try {
            List<Integer> enteros = new ArrayList<>();
            String[] casteado = Linea.split(" ");
            for (String numero :
                    casteado) {
                if (esNumero(numero)) {
                    enteros.add(Integer.parseInt(numero));
                }
            }
            int[][] arreglo = new int[1][enteros.size()];
            for (int i = 0; i < enteros.size(); i++) {
                arreglo[0][i] = enteros.get(i);
            }
            Matriz marcado = new Matriz(arreglo);
            return marcado;
        } catch (Exception e) {
            System.err.println("No se ha podido crear la matriz");
            return null;
        }
    }

    public List<Matriz> getHistorialMarcados() {
        List<Matriz> marcados = new ArrayList<Matriz>();
        List<String> lineas = extraerMarcados();
        try {
            for (String linea :
                    lineas) {
                marcados.add(this.convertirMarcado(linea).transpuesta());
            }
        } catch (Exception e) {
        }
        return marcados;

    }

    public String getMarcadosImprimibles() {
        String cadena = "";
        for (int i = 0; i < lectorPipe.nombrePlazas().size(); i++) {
            String campo = lectorPipe.nombrePlazas().get(i);
            while (campo.length() < 4) {
                campo = " " + campo;
            }
            cadena = cadena + campo;
        }
        return cadena;
    }

    public String getEncabezadoMarcados() {
        return this.EncabezadoMarcados;
    }

    public List<String> extraerDisparos() {
        List<String> lineas = new ArrayList<String>();
        List<String> lineasALeer = leerLineas();
        for (int i = 0; i < lineasALeer.size(); i++) {
            if (lineasALeer.get(i).contains("no ha podido disparar la transicion") ||
                    lineasALeer.get(i).contains("ha disparado la transicion  :")) {
                lineas.add(lineasALeer.get(i));
            }
        }
        return lineas;
    }

    public List<Integer> getHistorialDisparos() {
        List<String> lineasDisparos = extraerDisparos();
        List<Integer> disparos = new ArrayList<Integer>();
        String[] casteado;
        for (int i = 0; i < lineasDisparos.size(); i++) {
            casteado = lineasDisparos.get(i).split(":");
            try {
                disparos.add(traducirTransicion(casteado[1].trim()));
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
        return disparos;
    }

    public List<Boolean> getHistorialEstadoDisparos() {
        List<String> lineasDisparos = extraerDisparos();
        List<Boolean> estados = new ArrayList<Boolean>();
        String[] casteado;
        for (int i = 0; i < lineasDisparos.size(); i++) {
            if (lineasDisparos.get(i).contains("no")) {
                estados.add(false);
            } else {
                estados.add(true);
            }
        }
        return estados;
    }

    public List<String> getHistorialActividadHilos() {
        List<String> hilos = new ArrayList<String>();
        List<String> lineasALeer = this.extraerDisparos();
        for (String s :
                lineasALeer) {
            String[] cast;
            String hilo = "";
            if (s.contains("no")) {
                cast = s.split("no");
            } else {
                cast = s.split("ha disparado");
            }
            hilo = cast[0].trim();
            hilos.add(hilo);
        }
        return hilos;
    }

    public List<String> getListaDeHilos(String linea) {
        List<String> hilos = new ArrayList<String>();
        String[] cast = linea.split("=");
        cast = cast[1].split("\\|\\|");
        for (int i = 0; i < cast.length - 1; i++) {
            hilos.add(cast[i].trim());
        }
        return hilos;
    }

    public List<List<String>> getHistorialHilosSensibilizados() {
        List<List<String>> historialHilos = new ArrayList<>();
        List<String> hilos = extraerLineas("Hilos Sensibilizados  =", 0);
        for (String linea :
                hilos) {
            historialHilos.add(getListaDeHilos(linea));
        }
        return historialHilos;
    }

    public List<List<String>> getHistorialHilosEncolados() {
        List<List<String>> historialHilos = new ArrayList<>();
        List<String> hilos = extraerLineas("Hilos Encolados  =", 0);
        for (String linea :
                hilos) {
            historialHilos.add(getListaDeHilos(linea));
        }
        return historialHilos;
    }

    public List<List<String>> getHistorialHilosEnAmbas() {
        List<List<String>> historialHilos = new ArrayList<>();
        List<String> hilos = extraerLineas("Hilos en ambas  =", 0);
        for (String linea :
                hilos) {
            historialHilos.add(getListaDeHilos(linea));
        }
        return historialHilos;
    }

    public List<String> getHistorialHilosDespertados() {
        /*
        En el caso que no se haya despertado ni uno devuelve una cadena vacía.
         */
        List<String> historialHilos = new ArrayList<>();
        List<String> lineas = extraerLineas("Hilo despertado  =", 0);
        for (String linea :
                lineas) {
            String[] hilo = linea.split("=");
            historialHilos.add(hilo[1].trim());

        }
        return historialHilos;
    }

    public List<String> getHistorialHilosPermitidos() {
        List<String> hilosPermitidos = new ArrayList<String>();
        List<String> lineasALeer = this.leerLineas();
        for (String linea :
                lineasALeer) {
            if (linea.contains("obtiene el mutex")) {
                String[] cast = linea.split("obtiene");
                hilosPermitidos.add(cast[0].trim());
            } else {
                if (linea.contains("Hilo despertado  =")) {
                    String[] cast = linea.split("=");
                    if (cast[1].trim().length() != 0) {
                        hilosPermitidos.add(cast[1].trim());
                    }
                }
            }
        }
        return hilosPermitidos;
    }

    public int cantidadHilos(){
        List<String> hilos = extraerLineas("Nombre de Hilo =",0);
        return hilos.size();
    }

    public void leerHilos(){
        // No hace falta un list de list de string para las transiciones, basta con contains
        // Si va a hacer falta para verificar el orden de las trnasiciones
        this.nombreHilos = new ArrayList<String>();
        String[] cast;
        for (String linea :
                extraerLineas("Nombre de Hilo =",0)) {
            cast = linea.split("=");
            nombreHilos.add(cast[1].trim());
        }
        this.nombreTransiciones = new ArrayList<String>();
        for (String linea :
                extraerLineas("Nombre de Hilo =",1)) {
            nombreTransiciones.add(linea);
        }
    }
    public List<String> getNombreHilos(){
        return this.nombreHilos;
    }

    public List<String> getNombreTransiciones(){
        return this.nombreTransiciones;
    }

    public List<List<String>> getTransicionesDeHilos(){
        List<List<String>> lista= new ArrayList<List<String>>();
        List<String> lineas = new ArrayList<String>(extraerLineas("Nombre de Hilo =",1));
        for (int i = 0; i < lineas.size(); i++) {
            List<String> transiciones = new ArrayList<String>();
            String [] cast = lineas.get(i).split("-");
            for (int j = 0; j < cast.length-1; j++) {
                transiciones.add(cast[j].trim());
            }
            lista.add(transiciones);
        }
        return lista;
    }
    public List<String> getEstadosMonitor(){
        List<String> lineas = new ArrayList<String>();
        List<String> lineasALeer = leerLineas();
        for (int i = 0; i < lineasALeer.size(); i++) {
            if (lineasALeer.get(i).contains("obtiene el mutex")) {

                lineas.add(lineasALeer.get(i));
            }
            if (lineasALeer.get(i).contains("devuelve el mutex")) {

                lineas.add(lineasALeer.get(i));
            }
            if (lineasALeer.get(i).contains("Hilo despertado  =")) {
                String [] cast = lineasALeer.get(i).split("=");
                if(cast[1].trim().length()!=0){
                    lineas.add(lineasALeer.get(i));
                }
            }
        }
        return lineas;
    }
}
