import java.util.ArrayList;
import java.util.List;

public class Hilo extends Thread {

    private final String nombre;
    private final Monitor monitor;
    private final List<Integer> enteros;
    private static List<String> nombresUsados = new ArrayList<>();

    public Hilo(String nombre, List<Integer> enteros) throws Exception {
        if(Hilo.nombresUsados.contains(nombre)){
            throw new Exception("Nombre "+ nombre+ " ya ha sido usado.");
        }
        Hilo.nombresUsados.add(nombre);
        this.nombre = nombre;
        this.monitor = Monitor.getUniqueInstance();
        this.enteros = enteros;
        this.monitor.getLog().registrarHilo(this);
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
