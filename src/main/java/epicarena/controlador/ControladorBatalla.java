package epicarena.controlador;

import epicarena.modelo.GestorBatalla;
import epicarena.modelo.IHabilidadEspecial;
import epicarena.modelo.Personaje;
import epicarena.modelo.TipoAccion;
import epicarena.vista.VentanaBatalla;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Arbitro de la partida: traduce los clics de la vista en jugadas del modelo
 * y mantiene la pantalla sincronizada. No calcula dano ni decide reglas.
 */
public class ControladorBatalla {

    private final VentanaBatalla vista;
    private Supplier<GestorBatalla> fabricaDeBatallas;
    private String introduccion;
    private GestorBatalla gestor;
    private int turno;

    public ControladorBatalla(VentanaBatalla vista) {
        this.vista = vista;
        vista.alEjecutarTurno(evento -> ejecutarTurno());
        vista.alNuevaPartida(evento -> pedirNuevaPartida());
        vista.alCambiarAtacante(evento -> actualizarAcciones());
        vista.alCambiarAccion(evento -> actualizarObjetivos());
    }

    /**
     * Arranca una batalla. Recibe una fabrica en lugar de la batalla ya armada
     * para poder crearla de nuevo cuando el jugador pida otra partida.
     */
    public void comenzar(Supplier<GestorBatalla> fabricaDeBatallas, String introduccion) {
        this.fabricaDeBatallas = fabricaDeBatallas;
        this.introduccion = introduccion;
        nuevaPartida();
    }

    /** Descarta la batalla actual y empieza otra desde cero. */
    public void nuevaPartida() {
        if (fabricaDeBatallas == null) {
            return;
        }
        gestor = fabricaDeBatallas.get();
        turno = 0;
        vista.limpiarHistorial();
        vista.agregarAlHistorial(introduccion);
        vista.desbloquearControles();
        sincronizarVista();
        if (gestor.verificarFinDelJuego()) {
            terminarPartida();
        }
    }

    private void pedirNuevaPartida() {
        boolean enCurso = gestor != null && turno > 0 && !gestor.verificarFinDelJuego();
        if (!enCurso || vista.confirmar("¿Abandonar la batalla actual y empezar otra?")) {
            nuevaPartida();
        }
    }

    /** Un turno completo: la jugada del jugador y el contraataque de un monstruo. */
    public void ejecutarTurno() {
        if (gestor == null || gestor.verificarFinDelJuego()) {
            return;
        }

        Personaje atacante = gestor.buscarPorNombre(vista.getAtacanteSeleccionado());
        Personaje objetivo = gestor.buscarPorNombre(vista.getObjetivoSeleccionado());
        TipoAccion accion = vista.getAccionSeleccionada();

        if (atacante == null || objetivo == null || accion == null) {
            vista.mostrarMensaje("Selecciona quién actúa, la acción y el objetivo");
            return;
        }
        if (!gestor.esHeroe(atacante)
                || !gestor.accionesDisponibles(atacante).contains(accion)
                || !gestor.objetivosValidos(atacante, accion).contains(objetivo)) {
            sincronizarVista();
            vista.mostrarMensaje("Esa jugada no es válida");
            return;
        }

        turno++;
        vista.agregarAlHistorial("— Turno " + turno + " —");
        vista.agregarAlHistorial(realizarAccion(atacante, objetivo, accion));
        if (cerrarJugada()) {
            return;
        }

        contraataque();
        gestor.terminarTurno();
        cerrarJugada();
    }

    /**
     * Ejecuta la jugada sin preguntar que clase de personaje es: la JVM elige
     * el atacar() o el ejecutarEspecial() que corresponde a cada subclase.
     */
    private String realizarAccion(Personaje atacante, Personaje objetivo, TipoAccion accion) {
        if (accion == TipoAccion.HABILIDAD_ESPECIAL && atacante instanceof IHabilidadEspecial habilidad) {
            gestor.registrarEspecialUsada(atacante);
            return habilidad.ejecutarEspecial(objetivo);
        }
        return atacante.atacar(objetivo);
    }

    /** Un monstruo al azar responde; el modelo decide su jugada y su objetivo. */
    private void contraataque() {
        Personaje monstruo = gestor.elegirEnemigoAlAzar();
        if (monstruo == null) {
            return;
        }
        TipoAccion accion = gestor.elegirAccionAlAzar(monstruo);
        Personaje objetivo = gestor.elegirObjetivoAlAzar(monstruo, accion);
        if (objetivo == null) {
            return;
        }
        vista.agregarAlHistorial(realizarAccion(monstruo, objetivo, accion));
    }

    /**
     * Retira a los derrotados, refresca la pantalla y revisa si hay ganador.
     *
     * @return true si la partida termino
     */
    private boolean cerrarJugada() {
        gestor.eliminarDerrotados();
        sincronizarVista();
        if (gestor.verificarFinDelJuego()) {
            terminarPartida();
            return true;
        }
        return false;
    }

    private void terminarPartida() {
        vista.bloquearControles();
        if (gestor.ganoElJugador()) {
            vista.agregarAlHistorial("¡VICTORIA! Los héroes dominan la arena.");
            vista.mostrarMensaje("¡VICTORIA!");
        } else {
            vista.agregarAlHistorial("DERROTA. Los monstruos se alzan sobre los héroes caídos.");
            vista.mostrarMensaje("DERROTA");
        }
    }

    private void sincronizarVista() {
        vista.mostrarEquipos(gestor.getEquipoJugador(), gestor.getEquipoEnemigo());
        vista.cargarAtacantes(nombres(gestor.getEquipoJugador()));
        actualizarAcciones();
    }

    /** Las acciones dependen de quien ataca. */
    private void actualizarAcciones() {
        Personaje atacante = gestor.buscarPorNombre(vista.getAtacanteSeleccionado());
        vista.cargarAcciones(gestor.accionesDisponibles(atacante));
        vista.mostrarMensaje(mensajeDeJugada(atacante));
        actualizarObjetivos();
    }

    /** Los objetivos dependen de quien ataca y de la accion elegida. */
    private void actualizarObjetivos() {
        Personaje atacante = gestor.buscarPorNombre(vista.getAtacanteSeleccionado());
        TipoAccion accion = vista.getAccionSeleccionada();
        vista.cargarObjetivos(nombres(gestor.objetivosValidos(atacante, accion)));
        vista.setTextoObjetivo(gestor.esAccionDeApoyo(atacante, accion) ? "¿A quién ayuda?" : "¿A quién ataca?");
    }

    private String mensajeDeJugada(Personaje atacante) {
        int espera = gestor.turnosParaEspecial(atacante);
        if (espera == 0) {
            return "Elige tu jugada";
        }
        return "La habilidad especial de " + atacante.getNombre() + " estará lista en "
                + espera + (espera == 1 ? " turno" : " turnos");
    }

    private List<String> nombres(List<Personaje> equipo) {
        List<String> nombres = new ArrayList<>();
        for (Personaje personaje : equipo) {
            nombres.add(personaje.getNombre());
        }
        return nombres;
    }
}
