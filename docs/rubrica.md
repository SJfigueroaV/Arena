# Rúbrica como checklist (objetivo: Nivel Leyenda en los 4 criterios)

Cada punto se debe poder demostrar en el código o ejecutando el juego.

## 1. Modelado y polimorfismo

- [ ] `Personaje` es abstracta, con `nombre`, `puntosDeVida`, `puntosDeAtaque`, `estaVivo`.
- [ ] `recibirDano(int cantidad)` es concreto y vive en `Personaje`.
- [ ] `public abstract String atacar(Personaje enemigo)` está sobrescrito en cada subclase.
- [ ] Existen `IHabilidadEspecial` (`String ejecutarEspecial(Personaje objetivo)`) e `ICurable` (`void restaurarSalud(int cantidad)`).
- [ ] `Guerrero` inflige el 100 % de su ataque.
- [ ] `Mago` implementa `IHabilidadEspecial`: ataque normal débil, especial masivo que ignora la defensa.
- [ ] `Sacerdote` implementa `IHabilidadEspecial` e `ICurable`: su especial cura a un compañero.
- [ ] El controlador llama `atacante.atacar(objetivo)` de forma genérica.
- [ ] **Cero** `instanceof Guerrero / Mago / Sacerdote` en todo el proyecto.

## 2. Arquitectura MVC

- [ ] Ningún archivo de `modelo/` importa `javax.swing` ni `java.awt`.
- [ ] El modelo se puede probar completo con JUnit sin abrir una ventana.
- [ ] Ningún cálculo de daño, curación o victoria en la vista ni dentro de un `ActionListener`.
- [ ] La vista no conoce al controlador ni modifica el modelo.
- [ ] El controlador sigue los 6 pasos del turno del enunciado.
- [ ] Tras cada acción se actualizan a la vez el historial y las tablas.

## 3. Gestión de memoria y colecciones

- [ ] `GestorBatalla` tiene `List<Personaje> equipoJugador` y `List<Personaje> equipoEnemigo` (`ArrayList`).
- [ ] Los personajes con HP <= 0 se eliminan con `remove()` en el mismo turno.
- [ ] `verificarFinDelJuego()` retorna `true` cuando una lista queda vacía.
- [ ] Ningún arreglo estático: agregar un cuarto o quinto personaje no rompe nada.

## 4. Experiencia de usuario

- [ ] `BorderLayout` con panel superior (estado), central (historial) e inferior (controles).
- [ ] Dos `JTable`: héroes a la izquierda, monstruos a la derecha, con HP actualizado.
- [ ] `JTextArea` de solo lectura dentro de `JScrollPane`, con auto-scroll al final.
- [ ] Tres `JComboBox`: quién ataca, a quién ataca, qué acción.
- [ ] Botón rojo grande "EJECUTAR TURNO".
- [ ] Las tablas se repintan sin duplicar filas.
- [ ] Un personaje muerto desaparece de inmediato de tablas y desplegables.
- [ ] Nunca hay `NullPointerException`: selección vacía, equipo vacío, clic tras terminar la partida.
- [ ] Al terminar la partida se anuncia el resultado y se bloquea el botón.
- [ ] La interfaz se ve alineada y cuidada.
