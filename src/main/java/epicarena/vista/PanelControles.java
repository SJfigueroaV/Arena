package epicarena.vista;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
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

        mensaje.setFont(Paleta.texto(30));
        mensaje.setForeground(Paleta.DORADO_CLARO);

        Paleta.comoPlaca(btnSonido, Paleta.PLACA_LATERAL, Paleta.texto(20));
        Paleta.comoPlaca(btnSecundario, Paleta.PLACA_LATERAL, Paleta.texto(20));
        configurarBotonSecundario(btnSecundario.getText(), false);

        JPanel cabecera = new JPanel(new BorderLayout());
        cabecera.setOpaque(false);
        cabecera.add(btnSonido, BorderLayout.WEST);
        cabecera.add(mensaje, BorderLayout.CENTER);
        cabecera.add(btnSecundario, BorderLayout.EAST);

        heroe.setFont(Paleta.titulo(16));
        heroe.setForeground(Paleta.DORADO);
        Paleta.comoPlaca(btnAtaque, Paleta.PLACA_HABILIDAD, Paleta.texto(20));
        Paleta.comoPlaca(btnEspecial, Paleta.PLACA_HABILIDAD, Paleta.texto(20));
        Paleta.comoPlaca(btnCancelar, Paleta.PLACA_CANCELAR, Paleta.texto(22));
        barra.setOpaque(false);
        barra.setPreferredSize(new Dimension(0, 82));
        barra.add(heroe);
        barra.add(btnAtaque);
        barra.add(btnEspecial);
        barra.add(btnCancelar);

        ayuda.setFont(Paleta.texto(19));
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
        heroe.setText(nombre + " »");
        btnAtaque.setText(enDosLineas(textoAtaque));
        btnAtaque.setVisible(true);
        btnEspecial.setVisible(textoEspecial != null);
        if (textoEspecial != null) {
            btnEspecial.setText(enDosLineas(textoEspecial));
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
        Paleta.pintarPlaca(btnSecundario, Paleta.PLACA_LATERAL, destacado ? "elegido" : "normal");
    }

    /** Cuando la partida termina no hay habilidades que elegir. */
    public void bloquear() {
        ocultarHabilidades();
    }

    private void pintar(JButton boton, boolean elegido) {
        Paleta.pintarPlaca(boton, Paleta.PLACA_HABILIDAD, elegido ? "elegido" : "normal");
    }

    /**
     * "[E] La Luz de Chia · 2,5 veces su fuerza" queda en dos lineas: el nombre
     * de la habilidad en letra pixelada y debajo lo que hace.
     */
    private static String enDosLineas(String texto) {
        int corte = texto.indexOf(" · ");
        if (corte < 0) {
            return texto;
        }
        String nombre = texto.substring(0, corte);
        String detalle = texto.substring(corte + 3);
        return "<html><div style='text-align:center'>"
                + "<span style=\"font-family:'" + Paleta.familiaPixel() + "'; font-size:16pt\">" + nombre + "</span><br>"
                + "<span style=\"font-family:'" + Paleta.familiaTerminal() + "'; font-size:20pt\">" + detalle + "</span>"
                + "</div></html>";
    }
}
