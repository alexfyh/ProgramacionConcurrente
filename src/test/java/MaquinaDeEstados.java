/**
 * Created by YepezHinostroza on 3/12/2017.
 */
public class MaquinaDeEstados {
    private EstadoMonitor estadoMonitor;

    public MaquinaDeEstados() {
        this.estadoMonitor = EstadoMonitor.Disponible;
        this.estadoMonitor.setHilo(null);
    }

    public void desbloquear(String hilo) throws Exception {
        if (this.estadoMonitor == EstadoMonitor.Bloqueado && this.estadoMonitor.getHilo().equals(hilo)) {
            this.estadoMonitor = EstadoMonitor.Disponible;
            this.estadoMonitor.setHilo(null);
        } else {
            throw new Exception("Error");
        }
    }

    public void bloquear(String hilo) throws Exception {
        if (this.estadoMonitor == EstadoMonitor.Disponible) {
            this.estadoMonitor = EstadoMonitor.Bloqueado;
            this.estadoMonitor.setHilo(hilo);
        } else {
            throw new Exception("Error");
        }
    }

    public void despertar(String hilo) throws Exception {
        // hara falta dos atributos?
        if (this.estadoMonitor == EstadoMonitor.Bloqueado) {
            this.estadoMonitor.setHilo(hilo);
        } else {
            throw new Exception("Error");
        }
    }
}
