import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.util.Random;

class Filters {
    public static final String BLACK_WHITE = "Black & White";
    public static final String GRAYSCALE = "Grayscale";
    public static final String MIRROR = "Mirror";
    public static final String BRIGHTER = "Brighter";
    public static final String DARKER = "Darker";
    public static final String VIGNETTE = "Vignette";
    public static final String SHOW_BORDERS = "Show Borders";
    public static final String SEPIA = "Sepia";
    public static final String ADD_NOISE = "Add Noise";
    public static final String VINTAGE = "Vintage";
    public static final String CONTRAST = "Contrast";
    public static final String NEGATIVE = "Negative";
    public static final int INITIAL_VALUE = 0;
    private static final int MAX_VALUE = 255;
    private static final int MIN_VALUE = 0;
    public static BufferedImage applyFilter(BufferedImage src, String filterName) {
        return switch (filterName) {
            case BLACK_WHITE -> toBlackWhite(src);
            case GRAYSCALE -> toGrayscale(src);
            case MIRROR -> toMirror(src);
            case BRIGHTER -> toBrighter(src);
            case DARKER -> toDarker(src);
            case VIGNETTE -> toVignette(src);
            case SHOW_BORDERS -> toShowBorders(src);
            case SEPIA -> toSepia(src);
            case ADD_NOISE -> addNoise(src);
            case VINTAGE -> toVintage(src);
            case CONTRAST -> toContrast(src);
            case NEGATIVE -> toNegative(src);
            default -> src;
        };
    }

