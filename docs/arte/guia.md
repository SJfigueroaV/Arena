# Guía de arte — La Laguna de los Espantos (pixel art)

Esta guía es un punto de partida, no una regla: las descripciones y los
prompts sirven como referencia para dibujar y se pueden cambiar a gusto. Lo
único que conviene respetar son los **tamaños y nombres de archivo**, porque
el juego los carga tal cual.

La historia completa de cada personaje está en `docs/lore.md`.

**Arte final:** los personajes y los cinco fondos del juego son los diseños de
la compañera de equipo (hojas de poses y escenarios en pixel art). Sus hojas
se recortaron cuadro por cuadro, se llevaron a la medida del juego (cuadros de
72 × 96 con los pies en la fila 92, El Dorado de 108 × 96) y se armaron las
animaciones `idle`, `ataque`, `especial` y `caida` con sus poses. Lo que las
hojas no traen (`dano`, `curado`, `dormido` y algún ataque) se deriva del
cuadro quieto. Las tiras resultantes están en
`docs/arte/referencias/animaciones/` y los fondos a 480 × 160 en
`docs/arte/referencias/fondos/`.

En `docs/arte/referencias/` hay un boceto de cada personaje y de cada fondo,
dibujados con la paleta y las medidas reales, más dos hojas de contacto
(`hoja_personajes.png` y `hoja_fondos.png`). Sirven para ver silueta,
proporciones y colores; las poses son rígidas y el sombreado simple, así que
la versión final queda a cargo de quien dibuje.

## 1. Especificaciones técnicas

### Resolución base

El juego dibuja todo a escala **×2** sin suavizado, así que cada píxel del
dibujo se ve como un cuadro de 2×2.

| Elemento | Lienzo | En pantalla |
|---|---|---|
| Personaje normal | 64 × 64 px | 128 × 128 |
| Jefe (El Dorado) | 96 × 96 px | 192 × 192 |
| Fondo de etapa | 480 × 160 px | 960 × 320 |
| Ícono de interfaz | 16 × 16 px | 32 × 32 |
| Retrato (opcional) | 32 × 32 px | 64 × 64 |

### Reglas de los personajes

- **Vista lateral**, ligeramente de tres cuartos si ayuda a leer la cara.
- **Héroes miran a la derecha, espantos a la izquierda.**
- **Pies sobre la línea inferior**, a 4 px del borde (en la fila 60 de 64).
  Así todos quedan parados sobre el mismo suelo.
- **Altura del cuerpo:** unos 44–52 px; deja aire arriba para armas,
  sombreros y efectos de las animaciones.
- **Contorno de 1 px** con el color más oscuro de la paleta (`#16141F`), o un
  tono oscuro de la propia rampa si prefiere contorno de color.
- **Fondo transparente**, sin sombra en el suelo (el juego la dibuja).
- **Sin antialiasing ni degradados suaves**: sombreado por bandas o dithering.

### Paleta

`docs/arte/` trae la paleta **"Laguna"**, 32 colores en 11 rampas:

| Archivo | Para qué |
|---|---|
| `paleta-laguna.gpl` | Importar en Aseprite, LibreSprite, Pixelorama, GIMP o Krita |
| `paleta-laguna.hex` | Importar en Lospec, Piskel u otras herramientas web |
| `paleta-laguna.png` | Vista rápida, una fila por rampa |

| Rampa | Uso principal |
|---|---|
| Noche | Contornos, sombras, ropa oscura |
| Niebla | Ánimas, páramo, metal viejo |
| Hueso | Telas de algodón, huesos, velas |
| Piel | Piel de los personajes humanos |
| Oro | Ofrendas muiscas, todo lo de los héroes, El Dorado |
| Monte | Selva, Madremonte, magia de la tierra |
| Agua | Río, Madre de Agua, Mohán |
| Sangre | Peligro, Patasola, interfaz de daño |
| Fuego | Candileja, Ánima Sola, antorchas |
| Luna | Magia de Chie, noche del pueblo |
| Barro | Laguna vacía, Alquimista, suelo |

**Regla de bandos:** los héroes siempre llevan algo de oro; los espantos nunca,
salvo El Dorado, que está cubierto de él. Así se distinguen de un vistazo.

Si un color falta, mejor agregarlo a la paleta que salirse de ella.

## 2. Animaciones (para más adelante)

Por ahora basta **un cuadro quieto por personaje**. Cuando se animen, cada
animación va en una tira horizontal de cuadros del mismo tamaño:

