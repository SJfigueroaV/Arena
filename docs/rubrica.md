# Rúbrica como checklist (objetivo: Nivel Leyenda en los 4 criterios)

Cada punto se debe poder demostrar en el código o ejecutando el juego.

## 1. Modelado y polimorfismo

- [x] `Personaje` es abstracta, con `nombre`, `puntosDeVida`, `puntosDeAtaque`, `estaVivo`.
- [x] `recibirDano(int cantidad)` es concreto y vive en `Personaje`.
- [x] `public abstract String atacar(Personaje enemigo)` está sobrescrito en cada subclase.
- [x] Existen `IHabilidadEspecial` (`String ejecutarEspecial(Personaje objetivo)`) e `ICurable` (`void restaurarSalud(int cantidad)`).
- [x] `Guerrero` inflige el 100 % de su ataque.
- [x] `Mago` implementa `IHabilidadEspecial`: ataque normal débil, especial masivo que ignora la defensa.
- [x] `Sacerdote` implementa `IHabilidadEspecial` e `ICurable`: su especial cura a un compañero.
- [x] El controlador llama `atacante.atacar(objetivo)` de forma genérica (`ControladorBatalla.realizarAccion`).
- [x] **Cero** `instanceof Guerrero / Mago / Sacerdote` en todo el proyecto.

## 2. Arquitectura MVC

- [x] Ningún archivo de `modelo/` importa `javax.swing` ni `java.awt`.
- [x] El modelo se puede probar completo con JUnit sin abrir una ventana.
- [x] Ningún cálculo de daño, curación o victoria en la vista ni dentro de un `ActionListener`.
- [x] La vista no conoce al controlador ni modifica el modelo.
- [x] El controlador sigue los 6 pasos del turno del enunciado.
- [x] Tras cada acción se actualizan a la vez el historial y las tablas.

## 3. Gestión de memoria y colecciones

- [x] `GestorBatalla` tiene `List<Personaje> equipoJugador` y `List<Personaje> equipoEnemigo` (`ArrayList`).
- [x] Los personajes con HP <= 0 se eliminan con `remove()` en el mismo turno.
- [x] `verificarFinDelJuego()` retorna `true` cuando una lista queda vacía.
- [x] Ningún arreglo estático: agregar un cuarto o quinto personaje no rompe nada.

## 4. Experiencia de usuario

- [x] `BorderLayout` con panel superior (estado), central (historial) e inferior (controles).
- [x] Dos `JTable`: héroes a la izquierda, monstruos a la derecha, con HP actualizado.
- [x] `JTextArea` de solo lectura dentro de `JScrollPane`, con auto-scroll al final.
- [x] Tres `JComboBox`: quién ataca, a quién ataca, qué acción.
- [x] Botón rojo grande "EJECUTAR TURNO".
- [x] Las tablas se repintan sin duplicar filas.
- [x] Un personaje muerto desaparece de inmediato de tablas y desplegables.
- [x] Nunca hay `NullPointerException`: selección vacía, equipo vacío, clic tras terminar la partida.
- [x] Al terminar la partida se anuncia el resultado y se bloquea el botón.
- [x] La interfaz se ve alineada y cuidada.

## Cómo se comprueba

- `./mvnw test` corre 44 pruebas: reglas del modelo, partidas completas con la
  ventana real y dos reglas de arquitectura (modelo sin Swing y sin
  `instanceof` de clases concretas).
- Revisión manual: jugar una partida hasta el final y otra con "Nueva partida".
