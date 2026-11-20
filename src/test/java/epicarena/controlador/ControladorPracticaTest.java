package epicarena.controlador;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

import epicarena.historia.LagunaDeLosEspantos;
import epicarena.modelo.Campana;
import epicarena.modelo.PasoGuiado;
import epicarena.vista.PanelControles;
import epicarena.vista.VentanaBatalla;
import java.awt.GraphicsEnvironment;
import javax.swing.SwingUtilities;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Juega la practica guiada con la ventana (sin mostrarla). */
class ControladorPracticaTest {

    private Campana campana;
    private VentanaBatalla ventana;
    private PanelControles controles;
    private ControladorBatalla batalla;

    @BeforeEach
    void preparar() throws Exception {
        assumeFalse(GraphicsEnvironment.isHeadless());
        SwingUtilities.invokeAndWait(() -> {
            campana = new LagunaDeLosEspantos();
            ventana = new VentanaBatalla();
            controles = ventana.getPanelControles();
            batalla = new ControladorBatalla(campana, ventana, ventana.getPanelEstado(),
                    ventana.getPanelHistorial(), controles, ventana.getPanelEscena());
            batalla.iniciarPractica();
        });
    }

    private int vidaDe(String nombre) {
        javax.swing.table.DefaultTableModel tabla = ventana.getPanelEstado().getModeloMonstruos();
        for (int fila = 0; fila < tabla.getRowCount(); fila++) {
            if (tabla.getValueAt(fila, 0).equals(nombre)) {
                return Integer.parseInt(tabla.getValueAt(fila, 2).toString().split(" ")[0]);
            }
        }
        return -1;
    }

    @Test
    void empiezaContraLasDosSombrasYDiceElPrimerPaso() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            assertEquals(2, ventana.getPanelEstado().getModeloMonstruos().getRowCount());
            assertTrue(controles.getBtnSecundario().getText().startsWith("Saltar práctica"));
        });
    }

    @Test
    void rechazaUnaJugadaQueNoEsLaDelPaso() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            batalla.tocarPersonaje("Tyba");
            batalla.tocarPersonaje("Sombra del Silbón");   // el paso 1 pide la Madremonte
            assertEquals(90, vidaDe("Sombra del Silbón"), "no se jugó nada");
            assertEquals(40, vidaDe("Sombra de la Madremonte"));
        });
    }

    @Test
    void seguirLosSeisPasosGanaYLuegoEmpiezaLaEtapaUno() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            // Como el jugador: toca al heroe, elige la especial si hace falta y toca al objetivo.
            for (PasoGuiado paso : campana.getPasosDePractica()) {
                batalla.tocarPersonaje(paso.getAtacante());
                if (paso.getAccion().equals("Habilidad Especial")) {
                    controles.getBtnEspecial().doClick();
                } else {
                    controles.getBtnAtaque().doClick();
                }
                batalla.tocarPersonaje(paso.getObjetivo());
            }
            assertEquals(0, ventana.getPanelEstado().getModeloMonstruos().getRowCount());
            assertTrue(controles.getBtnSecundario().getText().startsWith("Comenzar el viaje"));

            batalla.accionSecundaria();

            assertEquals(3, ventana.getPanelEstado().getModeloMonstruos().getRowCount(), "etapa 1: tres espantos");
            assertTrue(controles.getBtnSecundario().getText().startsWith("Reiniciar campaña"));
        });
    }

    @Test
    void saltarLaPracticaLlevaALaEtapaUno() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            batalla.accionSecundaria();
            assertEquals(3, ventana.getPanelEstado().getModeloMonstruos().getRowCount());
            assertTrue(controles.getBtnSecundario().getText().startsWith("Reiniciar"));
        });
    }

    @Test
    void laPracticaSeGanaSoloConElTeclado() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            // Paso: (tecla del heroe, tecla de la accion, flechas hasta el objetivo)
            for (PasoGuiado paso : campana.getPasosDePractica()) {
                int numero = paso.getAtacante().equals("Tyba") ? 1 : paso.getAtacante().equals("Chie") ? 2 : 3;
                ventana.presionar(String.valueOf(numero));
                ventana.presionar(paso.getAccion().equals("Habilidad Especial") ? "E" : "Q");
                // Mueve el cursor hasta el objetivo que pide el paso (como maximo una vuelta).
                for (int vuelta = 0; vuelta < 4 && !objetivoBajoElCursor().equals(paso.getObjetivo()); vuelta++) {
                    ventana.presionar("RIGHT");
                }
                ventana.presionar("ENTER");
                // Mientras se ven las animaciones el teclado espera; aqui se adelantan.
                ventana.getPanelEscena().terminarAnimaciones();
            }
            assertTrue(controles.getBtnSecundario().getText().startsWith("Comenzar el viaje"));

            ventana.presionar("ENTER");          // tras ganar, Enter empieza el viaje
            assertEquals(3, ventana.getPanelEstado().getModeloMonstruos().getRowCount());
        });
    }

    /** Lee en el mensaje a quien apunta el cursor: el controlador lo marca en blanco en la escena. */
    private String objetivoBajoElCursor() {
        return ventana.getPanelEscena().getNombreConMarca(epicarena.vista.PanelEscena.MARCA_CURSOR);
    }
}
