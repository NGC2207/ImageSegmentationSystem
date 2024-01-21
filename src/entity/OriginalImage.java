package entity;

import util.image.ImageSupporter;

import java.awt.image.BufferedImage;
import java.nio.file.Path;

public class OriginalImage {
    private final BufferedImage originalImage;
    private final boolean isOriginalImageColor;

    public OriginalImage(Path imagePath, boolean isOriginalImageColor) {
        if (imagePath == null) {
            throw new IllegalArgumentException("图像路径不能为空!");
        }
        this.originalImage = ImageSupporter.loadImage(imagePath);
        this.isOriginalImageColor = isOriginalImageColor;
    }

    public BufferedImage getOriginalImage() {
        return originalImage;
    }

    public boolean isOriginalImageColor() {
        return isOriginalImageColor;
    }
}
