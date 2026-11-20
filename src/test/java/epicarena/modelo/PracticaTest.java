package epicarena.modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import epicarena.historia.LagunaDeLosEspantos;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Juega el guion de la practica sobre el modelo y revisa que las explicaciones digan la verdad. */
class PracticaTest {

    private void jugar(GestorBatalla batalla, Personaje actor, Personaje objetivo, String accion) {
        if (accion.equals(GestorBatalla.HABILIDAD_ESPECIAL)) {
            batalla.registrarEspecialUsada(actor);
            ((IHabilidadEspecial) actor).ejecutarEspecial(objetivo);
        } else {
            actor.atacar(objetivo);
        }
    }

    @Test
    void seguirLosPasosGanaLaPracticaEnElUltimoTurnoSinPerderANadie() {
        Campana campana = new LagunaDeLosEspantos();
        assertTrue(campana.tienePractica());
        GestorBatalla batalla = campana.crearPracticaNueva();
        List<PasoGuiado> pasos = campana.getPasosDePractica();

        for (int i = 0; i < pasos.size(); i++) {
            PasoGuiado paso = pasos.get(i);
            Personaje actor = batalla.buscarPorNombre(paso.getAtacante());
            Personaje objetivo = batalla.buscarPorNombre(paso.getObjetivo());
            assertNotNull(actor, "paso " + (i + 1));
            assertNotNull(objetivo, "paso " + (i + 1));
            assertTrue(batalla.accionesDisponibles(actor).contains(paso.getAccion()),
                    "paso " + (i + 1) + ": la acción no está disponible");
            assertTrue(batalla.objetivosValidos(actor, paso.getAccion()).contains(objetivo),
                    "paso " + (i + 1) + ": el objetivo no es válido");

            jugar(batalla, actor, objetivo, paso.getAccion());
            batalla.eliminarDerrotados();
            if (batalla.verificarFinDelJuego()) {
                assertEquals(pasos.size() - 1, i, "la práctica debe terminar en el último paso");
                break;
            }
            Personaje enemigo = batalla.buscarPorNombre(paso.getEnemigoQueResponde());
            Personaje heroe = batalla.buscarPorNombre(paso.getObjetivoDeLaRespuesta());
            jugar(batalla, enemigo, heroe, GestorBatalla.ATAQUE_NORMAL);
            batalla.terminarTurno();
            batalla.eliminarDerrotados();
            assertFalse(batalla.verificarFinDelJuego(), "nadie debe ganar antes del último paso");
        }

        assertTrue(batalla.ganoElJugador());
        assertEquals(3, batalla.getEquipoJugador().size(), "ningún héroe cae en la práctica");
    }

    @Test
    void lasCifrasDeLasExplicacionesSeCumplen() {
        Campana campana = new LagunaDeLosEspantos();
        GestorBatalla batalla = campana.crearPracticaNueva();
        Personaje tyba = batalla.buscarPorNombre("Tyba");
        Personaje chie = batalla.buscarPorNombre("Chie");
        Personaje sua = batalla.buscarPorNombre("Sua");
        Personaje silbon = batalla.buscarPorNombre("Sombra del Silbón");
        Personaje madremonte = batalla.buscarPorNombre("Sombra de la Madremonte");

        jugar(batalla, tyba, madremonte, GestorBatalla.ATAQUE_NORMAL);
        assertEquals(14, madremonte.getPuntosDeVida(), "paso 1: pierde 26 de 40");
        jugar(batalla, silbon, chie, GestorBatalla.ATAQUE_NORMAL);
        batalla.terminarTurno();
        jugar(batalla, tyba, madremonte, GestorBatalla.ATAQUE_NORMAL);
        assertFalse(madremonte.estaVivo(), "paso 2: el remate la derriba");
        batalla.eliminarDerrotados();
        jugar(batalla, silbon, chie, GestorBatalla.ATAQUE_NORMAL);
        batalla.terminarTurno();
        jugar(batalla, chie, silbon, GestorBatalla.HABILIDAD_ESPECIAL);
        assertEquals(30, silbon.getPuntosDeVida(), "paso 3: la Luz de Chía hace 60");
        jugar(batalla, silbon, chie, GestorBatalla.ATAQUE_NORMAL);
        assertEquals(21, chie.getPuntosDeVida(), "paso 4: Chie queda con 21");
        batalla.terminarTurno();
        jugar(batalla, sua, chie, GestorBatalla.HABILIDAD_ESPECIAL);
        assertEquals(53, chie.getPuntosDeVida(), "paso 4: el Calor de Sué devuelve 32");
        jugar(batalla, silbon, tyba, GestorBatalla.ATAQUE_NORMAL);
        batalla.terminarTurno();
        assertFalse(batalla.accionesDisponibles(chie).contains(GestorBatalla.HABILIDAD_ESPECIAL),
                "paso 5: la Luz de Chía todavía se recarga");
        jugar(batalla, tyba, silbon, GestorBatalla.ATAQUE_NORMAL);
        assertEquals(10, silbon.getPuntosDeVida(), "paso 5: Tyba hace 20 y le quedan 10");
    }
}
