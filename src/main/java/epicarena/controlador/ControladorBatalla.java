package epicarena.controlador;

import epicarena.modelo.GestorBatalla;
import epicarena.modelo.IHabilidadEspecial;
import epicarena.modelo.Personaje;
import epicarena.modelo.TipoAccion;
import epicarena.vista.VentanaBatalla;
import java.util.ArrayList;
import java.util.List;

/**
 * Arbitro de la partida: traduce los clics de la vista en jugadas del modelo
 * y mantiene la pantalla sincronizada. No calcula dano ni decide reglas.
 */
public class ControladorBatalla {

    private final VentanaBatalla vista;
    private GestorBatalla gestor;
    private int turno;

    public ControladorBatalla(VentanaBatalla vista) {
        this.vista = vista;
        vista.alEjecutarTurno(evento -> ejecutarTurno());
        vista.alCambiarAtacante(evento -> actualizarAcciones());
        vista.alCambiarAccion(evento -> actualizarObjetivos());
    }

    /** Arranca una batalla. Se puede llamar de nuevo con otros equipos. */
    public void comenzar(GestorBatalla nuevaBatalla, String introduccion) {
        gestor = nuevaBatalla;
        turno = 0;
        vista.limpiarHistorial();
        vista.agregarAlHistorial(introduccion);
        vista.desbloquearControles();
        sincronizarVista();
        if (gestor.verificarFinDelJuego()) {
            terminarPartida();
        } else {
            vista.mostrarMensaje("Elige tu jugada");
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
        if (!gestor.esHeroe(atacante) || !gestor.objetivosValidos(atacante, accion).contains(objetivo)) {
            vista.mostrarMensaje("Esa jugada no es válida");
            sincronizarVista();
            return;
        }

        turno++;
        vista.agregarAlHistorial("— Turno " + turno + " —");
        vista.agregarAlHistorial(realizarAccion(atacante, objetivo, accion));
        if (cerrarJugada()) {
            return;
        }

        contraataque();
        if (cerrarJugada()) {
            return;
        }
        vista.mostrarMensaje("Elige tu jugada");
    }

    /**
     * Ejecuta la jugada sin preguntar que clase de personaje es: la JVM elige
     * el atacar() o el ejecutarEspecial() que corresponde a cada subclase.
     */
    private String realizarAccion(Personaje atacante, Personaje objetivo, TipoAccion accion) {
        if (accion == TipoAccion.HABILIDAD_ESPECIAL && atacante instanceof IHabilidadEspecial habilidad) {
            return habilidad.ejecutarEspecial(objetivo);
        }
        return atacante.atacar(objetivo);
    }

    /** Un monstruo al azar ataca a un heroe al azar. */
    private void contraataque() {
        Personaje monstruo = gestor.elegirEnemigoAlAzar();
        Personaje heroe = gestor.elegirHeroeAlAzar();
        if (monstruo == null || heroe == null) {
            return;
        }
        vista.agregarAlHistorial(monstruo.atacar(heroe));
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
        actualizarObjetivos();
    }

    /** Los objetivos dependen de quien ataca y de la accion elegida. */
    private void actualizarObjetivos() {
        Personaje atacante = gestor.buscarPorNombre(vista.getAtacanteSeleccionado());
        TipoAccion accion = vista.getAccionSeleccionada();
        vista.cargarObjetivos(nombres(gestor.objetivosValidos(atacante, accion)));
        vista.setTextoObjetivo(gestor.esAccionDeApoyo(atacante, accion) ? "¿A quién ayuda?" : "¿A quién ataca?");
    }

    private List<String> nombres(List<Personaje> equipo) {
        List<String> nombres = new ArrayList<>();
        for (Personaje personaje : equipo) {
            nombres.add(personaje.getNombre());
        }
        return nombres;
    }
}
