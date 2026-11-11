package epicarena;

import epicarena.modelo.GestorBatalla;
import epicarena.modelo.Guerrero;
import epicarena.modelo.Mago;
import epicarena.modelo.Personaje;
import epicarena.modelo.Sacerdote;
import epicarena.modelo.TipoAccion;
import epicarena.vista.VentanaBatalla;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::iniciar);
    }

    private static void iniciar() {
        GestorBatalla gestor = crearBatalla();
        VentanaBatalla ventana = new VentanaBatalla();

        // Datos de muestra mientras se construye el controlador.
        ventana.mostrarEquipos(gestor.getEquipoJugador(), gestor.getEquipoEnemigo());
        ventana.cargarAtacantes(nombres(gestor.getEquipoJugador()));
        ventana.cargarAcciones(List.of(TipoAccion.ATAQUE_NORMAL, TipoAccion.HABILIDAD_ESPECIAL));
        ventana.cargarObjetivos(nombres(gestor.getEquipoEnemigo()));
        ventana.mostrarMensaje("Elige tu jugada");
        ventana.agregarAlHistorial("Los héroes entran en la arena. ¡Que comience la batalla!");

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

    private static List<String> nombres(List<Personaje> equipo) {
        List<String> nombres = new ArrayList<>();
        for (Personaje personaje : equipo) {
            nombres.add(personaje.getNombre());
        }
        return nombres;
    }
}
