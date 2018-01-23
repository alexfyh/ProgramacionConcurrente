import java.util.ArrayList;
import java.util.List;

public class Hilo extends Thread {

    final String nombre;
    final Monitor monitor;
    final List<Integer> enteros;

    public Hilo(String nombre, List<Integer> enteros, Monitor monitor) {
        this.nombre = nombre;
        this.monitor = monitor;
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
