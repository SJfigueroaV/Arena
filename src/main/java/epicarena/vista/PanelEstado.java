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

        // El titulo es un dibujo pixelado; si no esta, queda el texto.
        JLabel titulo = new JLabel("GUARDIANES DEL TUNJO", SwingConstants.CENTER);
        titulo.setFont(Paleta.titulo(32));
        titulo.setForeground(Paleta.DORADO);
        if (Paleta.imagen("logo") != null) {
            titulo.setText(null);
            titulo.setIcon(Paleta.imagen("logo"));
        }

        subtitulo.setFont(Paleta.subtitulo(24));
        subtitulo.setForeground(Paleta.DORADO_CLARO);

        JPanel cabecera = new JPanel(new BorderLayout(0, 0));
        cabecera.setOpaque(false);
        cabecera.add(titulo, BorderLayout.CENTER);
        cabecera.add(subtitulo, BorderLayout.SOUTH);
        add(cabecera, BorderLayout.NORTH);

        JPanel tablas = new JPanel(new GridLayout(1, 2, 14, 0));
        tablas.setOpaque(false);
        tablas.add(crearSeccion("HÉROES", "icono_heroes", Paleta.HEROES, modeloHeroes));
        tablas.add(crearSeccion("MONSTRUOS", "icono_monstruos", Paleta.MONSTRUOS, modeloMonstruos));
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

    private JPanel crearSeccion(String nombre, String icono, Color color, DefaultTableModel modelo) {
        JLabel etiqueta = new JLabel(nombre);
        etiqueta.setFont(Paleta.titulo(16));
        etiqueta.setForeground(color);
        etiqueta.setIcon(Paleta.imagen(icono));
        etiqueta.setIconTextGap(10);

        JTable tabla = new JTable(modelo);
        tabla.setRowHeight(ALTO_FILA);
        tabla.setFont(Paleta.texto(24));
        tabla.setBackground(Paleta.TIERRA);
        tabla.setForeground(Paleta.TEXTO);
        tabla.setGridColor(Paleta.CAFE);
        tabla.setShowVerticalLines(false);
        tabla.setIntercellSpacing(new Dimension(0, 1));
        tabla.setRowSelectionAllowed(false);
        tabla.setFocusable(false);
        tabla.setFillsViewportHeight(true);
        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.getTableHeader().setFont(Paleta.textoFuerte(8));
        tabla.getTableHeader().setBackground(Paleta.ROJO_MANTA);
        tabla.getTableHeader().setForeground(Paleta.DORADO_CLARO);
        tabla.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Paleta.NOCHE));
        tabla.getColumnModel().getColumn(0).setPreferredWidth(185);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(88);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(120);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(38);
        tabla.getColumnModel().getColumn(4).setPreferredWidth(38);

        JScrollPane desplazamiento = new JScrollPane(tabla);
        desplazamiento.getViewport().setBackground(Paleta.TIERRA);
        desplazamiento.setBorder(Paleta.marcoFino());
        desplazamiento.setPreferredSize(new Dimension(0, ALTO_FILA * FILAS_VISIBLES + 58));

        JPanel seccion = new JPanel(new BorderLayout(0, 6));
        seccion.setOpaque(false);
        seccion.add(etiqueta, BorderLayout.NORTH);
        seccion.add(desplazamiento, BorderLayout.CENTER);
        return seccion;
    }
}
