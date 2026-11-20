package epicarena.vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * VISTA 3 (Controles del jugador): un mensaje que guia la jugada y la barra
 * de habilidades del heroe elegido. La jugada se arma tocando personajes en
 * la escena; aqui solo se elige que hace el heroe.
 */
public class PanelControles extends JPanel {

    private static final int ANCHO_BOTON_LATERAL = 190;

    private final JLabel mensaje = new JLabel(" ", SwingConstants.CENTER);
    private final JButton btnSecundario = new JButton("Reiniciar");
    private final JButton btnSonido = new JButton("Sonido: encendido");

    private final JLabel heroe = new JLabel(" ");
    private final JButton btnAtaque = new JButton();
    private final JButton btnEspecial = new JButton();
    private final JButton btnCancelar = new JButton("Cancelar");
    private final JPanel barra = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 6));
    private final JLabel ayuda = new JLabel(" ", SwingConstants.CENTER);

    public PanelControles() {
        super(new BorderLayout(0, 8));
        setOpaque(false);

        mensaje.setFont(Paleta.titulo(20));
        mensaje.setForeground(Paleta.DORADO);

        prepararLateral(btnSonido);
        prepararLateral(btnSecundario);
        configurarBotonSecundario(btnSecundario.getText(), false);
        btnSonido.setBackground(Paleta.PANEL);
        btnSonido.setForeground(Paleta.TEXTO_SUAVE);

        JPanel cabecera = new JPanel(new BorderLayout());
        cabecera.setOpaque(false);
        cabecera.add(btnSonido, BorderLayout.WEST);
        cabecera.add(mensaje, BorderLayout.CENTER);
        cabecera.add(btnSecundario, BorderLayout.EAST);

        heroe.setFont(Paleta.titulo(22));
        heroe.setForeground(Paleta.DORADO);
        prepararAccion(btnAtaque);
        prepararAccion(btnEspecial);
        prepararAccion(btnCancelar);
        btnCancelar.setFont(Paleta.textoFuerte(13));
        barra.setOpaque(false);
        barra.setPreferredSize(new Dimension(0, 64));
        barra.add(heroe);
        barra.add(btnAtaque);
        barra.add(btnEspecial);
        barra.add(btnCancelar);

        ayuda.setFont(Paleta.texto(12));
        ayuda.setForeground(Paleta.TEXTO_SUAVE);

        add(cabecera, BorderLayout.NORTH);
        add(barra, BorderLayout.CENTER);
        add(ayuda, BorderLayout.SOUTH);
        ocultarHabilidades();
    }

    public JButton getBtnAtaque() {
        return btnAtaque;
    }

    public JButton getBtnEspecial() {
        return btnEspecial;
    }

    public JButton getBtnCancelar() {
        return btnCancelar;
    }

    /** Boton pequeno: reiniciar, pasar a la etapa siguiente o reintentar. */
    public JButton getBtnSecundario() {
        return btnSecundario;
    }

    public JButton getBtnSonido() {
        return btnSonido;
    }

    /**
     * Muestra las habilidades del heroe elegido.
     *
     * @param textoEspecial null si el heroe no tiene habilidad especial
     * @param especialLista false si esta recargando: se ve pero no se puede usar
     */
    public void mostrarHabilidades(String nombre, String textoAtaque, String textoEspecial, boolean especialLista) {
        heroe.setText(nombre + "  ▸");
        btnAtaque.setText(textoAtaque);
        btnAtaque.setVisible(true);
        btnEspecial.setVisible(textoEspecial != null);
        if (textoEspecial != null) {
            btnEspecial.setText(textoEspecial);
            btnEspecial.setEnabled(especialLista);
        }
        btnCancelar.setVisible(true);
        marcarAccion(false, false);
    }

    /** Resalta en dorado la accion que el jugador eligio. */
    public void marcarAccion(boolean ataque, boolean especial) {
        pintar(btnAtaque, ataque);
        pintar(btnEspecial, especial);
    }

    public void ocultarHabilidades() {
        heroe.setText(" ");
        btnAtaque.setVisible(false);
        btnEspecial.setVisible(false);
        btnCancelar.setVisible(false);
    }

    /** Linea pequena con las teclas que se pueden usar. */
    public void setAyudaTeclado(String texto) {
        ayuda.setText(texto);
    }

    public void mostrarMensaje(String texto) {
        mensaje.setText(texto == null || texto.isBlank() ? " " : texto);
    }

    public String getMensaje() {
        return mensaje.getText();
    }

    /** Cambia el texto del boton pequeno; destacado lo pinta en dorado para invitar al clic. */
    public void configurarBotonSecundario(String texto, boolean destacado) {
        btnSecundario.setText(texto);
        if (destacado) {
            btnSecundario.setBackground(Paleta.DORADO);
            btnSecundario.setForeground(Paleta.FONDO);
        } else {
            btnSecundario.setBackground(Paleta.PANEL_CLARO);
            btnSecundario.setForeground(Paleta.TEXTO);
        }
    }

    /** Cuando la partida termina no hay habilidades que elegir. */
    public void bloquear() {
        ocultarHabilidades();
    }

    private void pintar(JButton boton, boolean elegido) {
        if (elegido) {
            boton.setBackground(Paleta.DORADO);
            boton.setForeground(Paleta.FONDO);
        } else {
            boton.setBackground(Paleta.PANEL_CLARO);
            boton.setForeground(Paleta.TEXTO);
        }
    }

    private static void prepararAccion(JButton boton) {
        boton.setFont(Paleta.textoFuerte(16));
        boton.setOpaque(true);
        boton.setFocusPainted(false);
        boton.setFocusable(false);
        boton.setBackground(Paleta.PANEL_CLARO);
        boton.setForeground(Paleta.TEXTO);
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        boton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Paleta.DORADO_OSCURO, 2),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)));
    }

    private static void prepararLateral(JButton boton) {
        boton.setFont(Paleta.textoFuerte(13));
        boton.setOpaque(true);
        boton.setFocusPainted(false);
        boton.setFocusable(false);
        boton.setBorder(BorderFactory.createLineBorder(Paleta.BORDE));
        boton.setPreferredSize(new Dimension(ANCHO_BOTON_LATERAL, 32));
        boton.setForeground(Color.WHITE);
    }
}
