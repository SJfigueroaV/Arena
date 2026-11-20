package epicarena.vista;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * Ventana principal: solo acomoda las tres vistas con BorderLayout.
 * No contiene reglas del juego ni conoce al controlador.
 */
public class VentanaBatalla extends JFrame {

    private final PanelEstado panelEstado = new PanelEstado();
    private final PanelHistorial panelHistorial = new PanelHistorial();
    private final PanelControles panelControles = new PanelControles();
    private final Sonido sonido = new Sonido();
    private final PanelEscena panelEscena = new PanelEscena(sonido);
    private final PanelPrologo panelPrologo = new PanelPrologo(sonido);
    // Un JLabel con la textura de fondo hace de panel: los demas van encima.
    private final JLabel contenidoBatalla = new JLabel(Paleta.imagen("fondo_ventana"));

    // Teclas registradas: la tecla en una lista y quienes la escuchan en la otra.
    private final List<String> teclas = new ArrayList<>();
    private final List<List<ActionListener>> oyentesDeTeclas = new ArrayList<>();

    public VentanaBatalla() {
        super("Guardianes del Tunjo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel contenido = contenidoBatalla;
        contenido.setLayout(new BorderLayout(0, 12));
        contenido.setOpaque(true);
        contenido.setBackground(Paleta.FONDO);
        contenido.setBorder(BorderFactory.createEmptyBorder(14, 18, 18, 18));
        contenido.add(panelEstado, BorderLayout.NORTH);
        // Centro: la escena con los sprites arriba y el historial de batalla debajo.
        JPanel centro = new JPanel(new BorderLayout(0, 10));
        centro.setOpaque(false);
        centro.add(panelEscena, BorderLayout.NORTH);
        centro.add(panelHistorial, BorderLayout.CENTER);
        contenido.add(centro, BorderLayout.CENTER);
        contenido.add(panelControles, BorderLayout.SOUTH);
        setContentPane(contenido);

        setMinimumSize(new Dimension(1080, 960));
        setSize(1100, 1040);
        setLocationRelativeTo(null);
    }

    public PanelEstado getPanelEstado() {
        return panelEstado;
    }

    public PanelHistorial getPanelHistorial() {
        return panelHistorial;
    }

    /**
     * Avisa al oyente cuando se presiona la tecla en cualquier parte de la
     * ventana. La tecla se escribe como en KeyStroke: "Q", "1", "ENTER",
     * "SPACE", "ESCAPE", "LEFT", "RIGHT". Varios oyentes pueden escuchar la
     * misma tecla; cada uno decide si le toca (por ejemplo, segun la pantalla).
     */
    public void alPresionar(String tecla, ActionListener oyente) {
        int posicion = teclas.indexOf(tecla);
        if (posicion < 0) {
            teclas.add(tecla);
            oyentesDeTeclas.add(new ArrayList<>());
            posicion = teclas.size() - 1;
            getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(tecla), tecla);
            getRootPane().getActionMap().put(tecla, new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    presionar(tecla);
                }
            });
        }
        oyentesDeTeclas.get(posicion).add(oyente);
    }

    /** Hace lo mismo que presionar la tecla (lo usan tambien las pruebas). */
    public void presionar(String tecla) {
        int posicion = teclas.indexOf(tecla);
        if (posicion < 0) {
            return;
        }
        for (ActionListener oyente : new ArrayList<>(oyentesDeTeclas.get(posicion))) {
            oyente.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, tecla));
        }
    }

    public Sonido getSonido() {
        return sonido;
    }

    /** Enciende o apaga el sonido y actualiza los botones de las dos pantallas. */
    public void alternarSonido() {
        sonido.alternarSilencio();
        String texto = sonido.estaSilenciado() ? "Sonido: apagado" : "Sonido: encendido";
        panelControles.getBtnSonido().setText(texto);
        panelPrologo.getBtnSonido().setText(texto);
    }

    public PanelPrologo getPanelPrologo() {
        return panelPrologo;
    }

    /** Muestra el prologo en toda la ventana. */
    public void mostrarPrologo() {
        setContentPane(panelPrologo);
        revalidate();
        repaint();
    }

    /** Vuelve a la pantalla de batalla. */
    public void mostrarBatalla() {
        setContentPane(contenidoBatalla);
        revalidate();
        repaint();
    }

    public boolean estaEnPrologo() {
        return getContentPane() == panelPrologo;
    }

    public PanelEscena getPanelEscena() {
        return panelEscena;
    }

    public PanelControles getPanelControles() {
        return panelControles;
    }

    /** Pregunta al jugador y retorna true si acepta. */
    public boolean confirmar(String pregunta) {
        return JOptionPane.showConfirmDialog(this, pregunta, "Guardianes del Tunjo",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
    }
}
