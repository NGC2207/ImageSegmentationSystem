package entity;

import util.image.ImageSupporter;

import java.awt.image.BufferedImage;

public class GrayImage {
    private BufferedImage grayImage;

    public GrayImage(BufferedImage originalImage, boolean isOriginalImageColor) {
        if (originalImage == null) {
            throw new IllegalArgumentException("图像不能为空!");
        }
        this.grayImage = ImageSupporter.convertOriginalImageToGrayImage(originalImage, isOriginalImageColor);
    }

    public BufferedImage getGrayImage() {
        return grayImage;
    }
}
