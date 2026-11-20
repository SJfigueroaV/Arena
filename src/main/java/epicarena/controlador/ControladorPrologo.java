package epicarena.controlador;

import epicarena.modelo.Campana;
import epicarena.modelo.EscenaDelPrologo;
import epicarena.vista.PanelEscena;
import epicarena.vista.PanelPrologo;
import epicarena.vista.Sonido;
import epicarena.vista.SpriteAnimado;
import epicarena.vista.VentanaBatalla;
import java.util.ArrayList;
import java.util.List;

/**
 * CONTROLADOR DEL PROLOGO: muestra las escenas de la historia una por una y,
 * al terminar o al saltarlas, le pasa la partida al ControladorBatalla.
 */
public class ControladorPrologo {

    private final Campana campana;
    private final VentanaBatalla ventana;
    private final PanelPrologo vistaPrologo;
    private final ControladorBatalla controladorBatalla;
    private final Sonido sonido;
    private int escenaActual;
    private int tunjosLanzados;
    /** A quienes ya toco el jugador en la escena actual. */
    private final List<String> escuchados = new ArrayList<>();
    /** Personaje bajo el cursor de teclado (-1 = ninguno). */
    private int cursor = -1;

    public ControladorPrologo(Campana campana, VentanaBatalla ventana, PanelPrologo vistaPrologo,
            ControladorBatalla controladorBatalla) {
        this.campana = campana;
        this.ventana = ventana;
        this.vistaPrologo = vistaPrologo;
        this.controladorBatalla = controladorBatalla;
        this.sonido = ventana.getSonido();

        vistaPrologo.getBtnSiguiente().addActionListener(e -> avanzar());
        vistaPrologo.getBtnSaltar().addActionListener(e -> terminar());
        vistaPrologo.getBtnSonido().addActionListener(e -> ventana.alternarSonido());
        vistaPrologo.getEscena().alTocarPersonaje(e -> tocarPersonaje(e.getActionCommand()));

        // Teclado: Enter o Espacio avanzan, las flechas recorren a los personajes, Esc salta.
        ventana.alPresionar("ENTER", e -> {
            if (ventana.estaEnPrologo() && vistaPrologo.getBtnSiguiente().isEnabled()) {
                avanzar();
            }
        });
        ventana.alPresionar("SPACE", e -> {
            if (ventana.estaEnPrologo() && vistaPrologo.getBtnSiguiente().isEnabled()) {
                avanzar();
            }
        });
        ventana.alPresionar("LEFT", e -> moverCursor(-1));
        ventana.alPresionar("RIGHT", e -> moverCursor(1));
        ventana.alPresionar("ESCAPE", e -> {
            if (ventana.estaEnPrologo()) {
                terminar();
            }
        });
    }

    /** Muestra la primera escena. Si la historia no tiene prologo, empieza la batalla. */
    public void iniciar() {
        if (campana.getPrologo().isEmpty()) {
            terminar();
            return;
        }
        ventana.mostrarPrologo();
        vistaPrologo.setTitulo(campana.getNombre());
        sonido.musica("prologo");
        escenaActual = 0;
        mostrarEscena();
    }

    /**
     * Primer clic: si el texto aun se esta escribiendo, lo completa.
     * Si la escena pide una ofrenda, cada clic lanza un tunjo.
     * Despues: pasa a la escena siguiente o, en la ultima, empieza el juego.
     */
    public void avanzar() {
        if (vistaPrologo.estaEscribiendo()) {
            vistaPrologo.completarTexto();
            return;
        }
        EscenaDelPrologo escena = campana.getPrologo().get(escenaActual);
        if (tunjosLanzados < escena.getTunjosPorLanzar()) {
            lanzarTunjo(escena);
            return;
        }
        if (!faltanPorEscuchar(escena).isEmpty()) {
            return;                  // todavia no escucho a todos
        }
        escenaActual++;
        if (escenaActual >= campana.getPrologo().size()) {
            terminar();
        } else {
            mostrarEscena();
        }
    }

    /** Deja el prologo y arranca la practica guiada (y despues la primera etapa). */
    public void terminar() {
        vistaPrologo.completarTexto();
        ventana.mostrarBatalla();
        controladorBatalla.iniciarPractica();
    }

    /** ← o →: pasa al personaje siguiente de la escena y lo escucha, como si lo tocara. */
    private void moverCursor(int direccion) {
        if (!ventana.estaEnPrologo()) {
            return;
        }
        List<String> tocables = campana.getPrologo().get(escenaActual).getNombresConTexto();
        if (tocables.isEmpty()) {
            return;
        }
        if (cursor < 0) {
            cursor = direccion > 0 ? 0 : tocables.size() - 1;
        } else {
            cursor = (cursor + direccion + tocables.size()) % tocables.size();
        }
        PanelEscena vistaEscena = vistaPrologo.getEscena();
        vistaEscena.quitarMarcas();
        vistaEscena.marcar(tocables.get(cursor), PanelEscena.MARCA_CURSOR);
        tocarPersonaje(tocables.get(cursor));
    }

