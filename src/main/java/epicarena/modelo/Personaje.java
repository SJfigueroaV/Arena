package epicarena.modelo;

public abstract class Personaje implements ICurable {

    private final String nombre;
    private final int puntosDeVidaMaximos;
    private final int puntosDeAtaque;
    private final int defensa;
    private int puntosDeVida;
    private boolean estaVivo;

    protected Personaje(String nombre, int puntosDeVida, int puntosDeAtaque, int defensa) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El personaje necesita un nombre");
        }
        if (puntosDeVida <= 0) {
            throw new IllegalArgumentException("Los puntos de vida deben ser mayores que cero");
        }
        this.nombre = nombre;
        this.puntosDeVida = puntosDeVida;
        this.puntosDeVidaMaximos = puntosDeVida;
        this.puntosDeAtaque = Math.max(0, puntosDeAtaque);
        this.defensa = Math.max(0, defensa);
        this.estaVivo = true;
    }

    /** Cada subclase decide como ataca. Retorna la narrativa del ataque. */
    public abstract String atacar(Personaje enemigo);

    /**
     * Aplica un golpe que la defensa puede reducir. Un golpe siempre hace al
     * menos 1 punto para que ningun combate quede bloqueado.
     *
     * @return el dano que realmente se desconto
     */
    public int recibirDano(int cantidad) {
        if (cantidad <= 0) {
            return 0;
        }
        return descontarVida(Math.max(1, cantidad - defensa));
    }

    /** Aplica un golpe que ignora la defensa. */
    public int recibirDanoDirecto(int cantidad) {
        if (cantidad <= 0) {
            return 0;
        }
        return descontarVida(cantidad);
    }

    private int descontarVida(int dano) {
        if (!estaVivo) {
            return 0;
        }
        int aplicado = Math.min(dano, puntosDeVida);
        puntosDeVida -= aplicado;
        if (puntosDeVida == 0) {
            estaVivo = false;
        }
        return aplicado;
    }

    /** Recupera vida sin pasar del maximo. Un personaje derrotado no se puede curar. */
    @Override
    public void restaurarSalud(int cantidad) {
        if (!estaVivo || cantidad <= 0) {
            return;
        }
        puntosDeVida = Math.min(puntosDeVidaMaximos, puntosDeVida + cantidad);
    }

    /** Texto que se agrega a la narrativa cuando el golpe derrota al rival. */
    protected String remate(Personaje enemigo) {
        return enemigo.estaVivo() ? "" : " ¡" + enemigo.getNombre() + " ha sido derrotado!";
    }

    public String getNombre() {
        return nombre;
    }

    public int getPuntosDeVida() {
        return puntosDeVida;
    }

    public int getPuntosDeVidaMaximos() {
        return puntosDeVidaMaximos;
    }

    public int getPuntosDeAtaque() {
        return puntosDeAtaque;
    }

    public int getDefensa() {
        return defensa;
    }

    public boolean estaVivo() {
        return estaVivo;
    }

    /** Nombre de la clase del personaje, para mostrarlo en pantalla. */
    public String getClase() {
        return getClass().getSimpleName();
    }

    @Override
    public String toString() {
        return nombre;
    }
}
