package epicarena.vista;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * VISTA 4 (Escena): el fondo de la etapa con los heroes a la izquierda y los
 * espantos a la derecha. Un Timer cambia el cuadro de cada personaje cada
 * 110 ms; las animaciones que pide el Controlador se reproducen una tras otra
 * en orden de llegada, para que se vea primero el golpe y luego el dano.
 */
public class PanelEscena extends JPanel {

    /** Tamano real de los dibujos de fondo; en pantalla se ven a x2 o x3. */
    private static final int ANCHO_BASE = 480;
    private static final int ALTO_BASE = 160;
    private static final int MILISEGUNDOS_POR_CUADRO = 110;
    /** Distancia entre los pies de los personajes y el borde inferior de la escena. */
    private static final int MARGEN_INFERIOR_BASE = 21;

    private final JLabel fondo = new JLabel();
    private final JPanel filaHeroes = crearFila(FlowLayout.LEFT);
    private final JPanel filaEnemigos = crearFila(FlowLayout.RIGHT);
    private final List<SpriteAnimado> sprites = new ArrayList<>();
    private final List<FichaEnEscena> fichas = new ArrayList<>();
    private final List<Integer> colaVidas = new ArrayList<>();
    private final int escala;
    private boolean conVida = true;

    // Cola de animaciones pendientes: quien, que animacion y que efecto suena.
    private final List<String> colaNombres = new ArrayList<>();
    private final List<String> colaAnimaciones = new ArrayList<>();
    private final List<String> colaEfectos = new ArrayList<>();
    /** Quienes quieren enterarse cuando el jugador toca a un personaje. */
    private final List<ActionListener> oyentesDeClic = new ArrayList<>();
    private final Sonido sonido;
    // Marcas puestas: a quien (una lista) y cual (la otra).
    private final List<String> personajesMarcados = new ArrayList<>();
    private final List<String> nombresMarcados = new ArrayList<>();
    /** Carpeta de imagenes segun la escala: "/sprites/" (x2) o "/sprites/x3/". */
    private final String raiz;
    private SpriteAnimado actuando;
    private int tic;

    /** Escena a x2, la de la batalla. */
    public PanelEscena(Sonido sonido) {
        this(sonido, 2);
    }

    /** Escena a la escala indicada: 2 (960 x 320) o 3 (1440 x 480). */
    public PanelEscena(Sonido sonido, int escala) {
        // FlowLayout centrado: la escena mide siempre 960 x 320 y queda en el medio,
        // aunque la ventana sea mas ancha.
        super(new FlowLayout(FlowLayout.CENTER, 0, 0));
        setOpaque(false);
        int ancho = ANCHO_BASE * escala;
        int alto = ALTO_BASE * escala;
        setPreferredSize(new Dimension(ancho, alto));
        this.sonido = sonido;
        this.escala = escala;
        if (escala == 3) {
            raiz = "/sprites/x3/";
        } else {
            raiz = "/sprites/";
        }

        fondo.setPreferredSize(new Dimension(ancho, alto));
        fondo.setLayout(new BorderLayout());
        fondo.setOpaque(true);
        fondo.setBackground(Paleta.PANEL);
        fondo.setBorder(BorderFactory.createLineBorder(Paleta.BORDE));

        JPanel personajes = new JPanel(new BorderLayout());
        personajes.setOpaque(false);
        personajes.setBorder(BorderFactory.createEmptyBorder(0, 12 * escala, MARGEN_INFERIOR_BASE * escala, 12 * escala));
        personajes.add(filaHeroes, BorderLayout.WEST);
        personajes.add(filaEnemigos, BorderLayout.EAST);
        fondo.add(personajes, BorderLayout.SOUTH);
        add(fondo);

        Timer reloj = new Timer(MILISEGUNDOS_POR_CUADRO, e -> avanzarCuadro());
        reloj.start();
    }

    /** Cambia el fondo, por ejemplo "paramo". Si no hay dibujo queda un fondo liso. */
    public void mostrarEscenario(String escenario) {
        URL archivo = null;
        if (escenario != null) {
            archivo = getClass().getResource(raiz + "fondos/" + escenario + ".png");
        }
        if (archivo == null) {
            fondo.setIcon(null);
        } else {
            fondo.setIcon(new ImageIcon(archivo));
        }
    }

