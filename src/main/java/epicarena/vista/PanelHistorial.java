package epicarena.vista;

import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/** VISTA 2 (Historial de batalla): la narrativa del combate, de solo lectura. */
public class PanelHistorial extends JPanel {

    private final JTextArea area = new JTextArea();

    public PanelHistorial() {
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
        area.setFont(Paleta.narracion(16));
        area.setBackground(Paleta.PANEL);
        area.setForeground(Paleta.TEXTO);
        area.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

        JScrollPane desplazamiento = new JScrollPane(area);
        desplazamiento.setBorder(BorderFactory.createLineBorder(Paleta.BORDE));
        add(desplazamiento, BorderLayout.CENTER);
    }

    /** Agrega una linea y baja el scroll para que siempre se vea lo ultimo. */
    public void agregar(String texto) {
        if (texto == null || texto.isBlank()) {
            return;
        }
        area.append(texto + "\n");
        area.setCaretPosition(area.getText().length());
    }

    public void limpiar() {
        area.setText("");
    }
}
