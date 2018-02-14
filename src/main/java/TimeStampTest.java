import javafx.util.converter.IntegerStringConverter;

import javax.rmi.ssl.SslRMIClientSocketFactory;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;

public class TimeStampTest {
    public static void main(String[] args){
        for (EnumLog en :
                EnumLog.values()) {
            System.out.println(en);
        }


    }
}

/*

Test
    -tener cuidado si no uso el ling tiempo

EnumLog
    -Pasar a tipo enum todos los string usados en Log


P invariantes y T invariantes TEST

VERIFICAR TIEMSTAM EN DISPARADOS DESPUDES DE VOLVER DEL SLEEP

TODO VER SI SE PUEDE CAMBIAR A UNA CLASE LAS FUNCIONALIDADES DE POLITICA DE MAPEO
 */
