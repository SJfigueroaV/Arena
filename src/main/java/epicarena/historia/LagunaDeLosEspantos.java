package epicarena.historia;

import epicarena.modelo.Campana;
import epicarena.modelo.EscenaDelPrologo;
import epicarena.modelo.Etapa;
import epicarena.modelo.GestorBatalla;
import epicarena.modelo.Guerrero;
import epicarena.modelo.Mago;
import epicarena.modelo.PasoGuiado;
import epicarena.modelo.Sacerdote;
import java.util.ArrayList;
import java.util.List;

/**
 * Contenido de la campana: textos, enemigos y estadisticas de cada etapa.
 * No tiene reglas; solo arma objetos del modelo.
 */
public class LagunaDeLosEspantos extends Campana {

    /** Cuanto crecen la vida y el ataque de los heroes con cada etapa superada. */
    private static final double CRECIMIENTO_POR_ETAPA = 0.08;

    public LagunaDeLosEspantos() {
        super("La Laguna de los Espantos",
                "Tyba baja al fondo y deja el tunjo en el barro. El agua empieza a subir, sin prisa, "
                + "como si nunca se hubiera ido. En el monte, en el río y en el páramo los espantos "
                + "se quedan quietos. No se fueron. Volvieron a su sitio.",
                "Los tres caen y el tunjo rueda por el suelo. La laguna espera. Siempre ha sabido esperar.");

        agregarPrologo();
        agregarPractica();

        agregarEtapa(new Etapa(
                "El páramo de niebla", "paramo",
                "El camino empieza arriba, entre frailejones, donde la niebla no deja ver la mano "
                + "propia. Algo arde a lo lejos. No es una fogata.",
                "La niebla se abre. Las ánimas no murieron: dejaron de vagar. Abajo espera el monte."));

        agregarEtapa(new Etapa(
                "La selva que camina", "selva",
                "Bajando del páramo el monte se cierra. Los árboles parecen moverse cuando nadie "
                + "los mira. Una voz de mujer pide ayuda entre la maleza.",
                "La Madremonte se hunde en la hojarasca y el monte vuelve a respirar. "
                + "Se oye agua: el río grande está cerca."));

        agregarEtapa(new Etapa(
                "El río grande", "rio",
                "El Magdalena baja crecido y oscuro. En la orilla alguien fuma un tabaco sentado en "
                + "una piedra, con el pelo hasta la cintura. Los pescadores ya no salen de noche.",
                "El Mohán se sumerge sin decir nada y deja la canoa libre. "
                + "Al otro lado se ven las luces de un pueblo. Ninguna se mueve."));

        agregarEtapa(new Etapa(
                "El pueblo a medianoche", "pueblo",
                "Las puertas están trancadas y nadie contesta. Suena un silbido muy lejos. "
                + "Sua se detiene: cuando el Silbón suena lejos, es que está cerca.",
                "El llanto se apaga calle abajo. Amanece, y sobre los tejados se ve la montaña "
                + "con su herida: el tajo de la laguna."));

        agregarEtapa(new Etapa(
                "La laguna herida", "laguna",
                "El tajo sigue abierto en la montaña como una cicatriz. En el barro del fondo hay "
                + "un hombre de pie, cubierto de oro de la cabeza a los pies. Lleva cuatrocientos "
                + "años ahí. No puede morir y no puede dejar de buscar.",
                "El oro se le desprende de la piel como ceniza. Debajo solo quedaba un hombre cansado."));
    }

