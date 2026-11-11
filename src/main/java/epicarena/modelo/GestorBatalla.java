package epicarena.modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class GestorBatalla {

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
    public List<TipoAccion> accionesDisponibles(Personaje personaje) {
        List<TipoAccion> acciones = new ArrayList<>();
        if (personaje == null) {
            return acciones;
        }
        acciones.add(TipoAccion.ATAQUE_NORMAL);
        if (personaje instanceof IHabilidadEspecial) {
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

        boolean apoyaAliados = accion == TipoAccion.HABILIDAD_ESPECIAL
                && actor instanceof IHabilidadEspecial habilidad
                && habilidad.especialApuntaAAliado();
        return new ArrayList<>(apoyaAliados ? aliados : rivales);
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
