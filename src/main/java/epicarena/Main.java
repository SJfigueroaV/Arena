package epicarena;

import epicarena.controlador.ControladorBatalla;
import epicarena.historia.LagunaDeLosEspantos;
import epicarena.vista.VentanaBatalla;
import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::iniciar);
    }

    private static void iniciar() {
        VentanaBatalla ventana = new VentanaBatalla();
        ControladorBatalla controlador = new ControladorBatalla(ventana);
        controlador.comenzar(LagunaDeLosEspantos.crear());
        ventana.setVisible(true);
    }
}
