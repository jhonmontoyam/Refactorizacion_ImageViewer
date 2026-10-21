package imageviewer.presenter;

import imageviewer.model.ImageModel;
import imageviewer.service.ImageLoader;
import imageviewer.view.ImageView;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Presentador en la arquitectura MVP.
 * Orquesta la lógica entre la interfaz de usuario (ImageView), los datos (ImageModel)
 * y el servicio de lectura de archivos (ImageLoader).
 */
public class ImagePresenter {

    private final ImageView view;
    private final ImageModel model;
    private final ImageLoader loader;

    public ImagePresenter(ImageView view, ImageModel model, ImageLoader loader) {
        if (view == null || model == null || loader == null) {
            throw new IllegalArgumentException("Ninguno de los componentes (view, model, loader) puede ser nulo.");
        }
        this.view = view;
        this.model = model;
        this.loader = loader;

        this.view.setPresenter(this);
        this.view.updateNavigationControls(false, false);
        this.view.showStatus("Listo. Seleccione una imagen para visualizar.");
    }

    /**
     * Procesa la solicitud de carga de un archivo de imagen específico.
     *
     * @param file Archivo a cargar.
     */
    public void handleOpenImage(File file) {
        if (file == null) {
            return;
        }

        try {
            BufferedImage image = loader.loadImage(file);
            File parentDir = file.getParentFile();
            if (parentDir != null) {
                List<File> directoryImages = loader.getImageFilesInDirectory(parentDir);
                model.setPlaylist(directoryImages, file);
            }
            model.setCurrentImage(image, file);
            updateViewWithCurrentModel();
            view.showStatus("Imagen cargada exitosamente: " + file.getName());
        } catch (IOException | IllegalArgumentException e) {
            view.showErrorMessage("Error al cargar imagen", e.getMessage());
            view.showStatus("Error al intentar abrir el archivo.");
        }
    }

    /**
     * Navega a la imagen anterior en la carpeta actual.
     */
    public void handlePreviousImage() {
        if (!model.hasPrevious()) {
            return;
        }
        File prevFile = model.getPreviousFile();
        if (prevFile != null) {
            loadFileFromPlaylist(prevFile);
        }
    }

    /**
     * Navega a la siguiente imagen en la carpeta actual.
     */
    public void handleNextImage() {
        if (!model.hasNext()) {
            return;
        }
        File nextFile = model.getNextFile();
        if (nextFile != null) {
            loadFileFromPlaylist(nextFile);
        }
    }

    private void loadFileFromPlaylist(File file) {
        try {
            BufferedImage image = loader.loadImage(file);
            model.setCurrentImage(image, file);
            updateViewWithCurrentModel();
            view.showStatus("Mostrando imagen: " + file.getName());
        } catch (IOException | IllegalArgumentException e) {
            view.showErrorMessage("Error en navegación", "No se pudo cargar la imagen: " + e.getMessage());
        }
    }

    private void updateViewWithCurrentModel() {
        if (model.hasImage()) {
            int displayIndex = model.getCurrentIndex() >= 0 ? model.getCurrentIndex() + 1 : 1;
            int total = Math.max(model.getTotalImages(), 1);
            view.displayImage(
                    model.getCurrentImage(),
                    model.getCurrentImageName(),
                    model.getImageWidth(),
                    model.getImageHeight(),
                    displayIndex,
                    total
            );
            view.updateNavigationControls(model.hasPrevious(), model.hasNext());
        } else {
            view.clearDisplay();
            view.updateNavigationControls(false, false);
        }
    }

    public ImageModel getModel() {
        return model;
    }

    public ImageLoader getLoader() {
        return loader;
    }

    public ImageView getView() {
        return view;
    }
}
