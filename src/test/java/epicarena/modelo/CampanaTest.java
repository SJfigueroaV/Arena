package epicarena.modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import epicarena.historia.LagunaDeLosEspantos;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class CampanaTest {

    /** Campana minima para probar el recorrido de etapas: todas sus batallas estan vacias. */
    private static class CampanaDePrueba extends Campana {

        CampanaDePrueba() {
            super("Prueba", "Fin", "Perdiste");
        }

        @Override
        protected GestorBatalla crearBatalla(int numeroDeEtapa) {
            return new GestorBatalla();
        }
    }

    private Campana campanaDeDos() {
        Campana campana = new CampanaDePrueba();
        campana.agregarEtapa(new Etapa("Uno", "Intro uno", "Cierre uno"));
        campana.agregarEtapa(new Etapa("Dos", "Intro dos", "Cierre dos"));
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
        Campana campana = new LagunaDeLosEspantos();

        GestorBatalla primera = campana.crearBatallaActual();
        primera.getEquipoJugador().get(0).recibirDanoDirecto(50);
        GestorBatalla segunda = campana.crearBatallaActual();

        assertNotSame(primera, segunda);
        Personaje heroe = segunda.getEquipoJugador().get(0);
        assertEquals(heroe.getPuntosDeVidaMaximos(), heroe.getPuntosDeVida());
    }

    @Test
    void unaCampanaSinEtapasOConEtapasInvalidasSeRechaza() {
        Campana vacia = new CampanaDePrueba();

        assertThrows(IllegalStateException.class, () -> vacia.etapaActual());
        assertThrows(IllegalStateException.class, () -> vacia.crearBatallaActual());
        assertThrows(IllegalArgumentException.class, () -> vacia.agregarEtapa(null));
    }

    @Test
    void laLagunaTieneCincoEtapasJugables() {
        Campana campana = new LagunaDeLosEspantos();
        assertEquals(5, campana.totalDeEtapas());

        int vidaAnteriorDeTyba = 0;
        for (int numero = 1; numero <= 5; numero++) {
            Etapa etapa = campana.etapaActual();
            GestorBatalla batalla = campana.crearBatallaActual();

            assertFalse(etapa.getTitulo().isBlank());
            assertFalse(etapa.getIntroduccion().isBlank());
            assertFalse(etapa.getCierre().isBlank());
            assertEquals(3, batalla.getEquipoJugador().size());
            assertTrue(batalla.getEquipoEnemigo().size() >= 3);
            assertFalse(batalla.verificarFinDelJuego());

            List<String> nombres = new ArrayList<>();
            for (Personaje personaje : batalla.getEquipoJugador()) {
                assertFalse(nombres.contains(personaje.getNombre()), "nombre repetido");
                nombres.add(personaje.getNombre());
            }
            for (Personaje personaje : batalla.getEquipoEnemigo()) {
                assertFalse(nombres.contains(personaje.getNombre()), "nombre repetido");
                nombres.add(personaje.getNombre());
            }

            int vidaDeTyba = batalla.buscarPorNombre("Tyba").getPuntosDeVidaMaximos();
            assertTrue(vidaDeTyba > vidaAnteriorDeTyba, "los héroes crecen con cada etapa");
            vidaAnteriorDeTyba = vidaDeTyba;
            campana.avanzar();
        }
    }
}
