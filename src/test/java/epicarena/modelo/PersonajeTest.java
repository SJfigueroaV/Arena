package epicarena.modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class PersonajeTest {

    @Test
    void elGuerreroInfligeElCienPorCientoDeSuAtaque() {
        Personaje guerrero = new Guerrero("Aragorn", 100, 30, 0);
        Personaje orco = new Guerrero("Orco", 100, 10, 0);

        String narrativa = guerrero.atacar(orco);

        assertEquals(70, orco.getPuntosDeVida());
        assertTrue(narrativa.contains("Aragorn"));
        assertTrue(narrativa.contains("Orco"));
        assertTrue(narrativa.contains("30"));
    }

    @Test
    void laDefensaReduceElDanoRecibido() {
        Personaje orco = new Guerrero("Orco", 100, 10, 8);

        int aplicado = orco.recibirDano(30);

        assertEquals(22, aplicado);
        assertEquals(78, orco.getPuntosDeVida());
    }

    @Test
    void unGolpeSiempreHaceAlMenosUnPunto() {
        Personaje tanque = new Guerrero("Tanque", 100, 10, 50);

        assertEquals(1, tanque.recibirDano(5));
        assertEquals(99, tanque.getPuntosDeVida());
    }

    @Test
    void elDanoDirectoIgnoraLaDefensa() {
        Personaje tanque = new Guerrero("Tanque", 100, 10, 50);

        assertEquals(40, tanque.recibirDanoDirecto(40));
        assertEquals(60, tanque.getPuntosDeVida());
    }

    @Test
    void laVidaNuncaBajaDeCeroYElPersonajeMuere() {
        Personaje orco = new Guerrero("Orco", 20, 10, 0);

        int aplicado = orco.recibirDano(500);

        assertEquals(20, aplicado);
        assertEquals(0, orco.getPuntosDeVida());
        assertFalse(orco.estaVivo());
    }

    @Test
    void elDanoNegativoOCeroNoHaceNada() {
        Personaje orco = new Guerrero("Orco", 20, 10, 0);

        assertEquals(0, orco.recibirDano(-5));
        assertEquals(0, orco.recibirDano(0));
        assertEquals(0, orco.recibirDanoDirecto(-5));
        assertEquals(20, orco.getPuntosDeVida());
    }

    @Test
    void laCuracionNoSuperaElMaximo() {
        Personaje heroe = new Guerrero("Aragorn", 100, 30, 0);
        heroe.recibirDano(30);

        heroe.restaurarSalud(500);

        assertEquals(100, heroe.getPuntosDeVida());
    }

    @Test
    void unPersonajeDerrotadoNoSePuedeCurar() {
        Personaje heroe = new Guerrero("Aragorn", 100, 30, 0);
        heroe.recibirDano(100);

        heroe.restaurarSalud(50);

        assertEquals(0, heroe.getPuntosDeVida());
        assertFalse(heroe.estaVivo());
    }

    @Test
    void laNarrativaAnunciaLaDerrota() {
        Personaje guerrero = new Guerrero("Aragorn", 100, 30, 0);
        Personaje orco = new Guerrero("Orco", 10, 10, 0);

        String narrativa = guerrero.atacar(orco);

        assertTrue(narrativa.contains("Orco cae"));
    }

    @Test
    void noSeCreanPersonajesInvalidos() {
        assertThrows(IllegalArgumentException.class, () -> new Guerrero(" ", 100, 30, 0));
        assertThrows(IllegalArgumentException.class, () -> new Guerrero("Aragorn", 0, 30, 0));
    }

    @Test
    void elMagoAtacaDebilPeroSuEspecialIgnoraLaDefensa() {
        Mago mago = new Mago("Gandalf", 70, 20, 0);
        Personaje orco = new Guerrero("Orco", 200, 10, 5);

        mago.atacar(orco);
        assertEquals(195, orco.getPuntosDeVida(), "ataque normal: 10 - 5 de defensa");

        String narrativa = mago.ejecutarEspecial(orco);
        assertEquals(145, orco.getPuntosDeVida(), "especial: 50 sin descontar defensa");
        assertTrue(narrativa.contains("50"));
        assertFalse(mago.especialApuntaAAliado());
    }

    @Test
    void elSacerdoteCuraAUnCompaneroSinHacerDano() {
        Sacerdote sacerdote = new Sacerdote("Elrond", 80, 15, 0);
        Personaje herido = new Guerrero("Aragorn", 100, 30, 0);
        herido.recibirDano(60);

        String narrativa = sacerdote.ejecutarEspecial(herido);

        assertEquals(70, herido.getPuntosDeVida());
        assertTrue(narrativa.contains("30"));
        assertTrue(sacerdote.especialApuntaAAliado());
    }

    @Test
    void elSacerdoteAvisaCuandoLaSaludYaEstaCompleta() {
        Sacerdote sacerdote = new Sacerdote("Elrond", 80, 15, 0);
        Personaje sano = new Guerrero("Aragorn", 100, 30, 0);

        String narrativa = sacerdote.ejecutarEspecial(sano);

        assertEquals(100, sano.getPuntosDeVida());
        assertTrue(narrativa.contains("completa"));
    }

    @Test
    void elAtaqueEsPolimorfico() {
        Personaje objetivoDelGuerrero = new Guerrero("Orco A", 100, 0, 0);
        Personaje objetivoDelMago = new Guerrero("Orco B", 100, 0, 0);
        Personaje objetivoDelSacerdote = new Guerrero("Orco C", 100, 0, 0);
        Personaje[] atacantes = {
            new Guerrero("Aragorn", 100, 20, 0),
            new Mago("Gandalf", 100, 20, 0),
            new Sacerdote("Elrond", 100, 20, 0)
        };
        Personaje[] objetivos = {objetivoDelGuerrero, objetivoDelMago, objetivoDelSacerdote};

        for (int i = 0; i < atacantes.length; i++) {
            atacantes[i].atacar(objetivos[i]);
        }

        assertEquals(80, objetivoDelGuerrero.getPuntosDeVida());
        assertEquals(90, objetivoDelMago.getPuntosDeVida());
        assertEquals(88, objetivoDelSacerdote.getPuntosDeVida());
    }

    @Test
    void losNombresConArticuloSeAcomodanALaFrase() {
        assertEquals("al Mohán", new Mago("El Mohán", 10, 1, 0).comoObjetivo());
        assertEquals("a la Patasola", new Guerrero("La Patasola", 10, 1, 0).comoObjetivo());
        assertEquals("a Tyba", new Guerrero("Tyba", 10, 1, 0).comoObjetivo());
        assertEquals("el Mohán", new Mago("El Mohán", 10, 1, 0).enFrase());
    }

    @Test
    void cadaPersonajeNarraConSuPropioVerbo() {
        Personaje caiman = new Guerrero("El Hombre Caimán", 100, 20, 0, "muerde");
        Personaje tyba = new Guerrero("Tyba", 100, 20, 0);

        assertEquals("¡El Hombre Caimán muerde a Tyba por 20 puntos de daño!", caiman.atacar(tyba));
        assertEquals("¡Tyba golpea al Hombre Caimán por 20 puntos de daño!", tyba.atacar(caiman));
    }

    @Test
    void elSacerdoteNarraDistintoCuandoSeCuraASiMismo() {
        Sacerdote sua = new Sacerdote("Sua", 90, 16, 0, "golpea con su bastón", "el Calor de Sué");
        sua.recibirDanoDirecto(50);

        assertEquals("¡Sua invoca el Calor de Sué y recupera 32 puntos de vida!", sua.ejecutarEspecial(sua));
    }

    @Test
    void cadaSubclaseDiceSuClaseYSiTieneEspecial() {
        Personaje guerrero = new Guerrero("Aragorn", 100, 30, 0);
        Personaje mago = new Mago("Gandalf", 80, 20, 0);
        Personaje sacerdote = new Sacerdote("Elrond", 90, 15, 0);

        assertEquals("Guerrero", guerrero.getClase());
        assertEquals("Mago", mago.getClase());
        assertEquals("Sacerdote", sacerdote.getClase());
        assertFalse(guerrero.tieneHabilidadEspecial());
        assertTrue(mago.tieneHabilidadEspecial());
        assertTrue(sacerdote.tieneHabilidadEspecial());
    }

    @Test
    void laRecargaBajaTurnoATurnoSinPasarDeCero() {
        Personaje mago = new Mago("Gandalf", 80, 20, 0);

        mago.iniciarRecarga(2);
        assertEquals(2, mago.getTurnosDeRecarga());
        mago.avanzarRecarga();
        mago.avanzarRecarga();
        mago.avanzarRecarga();

        assertEquals(0, mago.getTurnosDeRecarga());
    }
}
