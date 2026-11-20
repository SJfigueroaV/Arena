package epicarena.controlador;

import epicarena.modelo.Campana;
import epicarena.modelo.Etapa;
import epicarena.modelo.GestorBatalla;
import epicarena.modelo.IHabilidadEspecial;
import epicarena.modelo.PasoGuiado;
import epicarena.modelo.Personaje;
import epicarena.vista.PanelControles;
import epicarena.vista.PanelEscena;
import epicarena.vista.PanelEstado;
import epicarena.vista.PanelHistorial;
import epicarena.vista.SpriteAnimado;
import epicarena.vista.VentanaBatalla;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;

/**
 * CONTROLADOR ORQUESTADOR (el arbitro de la partida): recibe el Modelo y
 * todas las vistas. Traduce los clics en jugadas del modelo y, tras cada una,
 * sincroniza el tablero, el historial y los controles. No calcula dano ni
 * decide reglas.
 */
public class ControladorBatalla {

    // Momentos de la partida: definen que hace el boton secundario.
    private static final String EN_BATALLA = "EN_BATALLA";
    private static final String ETAPA_SUPERADA = "ETAPA_SUPERADA";
    private static final String DERROTA = "DERROTA";
    private static final String CAMPANA_COMPLETA = "CAMPANA_COMPLETA";
    private static final String PRACTICA_SUPERADA = "PRACTICA_SUPERADA";

    private final Campana campana;
    private final VentanaBatalla ventana;
    private final PanelEstado vistaEstado;
    private final PanelHistorial vistaHistorial;
    private final PanelControles vistaControles;
    private final PanelEscena vistaEscena;

    private GestorBatalla gestor;
    private String estado;
    private int turno;
    /** True durante la batalla de practica guiada. */
    private boolean practicando;
    private List<PasoGuiado> pasos = new ArrayList<>();
    private int pasoActual;
    // La jugada que el jugador va armando con clics: heroe y accion (null = aun no elige).
    private String heroeElegido;
    private String accionElegida;
    /** Posicion del cursor de teclado dentro de los objetivos validos. */
    private int cursor;

    // El constructor recibe a todos los actores
    public ControladorBatalla(Campana campana, VentanaBatalla ventana, PanelEstado vistaEstado,
            PanelHistorial vistaHistorial, PanelControles vistaControles, PanelEscena vistaEscena) {
        this.campana = campana;
        this.ventana = ventana;
        this.vistaEstado = vistaEstado;
        this.vistaHistorial = vistaHistorial;
        this.vistaControles = vistaControles;
        this.vistaEscena = vistaEscena;

        vistaControles.getBtnSecundario().addActionListener(e -> pedirAccionSecundaria());
        vistaControles.getBtnSonido().addActionListener(e -> ventana.alternarSonido());
        vistaControles.getBtnAtaque().addActionListener(e -> elegirAccion(GestorBatalla.ATAQUE_NORMAL));
        vistaControles.getBtnEspecial().addActionListener(e -> elegirAccion(GestorBatalla.HABILIDAD_ESPECIAL));
        vistaControles.getBtnCancelar().addActionListener(e -> cancelarSeleccion());
        registrarTeclado();
        // La jugada se arma tocando a los personajes en la escena.
        vistaEscena.alTocarPersonaje(e -> {
            // Mientras se ven las animaciones del turno anterior, tocar no hace nada.
            if (!vistaEscena.estaAnimando()) {
                tocarPersonaje(e.getActionCommand());
            }
        });
    }