    public static BufferedImage toBlackWhite(BufferedImage src) {
        BufferedImage result = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_BYTE_BINARY);
        Graphics g = result.getGraphics();
        g.drawImage(src, INITIAL_VALUE, INITIAL_VALUE, null);
        g.dispose();
        return result;
    }

    public static BufferedImage toGrayscale(BufferedImage src) {
        BufferedImage result = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        Graphics g = result.getGraphics();
        g.drawImage(src, INITIAL_VALUE, INITIAL_VALUE, null);
        g.dispose();
        return result;
    }

    public static BufferedImage toMirror(BufferedImage src) {
        int width = src.getWidth();
        int height = src.getHeight();
        BufferedImage result = new BufferedImage(width, height, src.getType());
        for (int y = INITIAL_VALUE; y < height; y++) {
            for (int x = INITIAL_VALUE; x < width; x++) {
                result.setRGB(width - x - 1, y, src.getRGB(x, y));
            }
        }
        return result;
    }

    public static BufferedImage toBrighter(BufferedImage src) {
        BufferedImage result = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int y = INITIAL_VALUE; y < src.getHeight(); y++) {
            for (int x = INITIAL_VALUE; x < src.getWidth(); x++) {
                Color color = new Color(src.getRGB(x, y));
                int r = Math.min(color.getRed() + 50, MAX_VALUE);
                int g = Math.min(color.getGreen() + 50, MAX_VALUE);
                int b = Math.min(color.getBlue() + 50, MAX_VALUE);
                result.setRGB(x, y, new Color(r, g, b).getRGB());
            }
        }
        return result;
    }

    public static BufferedImage toDarker(BufferedImage src) {
        if (src.getType() == BufferedImage.TYPE_BYTE_INDEXED) {
            BufferedImage convertedImg = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D g = convertedImg.createGraphics();
            g.drawImage(src, INITIAL_VALUE, INITIAL_VALUE, null);
            g.dispose();
            src = convertedImg;
        }
        BufferedImage result = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_RGB);
        float scaleFactor = 0.5f;
        RescaleOp op = new RescaleOp(scaleFactor, 0, null);
        op.filter(src, result);
        return result;
    }

    public static BufferedImage toVignette(BufferedImage src) {
        BufferedImage result = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_RGB);
        int width = src.getWidth();
        int height = src.getHeight();
        float radius = width / 2f;
        float[] dist = {0.0f, 0.5f, 1.0f};
        Color[] colors = {new Color(MIN_VALUE, MIN_VALUE, MIN_VALUE, MIN_VALUE), new Color(MIN_VALUE, MIN_VALUE, MIN_VALUE, 127), new Color(MIN_VALUE, MIN_VALUE, MIN_VALUE, MAX_VALUE)};
        RadialGradientPaint paint = new RadialGradientPaint(new Point2D.Float((float) width / 2, (float) height / 2), radius, dist, colors);
        Graphics2D g2d = result.createGraphics();
        g2d.drawImage(src, INITIAL_VALUE, INITIAL_VALUE, null);
        g2d.setPaint(paint);
        g2d.fillRect(INITIAL_VALUE, INITIAL_VALUE, width, height);
        g2d.dispose();
        return result;
    }

    public static BufferedImage toShowBorders(BufferedImage src) {
        int width = src.getWidth();
        int height = src.getHeight();
        BufferedImage grayImage = toGrayscale(src);
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        int[][] sobelX = {
                {-1, 0, 1},
                {-2, 0, 2},
                {-1, 0, 1}
        };
        int[][] sobelY = {
                {-1, -2, -1},
                {0, 0, 0},
                {1, 2, 1}
        };

        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                int pixelX = applySobel(grayImage, x, y, sobelX);
                int pixelY = applySobel(grayImage, x, y, sobelY);
                int magnitude = (int) Math.sqrt((pixelX * pixelX) + (pixelY * pixelY));
                magnitude = Math.min(magnitude, MAX_VALUE);
                int edgeColor = new Color(magnitude, magnitude, magnitude).getRGB();
                result.setRGB(x, y, edgeColor);
            }
        }

        for (int y = INITIAL_VALUE; y < height; y++) {
            for (int x = INITIAL_VALUE; x < width; x++) {
                Color color = new Color(result.getRGB(x, y));
                if (color.getRed() > 128) {
                    result.setRGB(x, y, Color.BLACK.getRGB());
                } else {
                    result.setRGB(x, y, src.getRGB(x, y));
                }
            }
        }
        return result;
    }

    private static int applySobel(BufferedImage image, int x, int y, int[][] sobel) {
        int pixelValue = MIN_VALUE;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int rgb = new Color(image.getRGB(x + j, y + i)).getRed();
                pixelValue += sobel[i + 1][j + 1] * rgb;
            }
        }
        return pixelValue;
    }

    public static BufferedImage toSepia(BufferedImage src) {
        BufferedImage result = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int y = INITIAL_VALUE; y < src.getHeight(); y++) {
            for (int x = INITIAL_VALUE; x < src.getWidth(); x++) {
                Color color = new Color(src.getRGB(x, y));
                int tr = (int) (0.393 * color.getRed() + 0.769 * color.getGreen() + 0.189 * color.getBlue());
                int tg = (int) (0.349 * color.getRed() + 0.686 * color.getGreen() + 0.168 * color.getBlue());
                int tb = (int) (0.272 * color.getRed() + 0.534 * color.getGreen() + 0.131 * color.getBlue());
                result.setRGB(x, y, new Color(Math.min(tr, MAX_VALUE), Math.min(tg, MAX_VALUE), Math.min(tb, MAX_VALUE)).getRGB());
            }
        }
        return result;
    }

    public static BufferedImage addNoise(BufferedImage src) {
        BufferedImage result = new BufferedImage(src.getWidth(), src.getHeight(), src.getType());
        Random random = new Random();
        for (int y = INITIAL_VALUE; y < src.getHeight(); y++) {
            for (int x = INITIAL_VALUE; x < src.getWidth(); x++) {
                Color color = new Color(src.getRGB(x, y));
                int noise = random.nextInt(50) - 25;
                int r = Math.min(Math.max(color.getRed() + noise, MIN_VALUE), MAX_VALUE);
                int g = Math.min(Math.max(color.getGreen() + noise, MIN_VALUE), MAX_VALUE);
                int b = Math.min(Math.max(color.getBlue() + noise, MIN_VALUE), MAX_VALUE);
                result.setRGB(x, y, new Color(r, g, b).getRGB());
            }
        }
        return result;
    }

    public static BufferedImage toVintage(BufferedImage src) {
        return addNoise(toSepia(src));
    }

    public static BufferedImage toContrast(BufferedImage src) {
        BufferedImage result = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = result.createGraphics();
        g2d.drawImage(src, INITIAL_VALUE, INITIAL_VALUE, null);
        g2d.dispose();
        RescaleOp rescaleOp = new RescaleOp(1.5f, 0, null);
        rescaleOp.filter(result, result);
        return result;
    }
    public static BufferedImage toNegative(BufferedImage src) {
        BufferedImage result = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int y = INITIAL_VALUE; y < src.getHeight(); y++) {
            for (int x = INITIAL_VALUE; x < src.getWidth(); x++) {
                Color color = new Color(src.getRGB(x, y));
                int r = MAX_VALUE - color.getRed();
                int g = MAX_VALUE - color.getGreen();
                int b = MAX_VALUE - color.getBlue();
                result.setRGB(x, y, new Color(r, g, b).getRGB());
            }
        }
        return result;
    }
}