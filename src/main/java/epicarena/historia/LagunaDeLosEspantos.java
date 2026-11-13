package epicarena.historia;

import epicarena.modelo.Campana;
import epicarena.modelo.Etapa;
import epicarena.modelo.GestorBatalla;
import epicarena.modelo.Guerrero;
import epicarena.modelo.Mago;
import epicarena.modelo.Sacerdote;

/**
 * Contenido de la campana: textos, enemigos y estadisticas de cada etapa.
 * No tiene reglas; solo arma objetos del modelo.
 */
public final class LagunaDeLosEspantos {

    /** Cuanto crecen la vida y el ataque de los heroes con cada etapa superada. */
    private static final double CRECIMIENTO_POR_ETAPA = 0.08;

    private LagunaDeLosEspantos() {
    }

    public static Campana crear() {
        Campana campana = new Campana(
                "La Laguna de los Espantos",
                "Tyba baja al fondo y deja el tunjo en el barro. El agua empieza a subir, sin prisa, "
                + "como si nunca se hubiera ido. En el monte, en el río y en el páramo los espantos "
                + "se quedan quietos. No se fueron. Volvieron a su sitio.",
                "Los tres caen y el tunjo rueda por el suelo. La laguna espera. Siempre ha sabido esperar.");

        campana.agregarEtapa(new Etapa(
                "El páramo de niebla",
                "Durante siglos el oro arrojado a la laguna mantuvo dormido lo que vive en el monte, "
                + "en el río y en la noche. Hasta que abrieron un tajo para vaciarla. Tres muiscas "
                + "cargan el último tunjo de vuelta.\n"
                + "El camino empieza arriba, entre frailejones, donde la niebla no deja ver la mano "
                + "propia. Algo arde a lo lejos. No es una fogata.",
                "La niebla se abre. Las ánimas no murieron: dejaron de vagar. Abajo espera el monte.",
                LagunaDeLosEspantos::paramo));

        campana.agregarEtapa(new Etapa(
                "La selva que camina",
                "Bajando del páramo el monte se cierra. Los árboles parecen moverse cuando nadie "
                + "los mira. Una voz de mujer pide ayuda entre la maleza.",
                "La Madremonte se hunde en la hojarasca y el monte vuelve a respirar. "
                + "Se oye agua: el río grande está cerca.",
                LagunaDeLosEspantos::selva));

        campana.agregarEtapa(new Etapa(
                "El río grande",
                "El Magdalena baja crecido y oscuro. En la orilla alguien fuma un tabaco sentado en "
                + "una piedra, con el pelo hasta la cintura. Los pescadores ya no salen de noche.",
                "El Mohán se sumerge sin decir nada y deja la canoa libre. "
                + "Al otro lado se ven las luces de un pueblo. Ninguna se mueve.",
                LagunaDeLosEspantos::rio));

        campana.agregarEtapa(new Etapa(
                "El pueblo a medianoche",
                "Las puertas están trancadas y nadie contesta. Suena un silbido muy lejos. "
                + "Sua se detiene: cuando el Silbón suena lejos, es que está cerca.",
                "El llanto se apaga calle abajo. Amanece, y sobre los tejados se ve la montaña "
                + "con su herida: el tajo de la laguna.",
                LagunaDeLosEspantos::pueblo));

        campana.agregarEtapa(new Etapa(
                "La laguna herida",
                "El tajo sigue abierto en la montaña como una cicatriz. En el barro del fondo hay "
                + "un hombre de pie, cubierto de oro de la cabeza a los pies. Lleva cuatrocientos "
                + "años ahí. No puede morir y no puede dejar de buscar.",
                "El oro se le desprende de la piel como ceniza. Debajo solo quedaba un hombre cansado.",
                LagunaDeLosEspantos::laguna));

        return campana;
    }

    private static GestorBatalla paramo() {
        GestorBatalla batalla = conHeroes(1);
        batalla.agregarEnemigo(new Guerrero("Ánima en Pena", 70, 18, 2, "golpea con sus cadenas"));
        batalla.agregarEnemigo(new Guerrero("Ánima Errante", 70, 18, 2, "atraviesa con su frío"));
        batalla.agregarEnemigo(new Mago("La Candileja", 60, 22, 0, "chamusca", "su Llamarada"));
        return batalla;
    }

    private static GestorBatalla selva() {
        GestorBatalla batalla = conHeroes(2);
        batalla.agregarEnemigo(new Guerrero("La Patasola", 140, 34, 4, "desgarra con sus colmillos"));
        batalla.agregarEnemigo(new Sacerdote("La Madremonte", 120, 20, 6, "azota con sus ramas", "la Savia del Monte"));
        batalla.agregarEnemigo(new Mago("El Sombrerón", 80, 26, 2, "azota con sus cadenas", "su Jauría Negra"));
        return batalla;
    }

    private static GestorBatalla rio() {
        GestorBatalla batalla = conHeroes(3);
        batalla.agregarEnemigo(new Guerrero("El Hombre Caimán", 195, 32, 9, "muerde"));
        batalla.agregarEnemigo(new Mago("El Mohán", 110, 33, 3, "ahoga en humo de tabaco", "la Creciente del Río"));
        batalla.agregarEnemigo(new Sacerdote("La Madre de Agua", 110, 21, 4, "salpica con agua helada", "el Remanso"));
        return batalla;
    }

    private static GestorBatalla pueblo() {
        GestorBatalla batalla = conHeroes(4);
        batalla.agregarEnemigo(new Mago("La Llorona", 110, 34, 2, "araña", "su Lamento"));
        batalla.agregarEnemigo(new Guerrero("El Silbón", 160, 36, 6, "golpea con su costal de huesos"));
        batalla.agregarEnemigo(new Sacerdote("El Cura sin Cabeza", 120, 22, 5, "golpea con su cirio", "la Bendición sin Voz"));
        batalla.agregarEnemigo(new Guerrero("Ánima Sola", 80, 24, 2, "quema con sus llamas"));
        return batalla;
    }

    private static GestorBatalla laguna() {
        GestorBatalla batalla = conHeroes(5);
        batalla.agregarEnemigo(new Guerrero("El Dorado", 300, 41, 10, "aplasta con su puño de oro"));
        batalla.agregarEnemigo(new Sacerdote("El Fraile de la Codicia", 135, 23, 5, "golpea con su cofre", "la Absolución Comprada"));
        batalla.agregarEnemigo(new Mago("El Alquimista", 110, 35, 2, "arroja un frasco", "el Barro Vivo"));
        return batalla;
    }

    /** Los tres heroes, mas fuertes cuanto mas avanzada es la etapa. */
    private static GestorBatalla conHeroes(int numeroDeEtapa) {
        int superadas = numeroDeEtapa - 1;
        double factor = 1 + CRECIMIENTO_POR_ETAPA * superadas;
        int defensaExtra = superadas / 2;

        GestorBatalla batalla = new GestorBatalla();
        batalla.agregarHeroe(new Guerrero("Tyba", crecer(120, factor), crecer(28, factor), 6 + defensaExtra,
                "golpea con su macana"));
        batalla.agregarHeroe(new Mago("Chie", crecer(75, factor), crecer(24, factor), 2 + defensaExtra,
                "lanza un destello", "la Luz de Chía"));
        batalla.agregarHeroe(new Sacerdote("Sua", crecer(90, factor), crecer(16, factor), 4 + defensaExtra,
                "golpea con su bastón", "el Calor de Sué"));
        return batalla;
    }

    private static int crecer(int base, double factor) {
        return (int) Math.round(base * factor);
    }
}
