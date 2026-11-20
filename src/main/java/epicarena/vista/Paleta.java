package epicarena.vista;

import java.awt.Color;
import java.awt.Font;

/** Colores y fuentes compartidos por toda la interfaz. */
final class Paleta {

    static final Color FONDO = new Color(0x16141F);
    static final Color PANEL = new Color(0x211E2D);
    static final Color PANEL_CLARO = new Color(0x2C2839);
    static final Color BORDE = new Color(0x3D384F);
    static final Color TEXTO = new Color(0xECE6D4);
    static final Color TEXTO_SUAVE = new Color(0xA39DB5);
    static final Color DORADO = new Color(0xE0B44C);
    static final Color LUNA = new Color(0xC3B6EA);
    static final Color DORADO_OSCURO = new Color(0x7A5418);
    static final Color MARCA_ELEGIDO = new Color(0xF6DE8A);
    static final Color MARCA_ENEMIGO = new Color(0xD9534F);
    static final Color MARCA_ALIADO = new Color(0x5DBB8A);
    static final Color MARCA_SUGERIDO = new Color(0x6FA9C9);
    static final Color MARCA_CURSOR = new Color(0xFFFFFF);
    static final Color FONDO_ESCENA = new Color(0x211E2D);
    static final Color HEROES = new Color(0x58A6E8);
    static final Color MONSTRUOS = new Color(0xE2635C);
    static final Color BOTON = new Color(0xC62F2F);
    static final Color BOTON_ENCIMA = new Color(0xE23D3D);
    static final Color BOTON_APAGADO = new Color(0x5A4648);

    private Paleta() {
    }

    static Font titulo(int tamano) {
        return new Font(Font.SERIF, Font.BOLD, tamano);
    }

    static Font texto(int tamano) {
        return new Font(Font.SANS_SERIF, Font.PLAIN, tamano);
    }

    static Font textoFuerte(int tamano) {
        return new Font(Font.SANS_SERIF, Font.BOLD, tamano);
    }

    static Font narracion(int tamano) {
        return new Font(Font.SERIF, Font.PLAIN, tamano);
    }

    static Font subtitulo(int tamano) {
        return new Font(Font.SERIF, Font.ITALIC, tamano);
    }
}
