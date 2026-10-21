package imageviewer.view;

import imageviewer.presenter.ImagePresenter;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Ventana principal de la aplicación.
 * Implementa la interfaz ImageView y actúa como la Vista (View) en el patrón MVP.
 * Delega los eventos del usuario a su ImagePresenter y no contiene lógica de negocio.
 */
public class MainFrame extends JFrame implements ImageView {

    private ImagePresenter presenter;

    // Componentes gráficos
    private JButton openButton;
    private JButton prevButton;
    private JButton nextButton;
    private JLabel positionLabel;
    private JLabel statusLabel;
    private ImageCanvasPanel canvasPanel;

    public MainFrame() {
        super("ImageViewer - Visor de Imágenes MVP");
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 650);
        setMinimumSize(new Dimension(600, 450));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Barra de herramientas superior
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));

        openButton = new JButton("Abrir Imagen...");
        openButton.setFocusPainted(false);
        openButton.addActionListener(e -> onOpenImageClicked());

        prevButton = new JButton("◀ Anterior");
        prevButton.setFocusPainted(false);
        prevButton.setEnabled(false);
        prevButton.addActionListener(e -> {
            if (presenter != null) {
                presenter.handlePreviousImage();
            }
        });

        nextButton = new JButton("Siguiente ▶");
        nextButton.setFocusPainted(false);
        nextButton.setEnabled(false);
        nextButton.addActionListener(e -> {
            if (presenter != null) {
                presenter.handleNextImage();
            }
        });

        positionLabel = new JLabel(" Sin imágenes ");
        positionLabel.setForeground(Color.DARK_GRAY);

        toolBar.add(openButton);
        toolBar.add(Box.createHorizontalStrut(10));
        toolBar.addSeparator();
        toolBar.add(Box.createHorizontalStrut(10));
        toolBar.add(prevButton);
        toolBar.add(Box.createHorizontalStrut(5));
        toolBar.add(nextButton);
        toolBar.add(Box.createHorizontalStrut(15));
        toolBar.add(positionLabel);

        add(toolBar, BorderLayout.NORTH);

        // Panel central de visualización de imagen
        canvasPanel = new ImageCanvasPanel();
        add(canvasPanel, BorderLayout.CENTER);

        // Barra de estado inferior
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)
        ));
        statusLabel = new JLabel("Listo.");
        statusBar.add(statusLabel, BorderLayout.WEST);

        add(statusBar, BorderLayout.SOUTH);
    }

    private void onOpenImageClicked() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Seleccionar imagen");
        fileChooser.setFileFilter(new FileNameExtensionFilter(
                "Imágenes (*.jpg, *.png, *.gif, *.bmp)", "jpg", "jpeg", "png", "gif", "bmp"
        ));

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            if (presenter != null) {
                presenter.handleOpenImage(selectedFile);
            }
        }
    }

    @Override
    public void setPresenter(ImagePresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void displayImage(BufferedImage image, String imageName, int width, int height, int currentIndex, int totalImages) {
        SwingUtilities.invokeLater(() -> {
            canvasPanel.setImage(image);
            setTitle("ImageViewer - " + imageName + " (" + width + "x" + height + " px)");
            positionLabel.setText(" Imagen: " + currentIndex + " / " + totalImages + " ");
            showStatus(imageName + " | " + width + "x" + height + " px");
        });
    }

    @Override
    public void showErrorMessage(String title, String message) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
        });
    }

    @Override
    public void showStatus(String message) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText(message);
        });
    }

    @Override
    public void updateNavigationControls(boolean hasPrevious, boolean hasNext) {
        SwingUtilities.invokeLater(() -> {
            prevButton.setEnabled(hasPrevious);
            nextButton.setEnabled(hasNext);
        });
    }

    @Override
    public void clearDisplay() {
        SwingUtilities.invokeLater(() -> {
            canvasPanel.setImage(null);
            setTitle("ImageViewer - Visor de Imágenes MVP");
            positionLabel.setText(" Sin imágenes ");
            showStatus("No hay imagen cargada.");
        });
    }

    /**
     * Panel personalizado para renderizar imágenes ajustando escala y manteniendo relación de aspecto.
     */
    private static class ImageCanvasPanel extends JPanel {
        private BufferedImage image;

        public ImageCanvasPanel() {
            setBackground(new Color(245, 246, 248));
        }

        public void setImage(BufferedImage image) {
            this.image = image;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (image == null) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setColor(new Color(128, 134, 142));
                g2.setFont(new Font("SansSerif", Font.PLAIN, 16));
                String msg = "No hay ninguna imagen cargada. Presione 'Abrir Imagen...' para comenzar.";
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(msg)) / 2;
                int y = getHeight() / 2;
                g2.drawString(msg, Math.max(10, x), y);
                g2.dispose();
                return;
            }

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            int panelWidth = getWidth();
            int panelHeight = getHeight();
            int imgWidth = image.getWidth();
            int imgHeight = image.getHeight();

            double scale = Math.min((double) panelWidth / imgWidth, (double) panelHeight / imgHeight);
            if (scale > 1.0) {
                scale = 1.0; // Mantener tamaño original si cabe holgadamente
            }

            int targetWidth = (int) (imgWidth * scale);
            int targetHeight = (int) (imgHeight * scale);
            int x = (panelWidth - targetWidth) / 2;
            int y = (panelHeight - targetHeight) / 2;

            g2.drawImage(image, x, y, targetWidth, targetHeight, null);
            g2.dispose();
        }
    }
}
