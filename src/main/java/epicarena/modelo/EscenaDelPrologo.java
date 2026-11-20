package epicarena.modelo;

import java.util.ArrayList;
import java.util.List;

/** Un cuadro del prologo: lo que se narra, donde ocurre y quien aparece. */
public class EscenaDelPrologo {

    /** Los espantos estan presentes y se ven normales. */
    public static final String QUIETOS = "QUIETOS";
    /** Los espantos estan dormidos: se ven apagados. */
    public static final String DORMIDOS = "DORMIDOS";
    /** Los espantos empiezan dormidos y despiertan durante la escena. */
    public static final String DESPIERTAN = "DESPIERTAN";

    private final String texto;
    private final String escenario;
    private final List<String> heroes;
    private final List<String> espantos;
    private final String estadoDeLosEspantos;

    // Partes opcionales de la escena: lo que el jugador puede hacer en ella.
    private String instruccion;
    private String efectoAlEntrar;
    private int tunjosPorLanzar;
    /** Si es true, el jugador debe tocar a todos los que tienen texto antes de seguir. */
    private boolean hayQueTocarATodos;
    /** Lo que se cuenta al tocar a un personaje: el nombre en una lista y el texto en la otra. */
    private final List<String> nombresConTexto = new ArrayList<>();
    private final List<String> textosAlTocar = new ArrayList<>();
    /** Sonido de la demostracion al tocarlo, o null si solo se cuenta el texto. */
    private final List<String> efectosAlTocar = new ArrayList<>();

    public EscenaDelPrologo(String texto, String escenario, List<String> heroes,
            List<String> espantos, String estadoDeLosEspantos) {
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException("La escena necesita un texto");
        }
        this.texto = texto;
        this.escenario = escenario;
        this.heroes = new ArrayList<>(heroes);
        this.espantos = new ArrayList<>(espantos);
        this.estadoDeLosEspantos = estadoDeLosEspantos;
    }

    /** Escena solo con fondo y texto, sin personajes. */
    public EscenaDelPrologo(String texto, String escenario) {
        this(texto, escenario, new ArrayList<>(), new ArrayList<>(), QUIETOS);
    }

    public String getTexto() {
        return texto;
    }

    public String getEscenario() {
        return escenario;
    }

    public List<String> getHeroes() {
        return new ArrayList<>(heroes);
    }

    public List<String> getEspantos() {
        return new ArrayList<>(espantos);
    }

    public String getEstadoDeLosEspantos() {
        return estadoDeLosEspantos;
    }

    /** Lo que se le indica al jugador, por ejemplo "Toca a un espanto". Puede ser null. */
    public String getInstruccion() {
        return instruccion;
    }

    public void setInstruccion(String instruccion) {
        this.instruccion = instruccion;
    }

    /** Sonido que acompana la escena al aparecer. Puede ser null. */
    public String getEfectoAlEntrar() {
        return efectoAlEntrar;
    }

    public void setEfectoAlEntrar(String efectoAlEntrar) {
        this.efectoAlEntrar = efectoAlEntrar;
    }

    /** Cuantos tunjos debe lanzar el jugador antes de seguir. 0 si no hay ofrenda. */
    public int getTunjosPorLanzar() {
        return tunjosPorLanzar;
    }

    public void setTunjosPorLanzar(int tunjosPorLanzar) {
        if (tunjosPorLanzar < 0) {
            throw new IllegalArgumentException("No se pueden lanzar tunjos negativos");
        }
        this.tunjosPorLanzar = tunjosPorLanzar;
    }

    /** Lo que se cuenta cuando el jugador toca a ese personaje. */
    public void agregarTextoAlTocar(String nombre, String texto) {
        agregarTextoAlTocar(nombre, texto, null);
    }

    /**
     * Ademas del texto, el personaje hace una demostracion de su habilidad
     * acompanada del efecto de sonido indicado (por ejemplo "hechizo").
     */
    public void agregarTextoAlTocar(String nombre, String texto, String efecto) {
        nombresConTexto.add(nombre);
        textosAlTocar.add(texto);
        efectosAlTocar.add(efecto);
    }

    /** Retorna null si tocar a ese personaje no hace ninguna demostracion. */
    public String efectoAlTocar(String nombre) {
        int posicion = nombresConTexto.indexOf(nombre);
        if (posicion < 0) {
            return null;
        }
        return efectosAlTocar.get(posicion);
    }

    public boolean hayQueTocarATodos() {
        return hayQueTocarATodos;
    }

    public void setHayQueTocarATodos(boolean hayQueTocarATodos) {
        this.hayQueTocarATodos = hayQueTocarATodos;
    }

    /** Los nombres de quienes cuentan algo al tocarlos, en el orden en que se agregaron. */
    public List<String> getNombresConTexto() {
        return new ArrayList<>(nombresConTexto);
    }

    /** Retorna null si tocar a ese personaje no cuenta nada. */
    public String textoAlTocar(String nombre) {
        int posicion = nombresConTexto.indexOf(nombre);
        if (posicion < 0) {
            return null;
        }
        return textosAlTocar.get(posicion);
    }
}