    /** Lo que pasa cuando el jugador toca a un personaje en la escena. */
    public void tocarPersonaje(String nombre) {
        EscenaDelPrologo escena = campana.getPrologo().get(escenaActual);
        String texto = escena.textoAlTocar(nombre);
        if (texto == null) {
            return;
        }
        if (!escuchados.contains(nombre)) {
            escuchados.add(nombre);
        }
        String efecto = escena.efectoAlTocar(nombre);
        PanelEscena vistaEscena = vistaPrologo.getEscena();
        if (efecto == null) {
            sonido.efecto("leyenda");
        } else if (!vistaEscena.estaAnimando()) {
            // Demostracion: el heroe usa su habilidad (o su ataque, si no tiene).
            vistaEscena.animar(nombre, SpriteAnimado.ESPECIAL, efecto);
        }
        vistaPrologo.escribir(texto);
        if (escena.hayQueTocarATodos()) {
            List<String> faltan = faltanPorEscuchar(escena);
            if (faltan.isEmpty()) {
                vistaPrologo.setInstruccion("Ya conoces a los tres. Cuando quieras, comienza el viaje.");
            } else {
                vistaPrologo.setInstruccion("Falta escuchar a: " + String.join(" y ", faltan) + ".");
            }
            actualizarBotonSiguiente(escena);
        }
    }

    /** Quienes tienen algo que contar y el jugador aun no toco. Vacia si la escena no lo exige. */
    private List<String> faltanPorEscuchar(EscenaDelPrologo escena) {
        List<String> faltan = new ArrayList<>();
        if (!escena.hayQueTocarATodos()) {
            return faltan;
        }
        for (String nombre : escena.getNombresConTexto()) {
            if (!escuchados.contains(nombre)) {
                faltan.add(nombre);
            }
        }
        return faltan;
    }

    /** Cada tunjo cae al agua, suena y deja la laguna un poco mas dorada. */
    private void lanzarTunjo(EscenaDelPrologo escena) {
        tunjosLanzados++;
        sonido.efecto("salpicadura");
        vistaPrologo.getEscena().mostrarEscenario(escena.getEscenario() + "_" + tunjosLanzados);
        int faltan = escena.getTunjosPorLanzar() - tunjosLanzados;
        if (faltan > 0) {
            vistaPrologo.setInstruccion("Tunjos entregados: " + tunjosLanzados + " de " + escena.getTunjosPorLanzar() + ".");
        } else {
            vistaPrologo.setInstruccion("La ofrenda está completa. La laguna la recibe.");
        }
        actualizarBotonSiguiente(escena);
    }

    private void actualizarBotonSiguiente(EscenaDelPrologo escena) {
        List<String> faltan = faltanPorEscuchar(escena);
        vistaPrologo.setSiguienteHabilitado(faltan.isEmpty());
        if (!faltan.isEmpty()) {
            int total = escena.getNombresConTexto().size();
            vistaPrologo.setTextoSiguiente("Escucha a los héroes (" + (total - faltan.size()) + " de " + total + ")");
        } else if (tunjosLanzados < escena.getTunjosPorLanzar()) {
            vistaPrologo.setTextoSiguiente("Lanzar un tunjo");
        } else if (escenaActual == campana.getPrologo().size() - 1) {
            vistaPrologo.setTextoSiguiente("Comenzar el viaje");
        } else {
            vistaPrologo.setTextoSiguiente("Siguiente");
        }
    }

    private void mostrarEscena() {
        List<EscenaDelPrologo> prologo = campana.getPrologo();
        EscenaDelPrologo escena = prologo.get(escenaActual);
        PanelEscena vistaEscena = vistaPrologo.getEscena();
        tunjosLanzados = 0;
        escuchados.clear();
        cursor = -1;

        vistaEscena.mostrarEscenario(escena.getEscenario());
        vistaEscena.mostrarPersonajes(escena.getHeroes(), escena.getEspantos());
        String estado = escena.getEstadoDeLosEspantos();
        if (estado.equals(EscenaDelPrologo.DORMIDOS) || estado.equals(EscenaDelPrologo.DESPIERTAN)) {
            for (String espanto : escena.getEspantos()) {
                vistaEscena.dormir(espanto);
            }
        }
        if (estado.equals(EscenaDelPrologo.DESPIERTAN)) {
            // Uno tras otro, cada espanto despierta con su habilidad.
            for (String espanto : escena.getEspantos()) {
                vistaEscena.animar(espanto, SpriteAnimado.ESPECIAL, "despertar");
            }
        }

        sonido.efecto(escena.getEfectoAlEntrar());
        vistaPrologo.escribir(escena.getTexto());
        vistaPrologo.setInstruccion(escena.getInstruccion());
        vistaPrologo.mostrarContador(escenaActual + 1, prologo.size());
        actualizarBotonSiguiente(escena);
    }
}