| Animación | Cuadros | Quién la necesita | Nota |
|---|---|---|---|
| `idle` | 4 | Todos | Respiración o flotar suave, en bucle |
| `ataque` | 4–6 | Todos | Anticipación, golpe, regreso |
| `especial` | 6 | Magos y Sacerdotes | El momento más vistoso |
| `dano` | 2 | Todos | Retroceso y destello |
| `caida` | 6 | Todos | Para los espantos: cómo se apaciguan (ver fichas) |

Ejemplo: `tyba_idle.png` de 4 cuadros mide 256 × 64. El pivote de todas las
animaciones es el centro de los pies, que nunca se mueve de sitio entre una
animación y otra.

Velocidad pensada: unos 120 ms por cuadro (8 cuadros por segundo).

Mientras no haya animaciones, el juego simula el golpe moviendo y haciendo
parpadear el cuadro quieto.

## 3. Nombres y carpetas

Minúsculas, sin tildes, sin espacios. Hay dos lugares:

**Para dibujar** (`docs/arte/referencias/`), en tamaño real:

```
heroes/  enemigos/  fondos/      un cuadro quieto por personaje o etapa
animaciones/                     una tira por animación: tyba_idle.png, chie_especial.png...
hoja_personajes.png  hoja_fondos.png
```

**Lo que carga el juego** (`src/main/resources/sprites/`), ya escalado ×2,
un archivo por cuadro:

```
personajes/<nombre>/idle_0.png ... idle_3.png
                    ataque_0.png ... ataque_4.png
                    especial_0.png ... especial_5.png   (solo magos y sacerdotes)
                    dano_0.png  dano_1.png
                    curado_0.png ... curado_3.png
                    caida_0.png ... caida_5.png
                    dormido_0.png                       (pose apagada del prólogo)
fondos/paramo.png  selva.png  rio.png  pueblo.png  laguna.png   (960 × 320)
       ofrenda.png  tajo.png                                    (viñetas del prólogo)
```

Cada cuadro de personaje mide 128 × 192 (el dibujo de 64 × 64 al doble, con
64 px de aire arriba para los efectos). El Dorado mide 192 × 192. Se pueden
agregar o quitar cuadros: el juego lee `_0`, `_1`, `_2`... hasta que falte
uno.

`<nombre>` es el nombre del personaje sin artículo, en minúsculas, sin tildes
y con guiones bajos: "El Hombre Caimán" → `hombre_caiman`, "El Dorado" →
`dorado`, "El Fraile de la Codicia" → `fraile_de_la_codicia`.

Los cuadros actuales salen de los bocetos. Para reemplazarlos por dibujos
propios basta con guardar los PNG nuevos con los mismos nombres.

## 4. Prompts de referencia

Si usa un generador de imágenes como apoyo, el resultado será "estilo pixel
art" pero casi nunca pixel art real: la cuadrícula sale irregular y con
colores de más. Sirve como **boceto de concepto** para redibujar encima, no
como sprite final.

### Bloque de estilo (va al inicio de cada prompt de personaje)

```
Pixel art game sprite, 64x64, side view, full body, limited 32-color palette
of dark navy, mist grey, bone white, warm brown skin, gold, emerald green,
river blue, dark red, fire orange and moon violet, 1px dark outline, flat
shading with hard color bands, no anti-aliasing, no gradients, transparent
background, inspired by pre-Columbian Muisca goldwork and Colombian folklore
```

### Bloque de estilo para fondos

```
Pixel art game background, 480x160, wide side-scrolling panorama, 3 parallax
layers, limited 32-color palette, no characters, no text, flat ground strip
along the bottom fifth, inspired by pre-Columbian Muisca goldwork and
Colombian folklore
```

### Hoja de concepto (útil antes de animar)

Para ver a un personaje desde varios ángulos antes de dibujarlo, se añade al
final de su prompt:

```
character turnaround sheet, front view, side view and back view, same
character, same pose, evenly spaced
```

## 5. Fichas de personajes

Cada ficha trae: la **silueta** (lo que debe reconocerse en negro puro), los
**colores** dominantes, el **detalle** que lo identifica, ideas para sus
**animaciones** y el **prompt** de referencia.

### Héroes (miran a la derecha)

#### Tyba — Guerrero
- **Silueta:** ancho y compacto, escudo redondo al frente, macana al hombro.
- **Colores:** piel, algodón hueso, oro en pecho y nariz.
- **Detalle:** el tunjo (figurita de oro) colgando del cinturón; es lo que
  carga toda la historia.
