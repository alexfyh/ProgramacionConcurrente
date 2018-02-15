/**
 * Created by YepezHinostroza on 3/12/2017.
 */
public class MaquinaDeEstados {
    private EstadoMonitor estadoMonitor;

    public MaquinaDeEstados() {
        this.estadoMonitor = EstadoMonitor.Disponible;
        this.estadoMonitor.setHilo(null);
    }

    public boolean desbloquear(String hilo){
        if (this.estadoMonitor == EstadoMonitor.Bloqueado && this.estadoMonitor.getHilo().equals(hilo)) {
            this.estadoMonitor = EstadoMonitor.Disponible;
            this.estadoMonitor.setHilo(null);
            return true;
        } else {
            return false;
        }
    }

    public boolean bloquear(String hilo){
        if (this.estadoMonitor == EstadoMonitor.Disponible) {
            this.estadoMonitor = EstadoMonitor.Bloqueado;
            this.estadoMonitor.setHilo(hilo);
            return true;
        } else {
            return false;
        }
    }

    public boolean despertar(String hilo){
        if (this.estadoMonitor == EstadoMonitor.Bloqueado) {
            this.estadoMonitor.setHilo(hilo);
            return true;
        } else {
            return false;
        }
    }
}
