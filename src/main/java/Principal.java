import java.util.Scanner;

public class Principal {


    public static void main(String[] args) {
        try {
            System.out.println("Sistema de manufacturazion automatizado" + "\n");
            do {
                System.out.println("Seleccione la política = ");
                System.out.println("  1  para 1A-2B-1C");
                System.out.println("  2  para 3A-2B-1C");
                System.out.println("  3  para Politica Random");
                Scanner entrada = new Scanner(System.in);
                Monitor.pol = entrada.nextInt();
            }
            while (Monitor.pol != 1 && Monitor.pol != 2 && Monitor.pol != 3);

            ListasDeDisparos listas = new ListasDeDisparos();
            Monitor monitor = Monitor.getUniqueInstance();

            Hilo h1 = new Hilo("Hilo 1", listas.l1);
            Hilo h2 = new Hilo("Hilo 2", listas.l2);
            Hilo h3 = new Hilo("Hilo 3", listas.l3);
            Hilo h4 = new Hilo("Hilo 4", listas.l4);
            Hilo h5 = new Hilo("Hilo 5", listas.l5);
            Hilo h6 = new Hilo("Hilo 6", listas.l6);
            Hilo h7 = new Hilo("Hilo 7", listas.l7);
            Hilo h8 = new Hilo("Hilo 8", listas.l8);
            Hilo h9 = new Hilo("Hilo 9", listas.l9);

            h1.start();
            h2.start();
            h3.start();
            h4.start();
            h5.start();
            h6.start();
            h7.start();
            h8.start();
            h9.start();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
