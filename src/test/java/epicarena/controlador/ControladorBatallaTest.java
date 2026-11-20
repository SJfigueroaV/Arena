package epicarena.controlador;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
import epicarena.vista.PanelControles;
import epicarena.vista.VentanaBatalla;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.SwingUtilities;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Juega con la ventana real (sin mostrarla). Se omite si no hay entorno grafico. */
class ControladorBatallaTest {

    private static final String PAREJA = "pareja";
    private static final String FACIL = "facil";
    private static final String IMPOSIBLE = "imposible";
    private static final String SIN_ENEMIGOS = "sin enemigos";

    /** Batallas creadas durante la prueba, en orden. */
    private final List<GestorBatalla> creadas = new ArrayList<>();
    private PanelControles vista;
    private ControladorBatalla controlador;

    /**
     * Campana de prueba: cada etapa se describe con el nombre de una batalla
     * y cada batalla que se crea queda registrada en "creadas".
     */
    private class CampanaDePrueba extends Campana {

        private final List<String> batallas;

        CampanaDePrueba(List<String> batallas) {
            super("Prueba", "EPILOGO", "DERROTA");
            this.batallas = batallas;
            for (int numero = 1; numero <= batallas.size(); numero++) {
                agregarEtapa(new Etapa("Etapa " + numero, "Intro", "Cierre"));
            }
        }

        @Override
        protected GestorBatalla crearBatalla(int numeroDeEtapa) {
            GestorBatalla batalla = armar(batallas.get(numeroDeEtapa - 1));
            creadas.add(batalla);
            return batalla;
        }
    }

    @BeforeEach
    void prepararPartida() throws Exception {
        assumeFalse(GraphicsEnvironment.isHeadless());
        SwingUtilities.invokeAndWait(() -> jugar(new CampanaDePrueba(List.of(PAREJA))));
    }

    /** Arma la ventana y el controlador como lo hace Main, y empieza la campana. */
    private void jugar(Campana campana) {
        VentanaBatalla ventana = new VentanaBatalla();
        vista = ventana.getPanelControles();
        controlador = new ControladorBatalla(campana, ventana,
                ventana.getPanelEstado(), ventana.getPanelHistorial(), ventana.getPanelControles(),
                ventana.getPanelEscena());
        controlador.iniciar();
    }

    /** Juega un turno simple: el primer heroe ataca al primer espanto. */
    private void turno() {
        GestorBatalla batalla = ultima();
        if (batalla.getEquipoJugador().isEmpty() || batalla.getEquipoEnemigo().isEmpty()) {
            controlador.jugar(null, GestorBatalla.ATAQUE_NORMAL, null);
            return;
        }
        controlador.jugar(batalla.getEquipoJugador().get(0).getNombre(), GestorBatalla.ATAQUE_NORMAL,
                batalla.getEquipoEnemigo().get(0).getNombre());
    }

    private GestorBatalla ultima() {
        return creadas.get(creadas.size() - 1);
    }

