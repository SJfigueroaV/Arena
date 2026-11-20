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
        etiqueta.setFont(Paleta.titulo(16));
        etiqueta.setForeground(Paleta.DORADO);
        etiqueta.setIcon(Paleta.imagen("icono_historial"));
        etiqueta.setIconTextGap(10);
        add(etiqueta, BorderLayout.NORTH);

        area.setEditable(false);
        area.setFocusable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        // Pergamino: tinta cafe sobre papel claro.
        area.setFont(Paleta.narracion(23));
        area.setBackground(Paleta.PERGAMINO);
        area.setForeground(Paleta.TINTA);
        area.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));

        JScrollPane desplazamiento = new JScrollPane(area);
        desplazamiento.setBorder(Paleta.marcoFino());
        desplazamiento.getViewport().setBackground(Paleta.PERGAMINO);
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
