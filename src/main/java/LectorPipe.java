import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class LectorPipe {
    private File incYmarc;
    private Element tablaPosterior;
    private Element tablaPrevia;
    private Element tablaCombinada;
    private Element tablaMarcado;

    /*
        private int [][] incidenciaPosterior;
        private int [][] IncidenciaPrevia;
        private int [][] incidenciaCombinada;
        private int [][] marcados;
    */
    public LectorPipe() {
        try {
            String direccion = (new File(".")).getCanonicalPath() + "//PetriNet//incidenciaYmarcado.html";
            this.incYmarc = new File(direccion);
            Document doc = this.parsear();
            Elements tables;
            tables = doc.select("table");
            this.tablaPosterior = tables.first();
            this.tablaPrevia = tables.first().nextElementSibling();
            this.tablaCombinada = tables.first().nextElementSibling().nextElementSibling();
            this.tablaMarcado = tables.first().nextElementSibling().nextElementSibling().nextElementSibling().nextElementSibling();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Element getSubTabla(Element tabla) {
        Elements tablasHijas = tabla.select("tr");
        Element tablas = tablasHijas.first().nextElementSibling();
        Element subtabla = tablas.select("tbody").first();
        return subtabla;
    }

    public int[][] getArreglo(Element subTabla) {
        try {
            Elements filas = subTabla.select("tr");
            Elements columnas = filas.first().select("td");
            //System.out.println("Cantidad de filas = "+ filas.size());
            //System.out.println("Cantidad de columnas = "+ columnas.size());
            int[][] arreglo = new int[filas.size() - 1][columnas.size() - 1];
            for (int i = 1; i < filas.size(); i++) {
                columnas = filas.get(i).select("td");
                for (int j = 1; j < columnas.size(); j++) {
                    arreglo[i - 1][j - 1] = Integer.parseInt(columnas.get(j).text());
                }
            }
            return arreglo;
        } catch (Exception e) {
            return null;
        }
    }

    public int[][] getIncidenciaPosterior() {
        Element subtabla = this.getSubTabla(this.tablaPosterior);
        return this.getArreglo(subtabla);
    }

    public int[][] getIncidenciaPrevia() {
        Element subtabla = this.getSubTabla(this.tablaPrevia);
        return this.getArreglo(subtabla);
    }

    public int[][] getIncidenciaCombinada() {
        Element subtabla = this.getSubTabla(this.tablaCombinada);
        return this.getArreglo(subtabla);
    }

    public int[][] getMarcados() {
        Element subtabla = this.getSubTabla(this.tablaMarcado);
        return this.getArreglo(subtabla);
    }

    public Document parsear() {
        try {
            Document doc = Jsoup.parse(this.incYmarc, "UTF-8", "http://example.com/");
            return doc;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<String> nombreTransiciones() {
        List<String> nombresTransiciones = new ArrayList<String>();
        Element fila = this.tablaPosterior.select("table tr").first().nextElementSibling().select("table tr").first();
        Elements nombres = fila.select("td");
        for (Element nombre :
                nombres) {
            nombresTransiciones.add(nombre.text().toString());
        }
        nombresTransiciones.remove(0);
        return nombresTransiciones;
    }

    public List<String> nombrePlazas() {
        List<String> nombrePlazas = new ArrayList<String>();
        Element fila = this.tablaMarcado.select("table tr").first().nextElementSibling().select("table tr").first();
        Elements nombres = fila.select("td");
        for (Element nombre :
                nombres) {
            nombrePlazas.add(nombre.text().toString());
        }
        nombrePlazas.remove(0);
        return nombrePlazas;
    }

    public static void main(String[] args) {
        try {
            LectorPipe lectorPipe = new LectorPipe();
            /*
            System.out.println("Matriz Posterior = ---------------------------------------------------------------");
            Matriz matrizPost= new Matriz(lectorPipe.getIncidenciaPosterior());
            matrizPost.imprimir();
            System.out.println("Matriz Previa = ---------------------------------------------------------------");
            Matriz matrizPrev= new Matriz(lectorPipe.getIncidenciaPrevia());
            matrizPrev.imprimir();
            System.out.println("Matriz Posterior = ---------------------------------------------------------------");
            Matriz matrizComb= new Matriz(lectorPipe.getIncidenciaCombinada());
            matrizComb.imprimir();
            System.out.println("Matriz Marcados = ---------------------------------------------------------------");
            Matriz matrizMarcados= new Matriz(lectorPipe.getMarcados());
            matrizMarcados.imprimir();
            */
            System.out.println(lectorPipe.nombrePlazas());
        } catch (Exception e) {

        }
    }
}