    /** Las cinco escenas que cuentan por que despertaron los espantos. */
    private void agregarPrologo() {
        List<String> espantos = List.of("La Candileja", "La Patasola", "El Mohán", "La Llorona", "El Silbón");
        List<String> heroes = List.of("Tyba", "Chie", "Sua");
        List<String> nadie = new ArrayList<>();

        EscenaDelPrologo ofrenda = new EscenaDelPrologo(
                "Durante siglos, los muiscas entregaron oro a la laguna de Guatavita. En las noches de "
                + "ofrenda, el cacique cubierto de oro navegaba hasta el centro en una balsa y dejaba "
                + "caer los tunjos al agua.\nNo era riqueza. Era un pago.",
                "ofrenda");
        ofrenda.setTunjosPorLanzar(3);
        ofrenda.setInstruccion("Esta noche tú haces la ofrenda: lanza tres tunjos a la laguna.");
        agregarEscenaDelPrologo(ofrenda);

        EscenaDelPrologo pacto = new EscenaDelPrologo(
                "Cada tunjo que tocaba el fondo mantenía dormido lo que vive en el monte, en el río y "
                + "en la noche. Mientras el pacto se cumpliera, los espantos no despertarían.",
                "selva", nadie, espantos, EscenaDelPrologo.DORMIDOS);
        pacto.setInstruccion("Toca a un espanto dormido para escuchar su leyenda.");
        pacto.agregarTextoAlTocar("La Candileja", "La Candileja: una bola de fuego que persigue a los "
                + "caminantes en la noche. Dicen que dentro arde la cara de una anciana condenada a vagar.");
        pacto.agregarTextoAlTocar("La Patasola", "La Patasola: aparece en el monte como una mujer hermosa "
                + "que pide ayuda. Cuando el cazador se acerca, muestra su única pierna y sus colmillos.");
        pacto.agregarTextoAlTocar("El Mohán", "El Mohán: señor del río, peludo y hechicero. Fuma tabaco "
                + "sobre las piedras, guarda el oro del fondo y voltea las canoas de quienes pescan de noche.");
        pacto.agregarTextoAlTocar("La Llorona", "La Llorona: recorre las orillas buscando lo que perdió. "
                + "Quien escucha su lamento de cerca no vuelve a dormir tranquilo.");
        pacto.agregarTextoAlTocar("El Silbón", "El Silbón: carga en un costal los huesos de su padre. "
                + "Cuando su silbido suena lejos, es que está cerca.");
        agregarEscenaDelPrologo(pacto);

        EscenaDelPrologo tajo = new EscenaDelPrologo(
                "Entonces llegaron hombres que no ofrecían. Solo tomaban. Abrieron un tajo en el borde "
                + "de la laguna para vaciarla y sacar el oro del fondo.",
                "tajo");
        tajo.setEfectoAlEntrar("tajo");
        tajo.setInstruccion("Escucha: los picos ya están abriendo la montaña.");
        agregarEscenaDelPrologo(tajo);

        EscenaDelPrologo despertar = new EscenaDelPrologo(
                "El agua bajó. El pacto se rompió.\nY todo lo que dormía despertó.",
                "laguna", nadie, espantos, EscenaDelPrologo.DESPIERTAN);
        agregarEscenaDelPrologo(despertar);

        EscenaDelPrologo partida = new EscenaDelPrologo(
                "Queda un solo tunjo sin entregar. Lo guardan tres muiscas: Tyba, el guerrero que lo "
                + "carga; Chie, hechicera de la luna; y Sua, sacerdote del sol.\nSi el tunjo llega al "
                + "fondo de la laguna, el pacto se renueva. El camino atraviesa todo lo que despertó.",
                "paramo", heroes, nadie, EscenaDelPrologo.QUIETOS);
        partida.setInstruccion("Toca a cada héroe: te contará cómo pelea y te lo mostrará.");
        partida.setHayQueTocarATodos(true);
        partida.agregarTextoAlTocar("Tyba", "Tyba: «Yo no hago magia. Golpeo con todo lo que tengo.»\n"
                + "Guerrero. Su ataque usa el 100 % de su fuerza en cada turno. No tiene habilidad especial, "
                + "pero es quien más vida tiene y quien más daño hace turno a turno. La defensa del rival "
                + "le resta un poco a cada golpe.", "golpe");
        partida.agregarTextoAlTocar("Chie", "Chie: «La luz de la luna atraviesa cualquier armadura.»\n"
                + "Maga. Su ataque normal es débil: la mitad de su fuerza. Su habilidad especial, la Luz de "
                + "Chía, hace dos veces y media su fuerza e ignora la defensa del enemigo. Después de usarla "
                + "hay que esperar 2 turnos para repetirla.", "hechizo");
        partida.agregarTextoAlTocar("Sua", "Sua: «No vamos a matarlos. Vamos a devolverlos a su sitio.»\n"
                + "Sacerdote. Su habilidad especial, el Calor de Sué, no hace daño: le devuelve a un compañero, "
                + "o a él mismo, el doble de su fuerza en vida, sin pasar del máximo. También hay que esperar "
                + "2 turnos para repetirla. Úsala antes de que alguien caiga.", "curacion");
        agregarEscenaDelPrologo(partida);
    }

