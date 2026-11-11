# Diseño de Epic Arena

Borrador para revisar entre los dos **antes** de escribir código.

## Diagrama de clases

```mermaid
classDiagram
    direction TB

    class IHabilidadEspecial {
        <<interface>>
        +ejecutarEspecial(Personaje objetivo) String
        +especialApuntaAAliado() boolean
    }

    class ICurable {
        <<interface>>
        +restaurarSalud(int cantidad) void
    }

    class Personaje {
        <<abstract>>
        -String nombre
        -int puntosDeVida
        -int puntosDeVidaMaximos
        -int puntosDeAtaque
        -int defensa
        -boolean estaVivo
        +recibirDano(int cantidad) int
        +recibirDanoDirecto(int cantidad) int
        +restaurarSalud(int cantidad) void
        +atacar(Personaje enemigo)* String
        +getNombre() String
        +getPuntosDeVida() int
        +estaVivo() boolean
    }

    class Guerrero {
        +atacar(Personaje enemigo) String
    }

    class Mago {
        +atacar(Personaje enemigo) String
        +ejecutarEspecial(Personaje objetivo) String
    }

    class Sacerdote {
        +atacar(Personaje enemigo) String
        +ejecutarEspecial(Personaje objetivo) String
    }

    class TipoAccion {
        <<enumeration>>
        ATAQUE_NORMAL
        HABILIDAD_ESPECIAL
    }

    class GestorBatalla {
        -List~Personaje~ equipoJugador
        -List~Personaje~ equipoEnemigo
        -Random azar
        +agregarHeroe(Personaje p) void
        +agregarEnemigo(Personaje p) void
        +buscarPorNombre(String nombre) Personaje
        +accionesDisponibles(Personaje p) List~TipoAccion~
        +objetivosValidos(Personaje p, TipoAccion a) List~Personaje~
        +elegirEnemigoAlAzar() Personaje
        +elegirHeroeAlAzar() Personaje
        +eliminarDerrotados() List~Personaje~
        +esAccionDeApoyo(Personaje p, TipoAccion a) boolean
        +esHeroe(Personaje p) boolean
        +verificarFinDelJuego() boolean
        +ganoElJugador() boolean
    }

    class VentanaBatalla {
        +mostrarEquipos(heroes, enemigos) void
        +agregarAlHistorial(String texto) void
        +cargarAtacantes(nombres) void
        +cargarObjetivos(nombres) void
        +cargarAcciones(acciones) void
        +getAtacanteSeleccionado() String
        +getObjetivoSeleccionado() String
        +getAccionSeleccionada() TipoAccion
        +alEjecutarTurno(ActionListener l) void
        +setTextoObjetivo(String texto) void
        +mostrarMensaje(String texto) void
        +alCambiarAtacante(ActionListener l) void
        +alCambiarAccion(ActionListener l) void
        +bloquearControles() void
        +desbloquearControles() void
    }

    class ControladorBatalla {
        -GestorBatalla gestor
        -VentanaBatalla vista
        +comenzar(GestorBatalla batalla, String introduccion) void
        +ejecutarTurno() void
        -realizarAccion(atacante, objetivo, accion) String
        -contraataque() void
        -cerrarJugada() boolean
        -sincronizarVista() void
    }

    Personaje <|-- Guerrero
    Personaje <|-- Mago
    Personaje <|-- Sacerdote
    ICurable <|.. Personaje
    IHabilidadEspecial <|.. Mago
    IHabilidadEspecial <|.. Sacerdote
    ICurable <|.. Sacerdote
    GestorBatalla o-- "0..*" Personaje
    ControladorBatalla --> GestorBatalla
    ControladorBatalla --> VentanaBatalla
```

La vista se divide por dentro en `PanelEstado` (dos `JTable`), `PanelHistorial`
(`JTextArea` + `JScrollPane`) y `PanelControles` (tres `JComboBox` + botón).

## Flujo de un turno

