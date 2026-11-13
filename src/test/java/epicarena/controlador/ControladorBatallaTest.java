package epicarena.controlador;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

import epicarena.historia.LagunaDeLosEspantos;
import epicarena.modelo.Campana;
import epicarena.modelo.Etapa;
import epicarena.modelo.GestorBatalla;
import epicarena.modelo.Guerrero;
import epicarena.modelo.Mago;
import epicarena.modelo.Personaje;
import epicarena.modelo.Sacerdote;
import epicarena.modelo.TipoAccion;
import epicarena.vista.VentanaBatalla;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import javax.swing.SwingUtilities;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Juega con la ventana real (sin mostrarla). Se omite si no hay entorno grafico. */
class ControladorBatallaTest {

    /** Batallas creadas durante la prueba, en orden. */
    private final List<GestorBatalla> creadas = new ArrayList<>();
    private VentanaBatalla vista;
    private ControladorBatalla controlador;

    @BeforeEach
    void prepararPartida() throws Exception {
        assumeFalse(GraphicsEnvironment.isHeadless());
        SwingUtilities.invokeAndWait(() -> {
            vista = new VentanaBatalla();
            controlador = new ControladorBatalla(vista);
            controlador.comenzar(campana(this::batallaPareja));
        });
    }

    private GestorBatalla ultima() {
        return creadas.get(creadas.size() - 1);
    }

    /** Campana cuyas etapas registran cada batalla que crean. */
    @SafeVarargs
    private Campana campana(Supplier<GestorBatalla>... fabricas) {
        Campana campana = new Campana("Prueba", "EPILOGO", "DERROTA");
        int numero = 1;
        for (Supplier<GestorBatalla> fabrica : fabricas) {
            campana.agregarEtapa(new Etapa("Etapa " + numero++, "Intro", "Cierre", () -> {
                GestorBatalla batalla = fabrica.get();
                creadas.add(batalla);
                return batalla;
            }));
        }
        return campana;
    }

    private GestorBatalla batallaPareja() {
        GestorBatalla batalla = new GestorBatalla(new Random(11));
        batalla.agregarHeroe(new Guerrero("Aragorn", 120, 28, 6));
        batalla.agregarHeroe(new Mago("Gandalf", 75, 24, 2));
        batalla.agregarHeroe(new Sacerdote("Elrond", 90, 16, 4));
        batalla.agregarEnemigo(new Guerrero("Orco", 110, 22, 5));
        batalla.agregarEnemigo(new Mago("Nigromante", 70, 26, 1));
        return batalla;
    }

    /** El heroe gana en un solo golpe. */
    private GestorBatalla batallaFacil() {
        GestorBatalla batalla = new GestorBatalla(new Random(1));
        batalla.agregarHeroe(new Guerrero("Titán", 500, 500, 50));
        batalla.agregarEnemigo(new Guerrero("Rata", 5, 1, 0));
        return batalla;
    }

    /** El heroe cae con el primer contraataque. */
    private GestorBatalla batallaImposible() {
        GestorBatalla batalla = new GestorBatalla(new Random(1));
        batalla.agregarHeroe(new Guerrero("Novato", 5, 1, 0));
        batalla.agregarEnemigo(new Guerrero("Coloso", 500, 500, 50));
        return batalla;
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

            assertEquals(110 - (28 - 5), ultima().buscarPorNombre("Orco").getPuntosDeVida());
            int vidaHeroes = 0;
            for (Personaje heroe : ultima().getEquipoJugador()) {
                vidaHeroes += heroe.getPuntosDeVida();
            }
            assertTrue(vidaHeroes < 120 + 75 + 90, "algun heroe debio recibir el contraataque");
        });
    }

    @Test
    void unaPartidaCompletaTerminaSinErroresYConLaPantallaAlDia() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            GestorBatalla batalla = ultima();
            int turnos = 0;
            while (!batalla.verificarFinDelJuego() && turnos < 500) {
                controlador.ejecutarTurno();
                turnos++;
                if (!batalla.verificarFinDelJuego()) {
                    assertNotNull(batalla.buscarPorNombre(vista.getAtacanteSeleccionado()),
                            "el atacante seleccionado debe seguir en combate");
                    assertNotNull(batalla.buscarPorNombre(vista.getObjetivoSeleccionado()),
                            "el objetivo seleccionado debe seguir en combate");
                }
            }

            assertTrue(batalla.verificarFinDelJuego());
            assertDoesNotThrow(() -> controlador.ejecutarTurno(), "un clic tras el final no debe fallar");
        });
    }

    @Test
    void conUnEquipoVacioNoHayNadaQueSeleccionarNiErrores() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            controlador.comenzar(campana(() -> {
                GestorBatalla sinEnemigos = new GestorBatalla();
                sinEnemigos.agregarHeroe(new Guerrero("Solo", 50, 10, 0));
                return sinEnemigos;
            }));

            assertNull(vista.getObjetivoSeleccionado());
            assertDoesNotThrow(() -> controlador.ejecutarTurno());
        });
    }

    @Test
    void alGanarUnaEtapaElBotonSecundarioPasaALaSiguiente() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            controlador.comenzar(campana(this::batallaFacil, this::batallaPareja));
            int antes = creadas.size();

            controlador.ejecutarTurno();
            assertTrue(ultima().ganoElJugador());
            controlador.ejecutarTurno();
            assertEquals(antes, creadas.size(), "ganar no crea otra batalla por si solo");

            controlador.accionSecundaria();

            assertEquals(antes + 1, creadas.size());
            assertEquals("Aragorn", vista.getAtacanteSeleccionado());
        });
    }

    @Test
    void alPerderSeReintentaLaMismaEtapaDesdeCero() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            controlador.comenzar(campana(this::batallaFacil, this::batallaImposible));
            controlador.ejecutarTurno();
            controlador.accionSecundaria();
            controlador.ejecutarTurno();
            GestorBatalla perdida = ultima();
            assertTrue(perdida.verificarFinDelJuego());

            controlador.accionSecundaria();

            assertEquals("Novato", vista.getAtacanteSeleccionado(), "sigue en la segunda etapa");
            assertEquals(5, ultima().buscarPorNombre("Novato").getPuntosDeVida());
        });
    }

    @Test
    void alCompletarLaCampanaSeVuelveAEmpezarDesdeLaPrimeraEtapa() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            controlador.comenzar(campana(this::batallaFacil, this::batallaFacil));
            controlador.ejecutarTurno();
            controlador.accionSecundaria();
            controlador.ejecutarTurno();
            int antes = creadas.size();

            controlador.accionSecundaria();

            assertEquals(antes + 1, creadas.size());
            assertEquals(5, ultima().buscarPorNombre("Rata").getPuntosDeVida());
        });
    }

    @Test
    void laCampanaRealSeJuegaDePrincipioAFinSinErrores() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            controlador.comenzar(LagunaDeLosEspantos.crear());

            for (int paso = 0; paso < 3000; paso++) {
                controlador.ejecutarTurno();
                if (paso % 40 == 39) {
                    controlador.accionSecundaria();
                }
            }
        });
    }
}
