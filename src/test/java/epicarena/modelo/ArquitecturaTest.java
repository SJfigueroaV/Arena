package epicarena.modelo;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

/** Vigila las reglas de arquitectura que exige el proyecto. */
class ArquitecturaTest {

    private static final Path FUENTES = Path.of("src", "main", "java", "epicarena");
    private static final Pattern LIBRERIA_GRAFICA = Pattern.compile("import\\s+(javax\\.swing|java\\.awt)");
    private static final Pattern PREGUNTA_POR_CLASE_CONCRETA =
            Pattern.compile("instanceof\\s+(Guerrero|Mago|Sacerdote)\\b");

    @Test
    void elModeloNoUsaLibreriasGraficas() throws IOException {
        List<Path> archivos = archivosJava(FUENTES.resolve("modelo"));
        assertFalse(archivos.isEmpty());
        for (Path archivo : archivos) {
            assertFalse(LIBRERIA_GRAFICA.matcher(Files.readString(archivo)).find(),
                    archivo + " importa una libreria grafica");
        }
    }

    @Test
    void nadiePreguntaPorLaClaseConcretaDelPersonaje() throws IOException {
        for (Path archivo : archivosJava(FUENTES)) {
            assertFalse(PREGUNTA_POR_CLASE_CONCRETA.matcher(Files.readString(archivo)).find(),
                    archivo + " rompe el polimorfismo con instanceof");
        }
    }

    @Test
    void personajeEsAbstracta() {
        assertTrue(java.lang.reflect.Modifier.isAbstract(Personaje.class.getModifiers()));
    }

    private List<Path> archivosJava(Path carpeta) throws IOException {
        try (Stream<Path> rutas = Files.walk(carpeta)) {
            return rutas.filter(ruta -> ruta.toString().endsWith(".java")).toList();
        }
    }
}
