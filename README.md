# Epic Arena — La Laguna de los Espantos

RPG de combate por turnos hecho en Java Swing con arquitectura MVC.
Tres muiscas devuelven el último tunjo a la laguna de Guatavita a través de
cinco etapas llenas de espantos del folclor colombiano.
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
├── modelo/        Reglas del juego y de la campaña (sin librerías gráficas)
├── historia/      Contenido: textos, enemigos y estadísticas de cada etapa
├── vista/         Interfaz en Swing
├── controlador/   Árbitro del turno
└── Main.java
src/test/java/epicarena/          Pruebas del modelo, la campaña y el controlador
docs/              Enunciado, rúbrica, diseño (UML) y guion
docs/arte/         Guía de pixel art y paleta de colores
```