    /** Todo el juego se puede jugar con el teclado. */
    private void registrarTeclado() {
        vistaControles.setAyudaTeclado("Teclado — 1 2 3: héroe · Q: ataque · E: especial · Flechas: objetivo · "
                + "Enter: confirmar · Esc: cancelar · R: reiniciar o saltar práctica · M: sonido");
        ventana.alPresionar("1", e -> teclaDeHeroe(0));
        ventana.alPresionar("2", e -> teclaDeHeroe(1));
        ventana.alPresionar("3", e -> teclaDeHeroe(2));
        ventana.alPresionar("Q", e -> {
            if (puedeUsarTeclado()) {
                elegirAccion(GestorBatalla.ATAQUE_NORMAL);
            }
        });
        ventana.alPresionar("E", e -> {
            if (puedeUsarTeclado()) {
                elegirAccion(GestorBatalla.HABILIDAD_ESPECIAL);
            }
        });
        ventana.alPresionar("LEFT", e -> moverCursor(-1));
        ventana.alPresionar("RIGHT", e -> moverCursor(1));
        ventana.alPresionar("ENTER", e -> confirmar());
        ventana.alPresionar("SPACE", e -> confirmar());
        ventana.alPresionar("ESCAPE", e -> {
            if (puedeUsarTeclado()) {
                cancelarSeleccion();
            }
        });
        ventana.alPresionar("R", e -> {
            if (!ventana.estaEnPrologo() && estado != null && estado.equals(EN_BATALLA)) {
                pedirAccionSecundaria();
            }
        });
        ventana.alPresionar("M", e -> ventana.alternarSonido());
    }

    /** El teclado de la batalla solo responde en su pantalla y cuando no hay animaciones. */
    private boolean puedeUsarTeclado() {
        return !ventana.estaEnPrologo() && gestor != null && estado.equals(EN_BATALLA) && !vistaEscena.estaAnimando();
    }

    /** 1, 2 o 3: el heroe en esa posicion de la escena. */
    private void teclaDeHeroe(int posicion) {
        if (!puedeUsarTeclado() || posicion >= gestor.getEquipoJugador().size()) {
            return;
        }
        elegirHeroe(gestor.getEquipoJugador().get(posicion));
    }

    /** ← o →: mueve el cursor entre los objetivos. Sin accion elegida, usa el ataque normal. */
    private void moverCursor(int direccion) {
        if (!puedeUsarTeclado()) {
            return;
        }
        Personaje heroe = gestor.buscarPorNombre(heroeElegido);
        if (heroe == null) {
            vistaControles.mostrarMensaje("Primero elige un héroe con 1, 2 o 3");
            return;
        }
        if (accionElegida == null) {
            accionElegida = GestorBatalla.ATAQUE_NORMAL;
            cursor = 0;
        } else {
            int total = gestor.objetivosValidos(heroe, accionElegida).size();
            if (total > 0) {
                cursor = (cursor + direccion + total) % total;
            }
        }
        refrescarSeleccion();
    }

    /**
     * Enter o Espacio: juega sobre el objetivo del cursor. Si la pelea ya
     * termino, hace lo del boton pequeno (siguiente etapa, reintentar...).
     */
    private void confirmar() {
        if (ventana.estaEnPrologo() || gestor == null || vistaEscena.estaAnimando()) {
            return;
        }
        if (!estado.equals(EN_BATALLA)) {
            accionSecundaria();
            return;
        }
        Personaje heroe = gestor.buscarPorNombre(heroeElegido);
        if (heroe == null) {
            vistaControles.mostrarMensaje("Primero elige un héroe con 1, 2 o 3");
            return;
        }
        if (accionElegida == null) {
            moverCursor(0);
            return;
        }
        List<Personaje> objetivos = gestor.objetivosValidos(heroe, accionElegida);
        if (!objetivos.isEmpty()) {
            jugar(heroe.getNombre(), accionElegida, objetivos.get(cursor % objetivos.size()).getNombre());
        }
    }

    /** Empieza la campana desde su primera etapa. */
    public void iniciar() {
        practicando = false;
        campana.reiniciar();
        iniciarEtapa();
    }

    /**
     * Empieza la batalla de practica guiada. Si la historia no tiene practica,
     * empieza directamente la campana.
     */
    public void iniciarPractica() {
        if (!campana.tienePractica()) {
            iniciar();
            return;
        }
        practicando = true;
        pasos = campana.getPasosDePractica();
        pasoActual = 0;
        gestor = campana.crearPracticaNueva();
        mostrarBatalla(campana.getPractica(), "Práctica");
        vistaControles.configurarBotonSecundario("Saltar práctica (R)", false);
        mostrarPaso();
    }

