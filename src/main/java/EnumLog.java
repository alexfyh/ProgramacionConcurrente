public enum EnumLog {

    MotivoDisparadoSinSleep("Sensibilizado,dentro ventana y sin haber esperado "),
    MotivoNoSensibilizado("La transicion no estaba sensibilizada"),
    MotivoAntesDeVentana("Todavia no comenzo la ventana de disparo"),
    MotivoDespuesDeVentana("Expiro el tiempo de la ventana de disparo"),
    MotivoNoAutorizado("No estaba autorizado para disparar"),
    MotivoDisparadoConSleep("Volvio del sleep");

    private String cadena;

    EnumLog(String cadena) {
        this.cadena = cadena;
    }

    public String toString() {
        return this.cadena;
    }
}
