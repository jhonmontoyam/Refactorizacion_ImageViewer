package imageviewer.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias para ImageModel")
public class ImageModelTest {

    private ImageModel model;

    @BeforeEach
    public void setUp() {
        model = new ImageModel();
    }

    @Test
    @DisplayName("El modelo recién instanciado debe tener un estado inicial vacío y consistente")
    public void testInitialState() {
        assertFalse(model.hasImage(), "Inicialmente no debe haber imagen.");
        assertNull(model.getCurrentImage(), "La imagen actual debe ser null.");
        assertNull(model.getCurrentFile(), "El archivo actual debe ser null.");
        assertEquals(-1, model.getCurrentIndex(), "El índice inicial debe ser -1.");
        assertEquals(0, model.getTotalImages(), "El total de imágenes debe ser 0.");
        assertEquals(0, model.getImageWidth(), "El ancho inicial debe ser 0.");
        assertEquals(0, model.getImageHeight(), "El alto inicial debe ser 0.");
        assertFalse(model.hasPrevious(), "No debe haber imagen previa.");
        assertFalse(model.hasNext(), "No debe haber imagen siguiente.");
        assertEquals("Sin imagen seleccionada", model.getCurrentImageName());
        assertEquals("", model.getCurrentImagePath());
    }

    @Test
    @DisplayName("Debe actualizar y consultar correctamente la imagen actual y sus dimensiones")
    public void testSetAndGetCurrentImage() {
        BufferedImage sampleImage = new BufferedImage(320, 240, BufferedImage.TYPE_INT_RGB);
        File sampleFile = new File("foto_prueba.jpg");

        model.setCurrentImage(sampleImage, sampleFile);

        assertTrue(model.hasImage(), "El modelo debe indicar que contiene una imagen.");
        assertSame(sampleImage, model.getCurrentImage(), "La imagen obtenida debe ser la misma que se asignó.");
        assertEquals(sampleFile, model.getCurrentFile(), "El archivo asociado debe coincidir.");
        assertEquals(320, model.getImageWidth(), "El ancho debe ser 320 px.");
        assertEquals(240, model.getImageHeight(), "El alto debe ser 240 px.");
        assertEquals("foto_prueba.jpg", model.getCurrentImageName());
        assertEquals(sampleFile.getAbsolutePath(), model.getCurrentImagePath());
    }

    @Test
    @DisplayName("Debe gestionar adecuadamente la navegación (anterior y siguiente) dentro de una lista de imágenes")
    public void testPlaylistNavigation() {
        File file1 = new File("img1.png");
        File file2 = new File("img2.png");
        File file3 = new File("img3.png");
        List<File> playlist = Arrays.asList(file1, file2, file3);

        model.setPlaylist(playlist, file1);

        assertEquals(3, model.getTotalImages(), "El total de imágenes debe ser 3.");
        assertEquals(0, model.getCurrentIndex(), "El índice activo inicial debe ser 0.");
        assertFalse(model.hasPrevious(), "En el primer elemento no debe haber anterior.");
        assertTrue(model.hasNext(), "En el primer elemento debe haber siguiente.");
        assertEquals(file2, model.getNextFile(), "La siguiente imagen debe ser img2.png.");

        // Avanzar a la segunda imagen
        model.setCurrentImage(new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB), file2);
        assertEquals(1, model.getCurrentIndex(), "El índice activo ahora debe ser 1.");
        assertTrue(model.hasPrevious(), "En la posición intermedia debe haber anterior.");
        assertTrue(model.hasNext(), "En la posición intermedia debe haber siguiente.");
        assertEquals(file1, model.getPreviousFile(), "La anterior imagen debe ser img1.png.");
        assertEquals(file3, model.getNextFile(), "La siguiente imagen debe ser img3.png.");

        // Avanzar a la última imagen
        model.setCurrentImage(new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB), file3);
        assertEquals(2, model.getCurrentIndex(), "El índice activo debe ser 2.");
        assertTrue(model.hasPrevious(), "En la última posición debe haber anterior.");
        assertFalse(model.hasNext(), "En la última posición no debe haber siguiente.");
        assertNull(model.getNextFile(), "Intentar obtener siguiente al final debe retornar null.");
    }

    @Test
    @DisplayName("Debe reiniciar correctamente todos los valores al invocar clear()")
    public void testClearModel() {
        BufferedImage sampleImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        File sampleFile = new File("foto.png");
        model.setPlaylist(Arrays.asList(sampleFile), sampleFile);
        model.setCurrentImage(sampleImage, sampleFile);

        model.clear();

        assertFalse(model.hasImage());
        assertNull(model.getCurrentImage());
        assertNull(model.getCurrentFile());
        assertEquals(-1, model.getCurrentIndex());
        assertEquals(0, model.getTotalImages());
    }

    @Test
    @DisplayName("Casos límite: manejo de listas nulas o vacías")
    public void testBoundaryCases() {
        assertDoesNotThrow(() -> model.setPlaylist(null, null), "setPlaylist con nulos no debe lanzar excepción.");
        assertEquals(0, model.getTotalImages());
        assertEquals(-1, model.getCurrentIndex());
        assertNull(model.getPreviousFile());
        assertNull(model.getNextFile());
    }
}
