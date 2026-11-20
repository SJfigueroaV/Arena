package epicarena.modelo;

import java.util.ArrayList;
import java.util.List;

/**
 * La historia completa: una serie de etapas que se juegan en orden.
 * Es abstracta porque cada historia decide que personajes pelean en cada etapa.
 */
public abstract class Campana {

    private final String nombre;
    private final String epilogo;
    private final String textoDeDerrota;
    private final List<Etapa> etapas = new ArrayList<>();
    private final List<EscenaDelPrologo> prologo = new ArrayList<>();
    private final List<PasoGuiado> pasosDePractica = new ArrayList<>();
    private Etapa practica;
    private int indiceActual;

    public Campana(String nombre, String epilogo, String textoDeDerrota) {
        this.nombre = nombre;
        this.epilogo = epilogo;
        this.textoDeDerrota = textoDeDerrota;
    }

    /**
     * Arma la batalla de la etapa indicada (contando desde 1) con todos los
     * personajes intactos. Cada subclase pone sus propios heroes y monstruos.
     */
    protected abstract GestorBatalla crearBatalla(int numeroDeEtapa);

    /**
     * Arma la batalla de practica que va despues del prologo. Las historias
     * sin practica no la sobrescriben y retorna null.
     */
    protected GestorBatalla crearBatallaDePractica() {
        return null;
    }

    /** Textos de la practica (titulo, escenario, introduccion y cierre). */
    public void setPractica(Etapa practica) {
        this.practica = practica;
    }

    public Etapa getPractica() {
        return practica;
    }

    public void agregarPasoDePractica(PasoGuiado paso) {
        if (paso == null) {
            throw new IllegalArgumentException("El paso no puede ser nulo");
        }
        pasosDePractica.add(paso);
    }

    public List<PasoGuiado> getPasosDePractica() {
        return new ArrayList<>(pasosDePractica);
    }

    /** True si la historia trae una practica guiada completa. */
    public boolean tienePractica() {
        return practica != null && !pasosDePractica.isEmpty() && crearBatallaDePractica() != null;
    }

    /** Crea desde cero la batalla de practica. */
    public GestorBatalla crearPracticaNueva() {
        return crearBatallaDePractica();
    }

    /** Crea desde cero la batalla de la etapa actual; sirve tambien para reintentarla. */
    public GestorBatalla crearBatallaActual() {
        etapaActual();
        return crearBatalla(numeroDeEtapa());
    }

    public void agregarEtapa(Etapa etapa) {
        if (etapa == null) {
            throw new IllegalArgumentException("La etapa no puede ser nula");
        }
        etapas.add(etapa);
    }

    /** Agrega un cuadro al prologo que se ve antes de la primera etapa. */
    public void agregarEscenaDelPrologo(EscenaDelPrologo escena) {
        if (escena == null) {
            throw new IllegalArgumentException("La escena no puede ser nula");
        }
        prologo.add(escena);
    }

    /** Las escenas del prologo en orden. Puede estar vacia si la historia no tiene prologo. */
    public List<EscenaDelPrologo> getPrologo() {
        return new ArrayList<>(prologo);
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
