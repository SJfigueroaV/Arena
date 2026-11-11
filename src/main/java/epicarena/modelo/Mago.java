package epicarena.modelo;

public class Mago extends Personaje implements IHabilidadEspecial {

    private static final double FACTOR_ATAQUE_NORMAL = 0.5;
    private static final double FACTOR_ESPECIAL = 2.5;

    public Mago(String nombre, int puntosDeVida, int puntosDeAtaque, int defensa) {
        super(nombre, puntosDeVida, puntosDeAtaque, defensa);
    }

    /** Ataque debil: la mitad de sus puntos de ataque. */
    @Override
    public String atacar(Personaje enemigo) {
        int dano = enemigo.recibirDano((int) Math.round(getPuntosDeAtaque() * FACTOR_ATAQUE_NORMAL));
        return "¡El Mago " + getNombre() + " lanza una chispa a " + enemigo.getNombre()
                + " por " + dano + " puntos de daño!" + remate(enemigo);
    }

    /** Dano masivo que ignora la defensa del objetivo. */
    @Override
    public String ejecutarEspecial(Personaje objetivo) {
        int dano = objetivo.recibirDanoDirecto((int) Math.round(getPuntosDeAtaque() * FACTOR_ESPECIAL));
        return "¡El Mago " + getNombre() + " desata una tormenta arcana sobre " + objetivo.getNombre()
                + " por " + dano + " puntos de daño que ignoran su defensa!" + remate(objetivo);
    }

    @Override
    public boolean especialApuntaAAliado() {
        return false;
    }
}
