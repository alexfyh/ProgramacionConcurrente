import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LectorTina {
    private LectorPipe lectorPipe;
    private File struct;
    private List<String> LineasStruct;

    public LectorTina(LectorPipe lectorPipe) {
        this.LineasStruct = new ArrayList<String>();
        FileReader fr = null;
        BufferedReader br = null;
        try {
            this.lectorPipe = lectorPipe;
            String direccion = (new File(".")).getCanonicalPath() + "//PetriNet//tinaNetTemporal-struct.txt";
            this.struct = new File(direccion);

            fr = new FileReader(struct);
            br = new BufferedReader(fr);
            String linea;
            while ((linea = br.readLine()) != null)
                LineasStruct.add(linea);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != fr) {
                    fr.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    public List<String> extraerLineas(String comienzo) {
        String regex = "^" + comienzo;
        Pattern patron = Pattern.compile(regex);
        List<String> lineasExtraidas = new ArrayList<String>();

        for (String linea :
                this.LineasStruct) {
            Matcher match = patron.matcher(linea);
            if (match.find()) {
                lineasExtraidas.add(linea);
            }
        }
        return lineasExtraidas;
    }

    public int[] getArregloAlfa() {
        List<String> listaTransiciones = this.lectorPipe.nombreTransiciones();
        int[] alfa = new int[listaTransiciones.size()];
        for (int i = 0; i < listaTransiciones.size(); i++) {
            List<String> lineaDeTransicion = extraerLineas("tr " + listaTransiciones.get(i));
            if (lineaDeTransicion.get(0).contains(",")) {
                String[] cast = lineaDeTransicion.get(0).split(",");
                cast = cast[0].split("[\\[\\]]");
                alfa[i] = Integer.parseInt(cast[1]);
            }
        }
        return alfa;
    }

    public int[] getArregloBeta() {
        List<String> listaTransiciones = this.lectorPipe.nombreTransiciones();
        int[] beta = new int[listaTransiciones.size()];
        for (int i = 0; i < listaTransiciones.size(); i++) {
            List<String> lineaDeTransicion = extraerLineas("tr " + listaTransiciones.get(i));
            if (lineaDeTransicion.get(0).contains(",")) {
                String[] cast = lineaDeTransicion.get(0).split(",");
                cast = cast[1].split("[\\[\\]]");
                if (cast[0].trim().equals("w")) {
                    beta[i] = 100000;
                    //beta[i]= Integer.MAX_VALUE;
                } else {
                    beta[i] = Integer.parseInt(cast[0]);
                }
            } else {
                beta[i] = 100000;
                //beta[i]= Integer.MAX_VALUE;
            }
        }
        return beta;
    }

    public List<String> getLineasStruct() {
        return this.LineasStruct;
    }

    public List<String> extraerLineas(String desde, String hasta) {
        boolean leyendo = false;
        List<String> lineas = new ArrayList<String>();
        for (String linea :
                this.getLineasStruct()) {
            if (linea.contains(hasta)) {
                break;
            }
            if (leyendo && linea.trim().length() != 0) {
                lineas.add(linea.trim());
            }
            if (linea.contains(desde)) {
                leyendo = true;
            }
        }
        return lineas;
    }

    public List<String> getLineasTInvariantes() {
        List<String> tInvariantes = this.extraerLineas("T-SEMI-FLOWS GENERATING", "ANALYSIS COMPLETED");
        tInvariantes.remove(0);
        tInvariantes.remove(tInvariantes.size() - 1);
        return tInvariantes;
    }

    public List<List<String>> getTInvariantes() {
        List<List<String>> listasTInvariantes = new ArrayList<>();
        for (String lineaT :
                getLineasTInvariantes()) {
            List<String> transiciones = new ArrayList<>(Arrays.asList(lineaT.split(" ")));
            listasTInvariantes.add(transiciones);
        }
        return listasTInvariantes;
    }

    public List<Integer> traducirListaTInvariantes(List<String> lista) {
        try {
            LectorPipe lectorPipe = new LectorPipe();
            final String path = (new File(".")).getCanonicalPath().toString();
            final String invariantesregistro = "/registro.txt";
            Log log = new Log(path + invariantesregistro, lectorPipe);
            List<Integer> enteros = new ArrayList<Integer>();
            for (String transicion :
                    lista) {
                enteros.add(log.traducirTransicion(transicion));
            }
            return enteros;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<List<Integer>> getListaTInvariantes(){
        List<List<Integer>> lista = new ArrayList<>();
        for (List<String> transiciones :
             getTInvariantes()) {
            lista.add(traducirListaTInvariantes(transiciones));
        }
        return lista;
    }

    public static void main(String[] args) {
        LectorTina lectorTina = new LectorTina(new LectorPipe());

        for (int entero :
                lectorTina.getArregloAlfa()) {
            System.out.println(entero);
        }
        System.out.println("-----------");
        for (int entero :
                lectorTina.getArregloBeta()) {
            System.out.println(entero);
        }
        for (List<String> tinv :
                lectorTina.getTInvariantes()) {
            for (String transicion :
                    tinv) {
                System.out.print(transicion + " -- ");
            }
            System.out.println("Tamaño = " + tinv.size());
        }
        for (List<Integer> lista :
                lectorTina.getListaTInvariantes()) {
            System.out.println(lista);
        }
    }

}
