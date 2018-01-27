import java.io.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LectorTina {
    private LectorPipe lectorPipe;
    private File struct;
    private List<String> LineasStruct;

    public  LectorTina(LectorPipe lectorPipe){
        this.LineasStruct = new ArrayList<String>();
        FileReader fr = null;
        BufferedReader br = null;
        try {
        this.lectorPipe = lectorPipe;
            String direccion = (new File(".")).getCanonicalPath() + "//PetriNet//tinaNetTemporal-struct.txt";
            this.struct = new File(direccion);

            fr = new FileReader (struct);
            br = new BufferedReader(fr);
            String linea;
            while((linea=br.readLine())!=null)
                LineasStruct.add(linea);
        }
        catch (IOException e) { e.printStackTrace(); }
        catch (Exception e){ e.printStackTrace();}
        finally{
            try{
                if( null != fr ){
                    fr.close();
                }
            }catch (Exception e2){
                e2.printStackTrace();
            }
        }
    }
    public List<String> extraerLineas(String comienzo){
        String regex = "^"+comienzo;
        Pattern patron = Pattern.compile(regex);
        List<String> lineasExtraidas = new ArrayList<String>();

        for (String linea :
                this.LineasStruct) {
            Matcher match = patron.matcher(linea);
            if(match.find()){
                lineasExtraidas.add(linea);
            }
        }
        return lineasExtraidas;
    }

    public int[] getArregloAlfa(){
        List<String> listaTransiciones = this.lectorPipe.nombreTransiciones();
        int[] alfa = new int[listaTransiciones.size()];
        for (int i = 0; i <listaTransiciones.size() ; i++) {
            List<String> lineaDeTransicion = extraerLineas("tr " + listaTransiciones.get(i));
            if (lineaDeTransicion.get(0).contains(",")){
                String[] cast = lineaDeTransicion.get(0).split(",");
                cast = cast[0].split("[\\[\\]]");
                alfa[i]= Integer.parseInt(cast[1]);
            }
        }
        return alfa;
    }
    public int[] getArregloBeta(){
        List<String> listaTransiciones = this.lectorPipe.nombreTransiciones();
        int[] beta = new int[listaTransiciones.size()];
        for (int i = 0; i <listaTransiciones.size() ; i++) {
            List<String> lineaDeTransicion = extraerLineas("tr " + listaTransiciones.get(i));
            if (lineaDeTransicion.get(0).contains(",")){
                String[] cast = lineaDeTransicion.get(0).split(",");
                cast = cast[1].split("[\\[\\]]");
                if(cast[0].trim().equals("w")){
                    beta[i]= 100000;
                    //beta[i]= Integer.MAX_VALUE;
                }
                else{
                    beta[i]= Integer.parseInt(cast[0]);
                }
            }
            else{
                beta[i]= 100000;
                //beta[i]= Integer.MAX_VALUE;
            }
        }
        return beta;
    }

    public static void main(String[] args){
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
    }
}