- **Ataque:** levanta la macana por encima de la cabeza y la baja de golpe.
- **Caída:** cae de rodilla protegiendo el tunjo con la mano.
- **Prompt:** `Muisca elite warrior, short cropped black hair, gold nose ring
  and gold chest plate, white cotton tunic with geometric border, heavy wooden
  club over the shoulder, round woven shield, small gold figurine hanging from
  his belt, calm stern face, facing right`

#### Chie — Mago
- **Silueta:** delgada y alta, capa que se abre hacia abajo, una mano en alto.
- **Colores:** capa violeta luna, pelo negro, plumas niebla.
- **Detalle:** diadema con media luna de oro.
- **Ataque:** chasquea los dedos y salta un destello pequeño.
- **Especial (Luz de Chía):** alza los brazos, se forma un disco de luna sobre
  ella y cae un rayo frío.
- **Caída:** la capa se desploma sobre ella como un ala.
- **Prompt:** `Young Muisca sorceress, long straight black hair, gold crescent
  moon diadem, dark violet cloak trimmed with owl feathers, one hand raised
  holding a small orb of pale moonlight, facing right`

#### Sua — Sacerdote
- **Silueta:** encorvado, túnica larga hasta el suelo, bastón más alto que él.
- **Colores:** túnica hueso, oro brillante en el sol del bastón.
- **Detalle:** el poporo (calabazo) en la cintura y el tocado con disco solar.
- **Ataque:** golpe corto con la base del bastón.
- **Especial (Calor de Sué):** el sol del bastón brilla y suelta rayos cálidos
  hacia el aliado.
- **Caída:** se sienta despacio, apoyado en el bastón.
- **Prompt:** `Elderly Muisca sun priest, slightly hunched, sun disc
  headdress, long white cotton robe, tall staff topped with a gold sun, small
  gourd at his waist, kind tired eyes, facing right`

### Etapa 1 — El páramo de niebla (miran a la izquierda)

#### Ánima en Pena — Guerrero
- **Silueta:** forma de gota invertida, sin piernas, brazos largos colgando.
- **Colores:** niebla casi blanca, ojos noche.
- **Detalle:** cadenas que arrastra.
- **Caída:** se deshace en niebla de abajo hacia arriba.
- **Prompt:** `Wandering ghost made of mist, no legs, hollow dark eyes, ragged
  shroud, long arms with dragging chains, facing left`

#### Ánima Errante — Guerrero
- **Silueta:** como la anterior pero más alta y delgada, para no confundirlas.
- **Detalle:** farol apagado en la mano.
- **Caída:** el farol cae y se apaga; ella se disuelve.
- **Prompt:** `Tall thin ghost made of mist, holding an extinguished lantern,
  long flowing shroud, sad face, facing left`

#### La Candileja — Mago
- **Silueta:** esfera de fuego flotante, a media altura.
- **Colores:** fuego, sangre en el centro, chispas oro.
- **Detalle:** la cara de una anciana dentro de las llamas.
- **Especial (Llamarada):** la bola se infla y lanza una lengua de fuego.
- **Caída:** las llamas se encogen hasta una brasa que se apaga.
- **Prompt:** `Floating ball of fire with the face of an old woman inside the
  flames, sparks around it, facing left`

### Etapa 2 — La selva que camina

#### La Patasola — Guerrero
- **Silueta:** una sola pierna larga terminada en pezuña, pelo enorme y
  enmarañado, encorvada hacia adelante.
- **Colores:** pelo noche, piel pálida, bejucos monte, boca sangre.
- **Detalle:** la única pierna; es lo que se debe leer primero.
- **Ataque:** salta sobre la pierna y muerde.
- **Caída:** se hunde en la maleza que crece alrededor.
- **Prompt:** `Wild jungle woman with a single long leg ending in a hoof,
  huge tangled black hair, pale skin, sharp fangs, vines wrapped around her,
  hunched forward, facing left`

#### La Madremonte — Sacerdote
- **Silueta:** alta, ancha abajo como un arbusto, sombrero de hojas.
- **Colores:** rampa monte completa, ojos que brillan en verde claro.
- **Detalle:** la cara oculta bajo el sombrero, solo se ven los ojos.
- **Especial (Savia del Monte):** echa raíces al suelo y brotan hojas sobre
  el aliado.
- **Caída:** se deshace en hojarasca.
- **Prompt:** `Tall woman made of moss, leaves and branches, wide hat of
  foliage hiding her face, only two glowing green eyes visible, roots for
  feet, facing left`

