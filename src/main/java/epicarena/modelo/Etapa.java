package epicarena.modelo;

import java.util.function.Supplier;

/** Un combate de la campana: su texto y la forma de armar la batalla. */
public class Etapa {

    private final String titulo;
    private final String introduccion;
    private final String cierre;
    private final Supplier<GestorBatalla> fabricaDeBatalla;

    /**
     * Recibe una fabrica y no la batalla ya armada, para poder crearla de
     * nuevo desde cero cada vez que el jugador reintenta la etapa.
     */
    public Etapa(String titulo, String introduccion, String cierre, Supplier<GestorBatalla> fabricaDeBatalla) {
        if (fabricaDeBatalla == null) {
            throw new IllegalArgumentException("La etapa necesita una batalla");
        }
        this.titulo = titulo;
        this.introduccion = introduccion;
        this.cierre = cierre;
        this.fabricaDeBatalla = fabricaDeBatalla;
    }

    /** Crea una batalla nueva con todos los personajes intactos. */
    public GestorBatalla crearBatalla() {
        return fabricaDeBatalla.get();
    }

    public String getTitulo() {
        return titulo;
    }

    public String getIntroduccion() {
        return introduccion;
    }

    /** Texto que se narra al superar la etapa. */
    public String getCierre() {
        return cierre;
    }
}
