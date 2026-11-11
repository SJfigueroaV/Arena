# Guía de arte — La Laguna de los Espantos

Objetivo: que los 19 personajes y los 5 fondos parezcan dibujados por la misma
mano, aunque se generen por separado.

## Estilo

Ilustración 2D plana tipo recorte, inspirada en la orfebrería muisca: formas
geométricas, simetría, contorno grueso y pocos colores. Nada de 3D, fotos ni
degradados realistas.

| Uso | Color |
|---|---|
| Oro (héroes, ofrendas, acentos) | `#E0B44C` |
| Esmeralda (monte, magia de la tierra) | `#2E8B6E` |
| Niebla (fondos, ánimas) | `#A9B8C2` |
| Noche (sombras, contornos) | `#16141F` |
| Sangre seca (peligro, enemigos) | `#B5443C` |
| Hueso (pieles, telas) | `#ECE6D4` |

Los héroes llevan siempre algo de oro; los espantos, nunca, salvo El Dorado,
que está cubierto. Así se distinguen los bandos de un vistazo.

## Formato de los archivos

| | Personajes | Fondos |
|---|---|---|
| Tamaño | 512 × 512 px | 1280 × 400 px |
| Fondo | Transparente (PNG) | Opaco |
| Encuadre | Cuerpo entero, centrado, pies cerca del borde inferior | Horizontal, zona central despejada |
| Orientación | Héroes miran a la derecha, enemigos a la izquierda | — |

Carpetas y nombres (minúsculas, sin tildes ni espacios):

```
src/main/resources/sprites/
├── heroes/     tyba.png  chie.png  sua.png
├── enemigos/   anima_en_pena.png  candileja.png  patasola.png ...
└── fondos/     paramo.png  selva.png  rio.png  pueblo.png  laguna.png
```

Con una sola imagen por personaje alcanza: el golpe y el daño se pueden animar
por código (sacudida, destello, desvanecimiento). Si más adelante se quieren
poses, se añaden como `tyba_ataque.png` y `tyba_herido.png`.

## Cómo generar con consistencia

1. Usar siempre el mismo bloque de estilo, palabra por palabra.
2. Generar primero a Tyba hasta que guste y usar esa imagen como referencia
   de estilo para todas las demás.
3. Pedir fondo liso de un color que no esté en la paleta (magenta) y quitarlo
   después; el transparente directo suele dejar bordes sucios.
4. Un personaje por imagen. Nada de texto, marcos ni firmas.
5. Revisar en pequeño (unos 120 px): si la silueta no se reconoce, repetir.

### Bloque de estilo (va al inicio de todos los prompts)

```
Flat 2D game character sprite, paper cut-out illustration inspired by
pre-Columbian Muisca goldwork, bold geometric shapes, thick dark outline,
limited palette of gold, emerald green, mist grey, bone white and dark red,
no gradients, no text, full body, centered, plain solid magenta background
```

Para los fondos se cambia la primera línea por:

```
Flat 2D game background, wide panoramic landscape, paper cut-out illustration
inspired by pre-Columbian Muisca goldwork, layered silhouettes, limited
palette, no characters, no text, empty central area
```

## Prompts por personaje (se añaden después del bloque de estilo)

### Héroes (miran a la derecha)

| Archivo | Descripción |
|---|---|
| `tyba` | Muisca elite warrior, short cropped hair, gold nose ring and gold chest plate, cotton tunic, wooden club and round shield, a small gold figurine tied at his belt, calm stern face, facing right |
| `chie` | Young Muisca sorceress, long black hair, crescent moon headpiece, dark cloak with owl feathers, one hand raised holding pale moonlight, facing right |
| `sua` | Elderly Muisca sun priest, sun disc headdress, long white cotton robe, staff topped with a gold sun, gourd for coca at his waist, kind tired eyes, facing right |

### Enemigos (miran a la izquierda)

| Archivo | Descripción |
|---|---|
| `anima_en_pena` | Wandering ghost made of mist, hollow eyes, ragged shroud, faint chains |
| `anima_errante` | Same ghost, taller and thinner, carrying a dim lantern |
| `candileja` | Floating ball of fire with the face of an old woman inside the flames |
| `patasola` | Wild woman with a single leg ending in a hoof, long tangled hair, fangs, jungle vines |
| `madremonte` | Tall woman made of moss, leaves and branches, hat of foliage hiding her face, glowing eyes |
| `sombreron` | Thin man in black with an enormous wide hat covering his face, heavy chains, two black dogs at his feet |
| `hombre_caiman` | Half man half caiman, human torso and arms, caiman head and tail, wet scales |
| `mohan` | Huge hairy river sorcerer, hair down to the waist, smoking a cigar, gold trinkets tangled in his hair |
| `madre_de_agua` | River spirit woman made of flowing water, feet turned backwards, small fish around her |
| `llorona` | Weeping woman in a long white dress, black hair over her face, mouth open in a wail |
| `silbon` | Extremely tall gaunt man with a straw hat, carrying a sack of bones over his shoulder, lips pursed whistling |
| `cura_sin_cabeza` | Headless priest in a black cassock, raising one hand in blessing, holding a candle |
| `anima_sola` | Lonely soul surrounded by low flames, hands chained and raised in prayer |
| `el_dorado` | Spanish conquistador entirely covered in gold, armor and skin fused into gold, empty eyes, standing in mud, imposing, larger than the others |
| `fraile` | Hooded friar in a brown habit, clutching a heavy chest, gold coins spilling |
| `alquimista` | Thin old alchemist with stained robes, flasks at his belt, hands dripping mud |

### Fondos

| Archivo | Descripción |
|---|---|
| `paramo` | High Andean paramo at dawn, frailejon plants, thick fog, cold blue-grey light |
| `selva` | Dense jungle, giant roots and hanging vines, deep green, shafts of dim light |
| `rio` | Wide dark river at dusk, muddy shore, a single large rock, reeds |
| `pueblo` | Small colonial village at midnight, whitewashed houses, closed doors, one church tower, full moon |
| `laguna` | Round crater lagoon almost empty, a deep notch cut in the rim, muddy bottom with scattered gold offerings |

## Lista de control

- [ ] 3 héroes
- [ ] 16 enemigos
- [ ] 5 fondos
- [ ] Todos con el mismo grosor de contorno y la misma paleta
- [ ] Se reconocen en miniatura
- [ ] Fondos quitados sin halos de color
