import java.util.ArrayList;
import java.util.List;

public class Hilo extends Thread {

    final String nombre;
    final Monitor monitor;
    final List<Integer> enteros;
    static List<String> nombresUsados = new ArrayList<>();

    public Hilo(String nombre, List<Integer> enteros,int pol) throws Exception {
        if(Hilo.nombresUsados.contains(nombre)){
            throw new Exception("Nombre "+ nombre+ " ya ha sido usado.");
        }
        Hilo.nombresUsados.add(nombre);
        this.nombre = nombre;
        this.monitor = Monitor.getUniqueInstance(pol);
        this.enteros = enteros;
    }

    public String getNombre() {
        return this.nombre;
    }

    public void run() {
        try {
            while (true) {
                for (Integer i : enteros) {
                    monitor.dispararTransicion(i);
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public List<Integer> getTransiciones() {
        return enteros;
    }
}
