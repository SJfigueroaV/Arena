package epicarena.modelo;

import java.util.ArrayList;
import java.util.List;

/** La historia completa: una serie de etapas que se juegan en orden. */
public class Campana {

    private final String nombre;
    private final String epilogo;
    private final String textoDeDerrota;
    private final List<Etapa> etapas = new ArrayList<>();
    private int indiceActual;

    public Campana(String nombre, String epilogo, String textoDeDerrota) {
        this.nombre = nombre;
        this.epilogo = epilogo;
        this.textoDeDerrota = textoDeDerrota;
    }

    public void agregarEtapa(Etapa etapa) {
        if (etapa == null) {
            throw new IllegalArgumentException("La etapa no puede ser nula");
        }
        etapas.add(etapa);
    }

    public Etapa etapaActual() {
        if (etapas.isEmpty()) {
            throw new IllegalStateException("La campaña no tiene etapas");
        }
        return etapas.get(indiceActual);
    }

    /** Numero de la etapa actual contando desde 1. */
    public int numeroDeEtapa() {
        return indiceActual + 1;
    }

    public int totalDeEtapas() {
        return etapas.size();
    }

    public boolean hayMasEtapas() {
        return indiceActual < etapas.size() - 1;
    }

    /** Pasa a la etapa siguiente. En la ultima no hace nada. */
    public void avanzar() {
        if (hayMasEtapas()) {
            indiceActual++;
        }
    }

    public void reiniciar() {
        indiceActual = 0;
    }

    public String getNombre() {
        return nombre;
    }

    /** Texto final al superar la ultima etapa. */
    public String getEpilogo() {
        return epilogo;
    }

    public String getTextoDeDerrota() {
        return textoDeDerrota;
    }
}
