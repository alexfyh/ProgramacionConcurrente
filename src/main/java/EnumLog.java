public enum EnumLog {

    MotivoDisparadoSinSleep("Sensibilizado,dentro ventana y sin haber esperado"),
    MotivoNoSensibilizado("La transicion no estaba sensibilizada"),
    MotivoAntesDeVentana("Todavia no comenzo la ventana de disparo"),
    MotivoDespuesDeVentana("Expiro el tiempo de la ventana de disparo"),
    MotivoNoAutorizado("No estaba autorizado para disparar"),
    MotivoDisparadoConSleep("Volvio del sleep"),

    ResultadoPositivoDisparo(" ha disparado la transicion = "),
    ResultadoNegativoDisparo(" no" + ResultadoPositivoDisparo.toString()),

    Texto_Solicitud("Solicitud = "),
    Texto_ContadorDisparos("Contador de disparos = "),
    Texto_Tiempo("Tiempo = "),
    Texto_CantidadProducida("Cantidad de piezas producidas : "),
    Texto_Motivo("Motivo : "),
    Texto_Marcado("Marcado Actual : "),
    Texto_TransicionesPorDisparar("Transiciones esperando disparar = "),
    Texto_HilosSensibilizados("Hilos Sensibilizados  =  "),
    Texto_HilosEncolados("Hilos Encolados  =  "),
    Texto_HilosEnAmbas("Hilos en ambas  =  "),
    Texto_HiloDesperto("Hilo despertado  =  "),
    Texto_TimeStamp("TimeStamp = "),
    Texto_PideMutex(" pide el mutex."),
    Texto_ObtieneMutex(" obtiene el mutex."),
    Texto_DevuelveMutex(" devuelve el mutex."),
    Texto_SeDormira(" se dormira "),
    Texto_ms("milisegundos."),
    Texto_Inicio(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"),
    Texto_Fin("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");



    private String cadena;

    EnumLog(String cadena) {
        this.cadena = cadena;
    }

    public String toString() {
        return this.cadena;
    }
}
