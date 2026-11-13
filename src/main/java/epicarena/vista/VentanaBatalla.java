package epicarena.vista;

import epicarena.modelo.Personaje;
import epicarena.modelo.TipoAccion;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * Ventana principal. Solo muestra datos y avisa de los clics: no contiene
 * reglas del juego ni conoce al controlador.
 */
public class VentanaBatalla extends JFrame {

    private final PanelEstado panelEstado = new PanelEstado();
    private final PanelHistorial panelHistorial = new PanelHistorial();
    private final PanelControles panelControles = new PanelControles();

    public VentanaBatalla() {
        super("Epic Arena");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel contenido = new JPanel(new BorderLayout(0, 14));
        contenido.setBackground(Paleta.FONDO);
        contenido.setBorder(BorderFactory.createEmptyBorder(14, 18, 18, 18));
        contenido.add(panelEstado, BorderLayout.NORTH);
        contenido.add(panelHistorial, BorderLayout.CENTER);
        contenido.add(panelControles, BorderLayout.SOUTH);
        setContentPane(contenido);

        setMinimumSize(new Dimension(900, 680));
        setSize(1040, 760);
        setLocationRelativeTo(null);
    }

    /** Repinta las dos tablas con los personajes que siguen en combate. */
    public void mostrarEquipos(List<Personaje> heroes, List<Personaje> monstruos) {
        panelEstado.mostrarEquipos(heroes, monstruos);
    }

    public void agregarAlHistorial(String texto) {
        panelHistorial.agregar(texto);
    }

    public void limpiarHistorial() {
        panelHistorial.limpiar();
    }

    public void cargarAtacantes(List<String> nombres) {
        panelControles.cargarAtacantes(nombres);
    }

    public void cargarAcciones(List<TipoAccion> acciones) {
        panelControles.cargarAcciones(acciones);
    }

    public void cargarObjetivos(List<String> nombres) {
        panelControles.cargarObjetivos(nombres);
    }

    /** Retorna null si no hay nada seleccionado. */
    public String getAtacanteSeleccionado() {
        return panelControles.getAtacanteSeleccionado();
    }

    /** Retorna null si no hay nada seleccionado. */
    public TipoAccion getAccionSeleccionada() {
        return panelControles.getAccionSeleccionada();
    }

    /** Retorna null si no hay nada seleccionado. */
    public String getObjetivoSeleccionado() {
        return panelControles.getObjetivoSeleccionado();
    }

    /** Cambia la pregunta sobre el objetivo, por ejemplo "¿A quién ayuda?". */
    public void setTextoObjetivo(String texto) {
        panelControles.setTextoObjetivo(texto);
    }

    public void mostrarMensaje(String texto) {
        panelControles.mostrarMensaje(texto);
    }

    public void alEjecutarTurno(ActionListener oyente) {
        panelControles.alEjecutarTurno(oyente);
    }

    public void alBotonSecundario(ActionListener oyente) {
        panelControles.alBotonSecundario(oyente);
    }

    /** Texto del boton pequeno (reiniciar, siguiente etapa...) y si debe resaltar. */
    public void configurarBotonSecundario(String texto, boolean destacado) {
        panelControles.configurarBotonSecundario(texto, destacado);
    }

    /** Linea bajo el titulo: campana y etapa en curso. */
    public void setSubtitulo(String texto) {
        panelEstado.setSubtitulo(texto);
    }

    /** Pregunta al jugador y retorna true si acepta. */
    public boolean confirmar(String pregunta) {
        return JOptionPane.showConfirmDialog(this, pregunta, "Epic Arena",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
    }

    public void alCambiarAtacante(ActionListener oyente) {
        panelControles.alCambiarAtacante(oyente);
    }

    public void alCambiarAccion(ActionListener oyente) {
        panelControles.alCambiarAccion(oyente);
    }

    /** Desactiva los controles cuando la partida termina. */
    public void bloquearControles() {
        panelControles.bloquear();
    }

    /** Vuelve a activar los controles al comenzar una batalla nueva. */
    public void desbloquearControles() {
        panelControles.desbloquear();
    }
}
