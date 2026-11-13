package epicarena.controlador;

import epicarena.modelo.Campana;
import epicarena.modelo.Etapa;
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

    private enum Estado { EN_BATALLA, ETAPA_SUPERADA, DERROTA, CAMPANA_COMPLETA }

    private final VentanaBatalla vista;
    private Campana campana;
    private GestorBatalla gestor;
    private Estado estado;
    private int turno;

    public ControladorBatalla(VentanaBatalla vista) {
        this.vista = vista;
        vista.alEjecutarTurno(evento -> ejecutarTurno());
        vista.alBotonSecundario(evento -> pedirAccionSecundaria());
        vista.alCambiarAtacante(evento -> actualizarAcciones());
        vista.alCambiarAccion(evento -> actualizarObjetivos());
    }

    /** Empieza la campana desde su primera etapa. */
    public void comenzar(Campana nuevaCampana) {
        campana = nuevaCampana;
        campana.reiniciar();
        iniciarEtapa();
    }

    /** Arma la batalla de la etapa actual desde cero y la muestra. */
    private void iniciarEtapa() {
        Etapa etapa = campana.etapaActual();
        gestor = etapa.crearBatalla();
        turno = 0;
        estado = Estado.EN_BATALLA;

        String posicion = "Etapa " + campana.numeroDeEtapa() + " de " + campana.totalDeEtapas();
        vista.setSubtitulo(campana.getNombre() + "  ·  " + posicion + ": " + etapa.getTitulo());
        vista.limpiarHistorial();
        vista.agregarAlHistorial(posicion.toUpperCase() + " — " + etapa.getTitulo().toUpperCase());
        vista.agregarAlHistorial(etapa.getIntroduccion());
        vista.desbloquearControles();
        vista.configurarBotonSecundario("Reiniciar campaña", false);
        sincronizarVista();
        if (gestor.verificarFinDelJuego()) {
            terminarBatalla();
        }
    }

    /**
     * Lo que hace el boton pequeno segun el momento: pasar a la etapa
     * siguiente, reintentar la que se perdio o volver a empezar la campana.
     */
    public void accionSecundaria() {
        if (campana == null) {
            return;
        }
        if (estado == Estado.ETAPA_SUPERADA) {
            campana.avanzar();
        } else if (estado != Estado.DERROTA) {
            campana.reiniciar();
        }
        iniciarEtapa();
    }

    private void pedirAccionSecundaria() {
        if (estado == Estado.EN_BATALLA
                && !vista.confirmar("¿Abandonar el viaje y volver a la primera etapa?")) {
            return;
        }
        accionSecundaria();
    }

    /** Un turno completo: la jugada del jugador y el contraataque de un monstruo. */
    public void ejecutarTurno() {
        if (gestor == null || estado != Estado.EN_BATALLA) {
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
            terminarBatalla();
            return true;
        }
        return false;
    }

    /** Cierra la batalla y deja listo el paso siguiente de la campana. */
    private void terminarBatalla() {
        vista.bloquearControles();
        if (!gestor.ganoElJugador()) {
            estado = Estado.DERROTA;
            vista.agregarAlHistorial(campana.getTextoDeDerrota());
            vista.mostrarMensaje("DERROTA");
            vista.configurarBotonSecundario("Reintentar etapa", true);
            return;
        }
        vista.agregarAlHistorial(campana.etapaActual().getCierre());
        if (campana.hayMasEtapas()) {
            estado = Estado.ETAPA_SUPERADA;
            vista.mostrarMensaje("¡Etapa superada!");
            vista.configurarBotonSecundario("Siguiente etapa", true);
        } else {
            estado = Estado.CAMPANA_COMPLETA;
            vista.agregarAlHistorial(campana.getEpilogo());
            vista.mostrarMensaje("¡VICTORIA!");
            vista.configurarBotonSecundario("Jugar de nuevo", true);
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
