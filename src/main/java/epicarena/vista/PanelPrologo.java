package epicarena.vista;

import java.awt.BorderLayout;
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
            "Teclado — Enter o Espacio: siguiente · Flechas: personajes · Esc: saltar · M: sonido", SwingConstants.CENTER);
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

        titulo.setFont(Paleta.titulo(24));
        titulo.setForeground(Paleta.DORADO);

        texto.setEditable(false);
        texto.setFocusable(false);
        texto.setLineWrap(true);
        texto.setWrapStyleWord(true);
        // Cuadro de dialogo: texto claro sobre noche, dentro de un marco de piedra.
        texto.setFont(Paleta.narracion(28));
        texto.setForeground(Paleta.TEXTO);
        texto.setBackground(Paleta.NOCHE_CLARA);
        texto.setOpaque(true);
        texto.setBorder(BorderFactory.createCompoundBorder(Paleta.marcoNoche(),
                BorderFactory.createEmptyBorder(8, 14, 8, 14)));
        texto.setPreferredSize(new Dimension(anchoTexto, 170));

        instruccion.setFont(Paleta.subtitulo(24));
        instruccion.setForeground(Paleta.LUNA);

        JPanel lectura = new JPanel(new BorderLayout(0, 6));
        lectura.setOpaque(false);
        lectura.add(texto, BorderLayout.CENTER);
        ayuda.setFont(Paleta.texto(19));
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

        contador.setFont(Paleta.textoFuerte(16));
        contador.setForeground(Paleta.DORADO);
        Paleta.comoPlaca(btnSaltar, Paleta.PLACA_PROLOGO, Paleta.texto(22));
        Paleta.comoPlaca(btnSiguiente, Paleta.PLACA_PROLOGO, Paleta.texto(22));
        Paleta.pintarPlaca(btnSiguiente, Paleta.PLACA_PROLOGO, "elegido");
        Paleta.comoPlaca(btnSonido, Paleta.PLACA_LATERAL, Paleta.texto(20));

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        botones.setOpaque(false);
        botones.add(contador);
        botones.add(btnSaltar);
        botones.add(btnSiguiente);
        JPanel pie = new JPanel(new BorderLayout());
        pie.setOpaque(false);
        pie.setPreferredSize(new Dimension(480 * escala + 40, 48));
        pie.add(btnSonido, BorderLayout.WEST);
        pie.add(botones, BorderLayout.EAST);

        // Titulo, escena, texto y botones forman un solo bloque...
        JPanel bloque = new JPanel(new BorderLayout(0, 12));
        bloque.setOpaque(false);
        bloque.add(titulo, BorderLayout.NORTH);
        bloque.add(centro, BorderLayout.CENTER);
        bloque.add(centrar(pie), BorderLayout.SOUTH);

        // ...que GridBagLayout deja centrado en la ventana, sea del tamano que sea.
        // La textura de fondo es un JLabel que hace de panel.
        JLabel medio = new JLabel(Paleta.imagen("fondo_ventana"));
        medio.setLayout(new GridBagLayout());
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
}
