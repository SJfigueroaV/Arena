package epicarena.vista;

import epicarena.modelo.Personaje;
import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/** Dibuja la vida de un personaje como una barra dentro de la celda de la tabla. */
class BarraVida extends JComponent implements TableCellRenderer {

    private int vida;
    private int vidaMaxima = 1;

    BarraVida() {
        setOpaque(true);
        setFont(Paleta.textoFuerte(12));
    }

    @Override
    public Component getTableCellRendererComponent(JTable tabla, Object valor, boolean seleccionada,
            boolean conFoco, int fila, int columna) {
        if (valor instanceof Personaje personaje) {
            vida = personaje.getPuntosDeVida();
            vidaMaxima = Math.max(1, personaje.getPuntosDeVidaMaximos());
        } else {
            vida = 0;
            vidaMaxima = 1;
        }
        setBackground(tabla.getBackground());
        return this;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D lienzo = (Graphics2D) g.create();
        lienzo.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        lienzo.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        lienzo.setColor(getBackground());
        lienzo.fillRect(0, 0, getWidth(), getHeight());

        int margen = 5;
        int ancho = getWidth() - margen * 2;
        int alto = getHeight() - margen * 2;
        double proporcion = Math.min(1.0, (double) vida / vidaMaxima);

        lienzo.setColor(Paleta.FONDO);
        lienzo.fillRoundRect(margen, margen, ancho, alto, alto, alto);

        int relleno = (int) Math.round(ancho * proporcion);
        if (relleno > 0) {
            lienzo.setColor(colorSegunVida(proporcion));
            lienzo.fillRoundRect(margen, margen, Math.max(relleno, alto), alto, alto, alto);
        }

        String texto = vida + " / " + vidaMaxima;
        FontMetrics medidas = lienzo.getFontMetrics(getFont());
        int x = (getWidth() - medidas.stringWidth(texto)) / 2;
        int y = (getHeight() - medidas.getHeight()) / 2 + medidas.getAscent();
        lienzo.setFont(getFont());
        lienzo.setColor(Paleta.FONDO);
        lienzo.drawString(texto, x + 1, y + 1);
        lienzo.setColor(Paleta.TEXTO);
        lienzo.drawString(texto, x, y);
        lienzo.dispose();
    }

    private java.awt.Color colorSegunVida(double proporcion) {
        if (proporcion > 0.6) {
            return Paleta.VIDA_ALTA;
        }
        if (proporcion > 0.3) {
            return Paleta.VIDA_MEDIA;
        }
        return Paleta.VIDA_BAJA;
    }
}
