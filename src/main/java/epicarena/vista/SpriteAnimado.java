package epicarena.vista;

import java.awt.Cursor;
import java.awt.Dimension;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.SwingConstants;

/**
 * Un personaje dibujado en la escena. Es un boton transparente que guarda los
 * cuadros de cada animacion y muestra uno a la vez: animar es solo cambiar el
 * icono. Como es un boton, el jugador puede tocarlo. No sabe nada de reglas;
 * solo conoce el nombre del personaje que representa.
 */
public class SpriteAnimado extends JButton {

    public static final String IDLE = "idle";
    public static final String ATAQUE = "ataque";
    public static final String ESPECIAL = "especial";
    public static final String DANO = "dano";
    public static final String CURADO = "curado";
    public static final String CAIDA = "caida";
    public static final String DORMIDO = "dormido";

    private static final int ANCHO = 128;
    private static final int ALTO = 192;
    private static final int GROSOR_MARCA = 6;

    private final String nombre;
    private final List<ImageIcon> idle;
    private final List<ImageIcon> ataque;
    private final List<ImageIcon> especial;
    private final List<ImageIcon> dano;
    private final List<ImageIcon> curado;
    private final List<ImageIcon> caida;
    private final List<ImageIcon> dormido;

    /** Animacion que se esta mostrando y numero de cuadro dentro de ella. */
    private String animacionActual = IDLE;
    private int cuadro;
    /** Se vuelve true cuando termina la animacion de caida. */
    private boolean fueraDeCombate;

    public SpriteAnimado(String nombre, int desfase) {
        this(nombre, desfase, "/sprites/");
    }

    /** La raiz es "/sprites/" para los dibujos a x2 o "/sprites/x3/" para los de x3. */
    public SpriteAnimado(String nombre, int desfase, String raiz) {
        this.nombre = nombre;
        String carpeta = raiz + "personajes/" + archivoDe(nombre) + "/";
        idle = cargar(carpeta, IDLE);
        ataque = cargar(carpeta, ATAQUE);
        especial = cargar(carpeta, ESPECIAL);
        dano = cargar(carpeta, DANO);
        curado = cargar(carpeta, CURADO);
        caida = cargar(carpeta, CAIDA);
        dormido = cargar(carpeta, DORMIDO);
        cuadro = desfase;

        // Boton sin fondo ni borde: solo se ve el dibujo.
        setContentAreaFilled(false);
        setBorderPainted(true);      // el borde es la marca bajo los pies
        setFocusPainted(false);
        setFocusable(false);
        setMarca(null);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setActionCommand(nombre);
        setHorizontalAlignment(SwingConstants.CENTER);
        setVerticalAlignment(SwingConstants.BOTTOM);
        setToolTipText(nombre);
        if (idle.isEmpty()) {
            // Sin dibujo (por ejemplo, en las pruebas): se muestra el nombre.
            setText(nombre);
            setForeground(Paleta.TEXTO);
            setPreferredSize(new Dimension(ANCHO, ALTO));
        } else {
            int ancho = idle.get(0).getIconWidth();
            setPreferredSize(new Dimension(ancho, Math.max(ALTO, idle.get(0).getIconHeight())));
            setIcon(idle.get(cuadro % idle.size()));
        }
    }

    /** Linea de color bajo los pies (elegido, objetivo...). Null la quita. */
    public void setMarca(java.awt.Color color) {
        if (color == null) {
            setBorder(BorderFactory.createEmptyBorder(0, 0, GROSOR_MARCA, 0));
        } else {
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(0, 14, 0, 14),
                    BorderFactory.createMatteBorder(0, 0, GROSOR_MARCA, 0, color)));
        }
    }

    public String getNombre() {
        return nombre;
    }

    /** Empieza una animacion desde su primer cuadro. Si no existe, no hace nada. */
    public void reproducir(String animacion) {
        if (animacion.equals(ESPECIAL) && especial.isEmpty()) {
            animacion = ATAQUE;      // quien no tiene especial muestra su ataque
        }
        if (cuadrosDe(animacion).isEmpty()) {
            return;
        }
        animacionActual = animacion;
        cuadro = 0;
        setIcon(cuadrosDe(animacion).get(0));
    }

    /**
     * Pasa al cuadro siguiente.
     *
     * @return true si la animacion (que no sea idle) acaba de terminar
     */
    public boolean avanzar() {
        List<ImageIcon> cuadros = cuadrosDe(animacionActual);
        if (cuadros.isEmpty()) {
            return true;
        }
        if (animacionActual.equals(DORMIDO)) {
            return false;            // dormido se queda quieto hasta que lo despierten
        }
        cuadro++;
        if (cuadro < cuadros.size()) {
            setIcon(cuadros.get(cuadro));
            return false;
        }
        if (animacionActual.equals(IDLE)) {
            cuadro = 0;
            setIcon(cuadros.get(0));
            return false;
        }
        if (animacionActual.equals(CAIDA)) {
            setIcon(null);
            fueraDeCombate = true;
            return true;
        }
        reproducir(IDLE);
        return true;
    }

    /** True cuando ya termino de caer y debe salir de la escena. */
    public boolean estaFueraDeCombate() {
        return fueraDeCombate;
    }

    public boolean estaEnReposo() {
        return animacionActual.equals(IDLE);
    }

    private List<ImageIcon> cuadrosDe(String animacion) {
        if (animacion.equals(ATAQUE)) {
            return ataque;
        }
        if (animacion.equals(ESPECIAL)) {
            return especial;
        }
        if (animacion.equals(DANO)) {
            return dano;
        }
        if (animacion.equals(CURADO)) {
            return curado;
        }
        if (animacion.equals(CAIDA)) {
            return caida;
        }
        if (animacion.equals(DORMIDO)) {
            return dormido;
        }
        return idle;
    }

    /** Lee idle_0.png, idle_1.png... hasta que no encuentre el siguiente. */
    private List<ImageIcon> cargar(String carpeta, String animacion) {
        List<ImageIcon> cuadros = new ArrayList<>();
        URL archivo = getClass().getResource(carpeta + animacion + "_0.png");
        while (archivo != null) {
            cuadros.add(new ImageIcon(archivo));
            archivo = getClass().getResource(carpeta + animacion + "_" + cuadros.size() + ".png");
        }
        return cuadros;
    }

    /**
     * Nombre de la carpeta de dibujos de un personaje: sin articulo inicial,
     * en minusculas, sin tildes y con guiones bajos. "El Hombre Caimán" queda
     * "hombre_caiman".
     */
    public static String archivoDe(String nombre) {
        String archivo = nombre.trim();
        if (archivo.startsWith("El ") || archivo.startsWith("La ")) {
            archivo = archivo.substring(3);
        }
        archivo = archivo.toLowerCase()
                .replace('á', 'a').replace('é', 'e').replace('í', 'i')
                .replace('ó', 'o').replace('ú', 'u').replace('ñ', 'n')
                .replace(' ', '_');
        return archivo;
    }
}
