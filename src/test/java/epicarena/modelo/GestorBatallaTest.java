package epicarena.modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GestorBatallaTest {

    private GestorBatalla gestor;
    private Guerrero aragorn;
    private Mago gandalf;
    private Sacerdote elrond;
    private Guerrero orco;
    private Mago nigromante;

    @BeforeEach
    void prepararBatalla() {
        gestor = new GestorBatalla(new Random(7));
        aragorn = new Guerrero("Aragorn", 120, 30, 5);
        gandalf = new Mago("Gandalf", 70, 20, 0);
        elrond = new Sacerdote("Elrond", 80, 15, 2);
        orco = new Guerrero("Orco", 100, 20, 3);
        nigromante = new Mago("Nigromante", 60, 18, 0);
        gestor.agregarHeroe(aragorn);
        gestor.agregarHeroe(gandalf);
        gestor.agregarHeroe(elrond);
        gestor.agregarEnemigo(orco);
        gestor.agregarEnemigo(nigromante);
    }

    @Test
    void buscaPersonajesEnAmbosEquipos() {
        assertSame(gandalf, gestor.buscarPorNombre("Gandalf"));
        assertSame(orco, gestor.buscarPorNombre("Orco"));
        assertNull(gestor.buscarPorNombre("Nadie"));
        assertNull(gestor.buscarPorNombre(null));
    }

    @Test
    void noAceptaNombresRepetidosNiNulos() {
        assertThrows(IllegalArgumentException.class,
                () -> gestor.agregarEnemigo(new Guerrero("Aragorn", 50, 5, 0)));
        assertThrows(IllegalArgumentException.class, () -> gestor.agregarHeroe(null));
    }

    @Test
    void losEquiposCrecenSinLimiteFijo() {
        for (int i = 0; i < 20; i++) {
            gestor.agregarEnemigo(new Guerrero("Goblin " + i, 30, 5, 0));
        }

        assertEquals(22, gestor.getEquipoEnemigo().size());
    }

    @Test
    void lasListasNoSePuedenModificarDesdeAfuera() {
        assertThrows(UnsupportedOperationException.class,
                () -> gestor.getEquipoJugador().remove(0));
    }

    @Test
    void soloQuienTieneHabilidadEspecialLaPuedeElegir() {
        assertEquals(List.of(GestorBatalla.ATAQUE_NORMAL), gestor.accionesDisponibles(aragorn));
        assertEquals(List.of(GestorBatalla.ATAQUE_NORMAL, GestorBatalla.HABILIDAD_ESPECIAL),
                gestor.accionesDisponibles(gandalf));
        assertTrue(gestor.accionesDisponibles(null).isEmpty());
    }

    @Test
    void elAtaqueNormalApuntaAlEquipoRival() {
        assertEquals(List.of(orco, nigromante),
                gestor.objetivosValidos(aragorn, GestorBatalla.ATAQUE_NORMAL));
        assertEquals(List.of(aragorn, gandalf, elrond),
                gestor.objetivosValidos(orco, GestorBatalla.ATAQUE_NORMAL));
    }

    @Test
    void laEspecialDelMagoApuntaARivalesYLaDelSacerdoteAAliados() {
        assertEquals(List.of(orco, nigromante),
                gestor.objetivosValidos(gandalf, GestorBatalla.HABILIDAD_ESPECIAL));
        assertEquals(List.of(aragorn, gandalf, elrond),
                gestor.objetivosValidos(elrond, GestorBatalla.HABILIDAD_ESPECIAL));
    }

    @Test
    void sinActorOConUnDesconocidoNoHayObjetivos() {
        assertTrue(gestor.objetivosValidos(null, GestorBatalla.ATAQUE_NORMAL).isEmpty());
        assertTrue(gestor.objetivosValidos(aragorn, null).isEmpty());
        assertTrue(gestor.objetivosValidos(new Guerrero("Intruso", 10, 1, 0),
                GestorBatalla.ATAQUE_NORMAL).isEmpty());
    }

    @Test
    void losDerrotadosSeEliminanDeLaLista() {
        orco.recibirDanoDirecto(1000);

        List<Personaje> eliminados = gestor.eliminarDerrotados();

        assertEquals(List.of(orco), eliminados);
        assertEquals(List.of(nigromante), gestor.getEquipoEnemigo());
        assertNull(gestor.buscarPorNombre("Orco"));
        assertTrue(gestor.eliminarDerrotados().isEmpty());
    }

    @Test
    void elJuegoTerminaCuandoUnEquipoQuedaVacio() {
        assertFalse(gestor.verificarFinDelJuego());

        orco.recibirDanoDirecto(1000);
        gestor.eliminarDerrotados();
        assertFalse(gestor.verificarFinDelJuego());

        nigromante.recibirDanoDirecto(1000);
        gestor.eliminarDerrotados();
        assertTrue(gestor.verificarFinDelJuego());
        assertTrue(gestor.ganoElJugador());
    }

    @Test
    void elJugadorPierdeSiSeQuedaSinHeroes() {
        aragorn.recibirDanoDirecto(1000);
        gandalf.recibirDanoDirecto(1000);
        elrond.recibirDanoDirecto(1000);
        gestor.eliminarDerrotados();

        assertTrue(gestor.verificarFinDelJuego());
        assertFalse(gestor.ganoElJugador());
    }

    @Test
    void elAzarSoloEligePersonajesPresentes() {
        for (int i = 0; i < 50; i++) {
            assertTrue(gestor.getEquipoEnemigo().contains(gestor.elegirEnemigoAlAzar()));
            assertTrue(gestor.getEquipoJugador().contains(gestor.elegirHeroeAlAzar()));
        }
    }

    @Test
    void elegirAlAzarEnUnEquipoVacioRetornaNull() {
        GestorBatalla vacio = new GestorBatalla();

        assertNull(vacio.elegirEnemigoAlAzar());
        assertNull(vacio.elegirHeroeAlAzar());
        assertTrue(vacio.verificarFinDelJuego());
    }

    @Test
    void unaBatallaCompletaSiempreTermina() {
        int turnos = 0;
        while (!gestor.verificarFinDelJuego() && turnos < 1000) {
            Personaje heroe = gestor.elegirHeroeAlAzar();
            heroe.atacar(gestor.elegirEnemigoAlAzar());
            gestor.eliminarDerrotados();
            if (!gestor.verificarFinDelJuego()) {
                gestor.elegirEnemigoAlAzar().atacar(gestor.elegirHeroeAlAzar());
                gestor.eliminarDerrotados();
            }
            turnos++;
        }

        assertTrue(gestor.verificarFinDelJuego());
    }

    @Test
    void laEspecialQuedaEnRecargaDosTurnos() {
        gestor.registrarEspecialUsada(gandalf);
        gestor.terminarTurno();

        assertEquals(2, gestor.turnosParaEspecial(gandalf));
        assertEquals(List.of(GestorBatalla.ATAQUE_NORMAL), gestor.accionesDisponibles(gandalf));

        gestor.terminarTurno();
        assertEquals(1, gestor.turnosParaEspecial(gandalf));

        gestor.terminarTurno();
        assertEquals(0, gestor.turnosParaEspecial(gandalf));
        assertTrue(gestor.accionesDisponibles(gandalf).contains(GestorBatalla.HABILIDAD_ESPECIAL));
    }

    @Test
    void laRecargaDeUnoNoAfectaALosDemas() {
        gestor.registrarEspecialUsada(gandalf);

        assertTrue(gestor.accionesDisponibles(elrond).contains(GestorBatalla.HABILIDAD_ESPECIAL));
        assertEquals(0, gestor.turnosParaEspecial(aragorn));
    }

    @Test
    void quienNoTieneEspecialSiempreEligeElAtaqueNormal() {
        for (int i = 0; i < 100; i++) {
            assertEquals(GestorBatalla.ATAQUE_NORMAL, gestor.elegirAccionAlAzar(orco));
        }
    }

    @Test
    void elEnemigoUsaSuEspecialSoloAVecesYNuncaEnRecarga() {
        int especiales = 0;
        for (int i = 0; i < 400; i++) {
            if (GestorBatalla.HABILIDAD_ESPECIAL.equals(gestor.elegirAccionAlAzar(nigromante))) {
                especiales++;
            }
        }
        assertTrue(especiales > 60 && especiales < 180, "se esperaba cerca del 30%: " + especiales);

        gestor.registrarEspecialUsada(nigromante);
        for (int i = 0; i < 100; i++) {
            assertEquals(GestorBatalla.ATAQUE_NORMAL, gestor.elegirAccionAlAzar(nigromante));
        }
    }

    @Test
    void elSanadorNoGastaSuEspecialSiNadieEstaHerido() {
        for (int i = 0; i < 100; i++) {
            assertEquals(GestorBatalla.ATAQUE_NORMAL, gestor.elegirAccionAlAzar(elrond));
        }
    }

    @Test
    void elSanadorEligeAlCompaneroMasHerido() {
        aragorn.recibirDanoDirecto(30);
        gandalf.recibirDanoDirecto(50);

        assertSame(gandalf, gestor.elegirObjetivoAlAzar(elrond, GestorBatalla.HABILIDAD_ESPECIAL));
    }

    @Test
    void elObjetivoDeUnAtaqueEsSiempreUnRival() {
        for (int i = 0; i < 50; i++) {
            Personaje objetivo = gestor.elegirObjetivoAlAzar(orco, GestorBatalla.ATAQUE_NORMAL);
            assertTrue(gestor.getEquipoJugador().contains(objetivo));
        }
    }
}
