# Epic Arena

RPG de combate por turnos hecho en Java Swing con arquitectura MVC.
Proyecto final de Diseño de Software — Universidad Sergio Arboleda.

## Cómo ejecutar

Requiere JDK 21 o superior.

```bash
./mvnw compile exec:java   # ejecutar el juego
./mvnw test                # correr las pruebas del modelo
```

En Windows se abre como proyecto Maven desde NetBeans o IntelliJ.

## Estructura

```
src/main/java/epicarena/
├── modelo/        Reglas del juego (sin librerías gráficas)
├── vista/         Interfaz en Swing
├── controlador/   Árbitro del turno
└── Main.java
src/test/java/epicarena/modelo/   Pruebas del motor
docs/              Enunciado, rúbrica y diseño (UML)
```
