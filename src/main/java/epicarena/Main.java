package epicarena;

import epicarena.controlador.ControladorBatalla;
import epicarena.controlador.ControladorPrologo;
import epicarena.historia.LagunaDeLosEspantos;
import epicarena.modelo.Campana;
import epicarena.vista.VentanaBatalla;
import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Se arman el Modelo, las Vistas y el Controlador que los une
            Campana campana = new LagunaDeLosEspantos();
            VentanaBatalla ventana = new VentanaBatalla();
            ControladorBatalla controlador = new ControladorBatalla(campana, ventana,
                    ventana.getPanelEstado(), ventana.getPanelHistorial(), ventana.getPanelControles(),
                    ventana.getPanelEscena());
            // El prologo va primero; al terminar le pasa la partida al controlador de batalla
            ControladorPrologo prologo = new ControladorPrologo(campana, ventana,
                    ventana.getPanelPrologo(), controlador);
            prologo.iniciar();
            ventana.setVisible(true);
        });
    }
}
