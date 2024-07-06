import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

class ImagePanel extends JPanel {
    private BufferedImage originalImage;
    private int mouseX = Filters.INITIAL_VALUE;
    private String currentFilter = "";

    public ImagePanel() {
        setBackground(Color.WHITE);
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mouseX = e.getX();
                repaint();
            }
        });
    }

    public void loadImage(String path) {
        try {
            originalImage = ImageIO.read(new File(path));
            revalidate();
            repaint();
        } catch (IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    public void applyFilter(String filterName) {
        if (originalImage != null) {
            currentFilter = filterName;
            repaint();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (originalImage != null) {
            int panelWidth = getWidth();
            int panelHeight = getHeight();
            int imageWidth = originalImage.getWidth();
            int imageHeight = originalImage.getHeight();

            double widthRatio = (double) panelWidth / imageWidth;
            double heightRatio = (double) panelHeight / imageHeight;
            double scale = Math.min(widthRatio, heightRatio);

            int drawWidth = imageWidth;
            int drawHeight = imageHeight;
            int x = (panelWidth - drawWidth) / 2;
            int y = (panelHeight - drawHeight) / 2;

            if (imageWidth > panelWidth || imageHeight > panelHeight) {
                drawWidth = (int) (imageWidth * scale);
                drawHeight = (int) (imageHeight * scale);
                x = (panelWidth - drawWidth) / 2;
                y = (panelHeight - drawHeight) / 2;
                g.drawImage(originalImage, x, y, drawWidth, drawHeight, this);
            } else {
                g.drawImage(originalImage, x, y, drawWidth, drawHeight, this);
            }
            int divideX = mouseX - x;
            if (divideX > Filters.INITIAL_VALUE) {
                BufferedImage filteredImage = Filters.applyFilter(originalImage, currentFilter);
                if (divideX < drawWidth) {
                    g.drawImage(filteredImage.getSubimage(Filters.INITIAL_VALUE, Filters.INITIAL_VALUE, (int) (divideX / scale), imageHeight).getScaledInstance(divideX, drawHeight, Image.SCALE_SMOOTH), x, y, divideX, drawHeight, this);
                    g.setColor(Color.RED);
                    int lineX = x + divideX;
                    g.drawLine(lineX, y, lineX, y + drawHeight);
                } else {
                    g.drawImage(filteredImage.getScaledInstance(drawWidth, drawHeight, Image.SCALE_SMOOTH), x, y, drawWidth, drawHeight, this);
                }
            }
        }
    }
}