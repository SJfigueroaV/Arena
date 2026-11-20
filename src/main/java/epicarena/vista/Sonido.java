package epicarena.vista;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

/**
 * Reproduce la musica de fondo y los efectos con javax.sound.sampled, que
 * viene incluido en Java. Si el equipo no tiene audio, el juego sigue en
 * silencio sin fallar. Los archivos estan en /sonidos/musica y /sonidos/efectos.
 */
public class Sonido {

    /** La musica suena mas bajo que los efectos para no taparlos. */
    private static final float VOLUMEN_MUSICA_DB = -4f;

    /** Las pruebas arrancan con -Depicarena.silencio=true para no hacer ruido. */
    private final boolean disponible = !Boolean.getBoolean("epicarena.silencio");
    private boolean silenciado;

    // Efectos ya abiertos: el nombre en una lista y el Clip en la otra.
    private final List<String> nombresDeEfectos = new ArrayList<>();
    private final List<Clip> efectos = new ArrayList<>();

    private Clip musica;
    private String musicaActual;

    /** Suena una vez el efecto, por ejemplo "golpe". */
    public void efecto(String nombre) {
        if (!disponible || silenciado || nombre == null) {
            return;
        }
        Clip clip = buscarEfecto(nombre);
        if (clip == null) {
            return;
        }
        clip.stop();
        clip.setFramePosition(0);
        clip.start();
    }

    /** Cambia la musica de fondo, que se repite sin fin. Si ya sonaba esa, no la reinicia. */
    public void musica(String nombre) {
        if (nombre != null && nombre.equals(musicaActual)) {
            return;
        }
        detenerMusica();
        musicaActual = nombre;
        if (!disponible || nombre == null) {
            return;
        }
        musica = abrir("/sonidos/musica/" + nombre + ".wav");
        if (musica == null) {
            return;
        }
        if (musica.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl volumen = (FloatControl) musica.getControl(FloatControl.Type.MASTER_GAIN);
            volumen.setValue(Math.max(volumen.getMinimum(), VOLUMEN_MUSICA_DB));
        }
        if (!silenciado) {
            musica.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public void detenerMusica() {
        if (musica != null) {
            musica.stop();
            musica.close();
            musica = null;
        }
        musicaActual = null;
    }

    /** Apaga o vuelve a encender todo el sonido. */
    public void alternarSilencio() {
        silenciado = !silenciado;
        if (musica != null) {
            if (silenciado) {
                musica.stop();
            } else {
                musica.loop(Clip.LOOP_CONTINUOUSLY);
            }
        }
    }

    public boolean estaSilenciado() {
        return silenciado;
    }

    private Clip buscarEfecto(String nombre) {
        int posicion = nombresDeEfectos.indexOf(nombre);
        if (posicion >= 0) {
            return efectos.get(posicion);
        }
        Clip clip = abrir("/sonidos/efectos/" + nombre + ".wav");
        if (clip != null) {
            nombresDeEfectos.add(nombre);
            efectos.add(clip);
        }
        return clip;
    }

    /** Abre un archivo WAV. Retorna null si no existe o si no hay audio disponible. */
    private Clip abrir(String ruta) {
        URL archivo = getClass().getResource(ruta);
        if (archivo == null) {
            System.err.println("Sonido: no se encontró " + ruta);
            return null;
        }
        try {
            AudioInputStream entrada = AudioSystem.getAudioInputStream(archivo);
            Clip clip = AudioSystem.getClip();
            clip.open(entrada);
            return clip;
        } catch (Exception e) {
            System.err.println("Sonido: no se pudo reproducir " + ruta + " (" + e.getMessage() + ")");
            return null;
        }
    }
}