    private GestorBatalla armar(String tipo) {
        switch (tipo) {
            case FACIL:
                return batallaFacil();
            case IMPOSIBLE:
                return batallaImposible();
            case SIN_ENEMIGOS:
                GestorBatalla sinEnemigos = new GestorBatalla();
                sinEnemigos.agregarHeroe(new Guerrero("Solo", 50, 10, 0));
                return sinEnemigos;
            default:
                return batallaPareja();
        }
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
    void alComenzarPideTocarAUnHeroe() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            assertTrue(vista.getMensaje().startsWith("Toca a uno de tus héroes"));
            assertFalse(vista.getBtnAtaque().isVisible());
        });
    }

    @Test
    void tocarUnEspantoSinHeroePideElegirPrimeroAUnHeroe() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            controlador.tocarPersonaje("Orco");
            assertEquals("Primero toca a uno de tus héroes", vista.getMensaje());
            assertEquals(110, ultima().buscarPorNombre("Orco").getPuntosDeVida());
        });
    }

    @Test
    void tocarHeroeYLuegoEspantoLoAtacaConElGolpeNormal() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            controlador.tocarPersonaje("Aragorn");
            assertTrue(vista.getBtnAtaque().isVisible());
            assertFalse(vista.getBtnEspecial().isVisible(), "el guerrero no tiene especial");

            controlador.tocarPersonaje("Orco");

            assertEquals(110 - (28 - 5), ultima().buscarPorNombre("Orco").getPuntosDeVida());
        });
    }

    @Test
    void laHabilidadEspecialSeEligeEnLaBarraYLuegoSeTocaElObjetivo() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            controlador.tocarPersonaje("Gandalf");
            assertTrue(vista.getBtnEspecial().isVisible());
            assertTrue(vista.getBtnEspecial().getText().contains("Tormenta Arcana"));

            vista.getBtnEspecial().doClick();
            controlador.tocarPersonaje("Orco");

            assertEquals(110 - 60, ultima().buscarPorNombre("Orco").getPuntosDeVida(), "ignora la defensa");
        });
    }

    @Test
    void laCuracionNoSePuedeUsarSobreUnEspanto() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            controlador.tocarPersonaje("Elrond");
            vista.getBtnEspecial().doClick();
            controlador.tocarPersonaje("Orco");

            assertTrue(vista.getMensaje().startsWith("Esa habilidad es para tus compañeros"));
            assertEquals(110, ultima().buscarPorNombre("Orco").getPuntosDeVida());
        });
    }

    @Test
    void conElTecladoSeEligeHeroeObjetivoYSeConfirma() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            VentanaBatalla ventana = (VentanaBatalla) javax.swing.SwingUtilities.getWindowAncestor(vista);
            ventana.presionar("1");           // Aragorn: solo ataca, el cursor queda en el Orco
            ventana.presionar("RIGHT");       // pasa al Nigromante
            ventana.presionar("ENTER");

            assertEquals(110, ultima().buscarPorNombre("Orco").getPuntosDeVida());
            assertEquals(70 - (28 - 1), ultima().buscarPorNombre("Nigromante").getPuntosDeVida());
        });
    }

    @Test
    void conElTecladoSeUsaLaHabilidadEspecial() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            VentanaBatalla ventana = (VentanaBatalla) javax.swing.SwingUtilities.getWindowAncestor(vista);
            ventana.presionar("2");           // Gandalf
            ventana.presionar("E");
            ventana.presionar("SPACE");

            assertEquals(110 - 60, ultima().buscarPorNombre("Orco").getPuntosDeVida());
        });
    }

    @Test
    void escapeCancelaLaSeleccionDelTeclado() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            VentanaBatalla ventana = (VentanaBatalla) javax.swing.SwingUtilities.getWindowAncestor(vista);
            ventana.presionar("2");
            ventana.presionar("ESCAPE");
            ventana.presionar("ENTER");

            assertEquals(110, ultima().buscarPorNombre("Orco").getPuntosDeVida());
            assertTrue(vista.getMensaje().startsWith("Primero elige un héroe"));
        });
    }

    @Test
    void laBarraDeVidaBajaCuandoSeVeElGolpe() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            VentanaBatalla ventana = (VentanaBatalla) javax.swing.SwingUtilities.getWindowAncestor(vista);
            assertEquals(110, ventana.getPanelEscena().getVidaMostrada("Orco"));

            controlador.tocarPersonaje("Aragorn");
            controlador.tocarPersonaje("Orco");
            assertEquals(110, ventana.getPanelEscena().getVidaMostrada("Orco"), "todavía no se ve el golpe");

            ventana.getPanelEscena().terminarAnimaciones();
            assertEquals(110 - (28 - 5), ventana.getPanelEscena().getVidaMostrada("Orco"));
        });
    }

    @Test
    void cancelarBorraLaSeleccion() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            controlador.tocarPersonaje("Gandalf");
            vista.getBtnCancelar().doClick();
            assertFalse(vista.getBtnAtaque().isVisible());
            controlador.tocarPersonaje("Orco");
            assertEquals(110, ultima().buscarPorNombre("Orco").getPuntosDeVida());
        });
    }

    @Test
    void unTurnoDanaAlObjetivoYProvocaContraataque() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            turno();

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
                turno();
                turnos++;
            }

            assertTrue(batalla.verificarFinDelJuego());
            assertDoesNotThrow(() -> controlador.tocarPersonaje("Aragorn"), "un clic tras el final no debe fallar");
            assertFalse(vista.getBtnAtaque().isVisible(), "tras el final no se elige nada");
        });
    }

    @Test
    void conUnEquipoVacioNoHayNadaQueSeleccionarNiErrores() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            jugar(new CampanaDePrueba(List.of(SIN_ENEMIGOS)));

            assertDoesNotThrow(() -> controlador.tocarPersonaje("Solo"));
            assertDoesNotThrow(() -> turno());
        });
    }

    @Test
    void alGanarUnaEtapaElBotonSecundarioPasaALaSiguiente() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            jugar(new CampanaDePrueba(List.of(FACIL, PAREJA)));
            int antes = creadas.size();

            turno();
            assertTrue(ultima().ganoElJugador());
            turno();
            assertEquals(antes, creadas.size(), "ganar no crea otra batalla por si solo");

            controlador.accionSecundaria();

            assertEquals(antes + 1, creadas.size());
            assertNotNull(ultima().buscarPorNombre("Aragorn"));
        });
    }

    @Test
    void alPerderSeReintentaLaMismaEtapaDesdeCero() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            jugar(new CampanaDePrueba(List.of(FACIL, IMPOSIBLE)));
            turno();
            controlador.accionSecundaria();
            turno();
            GestorBatalla perdida = ultima();
            assertTrue(perdida.verificarFinDelJuego());

            controlador.accionSecundaria();

            assertNotNull(ultima().buscarPorNombre("Novato"), "sigue en la segunda etapa");
            assertEquals(5, ultima().buscarPorNombre("Novato").getPuntosDeVida());
        });
    }

    @Test
    void alCompletarLaCampanaSeVuelveAEmpezarDesdeLaPrimeraEtapa() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            jugar(new CampanaDePrueba(List.of(FACIL, FACIL)));
            turno();
            controlador.accionSecundaria();
            turno();
            int antes = creadas.size();

            controlador.accionSecundaria();

            assertEquals(antes + 1, creadas.size());
            assertEquals(5, ultima().buscarPorNombre("Rata").getPuntosDeVida());
        });
    }

    @Test
    void laCampanaRealSeJuegaDePrincipioAFinSinErrores() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            jugar(new LagunaDeLosEspantos());

            for (int paso = 0; paso < 3000; paso++) {
                turno();
                if (paso % 40 == 39) {
                    controlador.accionSecundaria();
                }
            }
        });
    }
}
