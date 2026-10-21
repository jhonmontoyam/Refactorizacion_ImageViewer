package imageviewer.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias para ImageLoader")
public class ImageLoaderTest {

    private ImageLoader loader;

    @BeforeEach
    public void setUp() {
        loader = new ImageLoader();
    }

    @Test
    @DisplayName("Debe cargar exitosamente una imagen válida desde un archivo temporal")
    public void testLoadValidImage(@TempDir Path tempDir) throws IOException {
        // Crear una imagen de prueba válida en memoria y guardarla en disco temporal
        File imageFile = tempDir.resolve("imagen_valida.png").toFile();
        BufferedImage testImage = new BufferedImage(150, 100, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = testImage.createGraphics();
        g.setColor(Color.BLUE);
        g.fillRect(0, 0, 150, 100);
        g.dispose();

        boolean written = ImageIO.write(testImage, "png", imageFile);
        assertTrue(written, "ImageIO debió escribir el archivo PNG temporal.");

        // Ejecutar la carga mediante ImageLoader
        BufferedImage loadedImage = loader.loadImage(imageFile);

        assertNotNull(loadedImage, "La imagen cargada no debe ser nula.");
        assertEquals(150, loadedImage.getWidth(), "El ancho debe ser 150 px.");
        assertEquals(100, loadedImage.getHeight(), "El alto debe ser 100 px.");
    }

    @Test
    @DisplayName("Debe lanzar FileNotFoundException cuando el archivo no existe")
    public void testLoadNonExistentFile(@TempDir Path tempDir) {
        File nonExistent = tempDir.resolve("archivo_que_no_existe_12345.jpg").toFile();

        FileNotFoundException exception = assertThrows(
                FileNotFoundException.class,
                () -> loader.loadImage(nonExistent),
                "Debe lanzar FileNotFoundException ante un archivo inexistente."
        );
        assertTrue(exception.getMessage().contains("no existe"), "El mensaje debe indicar que no existe.");
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException ante parámetros nulos o rutas vacías")
    public void testLoadInvalidParameters() {
        assertThrows(
                IllegalArgumentException.class,
                () -> loader.loadImage((File) null),
                "Debe fallar si el File es nulo."
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> loader.loadImage((String) null),
                "Debe fallar si la ruta en String es nula."
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> loader.loadImage("   "),
                "Debe fallar si la ruta en String está vacía o son solo espacios."
        );
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException si la ruta apunta a un directorio en lugar de un archivo")
    public void testLoadDirectoryAsFile(@TempDir Path tempDir) {
        File directory = tempDir.toFile();

        assertThrows(
                IllegalArgumentException.class,
                () -> loader.loadImage(directory),
                "No se debe permitir cargar un directorio como imagen."
        );
    }

    @Test
    @DisplayName("Debe lanzar IOException cuando el archivo existe pero no es una imagen válida decodificable")
    public void testLoadCorruptedOrNonImageFile(@TempDir Path tempDir) throws IOException {
        File fakeImage = tempDir.resolve("corrupta.png").toFile();
        try (FileWriter writer = new FileWriter(fakeImage)) {
            writer.write("Esto es texto plano y no un archivo binario PNG valido.");
        }

        IOException exception = assertThrows(
                IOException.class,
                () -> loader.loadImage(fakeImage),
                "Debe lanzar IOException si ImageIO no puede decodificar el contenido."
        );
        assertTrue(exception.getMessage().contains("No fue posible decodificar"), "El mensaje debe advertir sobre fallo de decodificación.");
    }

    @Test
    @DisplayName("Debe filtrar y ordenar correctamente los archivos de imagen dentro de un directorio")
    public void testGetImageFilesInDirectory(@TempDir Path tempDir) throws IOException {
        // Crear archivos de imagen
        File imgB = tempDir.resolve("b_imagen.jpg").toFile();
        File imgA = tempDir.resolve("a_imagen.png").toFile();
        File txtFile = tempDir.resolve("notas.txt").toFile();
        File pdfFile = tempDir.resolve("documento.pdf").toFile();

        assertTrue(imgB.createNewFile());
        assertTrue(imgA.createNewFile());
        assertTrue(txtFile.createNewFile());
        assertTrue(pdfFile.createNewFile());

        List<File> images = loader.getImageFilesInDirectory(tempDir.toFile());

        assertEquals(2, images.size(), "Solo deben reconocerse los 2 archivos con extensión de imagen.");
        assertEquals("a_imagen.png", images.get(0).getName(), "Las imágenes deben estar ordenadas alfabéticamente (A primero).");
        assertEquals("b_imagen.jpg", images.get(1).getName());
    }

    @Test
    @DisplayName("Debe identificar adecuadamente las extensiones compatibles en isImageFile")
    public void testIsImageFile(@TempDir Path tempDir) throws IOException {
        File validJpg = tempDir.resolve("foto.JPG").toFile();
        File validPng = tempDir.resolve("dibujo.png").toFile();
        File docx = tempDir.resolve("informe.docx").toFile();

        assertTrue(validJpg.createNewFile());
        assertTrue(validPng.createNewFile());
        assertTrue(docx.createNewFile());

        assertTrue(loader.isImageFile(validJpg), "Debe soportar JPG independientemente de mayúsculas.");
        assertTrue(loader.isImageFile(validPng), "Debe soportar PNG.");
        assertFalse(loader.isImageFile(docx), "No debe soportar extensiones no gráficas como docx.");
        assertFalse(loader.isImageFile(null), "Null debe retornar false.");
    }
}
