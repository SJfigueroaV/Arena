package epicarena.vista;

import java.awt.BorderLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/** Panel central: la narrativa de la batalla, de solo lectura. */
class PanelHistorial extends JPanel {

    private final JTextArea area = new JTextArea();

    PanelHistorial() {
        super(new BorderLayout(0, 6));
        setOpaque(false);

        JLabel etiqueta = new JLabel("HISTORIAL DE BATALLA");
        etiqueta.setFont(Paleta.titulo(18));
        etiqueta.setForeground(Paleta.DORADO);
        add(etiqueta, BorderLayout.NORTH);

        area.setEditable(false);
        area.setFocusable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(Paleta.titulo(16).deriveFont(java.awt.Font.PLAIN));
        area.setBackground(Paleta.PANEL);
        area.setForeground(Paleta.TEXTO);
        area.setMargin(new Insets(10, 14, 10, 14));

        JScrollPane desplazamiento = new JScrollPane(area);
        desplazamiento.setBorder(BorderFactory.createLineBorder(Paleta.BORDE));
        desplazamiento.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(desplazamiento, BorderLayout.CENTER);
    }

    /** Agrega una linea y baja el scroll para que siempre se vea lo ultimo. */
    void agregar(String texto) {
        if (texto == null || texto.isBlank()) {
            return;
        }
        area.append(texto + "\n");
        area.setCaretPosition(area.getDocument().getLength());
    }

    void limpiar() {
        area.setText("");
    }
}
