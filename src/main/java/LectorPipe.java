import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;



public class LectorPipe {
    private File incYmarc;
    Element incidenciaPosterior;
    Element incidenciaPrevia;
    Element incidencia;
    Element marcado;



    public static void main(String[] args){
        LectorPipe lectorPipe = new LectorPipe();
        Document doc = lectorPipe.parsear();
        Elements tables;
        tables = doc.select("table");
        lectorPipe.incidenciaPosterior = tables.first();
        lectorPipe.incidenciaPrevia=tables.first().nextElementSibling();
        lectorPipe.incidencia=tables.first().nextElementSibling().nextElementSibling();

        Element arreglo = lectorPipe.getArreglo(lectorPipe.incidenciaPosterior);
        lectorPipe.getMatriz(arreglo);



    }
    public Element getArreglo(Element tabla ){
        Elements tablasHijas= tabla.select("tr");
        Element tablas = tablasHijas.first().nextElementSibling();
        Element arreglo = tablas.select("tbody").first();
        return arreglo;
    }
    public Matriz getMatriz(Element arreglo){
        try {
        Elements filas = arreglo.select("tr");
        Elements columnas = filas.first().select("td");
        System.out.println("Cantidad de filas = "+ filas.size());
        System.out.println("Cantidad de columnas = "+ columnas.size());
        int[][] matriz = new int [filas.size()-1][columnas.size()-1];

        for (int i = 1; i < filas.size(); i++) {
            columnas=filas.get(i).select("td");
            for (int j = 1; j <columnas.size() ; j++) {
                matriz[i-1][j-1]=Integer.parseInt(columnas.get(j).text());
            }
        }
        Matriz matrix =  new Matriz(matriz);
        matrix.imprimir();
        return matrix;
        }
        catch(Exception e){
            return null;
        }


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
