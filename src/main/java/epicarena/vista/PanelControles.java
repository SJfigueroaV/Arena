package epicarena.vista;

import epicarena.modelo.TipoAccion;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/** Panel inferior: seleccion de la jugada y boton para ejecutar el turno. */
class PanelControles extends JPanel {

    private final JComboBox<String> comboAtacante = new JComboBox<>();
    private final JComboBox<TipoAccion> comboAccion = new JComboBox<>();
    private final JComboBox<String> comboObjetivo = new JComboBox<>();
    private final JLabel etiquetaObjetivo = crearEtiqueta("¿A quién ataca?");
    private final JLabel mensaje = new JLabel(" ", SwingConstants.CENTER);
    private final JButton botonTurno = new JButton("EJECUTAR TURNO");

    /** Evita avisar al controlador mientras la propia vista recarga las listas. */
    private boolean cargando;
    private boolean bloqueado;

    PanelControles() {
        super(new BorderLayout(0, 10));
        setOpaque(false);

        mensaje.setFont(Paleta.titulo(18));
        mensaje.setForeground(Paleta.DORADO);

        JPanel selectores = new JPanel(new GridLayout(2, 3, 14, 4));
        selectores.setOpaque(false);
        selectores.add(crearEtiqueta("¿Quién ataca?"));
        selectores.add(crearEtiqueta("¿Qué acción realiza?"));
        selectores.add(etiquetaObjetivo);
        selectores.add(preparar(comboAtacante));
        selectores.add(preparar(comboAccion));
        selectores.add(preparar(comboObjetivo));

        prepararBoton();

        add(mensaje, BorderLayout.NORTH);
        add(selectores, BorderLayout.CENTER);
        add(botonTurno, BorderLayout.SOUTH);
    }

    void cargarAtacantes(List<String> nombres) {
        recargar(comboAtacante, nombres);
    }

    void cargarAcciones(List<TipoAccion> acciones) {
        recargar(comboAccion, acciones);
    }

    void cargarObjetivos(List<String> nombres) {
        recargar(comboObjetivo, nombres);
    }

    String getAtacanteSeleccionado() {
        return (String) comboAtacante.getSelectedItem();
    }

    TipoAccion getAccionSeleccionada() {
        return (TipoAccion) comboAccion.getSelectedItem();
    }

    String getObjetivoSeleccionado() {
        return (String) comboObjetivo.getSelectedItem();
    }

    void setTextoObjetivo(String texto) {
        etiquetaObjetivo.setText(texto);
    }

    void mostrarMensaje(String texto) {
        mensaje.setText(texto == null || texto.isBlank() ? " " : texto);
    }

    void alEjecutarTurno(ActionListener oyente) {
        botonTurno.addActionListener(oyente);
    }

    void alCambiarAtacante(ActionListener oyente) {
        comboAtacante.addActionListener(evento -> {
            if (!cargando) {
                oyente.actionPerformed(evento);
            }
        });
    }

    void alCambiarAccion(ActionListener oyente) {
        comboAccion.addActionListener(evento -> {
            if (!cargando) {
                oyente.actionPerformed(evento);
            }
        });
    }

    void bloquear() {
        bloqueado = true;
        comboAtacante.setEnabled(false);
        comboAccion.setEnabled(false);
        comboObjetivo.setEnabled(false);
        botonTurno.setEnabled(false);
        botonTurno.setBackground(Paleta.BOTON_APAGADO);
    }

    /** Cambia el contenido conservando la seleccion anterior si todavia existe. */
    private <T> void recargar(JComboBox<T> combo, List<T> elementos) {
        Object anterior = combo.getSelectedItem();
        cargando = true;
        try {
            DefaultComboBoxModel<T> modelo = new DefaultComboBoxModel<>();
            for (T elemento : elementos) {
                modelo.addElement(elemento);
            }
            combo.setModel(modelo);
            if (anterior != null && elementos.contains(anterior)) {
                combo.setSelectedItem(anterior);
            }
        } finally {
            cargando = false;
        }
        combo.setEnabled(!bloqueado && !elementos.isEmpty());
    }

    private static JLabel crearEtiqueta(String texto) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(Paleta.textoFuerte(13));
        etiqueta.setForeground(Paleta.TEXTO_SUAVE);
        return etiqueta;
    }

    private <T> JComboBox<T> preparar(JComboBox<T> combo) {
        combo.setFont(Paleta.texto(15));
        combo.setPreferredSize(new Dimension(0, 34));
        combo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return combo;
    }

    private void prepararBoton() {
        botonTurno.setFont(Paleta.titulo(28));
        botonTurno.setForeground(java.awt.Color.WHITE);
        botonTurno.setBackground(Paleta.BOTON);
        botonTurno.setOpaque(true);
        botonTurno.setFocusPainted(false);
        botonTurno.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Paleta.BOTON_ENCIMA, 2),
                BorderFactory.createEmptyBorder(14, 0, 14, 0)));
        botonTurno.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        botonTurno.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evento) {
                if (botonTurno.isEnabled()) {
                    botonTurno.setBackground(Paleta.BOTON_ENCIMA);
                }
            }

            @Override
            public void mouseExited(MouseEvent evento) {
                if (botonTurno.isEnabled()) {
                    botonTurno.setBackground(Paleta.BOTON);
                }
            }
        });
    }
}
