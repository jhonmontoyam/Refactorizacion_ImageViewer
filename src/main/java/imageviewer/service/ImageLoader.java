package imageviewer.service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Servicio encargado de la carga y validación de imágenes desde el sistema de archivos local.
 */
public class ImageLoader {

    private static final List<String> SUPPORTED_EXTENSIONS = Arrays.asList(
            ".jpg", ".jpeg", ".png", ".gif", ".bmp"
    );

    /**
     * Carga una imagen a partir de un objeto File.
     *
     * @param file Archivo de imagen a cargar.
     * @return BufferedImage cargado en memoria.
     * @throws IOException Si ocurre un error de lectura o el formato no es compatible.
     * @throws IllegalArgumentException Si el parámetro es nulo o no representa un archivo válido.
     */
    public BufferedImage loadImage(File file) throws IOException, IllegalArgumentException {
        if (file == null) {
            throw new IllegalArgumentException("El archivo de imagen no puede ser nulo.");
        }
        if (!file.exists()) {
            throw new FileNotFoundException("El archivo especificado no existe: " + file.getAbsolutePath());
        }
        if (!file.isFile()) {
            throw new IllegalArgumentException("La ruta especificada no corresponde a un archivo: " + file.getAbsolutePath());
        }
        if (!file.canRead()) {
            throw new IOException("No se tienen permisos de lectura para el archivo: " + file.getAbsolutePath());
        }

        BufferedImage image = ImageIO.read(file);
        if (image == null) {
            throw new IOException("No fue posible decodificar la imagen o el formato no es compatible: " + file.getName());
        }

        return image;
    }

    /**
     * Carga una imagen a partir de la ruta del archivo.
     *
     * @param filePath Ruta del archivo en el sistema de archivos.
     * @return BufferedImage cargado en memoria.
     * @throws IOException Si ocurre un error de lectura o el formato no es compatible.
     * @throws IllegalArgumentException Si la ruta es nula o vacía.
     */
    public BufferedImage loadImage(String filePath) throws IOException, IllegalArgumentException {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("La ruta del archivo no puede ser nula ni estar vacía.");
        }
        return loadImage(new File(filePath));
    }

    /**
     * Obtiene la lista de archivos de imagen válidos dentro de un directorio, ordenados alfabéticamente.
     *
     * @param directory Directorio a inspeccionar.
     * @return Lista de archivos de imagen soportados.
     */
    public List<File> getImageFilesInDirectory(File directory) {
        if (directory == null || !directory.exists() || !directory.isDirectory()) {
            return Collections.emptyList();
        }

        File[] files = directory.listFiles((dir, name) -> isSupportedExtension(name));
        if (files == null) {
            return Collections.emptyList();
        }

        List<File> imageFiles = new ArrayList<>(Arrays.asList(files));
        imageFiles.sort((f1, f2) -> f1.getName().compareToIgnoreCase(f2.getName()));
        return imageFiles;
    }

    /**
     * Comprueba si un archivo tiene una extensión de imagen compatible.
     *
     * @param file Archivo a evaluar.
     * @return true si la extensión está soportada, false en caso contrario.
     */
    public boolean isImageFile(File file) {
        if (file == null || !file.isFile()) {
            return false;
        }
        return isSupportedExtension(file.getName());
    }

    private boolean isSupportedExtension(String fileName) {
        if (fileName == null) {
            return false;
        }
        String lower = fileName.toLowerCase(Locale.ROOT);
        for (String ext : SUPPORTED_EXTENSIONS) {
            if (lower.endsWith(ext)) {
                return true;
            }
        }
        return false;
    }
}
