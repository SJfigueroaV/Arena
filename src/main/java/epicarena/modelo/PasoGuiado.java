package epicarena.modelo;

/**
 * Un turno de la batalla de practica: la jugada que se le pide al jugador,
 * por que es la mejor y como responde el enemigo, para que las cifras de la
 * explicacion siempre se cumplan.
 */
public class PasoGuiado {

    private final String atacante;
    private final String accion;
    private final String objetivo;
    private final String explicacion;
    private final String enemigoQueResponde;
    private final String objetivoDeLaRespuesta;

    public PasoGuiado(String atacante, String accion, String objetivo, String explicacion,
            String enemigoQueResponde, String objetivoDeLaRespuesta) {
        this.atacante = atacante;
        this.accion = accion;
        this.objetivo = objetivo;
        this.explicacion = explicacion;
        this.enemigoQueResponde = enemigoQueResponde;
        this.objetivoDeLaRespuesta = objetivoDeLaRespuesta;
    }

    /** True si la jugada elegida es exactamente la que pide este paso. */
    public boolean coincide(String atacanteElegido, String accionElegida, String objetivoElegido) {
        return atacante.equals(atacanteElegido) && accion.equals(accionElegida)
                && objetivo.equals(objetivoElegido);
    }

    /** Por ejemplo "Tyba → Ataque Normal → Sombra del Silbón". */
    public String resumen() {
        return atacante + " → " + accion + " → " + objetivo;
    }

    public String getAtacante() {
        return atacante;
    }

    public String getAccion() {
        return accion;
    }

    public String getObjetivo() {
        return objetivo;
    }

    public String getExplicacion() {
        return explicacion;
    }

    /** El enemigo que ataca despues de la jugada (con ataque normal). */
    public String getEnemigoQueResponde() {
        return enemigoQueResponde;
    }

    public String getObjetivoDeLaRespuesta() {
        return objetivoDeLaRespuesta;
    }
}
