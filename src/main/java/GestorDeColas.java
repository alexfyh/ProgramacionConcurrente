import java.util.HashMap;
import java.util.Map;

public class GestorDeColas {
    private Map<Integer,Hilo> mapa;

    public GestorDeColas(){
        this.mapa= new HashMap<>();
    }
    public void encolar(Integer entero,Hilo h){
        mapa.put(entero,h);
    }
    public Hilo desencolar(Integer entero){
        Hilo h = mapa.get(entero);
        mapa.remove(entero);
        return h;
    }
}
