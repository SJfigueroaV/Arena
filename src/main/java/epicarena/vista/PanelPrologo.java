package epicarena.vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.Timer;

/**
 * VISTA 5 (Prologo): una escena con fondo y personajes, y debajo el texto que
 * aparece letra por letra. El Controlador decide que se muestra en cada cuadro.
 */
public class PanelPrologo extends JPanel {

    private static final int MILISEGUNDOS_POR_LETRA = 28;

    private final JLabel titulo = new JLabel(" ", SwingConstants.CENTER);
    private final PanelEscena escena;
    private final JTextArea texto = new JTextArea();
    private final JLabel instruccion = new JLabel(" ", SwingConstants.CENTER);
    private final JLabel ayuda = new JLabel(
            "Teclado — Enter o Espacio: siguiente · ← →: personajes · Esc: saltar · M: sonido", SwingConstants.CENTER);
    private final JButton btnSonido = new JButton("Sonido: encendido");
    private final JLabel contador = new JLabel(" ");
    private final JButton btnSaltar = new JButton("Saltar prólogo");
    private final JButton btnSiguiente = new JButton("Siguiente");
    private final Timer maquinaDeEscribir = new Timer(MILISEGUNDOS_POR_LETRA, e -> escribirLetra());

    /** Texto completo que se esta escribiendo y cuantas letras van. */
    private String textoCompleto = "";
    private int letras;

    public PanelPrologo(Sonido sonido) {
        super(new BorderLayout(0, 12));
        // Si la pantalla tiene espacio, la escena del prologo se ve a x3 (1440 x 480).
        Dimension pantalla = Toolkit.getDefaultToolkit().getScreenSize();
        int escala = 2;
        if (pantalla.width >= 1500 && pantalla.height >= 950) {
            escala = 3;
        }
        int anchoTexto = Math.min(480 * escala, 1200);
        escena = new PanelEscena(sonido, escala);
        escena.setMostrarVida(false);
        setBackground(Paleta.FONDO);
        setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));

        titulo.setFont(Paleta.titulo(34));
        titulo.setForeground(Paleta.DORADO);

        texto.setEditable(false);
        texto.setFocusable(false);
        texto.setLineWrap(true);
        texto.setWrapStyleWord(true);
        texto.setFont(Paleta.narracion(21));
        texto.setForeground(Paleta.TEXTO);
        texto.setBackground(Paleta.FONDO);
        texto.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
        texto.setPreferredSize(new Dimension(anchoTexto, 140));

        instruccion.setFont(Paleta.subtitulo(16));
        instruccion.setForeground(Paleta.LUNA);

        JPanel lectura = new JPanel(new BorderLayout(0, 6));
        lectura.setOpaque(false);
        lectura.add(texto, BorderLayout.CENTER);
        ayuda.setFont(Paleta.texto(12));
        ayuda.setForeground(Paleta.TEXTO_SUAVE);
        JPanel indicaciones = new JPanel(new BorderLayout(0, 4));
        indicaciones.setOpaque(false);
        indicaciones.add(instruccion, BorderLayout.NORTH);
        indicaciones.add(ayuda, BorderLayout.SOUTH);
        lectura.add(indicaciones, BorderLayout.SOUTH);

        JPanel centro = new JPanel(new BorderLayout(0, 12));
        centro.setOpaque(false);
        centro.add(centrar(escena), BorderLayout.NORTH);
        centro.add(centrar(lectura), BorderLayout.CENTER);

        contador.setFont(Paleta.textoFuerte(13));
        contador.setForeground(Paleta.TEXTO_SUAVE);
        preparar(btnSaltar, Paleta.PANEL_CLARO, Paleta.TEXTO);
        preparar(btnSiguiente, Paleta.DORADO, Paleta.FONDO);
        preparar(btnSonido, Paleta.PANEL, Paleta.TEXTO_SUAVE);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        botones.setOpaque(false);
        botones.add(contador);
        botones.add(btnSaltar);
        botones.add(btnSiguiente);
        JPanel pie = new JPanel(new BorderLayout());
        pie.setOpaque(false);
        pie.setPreferredSize(new Dimension(480 * escala, 40));
        pie.add(btnSonido, BorderLayout.WEST);
        pie.add(botones, BorderLayout.EAST);

        // Titulo, escena, texto y botones forman un solo bloque...
        JPanel bloque = new JPanel(new BorderLayout(0, 12));
        bloque.setOpaque(false);
        bloque.add(titulo, BorderLayout.NORTH);
        bloque.add(centro, BorderLayout.CENTER);
        bloque.add(centrar(pie), BorderLayout.SOUTH);

        // ...que GridBagLayout deja centrado en la ventana, sea del tamano que sea.
        JPanel medio = new JPanel(new GridBagLayout());
        medio.setOpaque(false);
        medio.add(bloque);
        add(medio, BorderLayout.CENTER);
    }

    public PanelEscena getEscena() {
        return escena;
    }

    public JButton getBtnSiguiente() {
        return btnSiguiente;
    }

    public JButton getBtnSaltar() {
        return btnSaltar;
    }

    public JButton getBtnSonido() {
        return btnSonido;
    }

    /** Linea pequena bajo el texto que dice que puede hacer el jugador. Null la borra. */
    public void setInstruccion(String texto) {
        instruccion.setText(texto == null || texto.isBlank() ? " " : texto);
    }

    public void setTitulo(String texto) {
        titulo.setText(texto);
    }

    /** Por ejemplo "2 / 5". */
    public void mostrarContador(int actual, int total) {
        contador.setText(actual + " / " + total);
    }

    public void setTextoSiguiente(String texto) {
        btnSiguiente.setText(texto);
    }

    /** Apagado, el boton se ve gris y no responde. */
    public void setSiguienteHabilitado(boolean habilitado) {
        btnSiguiente.setEnabled(habilitado);
        if (habilitado) {
            btnSiguiente.setBackground(Paleta.DORADO);
        } else {
            btnSiguiente.setBackground(Paleta.PANEL_CLARO);
        }
    }

    /** Empieza a escribir el texto letra por letra. */
    public void escribir(String nuevoTexto) {
        textoCompleto = nuevoTexto;
        letras = 0;
        texto.setText("");
        maquinaDeEscribir.restart();
    }

    /** Muestra de golpe lo que falta del texto. */
    public void completarTexto() {
        maquinaDeEscribir.stop();
        letras = textoCompleto.length();
        texto.setText(textoCompleto);
    }

    public boolean estaEscribiendo() {
        return maquinaDeEscribir.isRunning();
    }

    public String getTextoVisible() {
        return texto.getText();
    }

    private void escribirLetra() {
        if (letras >= textoCompleto.length()) {
            maquinaDeEscribir.stop();
            return;
        }
        letras++;
        texto.setText(textoCompleto.substring(0, letras));
    }

    /** Envuelve un componente para que quede centrado con su ancho preferido. */
    private static JPanel centrar(java.awt.Component componente) {
        JPanel envoltura = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        envoltura.setOpaque(false);
        envoltura.add(componente);
        return envoltura;
    }

    private static void preparar(JButton boton, Color fondo, Color letra) {
        boton.setFont(Paleta.textoFuerte(14));
        boton.setBackground(fondo);
        boton.setForeground(letra);
        boton.setOpaque(true);
        boton.setFocusPainted(false);
        boton.setFocusable(false);
        boton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Paleta.BORDE),
                BorderFactory.createEmptyBorder(8, 18, 8, 18)));
    }
}