    /** Arma la batalla de la etapa actual desde cero y la muestra. */
    private void iniciarEtapa() {
        gestor = campana.crearBatallaActual();
        mostrarBatalla(campana.etapaActual(), "Etapa " + campana.numeroDeEtapa() + " de " + campana.totalDeEtapas());
        vistaControles.configurarBotonSecundario("Reiniciar campaña (R)", false);
        if (gestor.verificarFinDelJuego()) {
            terminarBatalla();
        }
    }

    /** Pone en pantalla la batalla que ya esta en el gestor, con los textos de esa etapa. */
    private void mostrarBatalla(Etapa etapa, String posicion) {
        turno = 0;
        estado = EN_BATALLA;
        vistaEstado.setSubtitulo(campana.getNombre() + "  ·  " + posicion + ": " + etapa.getTitulo());
        vistaEscena.mostrarEscenario(etapa.getEscenario());
        ventana.getSonido().musica(etapa.getEscenario());
        vistaEscena.mostrarPersonajes(nombres(gestor.getEquipoJugador()), nombres(gestor.getEquipoEnemigo()));
        List<Personaje> todos = new ArrayList<>(gestor.getEquipoJugador());
        todos.addAll(gestor.getEquipoEnemigo());
        for (Personaje personaje : todos) {
            vistaEscena.fijarVida(personaje.getNombre(), personaje.getPuntosDeVida(), personaje.getPuntosDeVidaMaximos());
        }
        vistaHistorial.limpiar();
        vistaHistorial.agregar(posicion.toUpperCase() + " — " + etapa.getTitulo().toUpperCase());
        vistaHistorial.agregar(etapa.getIntroduccion());
        heroeElegido = null;
        accionElegida = null;
        sincronizarVistas();
    }

    /** Escribe en el historial que se pide en este paso y por que. */
    private void mostrarPaso() {
        PasoGuiado paso = pasos.get(pasoActual);
        vistaHistorial.agregar("");
        vistaHistorial.agregar("PASO " + (pasoActual + 1) + " DE " + pasos.size() + ": " + paso.resumen());
        vistaHistorial.agregar("Por qué: " + paso.getExplicacion());
        refrescarSeleccion();
    }

    /**
     * Lo que hace el boton pequeno segun el momento: pasar a la etapa
     * siguiente, reintentar la que se perdio o volver a empezar la campana.
     */
    public void accionSecundaria() {
        if (practicando) {
            // Tras ganar o al saltarla, empieza el viaje; si se pierde, se repite.
            if (estado.equals(DERROTA)) {
                iniciarPractica();
            } else {
                iniciar();
            }
            return;
        }
        if (estado.equals(ETAPA_SUPERADA)) {
            campana.avanzar();
        } else if (!estado.equals(DERROTA)) {
            campana.reiniciar();
        }
        iniciarEtapa();
    }

    private void pedirAccionSecundaria() {
        if (estado.equals(EN_BATALLA) && !practicando
                && !ventana.confirmar("¿Abandonar el viaje y volver a la primera etapa?")) {
            return;
        }
        accionSecundaria();
    }

    /**
     * Un turno completo: la jugada del jugador y el contraataque de un monstruo.
     * La jugada llega por nombres (asi la arman los clics) y el controlador
     * busca a los personajes reales en el modelo.
     */
    public void jugar(String nombreHeroe, String accion, String nombreObjetivo) {
        if (gestor == null || !estado.equals(EN_BATALLA)) {
            return;
        }
        Personaje atacante = gestor.buscarPorNombre(nombreHeroe);
        Personaje objetivo = gestor.buscarPorNombre(nombreObjetivo);

        // Validacion temprana: nada vacio ni ilegal antes de jugar
        if (atacante == null || objetivo == null || accion == null
                || !gestor.esHeroe(atacante)
                || !gestor.accionesDisponibles(atacante).contains(accion)
                || !gestor.objetivosValidos(atacante, accion).contains(objetivo)) {
            vistaControles.mostrarMensaje("Esa jugada no es válida");
            return;
        }
        // En la practica solo vale la jugada que pide el paso.
        if (practicando && !pasos.get(pasoActual).coincide(atacante.getNombre(), accion, objetivo.getNombre())) {
            heroeElegido = null;
            accionElegida = null;
            refrescarSeleccion();
            vistaControles.mostrarMensaje("Ahora no. Este paso pide: " + pasos.get(pasoActual).resumen());
            return;
        }
        heroeElegido = null;
        accionElegida = null;

        // 4-5. Jugar y sincronizar historial y tablas
        turno++;
        vistaHistorial.agregar("— Turno " + turno + " —");
        vistaHistorial.agregar(realizarAccion(atacante, objetivo, accion));
        if (cerrarJugada()) {
            return;
        }

        // 6. Contraataque de un monstruo al azar y nueva sincronizacion
        contraataque();
        gestor.terminarTurno();
        if (cerrarJugada()) {
            return;
        }
        if (practicando && pasoActual < pasos.size() - 1) {
            pasoActual++;
            mostrarPaso();
        }
    }