    /**
     * La practica guiada: seis turnos contra dos sombras de niebla. Las cifras de
     * cada explicacion salen de las estadisticas de la etapa 1 y las respuestas
     * del enemigo estan fijadas, asi que siempre se cumplen.
     */
    private void agregarPractica() {
        String silbon = "Sombra del Silbón";
        String madremonte = "Sombra de la Madremonte";
        String normal = GestorBatalla.ATAQUE_NORMAL;
        String especial = GestorBatalla.HABILIDAD_ESPECIAL;

        setPractica(new Etapa("Entre la niebla", "paramo",
                "Antes de bajar, Sua levanta la niebla del páramo y le da forma: la sombra del Silbón y la "
                + "sombra de la Madremonte. «No son ellos», dice, «pero pelean igual. Les diré qué hacer en "
                + "cada turno y por qué.» Para jugar: toca a un héroe, elige su habilidad abajo y toca a su "
                + "objetivo. Haz exactamente la jugada que pide cada paso.",
                "Las sombras se deshacen en niebla. Sua asiente: «Así se pelea. Primero el que cura, la luna "
                + "contra la armadura y el sol antes de que alguien caiga. Ahora sí, bajemos.»"));

        agregarPasoDePractica(new PasoGuiado("Tyba", normal, madremonte,
                "Primero el que cura. La Madremonte puede devolverle vida a su compañero y deshacer tu daño. "
                + "Tyba da el golpe normal más fuerte (28) y ella solo tiene 2 de defensa: perderá 26 de sus 40 "
                + "puntos de vida.", silbon, "Chie"));
        agregarPasoDePractica(new PasoGuiado("Tyba", normal, madremonte,
                "Remátala. Le quedan 14 puntos de vida y otro golpe de Tyba la derriba. Un enemigo herido cura "
                + "igual que uno sano: no lo dejes a medias.", silbon, "Chie"));
        agregarPasoDePractica(new PasoGuiado("Chie", especial, silbon,
                "La sombra del Silbón tiene 8 de defensa: a cada golpe normal le quita 8 puntos. La Luz de Chía "
                + "ignora la defensa y le hace 60 de daño de una vez. Después Chie tendrá que esperar 2 turnos "
                + "para repetirla.", silbon, "Chie"));
        agregarPasoDePractica(new PasoGuiado("Sua", especial, "Chie",
                "Chie quedó con 21 puntos de vida: otro golpe la deja casi en el suelo, y si cae pierdes tu "
                + "arma contra la armadura. El Calor de Sué le devuelve 32 puntos. Cura antes de que alguien "
                + "caiga, no después.", silbon, "Tyba"));
        agregarPasoDePractica(new PasoGuiado("Tyba", normal, silbon,
                "La Luz de Chía todavía se está recargando. Tyba es quien más daño hace con un golpe normal: "
                + "28 menos los 8 de defensa, 20 puntos. A la sombra le quedarán 10.", silbon, "Sua"));
        agregarPasoDePractica(new PasoGuiado("Tyba", normal, silbon,
                "Le quedan 10 puntos de vida. Un golpe más de Tyba termina la práctica.", null, null));
    }

    @Override
    protected GestorBatalla crearBatallaDePractica() {
        GestorBatalla batalla = conHeroes(1);
        batalla.agregarEnemigo(new Guerrero("Sombra del Silbón", 90, 20, 8, "golpea con su costal de niebla"));
        batalla.agregarEnemigo(new Sacerdote("Sombra de la Madremonte", 40, 14, 2, "azota con ramas de niebla",
                "la Savia de Niebla"));
        return batalla;
    }

    /** Los monstruos de cada etapa; los heroes crecen con cada etapa superada. */
    @Override
    protected GestorBatalla crearBatalla(int numeroDeEtapa) {
        switch (numeroDeEtapa) {
            case 1:
                return paramo();
            case 2:
                return selva();
            case 3:
                return rio();
            case 4:
                return pueblo();
            case 5:
                return laguna();
            default:
                throw new IllegalArgumentException("No existe la etapa " + numeroDeEtapa);
        }
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
