package epicarena.modelo;

public class Guerrero extends Personaje {

    public Guerrero(String nombre, int puntosDeVida, int puntosDeAtaque, int defensa) {
        super(nombre, puntosDeVida, puntosDeAtaque, defensa);
    }

    /** Golpea con el 100% de sus puntos de ataque. */
    @Override
    public String atacar(Personaje enemigo) {
        int dano = enemigo.recibirDano(getPuntosDeAtaque());
        return "¡El Guerrero " + getNombre() + " golpea a " + enemigo.getNombre()
                + " por " + dano + " puntos de daño!" + remate(enemigo);
    }
}
