package epicarena.vista;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import epicarena.historia.LagunaDeLosEspantos;
import epicarena.modelo.Campana;
import epicarena.modelo.EscenaDelPrologo;
import epicarena.modelo.GestorBatalla;
import epicarena.modelo.Personaje;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Revisa que cada personaje y cada etapa de la campana tengan sus dibujos. */
class SpritesTest {

    private static final String[] ANIMACIONES_DE_TODOS = {"idle", "ataque", "dano", "curado", "caida"};

    @Test
    void elNombreSeConvierteEnLaCarpetaDelDibujo() {
        assertEquals("tyba", SpriteAnimado.archivoDe("Tyba"));
        assertEquals("hombre_caiman", SpriteAnimado.archivoDe("El Hombre Caimán"));
        assertEquals("anima_en_pena", SpriteAnimado.archivoDe("Ánima en Pena"));
        assertEquals("madre_de_agua", SpriteAnimado.archivoDe("La Madre de Agua"));
        assertEquals("sombreron", SpriteAnimado.archivoDe("El Sombrerón"));
    }

    @Test
    void cadaPersonajeDeLaCampanaTieneSusAnimaciones() {
        Campana campana = new LagunaDeLosEspantos();
        for (int etapa = 1; etapa <= campana.totalDeEtapas(); etapa++) {
            GestorBatalla batalla = campana.crearBatallaActual();
            List<Personaje> todos = new ArrayList<>(batalla.getEquipoJugador());
            todos.addAll(batalla.getEquipoEnemigo());
            for (Personaje personaje : todos) {
                String carpeta = "/sprites/personajes/" + SpriteAnimado.archivoDe(personaje.getNombre()) + "/";
                for (String animacion : ANIMACIONES_DE_TODOS) {
                    assertNotNull(getClass().getResource(carpeta + animacion + "_0.png"),
                            personaje.getNombre() + " no tiene la animacion " + animacion);
                }
                if (personaje.tieneHabilidadEspecial()) {
                    assertNotNull(getClass().getResource(carpeta + "especial_0.png"),
                            personaje.getNombre() + " no tiene la animacion especial");
                }
            }
            assertNotNull(getClass().getResource("/sprites/fondos/" + campana.etapaActual().getEscenario() + ".png"),
                    "La etapa " + etapa + " no tiene fondo");
            campana.avanzar();
        }
    }

    @Test
    void cadaEscenaDelPrologoTieneFondoYPersonajes() {
        Campana campana = new LagunaDeLosEspantos();
        assertEquals(5, campana.getPrologo().size());
        for (EscenaDelPrologo escena : campana.getPrologo()) {
            assertNotNull(getClass().getResource("/sprites/fondos/" + escena.getEscenario() + ".png"),
                    "Falta el fondo " + escena.getEscenario());
            assertNotNull(getClass().getResource("/sprites/x3/fondos/" + escena.getEscenario() + ".png"),
                    "Falta el fondo grande " + escena.getEscenario());
            List<String> todos = new ArrayList<>(escena.getHeroes());
            todos.addAll(escena.getEspantos());
            for (String nombre : todos) {
                String carpeta = "/sprites/personajes/" + SpriteAnimado.archivoDe(nombre) + "/";
                assertNotNull(getClass().getResource(carpeta + "idle_0.png"), "Falta " + nombre);
                assertNotNull(getClass().getResource(carpeta + "dormido_0.png"), nombre + " no puede dormir");
                String grande = "/sprites/x3/personajes/" + SpriteAnimado.archivoDe(nombre) + "/";
                assertNotNull(getClass().getResource(grande + "idle_0.png"), "Falta " + nombre + " en grande");
                assertNotNull(getClass().getResource(grande + "dormido_0.png"), "Falta " + nombre + " dormido en grande");
            }
        }
    }

    @Test
    void existenLaMusicaDeCadaEtapaYLosEfectos() {
        Campana campana = new LagunaDeLosEspantos();
        assertNotNull(getClass().getResource("/sonidos/musica/prologo.wav"));
        for (int etapa = 1; etapa <= campana.totalDeEtapas(); etapa++) {
            String escenario = campana.etapaActual().getEscenario();
            assertNotNull(getClass().getResource("/sonidos/musica/" + escenario + ".wav"), "Sin música: " + escenario);
            campana.avanzar();
        }
        String[] efectos = {"golpe", "hechizo", "curacion", "dano", "caida", "victoria", "derrota",
            "salpicadura", "despertar", "leyenda", "tajo"};
        for (String efecto : efectos) {
            assertNotNull(getClass().getResource("/sonidos/efectos/" + efecto + ".wav"), "Sin efecto: " + efecto);
        }
        for (int tunjo = 1; tunjo <= 3; tunjo++) {
            assertNotNull(getClass().getResource("/sprites/fondos/ofrenda_" + tunjo + ".png"));
            assertNotNull(getClass().getResource("/sprites/x3/fondos/ofrenda_" + tunjo + ".png"));
        }
    }
}