    /**
     * Ejecuta la jugada sin preguntar que clase de personaje es: la JVM elige
     * el atacar() o el ejecutarEspecial() que corresponde a cada subclase.
     */
    private String realizarAccion(Personaje atacante, Personaje objetivo, String accion) {
        boolean especial = accion.equals(GestorBatalla.HABILIDAD_ESPECIAL) && atacante.tieneHabilidadEspecial();
        boolean apoyo = gestor.esAccionDeApoyo(atacante, accion);
        String narrativa;
        if (especial) {
            gestor.registrarEspecialUsada(atacante);
            IHabilidadEspecial habilidad = (IHabilidadEspecial) atacante;
            narrativa = habilidad.ejecutarEspecial(objetivo);
        } else {
            narrativa = atacante.atacar(objetivo);
        }
        animarJugada(atacante, objetivo, especial, apoyo);
        return narrativa;
    }

    /** Pide a la escena la animacion del que actua y la de quien la recibe. */
    private void animarJugada(Personaje atacante, Personaje objetivo, boolean especial, boolean apoyo) {
        if (especial) {
            vistaEscena.animar(atacante.getNombre(), SpriteAnimado.ESPECIAL, apoyo ? "curacion" : "hechizo");
        } else {
            vistaEscena.animar(atacante.getNombre(), SpriteAnimado.ATAQUE, "golpe");
        }
        if (apoyo) {
            vistaEscena.animar(objetivo.getNombre(), SpriteAnimado.CURADO, null, objetivo.getPuntosDeVida());
        } else {
            // La barra baja cuando se ve el golpe, no antes.
            vistaEscena.animar(objetivo.getNombre(), SpriteAnimado.DANO, "dano", objetivo.getPuntosDeVida());
        }
    }

    /**
     * Un monstruo al azar responde; el modelo decide su jugada y su objetivo.
     * En la practica la respuesta esta fijada por el paso, para que se cumpla
     * lo que dice la explicacion.
     */
    private void contraataque() {
        if (practicando) {
            PasoGuiado paso = pasos.get(pasoActual);
            Personaje enemigo = gestor.buscarPorNombre(paso.getEnemigoQueResponde());
            Personaje heroe = gestor.buscarPorNombre(paso.getObjetivoDeLaRespuesta());
            if (enemigo != null && heroe != null) {
                vistaHistorial.agregar(realizarAccion(enemigo, heroe, GestorBatalla.ATAQUE_NORMAL));
                return;
            }
        }
        Personaje monstruo = gestor.elegirEnemigoAlAzar();
        if (monstruo == null) {
            return;
        }
        String accion = gestor.elegirAccionAlAzar(monstruo);
        Personaje objetivo = gestor.elegirObjetivoAlAzar(monstruo, accion);
        if (objetivo == null) {
            return;
        }
        vistaHistorial.agregar(realizarAccion(monstruo, objetivo, accion));
    }

    /**
     * Retira a los derrotados, refresca las vistas y revisa si hay ganador.
     *
     * @return true si la partida termino
     */
    private boolean cerrarJugada() {
        List<Personaje> derrotados = gestor.eliminarDerrotados();
        for (Personaje derrotado : derrotados) {
            vistaEscena.animar(derrotado.getNombre(), SpriteAnimado.CAIDA, "caida");
        }
        sincronizarVistas();
        if (gestor.verificarFinDelJuego()) {
            terminarBatalla();
            return true;
        }
        return false;
    }

