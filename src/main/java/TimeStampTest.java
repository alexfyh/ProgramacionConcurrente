import static java.lang.Thread.sleep;

public class TimeStampTest {
    public static void main(String[] args){

        // usar LONG
        System.out.println(System.currentTimeMillis());
        System.out.println(Long.MAX_VALUE);
        System.out.println(Long.toString(Long.MAX_VALUE).length());
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(System.currentTimeMillis());
        int maximo = Integer.MAX_VALUE;
        long time = 318L;
        System.out.println("resultado" +maximo*100+0);

    }
}

/*

LectorTina
    -Definir si usar los [ ]
    -Extraer los T invariantes
Test
    -tener cuidado si no uso el ling tiempo

Politica
    - los arreglos equilibrio, etc, que se armen de acuerd  a LectorTina
Hilo
    - Que de error al querer ponerle un nombre a un hilo ya usado

Patrones graficos
    -Averiguar que poronga es esto y usarlo
EnumLog
    -Pasar a tipo enum todos los string usados en Log

Produccion C
    - Despierto al hilo cuando est{a sensibilizado o cuando ya está dentro de la ventana?

P invariantes y T invariantes TEST
 */