#### El Sombrerón — Mago
- **Silueta:** un sombrero gigante sobre un cuerpo flaco, como un clavo.
- **Colores:** noche casi total, cadenas niebla.
- **Detalle:** el sombrero, más ancho que sus hombros.
- **Especial (Jauría Negra):** silba y aparecen dos perros de sombra.
- **Caída:** el cuerpo desaparece y el sombrero cae vacío al suelo.
- **Prompt:** `Thin man dressed in black, enormous wide-brimmed black hat
  covering his face, heavy chains in his hands, two small black dogs at his
  feet, facing left`

### Etapa 3 — El río grande

#### El Hombre Caimán — Guerrero
- **Silueta:** torso humano, cabeza larga de caimán, cola que toca el suelo.
- **Colores:** escamas monte oscuro, vientre hueso, gotas agua.
- **Detalle:** el sombrero de paja que aún conserva de cuando era hombre.
- **Ataque:** abre la mandíbula de golpe.
- **Caída:** se tira al agua y solo quedan ondas.
- **Prompt:** `Half man half caiman, human torso and arms, long caiman head
  and tail, wet dark green scales, old straw hat, standing upright on a river
  bank, facing left`

#### El Mohán — Mago
- **Silueta:** enorme y peludo, pelo hasta las rodillas, sentado o agachado.
- **Colores:** pelo barro, piel piel oscura, humo niebla.
- **Detalle:** el tabaco encendido y baratijas de oro enredadas en el pelo
  (es la única excepción a la regla del oro: oro robado al río).
- **Especial (Creciente del Río):** golpea el suelo y sube una ola.
- **Caída:** se deshace en humo de tabaco.
- **Prompt:** `Huge hairy river sorcerer, hair down to his knees, smoking a
  cigar, small gold trinkets tangled in his hair, crouching on a rock, facing
  left`

#### La Madre de Agua — Sacerdote
- **Silueta:** mujer esbelta con el vestido convertido en cascada.
- **Colores:** rampa agua, reflejos niebla clara.
- **Detalle:** los pies al revés, y peces pequeños girando a su alrededor.
- **Especial (Remanso):** el agua se calma y envuelve al aliado.
- **Caída:** se derrama y queda un charco.
- **Prompt:** `River spirit woman made of flowing water, dress turning into a
  waterfall, feet turned backwards, small fish swimming around her, facing
  left`

### Etapa 4 — El pueblo a medianoche

#### La Llorona — Mago
- **Silueta:** vestido blanco largo que se ensancha, cabeza inclinada.
- **Colores:** hueso, pelo noche, sombras luna.
- **Detalle:** el pelo cubriendo la cara y la boca abierta.
- **Especial (Lamento):** ondas de sonido visibles que salen de la boca.
- **Caída:** se desvanece mientras el llanto (las ondas) se apaga.
- **Prompt:** `Weeping woman in a long white dress, black hair covering her
  face, mouth open in a wail, hands reaching forward, facing left`

#### El Silbón — Guerrero
- **Silueta:** el más alto del juego, flaquísimo, el costal al hombro.
- **Colores:** ropa barro, sombrero de paja hueso, huesos hueso claro.
- **Detalle:** el costal con huesos asomando y los labios fruncidos al silbar.
- **Ataque:** golpe con el costal.
- **Caída:** el costal cae y los huesos se desparraman; él ya no está.
- **Prompt:** `Extremely tall gaunt man, straw hat, ragged peasant clothes,
  carrying a sack of bones over his shoulder, lips pursed whistling, facing
  left`

#### El Cura sin Cabeza — Sacerdote
- **Silueta:** sotana negra con hombros y nada encima, un brazo en alto.
- **Colores:** noche, cirio hueso con llama fuego.
- **Detalle:** el cirio encendido en la mano.
- **Especial (Bendición sin Voz):** traza una cruz y cae luz pálida.
- **Caída:** el cirio se apaga y la sotana se desploma vacía.
- **Prompt:** `Headless priest in a long black cassock, one hand raised in
  blessing, the other holding a lit candle, no head at all above the collar,
  facing left`

#### Ánima Sola — Guerrero
- **Silueta:** figura de rodillas o de pie entre llamas bajas.
- **Colores:** fuego en las llamas, figura niebla.
- **Detalle:** manos encadenadas levantadas en oración.
- **Caída:** las llamas se apagan y queda en paz, luego se desvanece.
- **Prompt:** `Lonely soul surrounded by low flames, chained hands raised in
  prayer, sorrowful face, facing left`

### Etapa 5 — La laguna herida

#### El Dorado — Guerrero (jefe, lienzo 96 × 96)
- **Silueta:** armadura de conquistador con morrión (casco de cresta), mucho
  más grande que los demás.
