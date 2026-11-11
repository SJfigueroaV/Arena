package epicarena;

import epicarena.controlador.ControladorBatalla;
import epicarena.modelo.GestorBatalla;
import epicarena.modelo.Guerrero;
import epicarena.modelo.Mago;
import epicarena.modelo.Sacerdote;
import epicarena.vista.VentanaBatalla;
import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::iniciar);
    }

    private static void iniciar() {
        VentanaBatalla ventana = new VentanaBatalla();
        ControladorBatalla controlador = new ControladorBatalla(ventana);
        controlador.comenzar(Main::crearBatalla, "Los héroes entran en la arena. ¡Que comience la batalla!");
        ventana.setVisible(true);
    }

    private static GestorBatalla crearBatalla() {
        GestorBatalla gestor = new GestorBatalla();
        gestor.agregarHeroe(new Guerrero("Aragorn", 120, 28, 6));
        gestor.agregarHeroe(new Mago("Gandalf", 75, 24, 2));
        gestor.agregarHeroe(new Sacerdote("Elrond", 90, 16, 4));
        gestor.agregarEnemigo(new Guerrero("Orco", 110, 22, 5));
        gestor.agregarEnemigo(new Mago("Nigromante", 70, 26, 1));
        gestor.agregarEnemigo(new Guerrero("Troll", 150, 18, 8));
        gestor.agregarEnemigo(new Sacerdote("Chamán", 80, 14, 3));
        return gestor;
    }
}
