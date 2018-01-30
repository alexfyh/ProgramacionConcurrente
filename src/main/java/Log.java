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
    public final String[] resultadosDisparo = {"", "La transición no estaba sensibilizada",
            "No comenzó la ventana de disparo", "Expiró el tiempo de la ventana de disparo",
            "No estaba autorizado para disparar"};


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
            bw = new BufferedWriter(fw);
            bw.write(linea);
            bw.newLine();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        } finally {
            try {
                bw.close();
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

    public synchronized void registrarHilo(Hilo h) {
        this.escribir("Nombre de Hilo = " + h.getNombre(), this.registro);
        String transiciones = "";
        for (Integer i :
                h.getTransiciones()) {
            transiciones = transiciones + traducirDisparo(i) + " - ";
        }
        this.escribir(transiciones, this.registro);
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

    public synchronized void registrarBasico(Monitor m, int transicion, boolean bool, long tiempo, int resultado) {
        escribir("\n" + "Solicitud = " + m.getPetri().getContadorSolicitud(), this.getRegistro());
        escribir("Contador de disparos : " + m.getPetri().getContadorDisparos(), this.getRegistro());
        escribir("\n" + "Tiempo = " + tiempo, this.getRegistro());
        escribir("Cantidad de piezas producidas:  " + "A = " + (m.getPolitica().getLineaDeProduccion()[0] + m.getPolitica().getLineaDeProduccion()[1])
                + "   B = " + m.getPolitica().getLineaDeProduccion()[2] + "   C = " + m.getPolitica().getLineaDeProduccion()[3], this.getRegistro());
        escribir("\n", this.getRegistro());
        String cadena;
        if (bool) {
            cadena = "  ha disparado la transicion  : ";
        } else {
            cadena = "  no ha podido disparar la transicion  : ";
        }
        escribir(((Hilo) (Thread.currentThread())).getNombre() + cadena + traducirDisparo(transicion), this.getRegistro());
        escribir("\n" + "Motivo : " + this.resultadosDisparo[resultado], this.getRegistro());
    }

    public synchronized void registrarBasico2(Monitor m, Matriz sensi, Matriz enco) {
        escribir("\n", this.getRegistro());
        escribir("Marcado Actual : ", this.getRegistro());
        escribir(this.getMarcadosImprimibles(), this.getRegistro());
        escribir(m.getPetri().marcadoActual().toString() + "\n", this.getRegistro());
        escribir("\n", this.getRegistro());
        escribir(printHilosDeVector("Hilos Sensibilizados  =  ", sensi, m), this.getRegistro());
        escribir(printHilosDeVector("Hilos Encolados  =  ", enco, m), this.getRegistro());
    }

    public synchronized void registrarEXtendido(Monitor m, Matriz and, Hilo h) {
        escribir(printHilosDeVector("Hilos en ambas  =  ", and, m), this.getRegistro());
        if (h != null) {
            escribir("Hilo despertado  =  " + h.getNombre(), this.getRegistro());
        } else {
            escribir("Hilo despertado  =  " + "", this.getRegistro());
        }
        escribir("\n", this.getRegistro());
    }

    public synchronized void registrarTimeStamp(Monitor m) {
        escribir("TimeStamp = " + "\n", this.getRegistro());
        String cadena = "";
        for (long l :
                m.getPetri().getTimeStamp()) {
            cadena = cadena + "||" + Long.toString(l);
        }
        escribir(cadena, this.getRegistro());
        escribir("\n", this.getRegistro());
    }


    public synchronized void registrar(Monitor m, int transicion, boolean bool, Hilo h, long tiempo, int resultado) {
        registrarBasico(m, transicion, bool, tiempo, resultado);
        registrarBasico2(m, m.getPetri().getVectorSensibilizadas(), m.getVectorEncolados());
        registrarEXtendido(m, m.getVectorAnd(), h);
        registrarTimeStamp(m);
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

    public int cantidadHilos() {
        List<String> hilos = extraerLineas("Nombre de Hilo =", 0);
        return hilos.size();
    }

    public void leerHilos() {
        // No hace falta un list de list de string para las transiciones, basta con contains
        // Si va a hacer falta para verificar el orden de las trnasiciones
        this.nombreHilos = new ArrayList<String>();
        String[] cast;
        for (String linea :
                extraerLineas("Nombre de Hilo =", 0)) {
            cast = linea.split("=");
            nombreHilos.add(cast[1].trim());
        }
        this.nombreTransiciones = new ArrayList<String>();
        for (String linea :
                extraerLineas("Nombre de Hilo =", 1)) {
            nombreTransiciones.add(linea);
        }
    }

    public List<String> getNombreHilos() {
        return this.nombreHilos;
    }

    public List<String> getNombreTransiciones() {
        return this.nombreTransiciones;
    }

    public List<List<String>> getTransicionesDeHilos() {
        List<List<String>> lista = new ArrayList<List<String>>();
        List<String> lineas = new ArrayList<String>(extraerLineas("Nombre de Hilo =", 1));
        for (int i = 0; i < lineas.size(); i++) {
            List<String> transiciones = new ArrayList<String>();
            String[] cast = lineas.get(i).split("-");
            for (int j = 0; j < cast.length - 1; j++) {
                transiciones.add(cast[j].trim());
            }
            lista.add(transiciones);
        }
        return lista;
    }

    public List<String> getEstadosMonitor() {
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
                String[] cast = lineasALeer.get(i).split("=");
                if (cast[1].trim().length() != 0) {
                    lineas.add(lineasALeer.get(i));
                }
            }
        }
        return lineas;
    }

    public String printHilosDeVector(String inicio, Matriz Vector, Monitor monitor) {
        String cadena = inicio;
        for (int i = 0; i < Vector.getN(); i++) {
            if (Vector.getMatriz()[0][i] != 0) {
                cadena = cadena + monitor.getMapa().get(i).getNombre();
                cadena = cadena + " || ";
            }
        }
        return cadena;
    }

    public List<String> getHistorialMotivos() {
        List<String> motivos = new ArrayList<String>();
        List<String> lineasLeidas = extraerLineas("Motivo : ", 0);
        String[] cast;
        for (String linea :
                lineasLeidas) {
            cast = linea.split(":");
            motivos.add(cast[1].trim());
        }
        return motivos;
    }
}
