package epicarena.modelo;

public class Guerrero extends Personaje {

    private final String verboDeAtaque;

    public Guerrero(String nombre, int puntosDeVida, int puntosDeAtaque, int defensa) {
        this(nombre, puntosDeVida, puntosDeAtaque, defensa, "golpea");
    }

    /** El verbo personaliza la narrativa: "muerde", "golpea con su macana"... */
    public Guerrero(String nombre, int puntosDeVida, int puntosDeAtaque, int defensa, String verboDeAtaque) {
        super(nombre, puntosDeVida, puntosDeAtaque, defensa);
        this.verboDeAtaque = verboDeAtaque;
    }

    /** Golpea con el 100% de sus puntos de ataque. */
    @Override
    public String atacar(Personaje enemigo) {
        int dano = enemigo.recibirDano(getPuntosDeAtaque());
        return narrarGolpe(verboDeAtaque, enemigo, dano);
    }

    @Override
    public String getClase() {
        return "Guerrero";
    }

    @Override
    public String describirAtaque() {
        return "100 % de su fuerza";
    }
}
