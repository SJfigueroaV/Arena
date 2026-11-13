package epicarena.modelo;

public class Sacerdote extends Personaje implements IHabilidadEspecial, ICurable {

    private static final double FACTOR_ATAQUE_NORMAL = 0.6;
    private static final double FACTOR_CURACION = 2.0;

    private final String verboDeAtaque;
    private final String nombreDelEspecial;

    public Sacerdote(String nombre, int puntosDeVida, int puntosDeAtaque, int defensa) {
        this(nombre, puntosDeVida, puntosDeAtaque, defensa, "golpea con su báculo", "Plegaria");
    }

    public Sacerdote(String nombre, int puntosDeVida, int puntosDeAtaque, int defensa,
            String verboDeAtaque, String nombreDelEspecial) {
        super(nombre, puntosDeVida, puntosDeAtaque, defensa);
        this.verboDeAtaque = verboDeAtaque;
        this.nombreDelEspecial = nombreDelEspecial;
    }

    @Override
    public String atacar(Personaje enemigo) {
        int dano = enemigo.recibirDano((int) Math.round(getPuntosDeAtaque() * FACTOR_ATAQUE_NORMAL));
        return narrarGolpe(verboDeAtaque, enemigo, dano);
    }

    /** No hace dano: restaura la salud de un companero (o la propia). */
    @Override
    public String ejecutarEspecial(Personaje objetivo) {
        int vidaAntes = objetivo.getPuntosDeVida();
        objetivo.restaurarSalud((int) Math.round(getPuntosDeAtaque() * FACTOR_CURACION));
        int curado = objetivo.getPuntosDeVida() - vidaAntes;
        String invocacion = getNombre() + " invoca " + nombreDelEspecial;
        if (curado == 0) {
            return invocacion + ", pero la salud de " + objetivo.enFrase() + " ya está completa.";
        }
        if (objetivo == this) {
            return "¡" + invocacion + " y recupera " + curado + " puntos de vida!";
        }
        return "¡" + invocacion + " y le restaura " + curado + " puntos de vida "
                + objetivo.comoObjetivo() + "!";
    }

    @Override
    public boolean especialApuntaAAliado() {
        return true;
    }
}
