/**
 * Created by YepezHinostroza on 3/12/2017.
 */
public enum EstadoMonitor {
    Disponible, Bloqueado;
    private String hilo;

    public String getHilo() {
        return hilo;
    }

    public void setHilo(String hilo) {
        this.hilo = hilo;
    }
}
