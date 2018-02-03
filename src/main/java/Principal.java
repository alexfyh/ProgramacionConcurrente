import java.util.Scanner;

public class Principal {

    public static void main(String[] args) {
        try {
            System.out.println("Sistema de manufacturazion automatizado" + "\n");

            int politica;
            do {
                System.out.println("Seleccione la política = ");
                System.out.println("  1  para 1A-2B-1C");
                System.out.println("  2  para 3A-2B-1C");
                System.out.println("  3  para Politica Random");
                Scanner entrada = new Scanner(System.in);
                politica = entrada.nextInt();
            }
            while (politica != 1 && politica != 2 && politica != 3);

            ListasDeDisparos listas = new ListasDeDisparos();
            Monitor monitor = Monitor.getUniqueInstance(politica);

            Hilo h1 = new Hilo("Hilo 1", listas.l1, politica);
            Hilo h2 = new Hilo("Hilo 3", listas.l2, politica);
            Hilo h3 = new Hilo("Hilo 4", listas.l3, politica);
            Hilo h4 = new Hilo("Hilo 5", listas.l4, politica);
            Hilo h5 = new Hilo("Hilo 6", listas.l5, politica);
            Hilo h6 = new Hilo("Hilo 7", listas.l6, politica);
            Hilo h7 = new Hilo("Hilo 8", listas.l7, politica);
            Hilo h8 = new Hilo("Hilo 9", listas.l8, politica);

            monitor.getPolitica().registrarHilo(h1);
            monitor.getPolitica().registrarHilo(h2);
            monitor.getPolitica().registrarHilo(h3);
            monitor.getPolitica().registrarHilo(h4);
            monitor.getPolitica().registrarHilo(h5);
            monitor.getPolitica().registrarHilo(h6);
            monitor.getPolitica().registrarHilo(h7);
            monitor.getPolitica().registrarHilo(h8);

            h1.start();
            h2.start();
            h3.start();
            h4.start();
            h5.start();
            h6.start();
            h7.start();
            h8.start();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
