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
            String direccion = (new File(".")).getCanonicalPath() + "//PetriNet//tinaNet-struct.txt";
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

    public static void main(String[] args){
        LectorTina lectorTina = new LectorTina(new LectorPipe());
        for (String linea :
                lectorTina.extraerLineas("tr ")) {
            System.out.println(linea);
        }
    }
}
