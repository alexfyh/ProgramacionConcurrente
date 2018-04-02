public enum Evento {
    T10("Robot1 toma pieza A"),
    T11("Robot1 coloca pieza A en máquina 1 comienza a procesar la pieza A"),
    T12("Robot1 coloca pieza A en la maquina3 y maquina 3 comienza a procesar la pieza A"),
    T13("Robot2 toma la pieza A de la maquina"),
    T14("Robot2 toma la pieza A de la maquina3"),
    T15("Robot 2 coloca la pieza A en la maquina 2 que comienza a trabajar en pieza A"),
    T16("Robot 2 coloca la pieza A en la maquina 4 que comienza a trabajar en pieza A"),
    T17("Robot 3 toma la pieza A de la maquina2"),
    T18("Robot 3 toma la pieza A de la maquina4"),
    T19("Robot2 toma la pieza B de la entrada2"),
    T21("Robot2 toma la pieza B de la entrada2"),
    T22("Robot2 coloca la pieza B en la maquina2 que comienza a trabajar en la pieza B"),
    T23("Robot2 toma la pieza B de la maquina2"),
    T24("Robot2 coloca la pieza B en la salida2"),
    T31("Robot3 toma la pieza C de la entrada3"),
    T32("Robot3 coloca la pieza C en la maquina4 que comienza a trabajar en la pieza C"),
    T33("Robot2 toma la pieza C de la maquina4"),
    T34("Robot 2 coloca la pieza C en la maquina3 que comienza a trabajar en la pieza C"),
    T35("Robot1 toma la pieza C de la maquina3"),
    T36("Robot1 coloca la pieza C en la salida3");


    private String cadena;
    Evento(String cadena){
        this.cadena=cadena;
    }
    public String toString(){
        return cadena;
    }

    public static void main(String[] args){
        for (Evento evento:
             Evento.values()) {
            System.out.println(evento.toString());
        }
    }
}