- **Colores:** rampa oro completa; ojos noche vacíos.
- **Detalle:** la armadura y la piel fundidas en oro, con barro hasta las
  rodillas.
- **Ataque:** puñetazo lento y pesado.
- **Caída:** el oro se descascara como ceniza y queda un hombre pequeño y
  cansado. Es el final del juego: vale la pena darle más cuadros.
- **Prompt:** `Spanish conquistador entirely covered in gold, morion helmet,
  armor and skin fused into gold, empty dark eyes, standing knee-deep in mud,
  imposing, larger than other characters, facing left`

#### El Fraile de la Codicia — Sacerdote
- **Silueta:** capucha puntiaguda, cuerpo redondo, el cofre abrazado.
- **Colores:** hábito barro, cofre oro.
- **Detalle:** monedas que se le escapan del cofre.
- **Especial (Absolución Comprada):** lanza monedas que curan al aliado.
- **Caída:** las monedas se vuelven barro en sus manos.
- **Prompt:** `Hooded friar in a brown habit, round body, clutching a heavy
  wooden chest, gold coins spilling out, greedy smile, facing left`

#### El Alquimista — Mago
- **Silueta:** viejo flaco y encorvado, túnica manchada, frascos colgando.
- **Colores:** barro, frascos con líquidos verdes y violetas.
- **Detalle:** las manos goteando barro.
- **Especial (Barro Vivo):** lanza un frasco que estalla en barro que agarra.
- **Caída:** se convierte él mismo en un montón de barro.
- **Prompt:** `Thin hunched old alchemist, stained robes, glass flasks
  hanging from his belt, hands dripping with mud, facing left`

## 6. Fondos (480 × 160)

Cada fondo tiene una **franja de suelo plana en el quinto inferior** (las
últimas 32 filas), donde se paran los personajes, y el centro despejado.
Héroes a la izquierda, espantos a la derecha.

| Archivo | Descripción | Prompt (después del bloque de fondos) |
|---|---|---|
| `paramo` | Amanecer frío, frailejones, niebla espesa, un resplandor de fuego lejano | `High Andean paramo at dawn, frailejon plants, thick fog, cold blue-grey light, faint orange glow in the distance` |
| `selva` | Raíces gigantes, bejucos colgando, luz verde que entra en rayos | `Dense jungle, giant roots and hanging vines, deep green, shafts of dim light` |
| `rio` | Atardecer, río ancho y oscuro, orilla de barro, una piedra grande | `Wide dark river at dusk, muddy shore, a single large rock, reeds, distant mountains` |
| `pueblo` | Medianoche, casas coloniales encaladas, puertas cerradas, torre de iglesia, luna llena | `Small colonial village at midnight, whitewashed houses with closed doors, church tower, full moon` |
| `laguna` | Cráter casi vacío con el tajo en el borde, fondo de barro con ofrendas de oro | `Round crater lagoon almost empty, a deep notch cut in the rim, muddy bottom with scattered gold offerings` |

Para un efecto de profundidad después, cada fondo se puede entregar en 3
capas separadas (`paramo_lejos.png`, `paramo_medio.png`, `paramo_cerca.png`).

## 7. Interfaz (opcional)

| Elemento | Tamaño | Idea |
|---|---|---|
| Íconos de vida, ataque, defensa y especial | 16 × 16 | Corazón con greca, macana, escudo redondo, luna |
| Marco de panel | 24 × 24, en 9 partes | Borde con grecas muiscas en oro sobre noche |
| Botón "EJECUTAR TURNO" | 9 partes | Rojo sangre con borde oro; versión normal, encima y apagada |
| Retratos | 32 × 32 | Cabeza de cada personaje para tablas o desplegables |

"En 9 partes" quiere decir que las esquinas se dejan iguales y los lados se
estiran, así el mismo marco sirve para paneles de cualquier tamaño.

## 8. Orden sugerido

1. Tyba, hasta que el estilo convenza. Es la referencia de todo lo demás.
2. Chie y Sua, comparándolos con Tyba lado a lado.
3. El páramo y sus tres espantos: con eso ya se puede probar una etapa
   completa en el juego.
4. El resto de etapas en orden.
5. Animaciones, empezando por `idle` y `dano`, que son las que más se ven.

## Lista de control

- [ ] 3 héroes
- [ ] 16 espantos (15 de 64 × 64 y El Dorado de 96 × 96)
- [ ] 5 fondos
- [ ] Todos dentro de la paleta Laguna
- [ ] Pies en la misma línea en todos los personajes
- [ ] Siluetas reconocibles rellenas de negro
