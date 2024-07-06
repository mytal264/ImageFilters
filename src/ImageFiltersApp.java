import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;
import javax.imageio.ImageIO;

public class ImageFiltersApp extends JFrame {
    private final JPanel homePanel;
    private final JPanel mainPanel;
    private final ImagePanel imagePanel;
    private BufferedImage backgroundImage;
    public static final String[] FILTER_NAMES = {Filters.BLACK_WHITE, Filters.GRAYSCALE, Filters.MIRROR,
            Filters.BRIGHTER, Filters.DARKER, Filters.VIGNETTE, Filters.SHOW_BORDERS, Filters.SEPIA,
            Filters.ADD_NOISE, Filters.VINTAGE, Filters.CONTRAST, Filters.NEGATIVE};

    public ImageFiltersApp() {
        setTitle("Image Filters");
        int HEIGHT = 600;
        int WIDTH = 800;
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        try {
            backgroundImage = ImageIO.read(Objects.requireNonNull(getClass().getResource("/resources/logo.jpg")));
        } catch (IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }

        homePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        homePanel.setLayout(new BorderLayout());
        JPanel UploadButtonPanel = getPanel();
        homePanel.add(UploadButtonPanel, BorderLayout.SOUTH);
        imagePanel = new ImagePanel();
        mainPanel = new JPanel(new BorderLayout());

        JPanel filterButtonPanel = new JPanel();
        filterButtonPanel.setLayout(new BoxLayout(filterButtonPanel, BoxLayout.X_AXIS));
        for (String filterName : FILTER_NAMES) {
            JButton button = new JButton(filterName);
            button.addActionListener(e -> imagePanel.applyFilter(filterName));
            filterButtonPanel.add(button);
        }

        JScrollPane scrollPane = new JScrollPane(filterButtonPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        JPanel upperPanel = getjPanel();

        mainPanel.add(imagePanel, BorderLayout.CENTER);
        mainPanel.add(scrollPane, BorderLayout.SOUTH);
        mainPanel.add(upperPanel, BorderLayout.NORTH);

        add(homePanel, BorderLayout.CENTER);
    }

    private JPanel getPanel() {
        JButton uploadButton = new JButton("פתח תמונה");
        uploadButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                showMainPanel(fileChooser.getSelectedFile().getAbsolutePath());
            }
        });
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.add(uploadButton);
        return buttonPanel;
    }

    private JPanel getjPanel() {
        JPanel upperPanel = new JPanel(new BorderLayout());
        JButton uploadNewImageButton = new JButton("בחר תמונה חדשה");
        uploadNewImageButton.addActionListener(e -> showHomePanel());
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.add(uploadNewImageButton);

        JLabel instructionLabel = new JLabel("הפילטר שבחרת יחול על הצד השמאלי לעכבר בכל זמן נתון.");
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.add(instructionLabel);

        upperPanel.add(leftPanel, BorderLayout.WEST);
        upperPanel.add(rightPanel, BorderLayout.EAST);
        return upperPanel;
    }

    private void showMainPanel(String imagePath) {
        imagePanel.loadImage(imagePath);
        getContentPane().removeAll();
        add(mainPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void showHomePanel() {
        getContentPane().removeAll();
        add(homePanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }
}