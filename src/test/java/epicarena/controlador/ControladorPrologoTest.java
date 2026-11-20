package epicarena.controlador;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

import epicarena.historia.LagunaDeLosEspantos;
import epicarena.modelo.Campana;
import epicarena.vista.PanelPrologo;
import epicarena.vista.VentanaBatalla;
import java.awt.GraphicsEnvironment;
import javax.swing.SwingUtilities;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Recorre el prologo real con la ventana (sin mostrarla). */
class ControladorPrologoTest {

    private Campana campana;
    private VentanaBatalla ventana;
    private PanelPrologo vista;
    private ControladorPrologo prologo;

    @BeforeEach
    void preparar() throws Exception {
        assumeFalse(GraphicsEnvironment.isHeadless());
        SwingUtilities.invokeAndWait(() -> {
            campana = new LagunaDeLosEspantos();
            ventana = new VentanaBatalla();
            vista = ventana.getPanelPrologo();
            ControladorBatalla batalla = new ControladorBatalla(campana, ventana,
                    ventana.getPanelEstado(), ventana.getPanelHistorial(), ventana.getPanelControles(),
                    ventana.getPanelEscena());
            prologo = new ControladorPrologo(campana, ventana, vista, batalla);
            prologo.iniciar();
        });
    }

    @Test
    void empiezaEnElPrologoEscribiendoLaPrimeraEscena() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            assertTrue(ventana.estaEnPrologo());
            assertTrue(vista.estaEscribiendo());
        });
    }

    @Test
    void elPrimerClicCompletaElTexto() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            String primera = campana.getPrologo().get(0).getTexto();

            prologo.avanzar();

            assertEquals(primera, vista.getTextoVisible());
            assertFalse(vista.estaEscribiendo());
        });
    }

    @Test
    void laOfrendaPideLanzarLosTresTunjosAntesDeSeguir() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            prologo.avanzar();                       // completa el texto
            assertEquals("Lanzar un tunjo", vista.getBtnSiguiente().getText());
            prologo.avanzar();
            prologo.avanzar();
            assertEquals("Lanzar un tunjo", vista.getBtnSiguiente().getText());
            prologo.avanzar();                       // tercer tunjo
            assertEquals("Siguiente", vista.getBtnSiguiente().getText());
            assertFalse(vista.estaEscribiendo(), "sigue en la primera escena");

            prologo.avanzar();
            assertTrue(vista.estaEscribiendo(), "empezó la segunda escena");
        });
    }

    @Test
    void tocarAUnEspantoDormidoCuentaSuLeyenda() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            for (int clic = 0; clic < 5; clic++) {   // texto + 3 tunjos + siguiente
                prologo.avanzar();
            }
            prologo.tocarPersonaje("El Silbón");
            vista.completarTexto();
            assertTrue(vista.getTextoVisible().startsWith("El Silbón:"));

            prologo.tocarPersonaje("Nadie");          // tocar a alguien sin texto no cambia nada
            assertTrue(vista.getTextoVisible().startsWith("El Silbón:"));
        });
    }

    @Test
    void enLaUltimaEscenaCadaHeroeExplicaSuHabilidad() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            int ultima = campana.getPrologo().size() - 1;
            irALaUltimaEscena();
            assertTrue(ventana.estaEnPrologo());
            assertEquals("hechizo", campana.getPrologo().get(ultima).efectoAlTocar("Chie"));

            prologo.tocarPersonaje("Chie");
            vista.completarTexto();
            assertTrue(vista.getTextoVisible().contains("Luz de Chía"));
            assertTrue(vista.getTextoVisible().contains("2 turnos"));
            assertTrue(vista.getEscena().estaAnimando(), "Chie hace la demostración");
        });
    }

    /** Avanza hasta la escena de los heroes (la que pide escucharlos). */
    private void irALaUltimaEscena() {
        for (int clic = 0; clic < 30 && !vista.getBtnSiguiente().getText().startsWith("Escucha"); clic++) {
            prologo.avanzar();
        }
        assertTrue(vista.getBtnSiguiente().getText().startsWith("Escucha"));
    }

    @Test
    void noSePuedeComenzarElViajeSinEscucharALosTresHeroes() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            irALaUltimaEscena();
            assertFalse(vista.getBtnSiguiente().isEnabled());

            prologo.tocarPersonaje("Tyba");
            prologo.tocarPersonaje("Chie");
            assertEquals("Escucha a los héroes (2 de 3)", vista.getBtnSiguiente().getText());
            for (int clic = 0; clic < 5; clic++) {
                prologo.avanzar();
            }
            assertTrue(ventana.estaEnPrologo(), "falta Sua");

            prologo.tocarPersonaje("Sua");
            assertTrue(vista.getBtnSiguiente().isEnabled());
            assertEquals("Comenzar el viaje", vista.getBtnSiguiente().getText());
        });
    }

    @Test
    void alTerminarLaUltimaEscenaEmpiezaLaPrimeraEtapa() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            irALaUltimaEscena();
            prologo.tocarPersonaje("Tyba");
            prologo.tocarPersonaje("Chie");
            prologo.tocarPersonaje("Sua");
            for (int clic = 0; clic < 3 && ventana.estaEnPrologo(); clic++) {
                prologo.avanzar();
            }
            assertFalse(ventana.estaEnPrologo());
            assertEquals(2, ventana.getPanelEstado().getModeloMonstruos().getRowCount(), "empieza la práctica");
        });
    }

    @Test
    void saltarLlevaDirectoALaBatalla() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            prologo.terminar();
            assertFalse(ventana.estaEnPrologo());
            assertEquals(2, ventana.getPanelEstado().getModeloMonstruos().getRowCount(), "empieza la práctica");
        });
    }

    @Test
    void elPrologoSeRecorreSoloConElTeclado() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            for (int tecla = 0; tecla < 6; tecla++) {      // texto + 3 tunjos + siguiente
                ventana.presionar("ENTER");
            }
            ventana.presionar("RIGHT");                    // escucha al primer espanto
            vista.completarTexto();
            assertTrue(vista.getTextoVisible().startsWith("La Candileja:"));

            for (int tecla = 0; tecla < 12 && !vista.getBtnSiguiente().getText().startsWith("Escucha"); tecla++) {
                ventana.presionar("SPACE");
            }
            ventana.presionar("RIGHT");
            ventana.presionar("RIGHT");
            ventana.presionar("RIGHT");                    // Tyba, Chie y Sua
            ventana.presionar("ENTER");
            ventana.presionar("ENTER");

            assertFalse(ventana.estaEnPrologo(), "empezó la práctica");
        });
    }

    @Test
    void escapeSaltaElPrologo() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            ventana.presionar("ESCAPE");
            assertFalse(ventana.estaEnPrologo());
        });
    }
}