    /** Cierra la batalla y deja listo el paso siguiente de la campana. */
    private void terminarBatalla() {
        vistaControles.bloquear();
        if (practicando && gestor.ganoElJugador()) {
            estado = PRACTICA_SUPERADA;
            vistaHistorial.agregar("");
            vistaHistorial.agregar(campana.getPractica().getCierre());
            ventana.getSonido().efecto("victoria");
            vistaControles.mostrarMensaje("¡Práctica superada!");
            vistaControles.configurarBotonSecundario("Comenzar el viaje  (Enter)", true);
            return;
        }
        if (!gestor.ganoElJugador()) {
            estado = DERROTA;
            ventana.getSonido().efecto("derrota");
            vistaHistorial.agregar(campana.getTextoDeDerrota());
            vistaControles.mostrarMensaje("DERROTA");
            vistaControles.configurarBotonSecundario("Reintentar etapa  (Enter)", true);
            return;
        }
        vistaHistorial.agregar(campana.etapaActual().getCierre());
        ventana.getSonido().efecto("victoria");
        if (campana.hayMasEtapas()) {
            estado = ETAPA_SUPERADA;
            vistaControles.mostrarMensaje("¡Etapa superada!");
            vistaControles.configurarBotonSecundario("Siguiente etapa  (Enter)", true);
        } else {
            estado = CAMPANA_COMPLETA;
            vistaHistorial.agregar(campana.getEpilogo());
            vistaControles.mostrarMensaje("¡VICTORIA!");
            vistaControles.configurarBotonSecundario("Jugar de nuevo  (Enter)", true);
        }
    }

    /** Pone las dos tablas, las marcas de la escena y la barra al dia con lo que diga el modelo. */
    private void sincronizarVistas() {
        pintarTabla(vistaEstado.getModeloHeroes(), gestor.getEquipoJugador());
        pintarTabla(vistaEstado.getModeloMonstruos(), gestor.getEquipoEnemigo());
        refrescarSeleccion();
    }

    /** Borra el lienzo viejo y pinta una fila por cada personaje que sigue en combate. */
    private void pintarTabla(DefaultTableModel lienzo, List<Personaje> equipo) {
        lienzo.setRowCount(0);
        for (Personaje personaje : equipo) {
            Object[] fila = {
                personaje.getNombre(),
                personaje.getClase(),
                personaje.getPuntosDeVida() + " / " + personaje.getPuntosDeVidaMaximos(),
                personaje.getPuntosDeAtaque(),
                personaje.getDefensa()
            };
            lienzo.addRow(fila);
        }
    }

    /**
     * Lo que pasa al tocar a un personaje en la escena:
     * - si ya hay heroe y accion, y es un objetivo valido, se juega el turno;
     * - si es un heroe, pasa a ser el elegido;
     * - si es un espanto y hay heroe sin accion, lo ataca con el golpe normal (atajo).
     */
    public void tocarPersonaje(String nombre) {
        if (gestor == null || !estado.equals(EN_BATALLA)) {
            return;
        }
        Personaje tocado = gestor.buscarPorNombre(nombre);
        if (tocado == null) {
            return;
        }
        Personaje heroe = gestor.buscarPorNombre(heroeElegido);
        if (heroe != null && accionElegida != null
                && gestor.objetivosValidos(heroe, accionElegida).contains(tocado)) {
            jugar(heroe.getNombre(), accionElegida, nombre);
            return;
        }
        if (gestor.esHeroe(tocado)) {
            elegirHeroe(tocado);
            return;
        }
        if (heroe == null) {
            vistaControles.mostrarMensaje("Primero toca a uno de tus héroes");
            return;
        }
        if (accionElegida == null) {
            jugar(heroe.getNombre(), GestorBatalla.ATAQUE_NORMAL, nombre);
            return;
        }
        vistaControles.mostrarMensaje("Esa habilidad es para tus compañeros, no para " + tocado.enFrase());
    }

    /** El heroe queda elegido y aparece su barra de habilidades. */
    private void elegirHeroe(Personaje heroe) {
        heroeElegido = heroe.getNombre();
        accionElegida = null;
        cursor = 0;
        List<String> acciones = gestor.accionesDisponibles(heroe);
        if (acciones.size() == 1 && !heroe.tieneHabilidadEspecial()) {
            // Quien solo sabe atacar no necesita elegir: queda listo para tocar al espanto.
            accionElegida = acciones.get(0);
        }
        refrescarSeleccion();
    }

