package epicarena.modelo;

public class Sacerdote extends Personaje implements IHabilidadEspecial, ICurable {

    private static final double FACTOR_ATAQUE_NORMAL = 0.6;
    private static final double FACTOR_CURACION = 2.0;

    public Sacerdote(String nombre, int puntosDeVida, int puntosDeAtaque, int defensa) {
        super(nombre, puntosDeVida, puntosDeAtaque, defensa);
    }

    @Override
    public String atacar(Personaje enemigo) {
        int dano = enemigo.recibirDano((int) Math.round(getPuntosDeAtaque() * FACTOR_ATAQUE_NORMAL));
        return "¡El Sacerdote " + getNombre() + " golpea con su báculo a " + enemigo.getNombre()
                + " por " + dano + " puntos de daño!" + remate(enemigo);
    }

    /** No hace dano: restaura la salud de un companero. */
    @Override
    public String ejecutarEspecial(Personaje objetivo) {
        int vidaAntes = objetivo.getPuntosDeVida();
        objetivo.restaurarSalud((int) Math.round(getPuntosDeAtaque() * FACTOR_CURACION));
        int curado = objetivo.getPuntosDeVida() - vidaAntes;
        if (curado == 0) {
            return "El Sacerdote " + getNombre() + " reza por " + objetivo.getNombre()
                    + ", pero su salud ya está completa.";
        }
        return "¡El Sacerdote " + getNombre() + " sana a " + objetivo.getNombre()
                + " y le restaura " + curado + " puntos de vida!";
    }

    @Override
    public boolean especialApuntaAAliado() {
        return true;
    }
}
