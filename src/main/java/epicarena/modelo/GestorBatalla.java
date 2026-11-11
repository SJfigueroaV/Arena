package epicarena.modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class GestorBatalla {

    private final List<Personaje> equipoJugador = new ArrayList<>();
    private final List<Personaje> equipoEnemigo = new ArrayList<>();
    /** Turnos que una habilidad especial queda sin poder usarse despues de lanzarla. */
    public static final int TURNOS_DE_RECARGA = 2;

    private static final double PROBABILIDAD_ESPECIAL_ENEMIGA = 0.3;

    /** Turnos que le faltan a cada personaje para volver a usar su especial. */
    private final Map<Personaje, Integer> recargas = new HashMap<>();
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
    public List<TipoAccion> accionesDisponibles(Personaje personaje) {
        List<TipoAccion> acciones = new ArrayList<>();
        if (personaje == null) {
            return acciones;
        }
        acciones.add(TipoAccion.ATAQUE_NORMAL);
        if (personaje instanceof IHabilidadEspecial && turnosParaEspecial(personaje) == 0) {
            acciones.add(TipoAccion.HABILIDAD_ESPECIAL);
        }
        return acciones;
    }

    /**
     * Personajes sobre los que el actor puede aplicar la accion: normalmente
     * el equipo rival, o el propio si su habilidad especial es de apoyo.
     */
    public List<Personaje> objetivosValidos(Personaje actor, TipoAccion accion) {
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
    public boolean esAccionDeApoyo(Personaje actor, TipoAccion accion) {
        return accion == TipoAccion.HABILIDAD_ESPECIAL
                && actor instanceof IHabilidadEspecial habilidad
                && habilidad.especialApuntaAAliado();
    }

    public boolean esHeroe(Personaje personaje) {
        return equipoJugador.contains(personaje);
    }

    /** Anota que el personaje acaba de usar su especial y debe recargarla. */
    public void registrarEspecialUsada(Personaje personaje) {
        // +1 porque el turno en que se usa tambien descuenta al terminar.
        recargas.put(personaje, TURNOS_DE_RECARGA + 1);
    }

    /** Turnos que faltan para que el personaje pueda usar su especial. 0 si ya puede. */
    public int turnosParaEspecial(Personaje personaje) {
        return recargas.getOrDefault(personaje, 0);
    }

    /** Se llama al cerrar cada turno completo: avanza todas las recargas. */
    public void terminarTurno() {
        Iterator<Map.Entry<Personaje, Integer>> iterador = recargas.entrySet().iterator();
        while (iterador.hasNext()) {
            Map.Entry<Personaje, Integer> recarga = iterador.next();
            if (recarga.getValue() <= 1) {
                iterador.remove();
            } else {
                recarga.setValue(recarga.getValue() - 1);
            }
        }
    }

    /**
     * Decide que hace un personaje controlado por el juego: casi siempre el
     * ataque normal y, a veces, su especial si la tiene lista y sirve de algo.
     */
    public TipoAccion elegirAccionAlAzar(Personaje actor) {
        if (!accionesDisponibles(actor).contains(TipoAccion.HABILIDAD_ESPECIAL)) {
            return TipoAccion.ATAQUE_NORMAL;
        }
        if (esAccionDeApoyo(actor, TipoAccion.HABILIDAD_ESPECIAL) && masHerido(aliadosDe(actor)) == null) {
            return TipoAccion.ATAQUE_NORMAL;
        }
        return azar.nextDouble() < PROBABILIDAD_ESPECIAL_ENEMIGA
                ? TipoAccion.HABILIDAD_ESPECIAL
                : TipoAccion.ATAQUE_NORMAL;
    }

    /**
     * Elige sobre quien aplica la accion: un rival al azar, o el companero
     * mas herido si la accion es de apoyo. Retorna null si no hay nadie.
     */
    public Personaje elegirObjetivoAlAzar(Personaje actor, TipoAccion accion) {
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
                recargas.remove(personaje);
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
