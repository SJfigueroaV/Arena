package epicarena.vista;

import epicarena.modelo.Personaje;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

/** Panel superior: heroes vivos a la izquierda y monstruos vivos a la derecha. */
class PanelEstado extends JPanel {

    private static final int ALTO_FILA = 30;
    private static final int FILAS_VISIBLES = 5;

    private final ModeloTablaPersonajes modeloHeroes = new ModeloTablaPersonajes();
    private final ModeloTablaPersonajes modeloMonstruos = new ModeloTablaPersonajes();
    private final JLabel subtitulo = new JLabel(" ", SwingConstants.CENTER);

    PanelEstado() {
        super(new BorderLayout(0, 10));
        setOpaque(false);

        JLabel titulo = new JLabel("EPIC ARENA", SwingConstants.CENTER);
        titulo.setFont(Paleta.titulo(34));
        titulo.setForeground(Paleta.DORADO);

        subtitulo.setFont(Paleta.titulo(16).deriveFont(java.awt.Font.ITALIC));
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

    void setSubtitulo(String texto) {
        subtitulo.setText(texto == null || texto.isBlank() ? " " : texto);
    }

    void mostrarEquipos(List<Personaje> heroes, List<Personaje> monstruos) {
        modeloHeroes.actualizar(heroes);
        modeloMonstruos.actualizar(monstruos);
    }

    private JPanel crearSeccion(String nombre, Color color, ModeloTablaPersonajes modelo) {
        JLabel etiqueta = new JLabel(nombre);
        etiqueta.setFont(Paleta.titulo(18));
        etiqueta.setForeground(color);

        JTable tabla = new JTable(modelo);
        tabla.setRowHeight(ALTO_FILA);
        tabla.setFont(Paleta.texto(13));
        tabla.setBackground(Paleta.PANEL);
        tabla.setForeground(Paleta.TEXTO);
        tabla.setShowGrid(false);
        tabla.setIntercellSpacing(new Dimension(0, 0));
        tabla.setRowSelectionAllowed(false);
        tabla.setFocusable(false);
        tabla.setFillsViewportHeight(true);
        tabla.getTableHeader().setReorderingAllowed(false);

        DefaultTableCellRenderer celda = crearCelda(SwingConstants.LEFT, null);
        DefaultTableCellRenderer celdaCentrada = crearCelda(SwingConstants.CENTER, null);
        tabla.getColumnModel().getColumn(0).setCellRenderer(celda);
        tabla.getColumnModel().getColumn(1).setCellRenderer(celda);
        tabla.getColumnModel().getColumn(ModeloTablaPersonajes.COLUMNA_VIDA).setCellRenderer(new BarraVida());
        tabla.getColumnModel().getColumn(3).setCellRenderer(celdaCentrada);
        tabla.getColumnModel().getColumn(4).setCellRenderer(celdaCentrada);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(185);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(88);
        tabla.getColumnModel().getColumn(ModeloTablaPersonajes.COLUMNA_VIDA).setPreferredWidth(120);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(38);
        tabla.getColumnModel().getColumn(4).setPreferredWidth(38);

        JTableHeader encabezado = tabla.getTableHeader();
        encabezado.setBackground(Paleta.PANEL_CLARO);
        encabezado.setPreferredSize(new Dimension(0, 30));
        DefaultTableCellRenderer titulo = crearCelda(SwingConstants.LEFT, color);
        DefaultTableCellRenderer tituloCentrado = crearCelda(SwingConstants.CENTER, color);
        for (int columna = 0; columna < tabla.getColumnCount(); columna++) {
            tabla.getColumnModel().getColumn(columna).setHeaderRenderer(columna < 2 ? titulo : tituloCentrado);
        }

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

    /** Celda con margen interno. Si recibe un color, se pinta como encabezado. */
    private static DefaultTableCellRenderer crearCelda(int alineacion, Color colorEncabezado) {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tabla, Object valor, boolean seleccionada,
                    boolean conFoco, int fila, int columna) {
                super.getTableCellRendererComponent(tabla, valor, false, false, fila, columna);
                setHorizontalAlignment(alineacion);
                int margen = alineacion == SwingConstants.CENTER ? 2 : 10;
                setBorder(BorderFactory.createEmptyBorder(0, margen, 0, margen));
                if (colorEncabezado != null) {
                    setFont(Paleta.textoFuerte(13));
                    setBackground(Paleta.PANEL_CLARO);
                    setForeground(colorEncabezado);
                }
                return this;
            }
        };
    }
}
