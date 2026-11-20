package epicarena.vista;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.InputStream;
import java.net.URL;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

/**
 * Colores, fuentes, marcos y botones compartidos por toda la interfaz.
 * La interfaz imita el pixel art del juego: fuentes pixeladas (Silkscreen y
 * VT323, licencia OFL), marcos con grecas muiscas y botones dibujados como
 * placas de piedra y oro (imagenes en /interfaz).
 */
final class Paleta {

    // Noche y piedra
    static final Color FONDO = new Color(0x16141F);
    static final Color NOCHE = new Color(0x16141F);
    static final Color NOCHE_CLARA = new Color(0x2B2638);
    static final Color PIEDRA = new Color(0x433C55);
    // Tierra
    static final Color TIERRA = new Color(0x241A15);
    static final Color CAFE_OSCURO = new Color(0x3B2A20);
    static final Color CAFE = new Color(0x6B4A33);
    // Oro de los tunjos
    static final Color DORADO = new Color(0xE0B44C);
    static final Color DORADO_CLARO = new Color(0xF6DE8A);
    static final Color DORADO_OSCURO = new Color(0x7A5418);
    static final Color ORO = new Color(0xB5852A);
    // Manta muisca
    static final Color ROJO_MANTA = new Color(0x8A2E2E);
    static final Color PERGAMINO = new Color(0xECE6D4);
    static final Color TINTA = new Color(0x3B2A20);

    static final Color TEXTO = new Color(0xECE6D4);
    static final Color TEXTO_SUAVE = new Color(0xA9B8C2);
    static final Color LUNA = new Color(0xC3B6EA);
    static final Color MARCA_ELEGIDO = new Color(0xF6DE8A);
    static final Color MARCA_ENEMIGO = new Color(0xD9534F);
    static final Color MARCA_ALIADO = new Color(0x5DBB8A);
    static final Color MARCA_SUGERIDO = new Color(0x6FA9C9);
    static final Color MARCA_CURSOR = new Color(0xFFFFFF);
    static final Color HEROES = new Color(0x6FA9C9);
    static final Color MONSTRUOS = new Color(0xE2635C);
    static final Color VIDA_ALTA = new Color(0x5DBB8A);
    static final Color VIDA_MEDIA = new Color(0xE0B44C);
    static final Color VIDA_BAJA = new Color(0xB5443C);

    /** Tamanos de las placas dibujadas para los botones. */
    static final String PLACA_HABILIDAD = "habilidad";
    static final String PLACA_LATERAL = "lateral";
    static final String PLACA_CANCELAR = "cancelar";
    static final String PLACA_PROLOGO = "prologo";

    private static final Font PIXEL = cargarFuente("Silkscreen-Regular.ttf");
    private static final Font PIXEL_GRUESA = cargarFuente("Silkscreen-Bold.ttf");
    private static final Font TERMINAL = cargarFuente("VT323-Regular.ttf");

    private Paleta() {
    }

    /** Titulos y rotulos: Silkscreen gruesa (se ve nitida en 8, 16 o 24). */
    static Font titulo(int tamano) {
        return PIXEL_GRUESA.deriveFont(Font.PLAIN, tamano);
    }

    /** Botones y etiquetas cortas: Silkscreen normal. */
    static Font textoFuerte(int tamano) {
        return PIXEL.deriveFont(Font.PLAIN, tamano);
    }

    /** Texto corrido, tablas y narracion: VT323, de terminal antigua. */
    static Font texto(int tamano) {
        return TERMINAL.deriveFont(Font.PLAIN, tamano);
    }

    static Font narracion(int tamano) {
        return texto(tamano);
    }

    static Font subtitulo(int tamano) {
        return texto(tamano);
    }

    /** Nombre de la familia para usarla dentro de textos HTML de Swing. */
    static String familiaPixel() {
        return PIXEL.getFamily();
    }

    static String familiaTerminal() {
        return TERMINAL.getFamily();
    }

    /** Imagen de /interfaz, por ejemplo "logo". Null si no existe. */
    static ImageIcon imagen(String nombre) {
        URL archivo = Paleta.class.getResource("/interfaz/" + nombre + ".png");
        if (archivo == null) {
            return null;
        }
        return new ImageIcon(archivo);
    }

    /** Marco grueso con la greca de rombos (para la escena). */
    static Border marcoGreca() {
        return marco("greca", 16, DORADO_OSCURO);
    }

    /** Marco delgado de escalones (tablas, historial, cuadro de texto). */
    static Border marcoFino() {
        return marco("greca_fina", 8, NOCHE);
    }

    /** Marco delgado de piedra azulada. */
    static Border marcoNoche() {
        return marco("greca_noche", 8, NOCHE);
    }

    /**
     * Convierte el boton en una placa dibujada: la imagen cambia sola al pasar
     * el mouse, al presionar y cuando el boton esta apagado; el texto va encima.
     */
    static void comoPlaca(JButton boton, String tamano, Font fuente) {
        boton.setFont(fuente);
        boton.setHorizontalTextPosition(SwingConstants.CENTER);
        boton.setVerticalTextPosition(SwingConstants.CENTER);
        boton.setIconTextGap(0);
        boton.setContentAreaFilled(false);
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);
        boton.setFocusable(false);
        boton.setOpaque(false);
        boton.setBorder(BorderFactory.createEmptyBorder());
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        boton.setRolloverEnabled(true);
        boton.setPressedIcon(imagen("boton_" + tamano + "_presionado"));
        boton.setDisabledIcon(imagen("boton_" + tamano + "_apagado"));
        pintarPlaca(boton, tamano, "normal");
    }

    /**
     * Cambia el aspecto de la placa: "normal", "elegido" (oro, para la accion
     * escogida o el boton que invita al clic) o "rojo" (peligro).
     */
    static void pintarPlaca(JButton boton, String tamano, String estado) {
        boton.setIcon(imagen("boton_" + tamano + "_" + estado));
        if ("elegido".equals(estado)) {
            boton.setRolloverIcon(imagen("boton_" + tamano + "_elegido"));
            boton.setForeground(TINTA);
        } else {
            boton.setRolloverIcon(imagen("boton_" + tamano + "_encima"));
            boton.setForeground(TEXTO);
        }
    }

    private static Border marco(String greca, int grosor, Color filo) {
        ImageIcon dibujo = imagen(greca);
        Border dentro;
        if (dibujo == null) {
            dentro = BorderFactory.createLineBorder(DORADO_OSCURO, grosor);
        } else {
            dentro = BorderFactory.createMatteBorder(grosor, grosor, grosor, grosor, dibujo);
        }
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(NOCHE, 2),
                BorderFactory.createCompoundBorder(dentro, BorderFactory.createLineBorder(filo, 2)));
    }

    /** Lee una fuente de /fuentes; si algo falla usa una fuente del sistema. */
    private static Font cargarFuente(String archivo) {
        try (InputStream entrada = Paleta.class.getResourceAsStream("/fuentes/" + archivo)) {
            if (entrada == null) {
                return new Font(Font.MONOSPACED, Font.PLAIN, 12);
            }
            Font fuente = Font.createFont(Font.TRUETYPE_FONT, entrada);
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(fuente);
            return fuente;
        } catch (Exception e) {
            System.err.println("No se pudo cargar la fuente " + archivo + ": " + e.getMessage());
            return new Font(Font.MONOSPACED, Font.PLAIN, 12);
        }
    }
}
