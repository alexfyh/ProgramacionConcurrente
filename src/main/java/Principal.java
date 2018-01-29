import java.util.Scanner;

public class Principal {

    public static void main (String[] args){
        System.out.println("Sistema de manufacturazion automatizado"+"\n");

        int  politica;
        do{
            System.out.println("Seleccione la política = ");
            System.out.println("  1  para 1A-2B-1C");
            System.out.println("  2  para 3A-2B-1C");
            System.out.println("  3  para Politica Random");
            Scanner entrada = new Scanner(System.in);
            politica = entrada.nextInt();
        }
        while(politica!=1&&politica!=2&&politica!=3);

        ListasDeDisparos listas =  new ListasDeDisparos();
        Monitor monitor= Monitor.getUniqueInstance(politica);

        Hilo h1 = new Hilo("Hilo 1",listas.l1, politica);
        Hilo h2 = new Hilo("Hilo 2",listas.l2, politica);
        Hilo h3 = new Hilo("Hilo 3",listas.l3, politica);
        Hilo h4 = new Hilo("Hilo 4",listas.l4, politica);
        Hilo h5 = new Hilo("Hilo 5",listas.l5, politica);
        Hilo h6 = new Hilo("Hilo 6",listas.l6, politica);
        Hilo h7 = new Hilo("Hilo 7",listas.l7, politica);
        Hilo h8 = new Hilo("Hilo 8",listas.l8, politica);
        Hilo h9 = new Hilo("Hilo 9",listas.l9, politica);


        monitor.mapeo(h1);
        monitor.mapeo(h2);
        monitor.mapeo(h3);
        monitor.mapeo(h4);
        monitor.mapeo(h5);
        monitor.mapeo(h6);
        monitor.mapeo(h7);
        monitor.mapeo(h8);
        monitor.mapeo(h9);

        h1.start();
        h2.start();
        h3.start();
        h4.start();
        h5.start();
        h6.start();
        h7.start();
        h8.start();
        h9.start();
    }
}