```mermaid
sequenceDiagram
    actor Jugador
    participant V as VentanaBatalla
    participant C as ControladorBatalla
    participant G as GestorBatalla
    participant P as Personaje

    Jugador->>V: clic en EJECUTAR TURNO
    V->>C: actionPerformed
    C->>V: leer atacante, objetivo y acción
    C->>G: buscarPorNombre(...)
    C->>P: atacar(objetivo) o ejecutarEspecial(objetivo)
    P-->>C: narrativa (String)
    C->>G: eliminarDerrotados()
    C->>V: agregarAlHistorial + mostrarEquipos + recargar desplegables
    C->>G: verificarFinDelJuego()
    C->>G: elegirEnemigoAlAzar(), elegirHeroeAlAzar()
    C->>P: monstruo.atacar(heroe)
    C->>G: eliminarDerrotados()
    C->>V: actualizar pantalla otra vez
    C->>G: verificarFinDelJuego()
```

## Decisiones de diseño

| # | Decisión | Motivo |
|---|---|---|
| 1 | `Personaje` tiene un atributo `defensa` que el enunciado no lista | El Mago "ignora la defensa": sin defensa esa regla no significa nada. `recibirDano` descuenta la defensa; `recibirDanoDirecto` no. |
| 2 | `puntosDeVidaMaximos` como atributo extra | La curación no debe pasar del máximo y la tabla puede mostrar una barra de vida. |
| 3 | El controlador nunca pregunta por la clase concreta | Para el ataque normal llama `atacante.atacar(objetivo)`. Para la especial solo comprueba la capacidad (`IHabilidadEspecial`), no si es Mago o Sacerdote. |
| 4 | `especialApuntaAAliado()` en `IHabilidadEspecial` | La especial del Sacerdote va a un compañero, así que el desplegable de objetivos debe cambiar de equipo sin que nadie pregunte "¿es Sacerdote?". |
| 5 | `GestorBatalla.accionesDisponibles` y `objetivosValidos` | La vista solo ofrece opciones legales, así desaparecen los casos de error en vez de validarlos después. |
| 6 | `Random` se recibe por constructor en `GestorBatalla` | Permite pruebas con semilla fija para el contraataque. |
| 8 | `ControladorBatalla.comenzar(...)` recibe la batalla en vez de crearla | El mismo controlador sirve para encadenar varios combates (campaña) sin cambios. |
| 7 | La vista trabaja con nombres (`String`), el controlador busca el objeto real | Es el paso 3 del enunciado. Exige nombres únicos por partida. |

## Dudas para resolver (algunas con el docente)

1. **Método extra en la interfaz**: `especialApuntaAAliado()` no aparece en el
   enunciado. Si el docente exige la interfaz con un único método, se mueve a
   `Personaje` o al gestor.
2. **Monstruos con especial**: el enunciado solo pide un contraataque normal al
   azar. Que usen su especial a veces sería un extra.
3. **Librerías de apariencia**: el enunciado dice "puramente Java Swing". No se
   usa ninguna externa salvo que el docente lo autorice.
4. **Costo de la especial**: hoy se puede usar en todos los turnos. Un tiempo
   de recarga o maná la haría una decisión táctica (extra opcional).

## Dudas ya resueltas

- **`ICurable`**: `Personaje` implementa `ICurable`, porque para que el
  Sacerdote cure a un compañero ese compañero debe poder recibir
  `restaurarSalud`. El Sacerdote la declara además de forma explícita, como
  pide el enunciado. Así cualquier personaje vivo puede ser curado.
- **Monstruos**: son instancias de las mismas subclases (`Guerrero`, `Mago`,
  `Sacerdote`) con otros nombres y estadísticas. El enunciado habla de
  "Héroes y Villanos" sobre la misma jerarquía, y que la misma clase funcione
  en ambos equipos demuestra el polimorfismo sin clases de relleno.
- **`recibirDano` retorna `int`**: el daño realmente aplicado, para que la
  narrativa muestre la cifra real después de la defensa.