    /** Elige que hace el heroe y marca a quienes puede recibirlo. */
    private void elegirAccion(String accion) {
        Personaje heroe = gestor.buscarPorNombre(heroeElegido);
        if (heroe == null || !gestor.accionesDisponibles(heroe).contains(accion)) {
            return;
        }
        accionElegida = accion;
        cursor = 0;
        refrescarSeleccion();
    }

    private void cancelarSeleccion() {
        heroeElegido = null;
        accionElegida = null;
        refrescarSeleccion();
    }

    /** Pinta las marcas, la barra de habilidades y el mensaje segun lo elegido. */
    private void refrescarSeleccion() {
        vistaEscena.quitarMarcas();
        if (!estado.equals(EN_BATALLA)) {
            return;
        }
        Personaje heroe = gestor.buscarPorNombre(heroeElegido);
        if (heroe == null) {
            heroeElegido = null;
            accionElegida = null;
            vistaControles.ocultarHabilidades();
            if (practicando) {
                vistaEscena.marcar(pasos.get(pasoActual).getAtacante(), PanelEscena.MARCA_SUGERIDO);
            }
            vistaControles.mostrarMensaje(mensajeSinHeroe());
            return;
        }

        vistaEscena.marcar(heroe.getNombre(), PanelEscena.MARCA_ELEGIDO);
        String textoEspecial = null;
        if (heroe.tieneHabilidadEspecial()) {
            // "la Luz de Chía" -> "La Luz de Chía" para el boton
            String especial = heroe.getNombreDelEspecial();
            especial = especial.substring(0, 1).toUpperCase() + especial.substring(1);
            textoEspecial = especial + " · " + heroe.describirEspecial();
            int espera = gestor.turnosParaEspecial(heroe);
            if (espera == 1) {
                textoEspecial = especial + " · lista en 1 turno";
            } else if (espera > 1) {
                textoEspecial = especial + " · lista en " + espera + " turnos";
            }
        }
        if (textoEspecial != null) {
            textoEspecial = "[E] " + textoEspecial;
        }
        vistaControles.mostrarHabilidades(heroe.getNombre(), "[Q] Ataque normal · " + heroe.describirAtaque(),
                textoEspecial, gestor.turnosParaEspecial(heroe) == 0);
        vistaControles.marcarAccion(GestorBatalla.ATAQUE_NORMAL.equals(accionElegida),
                GestorBatalla.HABILIDAD_ESPECIAL.equals(accionElegida));

        if (accionElegida == null) {
            vistaControles.mostrarMensaje("¿Qué hará " + heroe.getNombre() + "? Elige una habilidad o toca a un espanto");
            return;
        }
        boolean apoyo = gestor.esAccionDeApoyo(heroe, accionElegida);
        List<Personaje> objetivos = gestor.objetivosValidos(heroe, accionElegida);
        for (Personaje objetivo : objetivos) {
            vistaEscena.marcar(objetivo.getNombre(), apoyo ? PanelEscena.MARCA_ALIADO : PanelEscena.MARCA_ENEMIGO);
        }
        if (!objetivos.isEmpty()) {
            // El objetivo del cursor (el que se juega con Enter) lleva la marca blanca.
            vistaEscena.marcar(objetivos.get(cursor % objetivos.size()).getNombre(), PanelEscena.MARCA_CURSOR);
        }
        if (apoyo) {
            vistaControles.mostrarMensaje("Toca al compañero que quieres curar");
        } else {
            vistaControles.mostrarMensaje("Toca al espanto que " + heroe.getNombre() + " va a atacar");
        }
    }

    private String mensajeSinHeroe() {
        if (practicando) {
            return "Paso " + (pasoActual + 1) + " de " + pasos.size() + ": " + pasos.get(pasoActual).resumen();
        }
        return "Toca a uno de tus héroes para elegir su jugada";
    }

    private List<String> nombres(List<Personaje> equipo) {
        List<String> nombres = new ArrayList<>();
        for (Personaje personaje : equipo) {
            nombres.add(personaje.getNombre());
        }
        return nombres;
    }
}
