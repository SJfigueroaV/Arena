package epicarena.vista;

import epicarena.modelo.Personaje;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/** Adapta un equipo de personajes a las filas de un JTable. Solo lee datos. */
class ModeloTablaPersonajes extends AbstractTableModel {

    static final int COLUMNA_VIDA = 2;

    private static final String[] COLUMNAS = {"Nombre", "Clase", "Puntos de Vida", "ATQ", "DEF"};

    private final List<Personaje> personajes = new ArrayList<>();

    /** Reemplaza todas las filas, asi la tabla nunca duplica datos. */
    void actualizar(List<Personaje> equipo) {
        personajes.clear();
        personajes.addAll(equipo);
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return personajes.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMNAS.length;
    }

    @Override
    public String getColumnName(int columna) {
        return COLUMNAS[columna];
    }

    @Override
    public Object getValueAt(int fila, int columna) {
        Personaje personaje = personajes.get(fila);
        switch (columna) {
            case 0:
                return personaje.getNombre();
            case 1:
                return personaje.getClase();
            case COLUMNA_VIDA:
                return personaje;
            case 3:
                return personaje.getPuntosDeAtaque();
            default:
                return personaje.getDefensa();
        }
    }

    @Override
    public boolean isCellEditable(int fila, int columna) {
        return false;
    }
}
