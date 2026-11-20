package epicarena.vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

/**
 * VISTA 1 (Tablero de estado): heroes a la izquierda y monstruos a la derecha.
 * Las tablas arrancan vacias; el Controlador es quien les pinta las filas.
 */
public class PanelEstado extends JPanel {

    private static final String[] COLUMNAS = {"Nombre", "Clase", "Puntos de Vida", "ATQ", "DEF"};
    private static final int ALTO_FILA = 30;
    private static final int FILAS_VISIBLES = 4;

    private final DefaultTableModel modeloHeroes = crearModeloTabla();
    private final DefaultTableModel modeloMonstruos = crearModeloTabla();
    private final JLabel subtitulo = new JLabel(" ", SwingConstants.CENTER);

    public PanelEstado() {
        super(new BorderLayout(0, 10));
        setOpaque(false);

        JLabel titulo = new JLabel("EPIC ARENA", SwingConstants.CENTER);
        titulo.setFont(Paleta.titulo(34));
        titulo.setForeground(Paleta.DORADO);

        subtitulo.setFont(Paleta.subtitulo(16));
        subtitulo.setForeground(Paleta.TEXTO_SUAVE);

        JPanel cabecera = new JPanel(new GridLayout(2, 1, 0, 2));
        cabecera.setOpaque(false);
        cabecera.add(titulo);
        cabecera.add(subtitulo);
        add(cabecera, BorderLayout.NORTH);

        JPanel tablas = new JPanel(new GridLayout(1, 2, 14, 0));
        tablas.setOpaque(false);
        tablas.add(crearSeccion("HÉROES", Paleta.HEROES, modeloHeroes));
        tablas.add(crearSeccion("MONSTRUOS", Paleta.MONSTRUOS, modeloMonstruos));
        add(tablas, BorderLayout.CENTER);
    }

    /** El "lienzo" de la tabla de heroes, donde el Controlador pinta las filas. */
    public DefaultTableModel getModeloHeroes() {
        return modeloHeroes;
    }

    /** El "lienzo" de la tabla de monstruos. */
    public DefaultTableModel getModeloMonstruos() {
        return modeloMonstruos;
    }

    /** Linea bajo el titulo: campana y etapa en curso. */
    public void setSubtitulo(String texto) {
        subtitulo.setText(texto == null || texto.isBlank() ? " " : texto);
    }

    /** Tabla de solo lectura: el jugador no puede escribir en las celdas. */
    private static DefaultTableModel crearModeloTabla() {
        return new DefaultTableModel(COLUMNAS, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
    }

    private JPanel crearSeccion(String nombre, Color color, DefaultTableModel modelo) {
        JLabel etiqueta = new JLabel(nombre);
        etiqueta.setFont(Paleta.titulo(18));
        etiqueta.setForeground(color);

        JTable tabla = new JTable(modelo);
        tabla.setRowHeight(ALTO_FILA);
        tabla.setFont(Paleta.texto(14));
        tabla.setBackground(Paleta.PANEL);
        tabla.setForeground(Paleta.TEXTO);
        tabla.setGridColor(Paleta.BORDE);
        tabla.setRowSelectionAllowed(false);
        tabla.setFocusable(false);
        tabla.setFillsViewportHeight(true);
        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.getTableHeader().setFont(Paleta.textoFuerte(13));
        tabla.getTableHeader().setBackground(Paleta.PANEL_CLARO);
        tabla.getTableHeader().setForeground(color);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(185);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(88);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(120);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(38);
        tabla.getColumnModel().getColumn(4).setPreferredWidth(38);

        JScrollPane desplazamiento = new JScrollPane(tabla);
        desplazamiento.getViewport().setBackground(Paleta.PANEL);
        desplazamiento.setBorder(BorderFactory.createLineBorder(Paleta.BORDE));
        desplazamiento.setPreferredSize(new Dimension(0, ALTO_FILA * FILAS_VISIBLES + 32));

        JPanel seccion = new JPanel(new BorderLayout(0, 6));
        seccion.setOpaque(false);
        seccion.add(etiqueta, BorderLayout.NORTH);
        seccion.add(desplazamiento, BorderLayout.CENTER);
        return seccion;
    }
}