    /** Pone en escena a los dos equipos desde cero y vacia las animaciones pendientes. */
    public void mostrarPersonajes(List<String> heroes, List<String> enemigos) {
        sprites.clear();
        fichas.clear();
        colaVidas.clear();
        colaNombres.clear();
        colaAnimaciones.clear();
        colaEfectos.clear();
        actuando = null;
        filaHeroes.removeAll();
        filaEnemigos.removeAll();
        for (int i = 0; i < heroes.size(); i++) {
            agregar(filaHeroes, new SpriteAnimado(heroes.get(i), i, raiz), true);
        }
        for (int i = 0; i < enemigos.size(); i++) {
            agregar(filaEnemigos, new SpriteAnimado(enemigos.get(i), i + 2, raiz), false);
        }
        filaHeroes.revalidate();
        filaEnemigos.revalidate();
        repaint();
    }

    /** Pide una animacion (por ejemplo "ataque") para el personaje con ese nombre. */
    public void animar(String nombre, String animacion) {
        animar(nombre, animacion, null);
    }

    /** Igual, y al empezar la animacion suena el efecto indicado (por ejemplo "golpe"). */
    public void animar(String nombre, String animacion, String efecto) {
        animar(nombre, animacion, efecto, -1);
    }

    /** Igual, y al empezar la animacion la barra de vida pasa a ese valor (-1 = no cambia). */
    public void animar(String nombre, String animacion, String efecto, int vidaTras) {
        colaNombres.add(nombre);
        colaAnimaciones.add(animacion);
        colaEfectos.add(efecto);
        colaVidas.add(vidaTras);
    }

    /** En el prologo no hay barras de vida, solo nombres. */
    public void setMostrarVida(boolean mostrar) {
        conVida = mostrar;
    }

    /** Pone la barra de vida de un personaje al instante. */
    public void fijarVida(String nombre, int actual, int maxima) {
        FichaEnEscena ficha = buscarFicha(nombre);
        if (ficha != null) {
            ficha.setVida(actual, maxima);
        }
    }

    /** Lo que muestra la barra ahora (para las pruebas). -1 si no esta. */
    public int getVidaMostrada(String nombre) {
        FichaEnEscena ficha = buscarFicha(nombre);
        return ficha == null ? -1 : ficha.getVidaMostrada();
    }

    private FichaEnEscena buscarFicha(String nombre) {
        for (FichaEnEscena ficha : fichas) {
            if (ficha.getSprite().getNombre().equals(nombre)) {
                return ficha;
            }
        }
        return null;
    }

    /** El ActionEvent que recibe el oyente trae el nombre del personaje en getActionCommand(). */
    public void alTocarPersonaje(ActionListener oyente) {
        oyentesDeClic.add(oyente);
        for (SpriteAnimado sprite : sprites) {
            sprite.addActionListener(oyente);
        }
    }

    // Marcas bajo los pies de los personajes
    public static final String MARCA_ELEGIDO = "ELEGIDO";
    public static final String MARCA_ENEMIGO = "ENEMIGO";
    public static final String MARCA_ALIADO = "ALIADO";
    public static final String MARCA_SUGERIDO = "SUGERIDO";
    public static final String MARCA_CURSOR = "CURSOR";

    /** Pone una marca de color bajo los pies: elegido, objetivo enemigo, objetivo aliado o sugerido. */
    public void marcar(String nombre, String marca) {
        SpriteAnimado sprite = buscar(nombre);
        if (sprite == null) {
            return;
        }
        personajesMarcados.add(nombre);
        nombresMarcados.add(marca);
        if (marca.equals(MARCA_ELEGIDO)) {
            sprite.setMarca(Paleta.MARCA_ELEGIDO);
        } else if (marca.equals(MARCA_ENEMIGO)) {
            sprite.setMarca(Paleta.MARCA_ENEMIGO);
        } else if (marca.equals(MARCA_ALIADO)) {
            sprite.setMarca(Paleta.MARCA_ALIADO);
        } else if (marca.equals(MARCA_CURSOR)) {
            sprite.setMarca(Paleta.MARCA_CURSOR);
        } else {
            sprite.setMarca(Paleta.MARCA_SUGERIDO);
        }
    }

