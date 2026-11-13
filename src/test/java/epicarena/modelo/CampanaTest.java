package epicarena.modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import epicarena.historia.LagunaDeLosEspantos;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class CampanaTest {

    private Campana campanaDeDos() {
        Campana campana = new Campana("Prueba", "Fin", "Perdiste");
        campana.agregarEtapa(new Etapa("Uno", "Intro uno", "Cierre uno", GestorBatalla::new));
        campana.agregarEtapa(new Etapa("Dos", "Intro dos", "Cierre dos", GestorBatalla::new));
        return campana;
    }

    @Test
    void avanzaEtapaPorEtapaYSeDetieneEnLaUltima() {
        Campana campana = campanaDeDos();

        assertEquals(1, campana.numeroDeEtapa());
        assertEquals("Uno", campana.etapaActual().getTitulo());
        assertTrue(campana.hayMasEtapas());

        campana.avanzar();
        assertEquals(2, campana.numeroDeEtapa());
        assertFalse(campana.hayMasEtapas());

        campana.avanzar();
        assertEquals("Dos", campana.etapaActual().getTitulo());
    }

    @Test
    void reiniciarVuelveALaPrimeraEtapa() {
        Campana campana = campanaDeDos();
        campana.avanzar();

        campana.reiniciar();

        assertEquals(1, campana.numeroDeEtapa());
    }

    @Test
    void cadaIntentoDeUnaEtapaCreaUnaBatallaNueva() {
        Etapa etapa = LagunaDeLosEspantos.crear().etapaActual();

        GestorBatalla primera = etapa.crearBatalla();
        primera.getEquipoJugador().get(0).recibirDanoDirecto(50);
        GestorBatalla segunda = etapa.crearBatalla();

        assertNotSame(primera, segunda);
        Personaje heroe = segunda.getEquipoJugador().get(0);
        assertEquals(heroe.getPuntosDeVidaMaximos(), heroe.getPuntosDeVida());
    }

    @Test
    void unaCampanaSinEtapasOConEtapasInvalidasSeRechaza() {
        Campana vacia = new Campana("Vacía", "", "");

        assertThrows(IllegalStateException.class, vacia::etapaActual);
        assertThrows(IllegalArgumentException.class, () -> vacia.agregarEtapa(null));
        assertThrows(IllegalArgumentException.class, () -> new Etapa("x", "x", "x", null));
    }

    @Test
    void laLagunaTieneCincoEtapasJugables() {
        Campana campana = LagunaDeLosEspantos.crear();
        assertEquals(5, campana.totalDeEtapas());

        int vidaAnteriorDeTyba = 0;
        for (int numero = 1; numero <= 5; numero++) {
            Etapa etapa = campana.etapaActual();
            GestorBatalla batalla = etapa.crearBatalla();

            assertFalse(etapa.getTitulo().isBlank());
            assertFalse(etapa.getIntroduccion().isBlank());
            assertFalse(etapa.getCierre().isBlank());
            assertEquals(3, batalla.getEquipoJugador().size());
            assertTrue(batalla.getEquipoEnemigo().size() >= 3);
            assertFalse(batalla.verificarFinDelJuego());

            Set<String> nombres = new HashSet<>();
            for (Personaje personaje : batalla.getEquipoJugador()) {
                assertTrue(nombres.add(personaje.getNombre()));
            }
            for (Personaje personaje : batalla.getEquipoEnemigo()) {
                assertTrue(nombres.add(personaje.getNombre()));
            }

            int vidaDeTyba = batalla.buscarPorNombre("Tyba").getPuntosDeVidaMaximos();
            assertTrue(vidaDeTyba > vidaAnteriorDeTyba, "los héroes crecen con cada etapa");
            vidaAnteriorDeTyba = vidaDeTyba;
            campana.avanzar();
        }
    }
}
