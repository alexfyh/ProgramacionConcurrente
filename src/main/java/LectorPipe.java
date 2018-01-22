import java.io.File;
import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
public class LectorPipe {
    private File incYmarc;


    public static void main(String[] args){
        LectorPipe lectorPipe = new LectorPipe();
        Document doc = lectorPipe.parsear();
        //System.out.println(doc);
        Elements tables;
        // Para la primera matriz = matriz de Incidencia Posterior
        tables = doc.select("table");
        System.out.println("Posterior:");
        System.out.println(tables.first().nextElementSibling().nextElementSibling().nextElementSibling().nextElementSibling().nextElementSibling().nextElementSibling());
        /*
        System.out.println("----------------------------------------------------------------------------------------------");
        System.out.println("Previo:");
        System.out.println(tables.next());
        System.out.println("----------------------------------------------------------------------------------------------");
        System.out.println("Combinado:");
        System.out.println(tables.next());
        System.out.println("----------------------------------------------------------------------------------------------");
        System.out.println("Inhibido:");
        System.out.println(tables.next());
        */

        /*
        for (Element table :
                tables) {
            System.out.println("----------------------------------------------------------------------------------------------");
            System.out.println(table);
        }
        */

        System.err.print("--------------------");

        // Para la segunda matriz = matriz de Incidencia Previa
        tables = doc.select("zdzxc");
        //System.out.print(table);

        // Para la cuarta matriz = matriz de marcado
        //table = doc.select("table").get(4);
        //System.out.print(table);



    }
    public LectorPipe(){
        try {
            String direccion = (new File(".")).getCanonicalPath()+"//PetriNet//incidenciaYmarcado.html";
            this.incYmarc= new File(direccion);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public File getIncYmarc(){
        return incYmarc;
    }
    public Document parsear(){
        try {
        Document doc = Jsoup.parse(this.incYmarc, "UTF-8", "http://example.com/");
        return doc;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
