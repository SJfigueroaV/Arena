package epicarena.controlador;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

import epicarena.modelo.GestorBatalla;
import epicarena.modelo.Guerrero;
import epicarena.modelo.Mago;
import epicarena.modelo.Personaje;
import epicarena.modelo.Sacerdote;
import epicarena.modelo.TipoAccion;
import epicarena.vista.VentanaBatalla;
import java.awt.GraphicsEnvironment;
import java.util.Random;
import javax.swing.SwingUtilities;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Juega con la ventana real (sin mostrarla). Se omite si no hay entorno grafico. */
class ControladorBatallaTest {

    private GestorBatalla gestor;
    private VentanaBatalla vista;
    private ControladorBatalla controlador;

    @BeforeEach
    void prepararPartida() throws Exception {
        assumeFalse(GraphicsEnvironment.isHeadless());
        SwingUtilities.invokeAndWait(() -> {
            gestor = new GestorBatalla(new Random(11));
            gestor.agregarHeroe(new Guerrero("Aragorn", 120, 28, 6));
            gestor.agregarHeroe(new Mago("Gandalf", 75, 24, 2));
            gestor.agregarHeroe(new Sacerdote("Elrond", 90, 16, 4));
            gestor.agregarEnemigo(new Guerrero("Orco", 110, 22, 5));
            gestor.agregarEnemigo(new Mago("Nigromante", 70, 26, 1));
            vista = new VentanaBatalla();
            controlador = new ControladorBatalla(vista);
            controlador.comenzar(gestor, "Inicio");
        });
    }

    @Test
    void alComenzarLaVistaOfreceUnaJugadaValida() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            assertEquals("Aragorn", vista.getAtacanteSeleccionado());
            assertEquals(TipoAccion.ATAQUE_NORMAL, vista.getAccionSeleccionada());
            assertEquals("Orco", vista.getObjetivoSeleccionado());
        });
    }

    @Test
    void unTurnoDanaAlObjetivoYProvocaContraataque() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            controlador.ejecutarTurno();

            assertEquals(110 - (28 - 5), gestor.buscarPorNombre("Orco").getPuntosDeVida());
            int vidaHeroes = 0;
            for (Personaje heroe : gestor.getEquipoJugador()) {
                vidaHeroes += heroe.getPuntosDeVida();
            }
            assertTrue(vidaHeroes < 120 + 75 + 90, "algun heroe debio recibir el contraataque");
        });
    }

    @Test
    void unaPartidaCompletaTerminaSinErroresYConLaPantallaAlDia() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            int turnos = 0;
            while (!gestor.verificarFinDelJuego() && turnos < 500) {
                controlador.ejecutarTurno();
                turnos++;
                if (!gestor.verificarFinDelJuego()) {
                    assertNotNull(gestor.buscarPorNombre(vista.getAtacanteSeleccionado()),
                            "el atacante seleccionado debe seguir en combate");
                    assertNotNull(gestor.buscarPorNombre(vista.getObjetivoSeleccionado()),
                            "el objetivo seleccionado debe seguir en combate");
                }
            }

            assertTrue(gestor.verificarFinDelJuego());
            assertDoesNotThrow(() -> controlador.ejecutarTurno(), "un clic tras el final no debe fallar");
        });
    }

    @Test
    void conUnEquipoVacioNoHayNadaQueSeleccionarNiErrores() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            GestorBatalla sinEnemigos = new GestorBatalla();
            sinEnemigos.agregarHeroe(new Guerrero("Solo", 50, 10, 0));

            controlador.comenzar(sinEnemigos, "Arena vacía");

            assertNull(vista.getObjetivoSeleccionado());
            assertDoesNotThrow(() -> controlador.ejecutarTurno());
        });
    }

    @Test
    void sePuedeComenzarOtraBatallaConElMismoControlador() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            GestorBatalla otra = new GestorBatalla(new Random(3));
            otra.agregarHeroe(new Guerrero("Boromir", 100, 25, 4));
            otra.agregarEnemigo(new Guerrero("Uruk", 60, 15, 2));

            controlador.comenzar(otra, "Segunda batalla");
            controlador.ejecutarTurno();

            assertEquals("Boromir", vista.getAtacanteSeleccionado());
            assertEquals(60 - (25 - 2), otra.buscarPorNombre("Uruk").getPuntosDeVida());
        });
    }
}
