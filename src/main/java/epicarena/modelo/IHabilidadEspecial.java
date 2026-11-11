package epicarena.modelo;

public interface IHabilidadEspecial {

    /** Ejecuta la habilidad sobre el objetivo y retorna la narrativa de lo ocurrido. */
    String ejecutarEspecial(Personaje objetivo);

    /** Indica si la habilidad se usa sobre un companero (true) o sobre un rival (false). */
    boolean especialApuntaAAliado();
}