    /** Nombre del personaje que tiene esa marca, o "" si nadie la tiene. */
    public String getNombreConMarca(String marca) {
        int posicion = nombresMarcados.lastIndexOf(marca);
        if (posicion < 0) {
            return "";
        }
        return personajesMarcados.get(posicion);
    }

    public void quitarMarcas() {
        personajesMarcados.clear();
        nombresMarcados.clear();
        for (SpriteAnimado sprite : sprites) {
            sprite.setMarca(null);
        }
    }

    /** Deja al personaje quieto y apagado hasta que reciba otra animacion. */
    public void dormir(String nombre) {
        SpriteAnimado sprite = buscar(nombre);
        if (sprite != null) {
            sprite.reproducir(SpriteAnimado.DORMIDO);
        }
    }

    /** Muestra de golpe el final de todas las animaciones pendientes (las caidas incluidas). */
    public void terminarAnimaciones() {
        int limite = 1000;
        while (estaAnimando() && limite > 0) {
            limite--;
            if (actuando == null) {
                empezarSiguiente();
            } else if (actuando.avanzar()) {
                terminarActuacion();
            }
        }
    }

    /** True mientras quedan animaciones por mostrar. */
    public boolean estaAnimando() {
        return actuando != null || !colaNombres.isEmpty();
    }

    private void agregar(JPanel fila, SpriteAnimado sprite, boolean esHeroe) {
        for (ActionListener oyente : oyentesDeClic) {
            sprite.addActionListener(oyente);
        }
        FichaEnEscena ficha = new FichaEnEscena(sprite, raiz, escala, conVida, esHeroe);
        sprites.add(sprite);
        fichas.add(ficha);
        fila.add(ficha);
    }

    /** Lo que hace el Timer en cada tic. */
    private void avanzarCuadro() {
        tic++;
        if (actuando == null) {
            empezarSiguiente();
        } else if (actuando.avanzar()) {
            terminarActuacion();
            empezarSiguiente();
        }
        // Los que no actuan respiran a la mitad de velocidad.
        if (tic % 2 == 0) {
            for (SpriteAnimado sprite : sprites) {
                if (sprite != actuando && sprite.estaEnReposo()) {
                    sprite.avanzar();
                }
            }
        }
    }

    private void empezarSiguiente() {
        while (!colaNombres.isEmpty()) {
            String nombre = colaNombres.remove(0);
            String animacion = colaAnimaciones.remove(0);
            String efecto = colaEfectos.remove(0);
            int vidaTras = colaVidas.remove(0);
            SpriteAnimado sprite = buscar(nombre);
            if (sprite != null) {
                FichaEnEscena ficha = buscarFicha(nombre);
                if (vidaTras >= 0 && ficha != null) {
                    ficha.setVida(vidaTras, ficha.getVidaMaxima());
                }
                actuando = sprite;
                sprite.reproducir(animacion);
                sonido.efecto(efecto);
                return;
            }
        }
        actuando = null;
    }

    /** Al terminar la caida, el personaje sale de la escena. */
    private void terminarActuacion() {
        if (actuando.estaFueraDeCombate()) {
            FichaEnEscena ficha = buscarFicha(actuando.getNombre());
            sprites.remove(actuando);
            fichas.remove(ficha);
            filaHeroes.remove(ficha);
            filaEnemigos.remove(ficha);
            filaHeroes.revalidate();
            filaEnemigos.revalidate();
            repaint();
        }
        actuando = null;
    }

    private SpriteAnimado buscar(String nombre) {
        for (SpriteAnimado sprite : sprites) {
            if (sprite.getNombre().equals(nombre)) {
                return sprite;
            }
        }
        return null;
    }

    private static JPanel crearFila(int alineacion) {
        JPanel fila = new JPanel(new FlowLayout(alineacion, 0, 0));
        fila.setOpaque(false);
        return fila;
    }
}
