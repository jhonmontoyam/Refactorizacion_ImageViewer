package imageviewer;

import imageviewer.model.ImageModel;
import imageviewer.presenter.ImagePresenter;
import imageviewer.service.ImageLoader;
import imageviewer.view.MainFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Punto de entrada principal de la aplicación ImageViewer.
 * Inicializa y ensambla los componentes del patrón Model-View-Presenter (MVP).
 */
public class Main {

    public static void main(String[] args) {
        // Configurar Look & Feel nativo del sistema operativo si es posible
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Se mantiene el Look and Feel por defecto si no es posible aplicar el del sistema
        }

        SwingUtilities.invokeLater(() -> {
            // 1. Inicializar el Modelo
            ImageModel model = new ImageModel();

            // 2. Inicializar el Servicio de Carga
            ImageLoader loader = new ImageLoader();

            // 3. Inicializar la Vista
            MainFrame view = new MainFrame();

            // 4. Inicializar el Presentador y conectar los componentes MVP
            new ImagePresenter(view, model, loader);

            // 5. Hacer visible la interfaz gráfica
            view.setVisible(true);
        });
    }
}
