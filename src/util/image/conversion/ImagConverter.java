package util.image.conversion;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

public class ImagConverter {
    private static final double WEIGHT_RED = 0.2989;
    private static final double WEIGHT_GREEN = 0.5870;
    private static final double WEIGHT_BLUE = 0.1140;

    public static BufferedImage convertColorImageToGrayImage(BufferedImage colorImage) {
        int height = colorImage.getHeight();
        int width = colorImage.getWidth();
        BufferedImage grayImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = new Color(colorImage.getRGB(x, y));
                int gray = (int) (WEIGHT_RED * color.getRed() + WEIGHT_GREEN * color.getGreen() + WEIGHT_BLUE * color.getBlue());
                Color grayColor = new Color(gray, gray, gray);
                grayImage.setRGB(x, y, grayColor.getRGB());
            }
        }
        return grayImage;
    }

    public static BufferedImage convertGrayImageToBinaryImage(BufferedImage grayImage, int threshold) {
        int height = grayImage.getHeight();
        int width = grayImage.getWidth();
        BufferedImage binaryImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = grayImage.getRGB(x, y);
                int gray = (rgb & 0xff0000) >> 16;
                if (gray > threshold) {
                    binaryImage.setRGB(x, y, 0xffffff);
                } else {
                    binaryImage.setRGB(x, y, 0);
                }
            }
        }
        return binaryImage;
    }

    public static Integer[][] convertGrayImageToGrayScale(BufferedImage grayImage) {
        int height = grayImage.getHeight();
        int width = grayImage.getWidth();
        Integer[][] grayScale = new Integer[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = grayImage.getRGB(x, y);
                int gray = (rgb & 0xff0000) >> 16;
                grayScale[y][x] = gray;
            }
        }
        return grayScale;
    }
}
