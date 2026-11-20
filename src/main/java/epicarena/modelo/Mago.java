package epicarena.modelo;

public class Mago extends Personaje implements IHabilidadEspecial {

    private static final double FACTOR_ATAQUE_NORMAL = 0.5;
    private static final double FACTOR_ESPECIAL = 2.5;

    private final String verboDeAtaque;
    private final String nombreDelEspecial;

    public Mago(String nombre, int puntosDeVida, int puntosDeAtaque, int defensa) {
        this(nombre, puntosDeVida, puntosDeAtaque, defensa, "lanza una chispa", "Tormenta Arcana");
    }

    public Mago(String nombre, int puntosDeVida, int puntosDeAtaque, int defensa,
            String verboDeAtaque, String nombreDelEspecial) {
        super(nombre, puntosDeVida, puntosDeAtaque, defensa);
        this.verboDeAtaque = verboDeAtaque;
        this.nombreDelEspecial = nombreDelEspecial;
    }

    /** Ataque debil: la mitad de sus puntos de ataque. */
    @Override
    public String atacar(Personaje enemigo) {
        int dano = enemigo.recibirDano((int) Math.round(getPuntosDeAtaque() * FACTOR_ATAQUE_NORMAL));
        return narrarGolpe(verboDeAtaque, enemigo, dano);
    }

    /** Dano masivo que ignora la defensa del objetivo. */
    @Override
    public String ejecutarEspecial(Personaje objetivo) {
        int dano = objetivo.recibirDanoDirecto((int) Math.round(getPuntosDeAtaque() * FACTOR_ESPECIAL));
        return "¡" + getNombre() + " desata " + nombreDelEspecial + " contra " + objetivo.enFrase()
                + ": " + dano + " puntos de daño que ignoran su defensa!" + remate(objetivo);
    }

    @Override
    public boolean especialApuntaAAliado() {
        return false;
    }

    @Override
    public String getClase() {
        return "Mago";
    }

    @Override
    public boolean tieneHabilidadEspecial() {
        return true;
    }

    @Override
    public String describirAtaque() {
        return "débil: la mitad de su fuerza";
    }

    @Override
    public String getNombreDelEspecial() {
        return nombreDelEspecial;
    }

    @Override
    public String describirEspecial() {
        return "2,5 veces su fuerza, ignora la defensa";
    }
}
