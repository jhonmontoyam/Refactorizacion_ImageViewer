package imageviewer.model;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Modelo que encapsula el estado de la imagen actual y la navegación del visor.
 * No posee dependencias con la interfaz gráfica ni lógica de renderizado.
 */
public class ImageModel {

    private BufferedImage currentImage;
    private File currentFile;
    private final List<File> playlist;
    private int currentIndex;

    public ImageModel() {
        this.playlist = new ArrayList<>();
        this.currentIndex = -1;
        this.currentImage = null;
        this.currentFile = null;
    }

    /**
     * Establece la imagen actual y el archivo asociado.
     *
     * @param image Imagen en memoria (puede ser nula si se limpia).
     * @param file  Archivo en disco correspondiente a la imagen.
     */
    public void setCurrentImage(BufferedImage image, File file) {
        this.currentImage = image;
        this.currentFile = file;
        if (file != null && !playlist.isEmpty()) {
            this.currentIndex = playlist.indexOf(file);
        }
    }

    /**
     * Define la lista de archivos para navegación secuencial (anterior / siguiente).
     *
     * @param files Lista de archivos disponibles.
     * @param activeFile Archivo que debe seleccionarse como activo.
     */
    public void setPlaylist(List<File> files, File activeFile) {
        this.playlist.clear();
        if (files != null) {
            this.playlist.addAll(files);
        }
        if (activeFile != null && !this.playlist.isEmpty()) {
            this.currentIndex = this.playlist.indexOf(activeFile);
        } else {
            this.currentIndex = this.playlist.isEmpty() ? -1 : 0;
        }
    }

    public BufferedImage getCurrentImage() {
        return currentImage;
    }

    public File getCurrentFile() {
        return currentFile;
    }

    public boolean hasImage() {
        return currentImage != null;
    }

    public String getCurrentImageName() {
        return currentFile != null ? currentFile.getName() : "Sin imagen seleccionada";
    }

    public String getCurrentImagePath() {
        return currentFile != null ? currentFile.getAbsolutePath() : "";
    }

    public int getImageWidth() {
        return currentImage != null ? currentImage.getWidth() : 0;
    }

    public int getImageHeight() {
        return currentImage != null ? currentImage.getHeight() : 0;
    }

    public List<File> getPlaylist() {
        return Collections.unmodifiableList(playlist);
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public int getTotalImages() {
        return playlist.size();
    }

    public boolean hasPrevious() {
        return currentIndex > 0 && !playlist.isEmpty();
    }

    public boolean hasNext() {
        return currentIndex >= 0 && currentIndex < playlist.size() - 1;
    }

    public File getPreviousFile() {
        if (hasPrevious()) {
            return playlist.get(currentIndex - 1);
        }
        return null;
    }

    public File getNextFile() {
        if (hasNext()) {
            return playlist.get(currentIndex + 1);
        }
        return null;
    }

    /**
     * Reinicia el modelo al estado inicial vacío.
     */
    public void clear() {
        this.currentImage = null;
        this.currentFile = null;
        this.playlist.clear();
        this.currentIndex = -1;
    }
}
