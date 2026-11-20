package epicarena.vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.InputStream;
import java.util.Scanner;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.OverlayLayout;
import javax.swing.SwingConstants;

/**
 * Un personaje en la escena con su nombre y su barra de vida encima de la
 * cabeza. OverlayLayout pone el nombre y la barra sobre el dibujo.
 */
public class FichaEnEscena extends JPanel {

    private final SpriteAnimado sprite;
    private final JProgressBar vida = new JProgressBar();

    public FichaEnEscena(SpriteAnimado sprite, String raiz, int escala, boolean conVida, boolean esHeroe,
            int anchoMaximo) {
        this.sprite = sprite;
        setLayout(new OverlayLayout(this));
        setOpaque(false);

        JLabel nombre = new JLabel(sprite.getNombre(), SwingConstants.CENTER);
        Color color = esHeroe ? Paleta.HEROES : Paleta.MONSTRUOS;
        nombre.setFont(Paleta.texto(9 * escala));
        nombre.setForeground(color);
        nombre.setOpaque(true);
        nombre.setBackground(Paleta.NOCHE);
        nombre.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, escala, 0, color.darker()),
                BorderFactory.createEmptyBorder(0, 3 * escala, 0, 3 * escala)));

        vida.setPreferredSize(new Dimension(40 * escala, 4 * escala));
        vida.setBorderPainted(true);
        vida.setBorder(BorderFactory.createLineBorder(Paleta.NOCHE, 1));
        vida.setBackground(Paleta.NOCHE_CLARA);
        vida.setVisible(conVida);

        JPanel columna = new JPanel(new BorderLayout(0, 2));
        columna.setOpaque(false);
        JPanel filaNombre = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        filaNombre.setOpaque(false);
        filaNombre.add(nombre);
        JPanel filaVida = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        filaVida.setOpaque(false);
        filaVida.add(vida);
        columna.add(filaNombre, BorderLayout.NORTH);
        columna.add(filaVida, BorderLayout.CENTER);

        Dimension tamano = sprite.getPreferredSize();
        int[] cabeza = leerCabeza(raiz, escala, tamano.width);
        // Cuanto se aparta el cuerpo del centro del cuadro (el escudo o el costal lo cargan a un lado)
        int desvio = cabeza[1] - tamano.width / 2;
        int ancho = Math.max(tamano.width, nombre.getPreferredSize().width + 18 + 2 * Math.abs(desvio));
        // Nunca mas ancha de lo que le toca en la escena (el borde del cuadro es transparente).
        ancho = Math.min(ancho, anchoMaximo);
        Dimension total = new Dimension(ancho, tamano.height);
        int alturaInfo = nombre.getPreferredSize().height + vida.getPreferredSize().height + 4;
        int arriba = Math.max(0, cabeza[0] - alturaInfo - 2);
        // Correr la columna para que el nombre quede sobre el cuerpo y no sobre el centro del cuadro
        if (desvio > 0) {
            columna.setBorder(BorderFactory.createEmptyBorder(0, 2 * desvio, 0, 0));
        } else if (desvio < 0) {
            columna.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, -2 * desvio));
        }

        // Capa de arriba: nombre y barra justo encima de la cabeza
        JPanel capa = new JPanel(new BorderLayout());
        capa.setOpaque(false);
        capa.setBorder(BorderFactory.createEmptyBorder(arriba, 0, 0, 0));
        capa.add(columna, BorderLayout.NORTH);
        capa.setPreferredSize(total);
        capa.setMaximumSize(total);
        sprite.setPreferredSize(total);
        sprite.setMaximumSize(total);
        // OverlayLayout superpone segun la alineacion: las dos capas centradas, o se corren.
        capa.setAlignmentX(0.5f);
        capa.setAlignmentY(0.5f);
        sprite.setAlignmentX(0.5f);
        sprite.setAlignmentY(0.5f);
        add(capa);
        add(sprite);
        setPreferredSize(total);
    }

    public SpriteAnimado getSprite() {
        return sprite;
    }

    /** Actualiza la barra: verde, amarilla o roja segun cuanto le queda. */
    public void setVida(int actual, int maxima) {
        vida.setMaximum(Math.max(1, maxima));
        vida.setValue(Math.max(0, actual));
        double proporcion = (double) actual / Math.max(1, maxima);
        Color color = Paleta.VIDA_ALTA;
        if (proporcion <= 0.3) {
            color = Paleta.VIDA_BAJA;
        } else if (proporcion <= 0.6) {
            color = Paleta.VIDA_MEDIA;
        }
        vida.setForeground(color);
    }

    public int getVidaMaxima() {
        return vida.getMaximum();
    }

    public int getVidaMostrada() {
        return vida.getValue();
    }

    /** Las capas se superponen: Swing debe pintarlas en orden. */
    @Override
    public boolean isOptimizedDrawingEnabled() {
        return false;
    }

    /**
     * Lee de cabeza.txt la fila donde empieza la cabeza y la columna del centro
     * del cuerpo. Si no existe, supone la cabeza arriba y el cuerpo centrado.
     */
    private int[] leerCabeza(String raiz, int escala, int anchoDelCuadro) {
        int[] cabeza = {32 * escala, anchoDelCuadro / 2};
        InputStream archivo = getClass().getResourceAsStream(
                raiz + "personajes/" + SpriteAnimado.archivoDe(sprite.getNombre()) + "/cabeza.txt");
        if (archivo == null) {
            return cabeza;
        }
        Scanner lector = new Scanner(archivo);
        if (lector.hasNextInt()) {
            cabeza[0] = lector.nextInt();
        }
        if (lector.hasNextInt()) {
            cabeza[1] = lector.nextInt();
        }
        lector.close();
        return cabeza;
    }
}
