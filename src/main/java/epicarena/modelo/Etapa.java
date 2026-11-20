package epicarena.modelo;

/** Un combate de la campana: los textos que se narran antes y despues. */
public class Etapa {

    private final String titulo;
    private final String escenario;
    private final String introduccion;
    private final String cierre;

    public Etapa(String titulo, String introduccion, String cierre) {
        this(titulo, null, introduccion, cierre);
    }

    /** El escenario es el nombre del lugar donde se pelea, por ejemplo "paramo". */
    public Etapa(String titulo, String escenario, String introduccion, String cierre) {
        this.titulo = titulo;
        this.escenario = escenario;
        this.introduccion = introduccion;
        this.cierre = cierre;
    }

    public String getTitulo() {
        return titulo;
    }

    /** Retorna null si la etapa no tiene escenario. */
    public String getEscenario() {
        return escenario;
    }

    public String getIntroduccion() {
        return introduccion;
    }

    /** Texto que se narra al superar la etapa. */
    public String getCierre() {
        return cierre;
    }
}
