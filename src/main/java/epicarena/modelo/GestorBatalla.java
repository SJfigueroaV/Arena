package epicarena.modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class GestorBatalla {

    /** Las dos acciones posibles, tal como se muestran en el desplegable. */
    public static final String ATAQUE_NORMAL = "Ataque Normal";
    public static final String HABILIDAD_ESPECIAL = "Habilidad Especial";

    /** Turnos que una habilidad especial queda sin poder usarse despues de lanzarla. */
    public static final int TURNOS_DE_RECARGA = 2;

    private static final double PROBABILIDAD_ESPECIAL_ENEMIGA = 0.3;

    private final List<Personaje> equipoJugador = new ArrayList<>();
    private final List<Personaje> equipoEnemigo = new ArrayList<>();
    private final Random azar;

    public GestorBatalla() {
        this(new Random());
    }

    /** Recibe el generador de azar para poder probar el contraataque con una semilla fija. */
    public GestorBatalla(Random azar) {
        this.azar = azar;
    }

    public void agregarHeroe(Personaje heroe) {
        agregar(equipoJugador, heroe);
    }

    public void agregarEnemigo(Personaje enemigo) {
        agregar(equipoEnemigo, enemigo);
    }

    private void agregar(List<Personaje> equipo, Personaje personaje) {
        if (personaje == null) {
            throw new IllegalArgumentException("El personaje no puede ser nulo");
        }
        if (buscarPorNombre(personaje.getNombre()) != null) {
            throw new IllegalArgumentException("Ya existe un personaje llamado " + personaje.getNombre());
        }
        equipo.add(personaje);
    }

    /** Vista de solo lectura del equipo: nadie fuera del gestor modifica las listas. */
    public List<Personaje> getEquipoJugador() {
        return Collections.unmodifiableList(equipoJugador);
    }

    public List<Personaje> getEquipoEnemigo() {
        return Collections.unmodifiableList(equipoEnemigo);
    }

    /** Busca en ambos equipos. Retorna null si no existe o ya fue eliminado. */
    public Personaje buscarPorNombre(String nombre) {
        Personaje encontrado = buscarEn(equipoJugador, nombre);
        return encontrado != null ? encontrado : buscarEn(equipoEnemigo, nombre);
    }

    private Personaje buscarEn(List<Personaje> equipo, String nombre) {
        for (Personaje personaje : equipo) {
            if (personaje.getNombre().equals(nombre)) {
                return personaje;
            }
        }
        return null;
    }

    /** Acciones que puede realizar el personaje segun sus capacidades. */
    public List<String> accionesDisponibles(Personaje personaje) {
        List<String> acciones = new ArrayList<>();
        if (personaje == null) {
            return acciones;
        }
        acciones.add(ATAQUE_NORMAL);
        if (personaje.tieneHabilidadEspecial() && turnosParaEspecial(personaje) == 0) {
            acciones.add(HABILIDAD_ESPECIAL);
        }
        return acciones;
    }

    /**
     * Personajes sobre los que el actor puede aplicar la accion: normalmente
     * el equipo rival, o el propio si su habilidad especial es de apoyo.
     */
    public List<Personaje> objetivosValidos(Personaje actor, String accion) {
        if (actor == null || accion == null) {
            return new ArrayList<>();
        }
        boolean esHeroe = equipoJugador.contains(actor);
        if (!esHeroe && !equipoEnemigo.contains(actor)) {
            return new ArrayList<>();
        }
        List<Personaje> aliados = esHeroe ? equipoJugador : equipoEnemigo;
        List<Personaje> rivales = esHeroe ? equipoEnemigo : equipoJugador;

        return new ArrayList<>(esAccionDeApoyo(actor, accion) ? aliados : rivales);
    }

    /** Indica si la accion se aplica sobre un companero en lugar de un rival. */
    public boolean esAccionDeApoyo(Personaje actor, String accion) {
        if (actor == null || !HABILIDAD_ESPECIAL.equals(accion) || !actor.tieneHabilidadEspecial()) {
            return false;
        }
        IHabilidadEspecial habilidad = (IHabilidadEspecial) actor;
        return habilidad.especialApuntaAAliado();
    }

    public boolean esHeroe(Personaje personaje) {
        return equipoJugador.contains(personaje);
    }

    /** Anota que el personaje acaba de usar su especial y debe recargarla. */
    public void registrarEspecialUsada(Personaje personaje) {
        // +1 porque el turno en que se usa tambien descuenta al terminar.
        personaje.iniciarRecarga(TURNOS_DE_RECARGA + 1);
    }

    /** Turnos que faltan para que el personaje pueda usar su especial. 0 si ya puede. */
    public int turnosParaEspecial(Personaje personaje) {
        if (personaje == null) {
            return 0;
        }
        return personaje.getTurnosDeRecarga();
    }

    /** Se llama al cerrar cada turno completo: avanza todas las recargas. */
    public void terminarTurno() {
        for (Personaje personaje : equipoJugador) {
            personaje.avanzarRecarga();
        }
        for (Personaje personaje : equipoEnemigo) {
            personaje.avanzarRecarga();
        }
    }

    /**
     * Decide que hace un personaje controlado por el juego: casi siempre el
     * ataque normal y, a veces, su especial si la tiene lista y sirve de algo.
     */
    public String elegirAccionAlAzar(Personaje actor) {
        if (!accionesDisponibles(actor).contains(HABILIDAD_ESPECIAL)) {
            return ATAQUE_NORMAL;
        }
        if (esAccionDeApoyo(actor, HABILIDAD_ESPECIAL) && masHerido(aliadosDe(actor)) == null) {
            return ATAQUE_NORMAL;
        }
        return azar.nextDouble() < PROBABILIDAD_ESPECIAL_ENEMIGA ? HABILIDAD_ESPECIAL : ATAQUE_NORMAL;
    }

    /**
     * Elige sobre quien aplica la accion: un rival al azar, o el companero
     * mas herido si la accion es de apoyo. Retorna null si no hay nadie.
     */
    public Personaje elegirObjetivoAlAzar(Personaje actor, String accion) {
        List<Personaje> candidatos = objetivosValidos(actor, accion);
        if (esAccionDeApoyo(actor, accion)) {
            Personaje herido = masHerido(candidatos);
            if (herido != null) {
                return herido;
            }
        }
        return elegirAlAzar(candidatos);
    }

    private List<Personaje> aliadosDe(Personaje actor) {
        return esHeroe(actor) ? equipoJugador : equipoEnemigo;
    }

    /** El personaje vivo al que le falta mayor proporcion de vida, o null si todos estan sanos. */
    private Personaje masHerido(List<Personaje> equipo) {
        Personaje elegido = null;
        double menorProporcion = 1.0;
        for (Personaje personaje : equipo) {
            double proporcion = (double) personaje.getPuntosDeVida() / personaje.getPuntosDeVidaMaximos();
            if (personaje.estaVivo() && proporcion < menorProporcion) {
                menorProporcion = proporcion;
                elegido = personaje;
            }
        }
        return elegido;
    }

    /** Retorna null si ya no quedan enemigos. */
    public Personaje elegirEnemigoAlAzar() {
        return elegirAlAzar(equipoEnemigo);
    }

    /** Retorna null si ya no quedan heroes. */
    public Personaje elegirHeroeAlAzar() {
        return elegirAlAzar(equipoJugador);
    }

    private Personaje elegirAlAzar(List<Personaje> equipo) {
        if (equipo.isEmpty()) {
            return null;
        }
        return equipo.get(azar.nextInt(equipo.size()));
    }

    /**
     * Saca de las listas a todos los personajes sin vida.
     *
     * @return los personajes eliminados en esta pasada
     */
    public List<Personaje> eliminarDerrotados() {
        List<Personaje> eliminados = new ArrayList<>();
        retirarCaidos(equipoJugador, eliminados);
        retirarCaidos(equipoEnemigo, eliminados);
        return eliminados;
    }

    private void retirarCaidos(List<Personaje> equipo, List<Personaje> eliminados) {
        Iterator<Personaje> iterador = equipo.iterator();
        while (iterador.hasNext()) {
            Personaje personaje = iterador.next();
            if (!personaje.estaVivo()) {
                iterador.remove();
                eliminados.add(personaje);
            }
        }
    }

    /** La partida termina cuando algun equipo se queda sin personajes vivos. */
    public boolean verificarFinDelJuego() {
        return contarVivos(equipoJugador) == 0 || contarVivos(equipoEnemigo) == 0;
    }

    public boolean ganoElJugador() {
        return contarVivos(equipoEnemigo) == 0 && contarVivos(equipoJugador) > 0;
    }

    private int contarVivos(List<Personaje> equipo) {
        int vivos = 0;
        for (Personaje personaje : equipo) {
            if (personaje.estaVivo()) {
                vivos++;
            }
        }
        return vivos;
    }
}
