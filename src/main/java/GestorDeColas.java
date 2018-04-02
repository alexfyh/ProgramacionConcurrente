import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class GestorDeColas {
    private Map<Integer,Queue<Hilo>> mapa;

    public GestorDeColas(){
        this.mapa= new HashMap<>();
    }
    public void encolar(Integer entero,Hilo h){
        if(mapa.containsKey(entero)){
            mapa.get(entero).add(h);
        }
        else{
            Queue<Hilo> cola = new LinkedList<>();
            cola.add(h);
            mapa.put(entero,cola);
        }
    }
    public Hilo desencolar(Integer entero){
        if(mapa.containsKey(entero)){
            Hilo h = mapa.get(entero).remove();
            if(mapa.get(entero).peek()==null){
                mapa.remove(entero);
            }
            return h;
        }
        else{
            // tal vez debería devolver una excepcion
            return null;
        }

    }
}
