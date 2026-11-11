package epicarena.modelo;

public enum TipoAccion {

    ATAQUE_NORMAL("Ataque Normal"),
    HABILIDAD_ESPECIAL("Habilidad Especial");

    private final String etiqueta;

    TipoAccion(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}
