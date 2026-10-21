package imageviewer.view;

import imageviewer.presenter.ImagePresenter;
import java.awt.image.BufferedImage;

/**
 * Contrato de la Vista en la arquitectura MVP.
 * Define las operaciones que el presentador puede invocar sobre la interfaz gráfica.
 */
public interface ImageView {

    /**
     * Muestra la imagen cargada y sus metadatos en la interfaz de usuario.
     *
     * @param image Imagen a desplegar.
     * @param imageName Nombre del archivo.
     * @param width Ancho en píxeles.
     * @param height Alto en píxeles.
     * @param currentIndex Posición actual en la secuencia (1-indexed).
     * @param totalImages Total de imágenes en la carpeta.
     */
    void displayImage(BufferedImage image, String imageName, int width, int height, int currentIndex, int totalImages);

    /**
     * Muestra una notificación o diálogo de error al usuario.
     *
     * @param title Título del mensaje.
     * @param message Detalle del error.
     */
    void showErrorMessage(String title, String message);

    /**
     * Muestra un mensaje informativo en la barra de estado o cuadro de diálogo.
     *
     * @param message Texto informativo.
     */
    void showStatus(String message);

    /**
     * Actualiza el estado de habilitación de los controles de navegación (Anterior / Siguiente).
     *
     * @param hasPrevious true si hay una imagen previa.
     * @param hasNext true si hay una imagen siguiente.
     */
    void updateNavigationControls(boolean hasPrevious, boolean hasNext);

    /**
     * Limpia la vista cuando no hay ninguna imagen cargada.
     */
    void clearDisplay();

    /**
     * Asocia el presentador que gestionará los eventos de la vista.
     *
     * @param presenter Instancia de ImagePresenter.
     */
    void setPresenter(ImagePresenter presenter);
}
