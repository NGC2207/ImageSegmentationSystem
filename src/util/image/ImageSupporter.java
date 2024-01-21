package util.image;

import util.image.conversion.ImagConverter;
import util.image.io.ImagIO;

import java.awt.image.BufferedImage;
import java.nio.file.Path;

public class ImageSupporter {
    public static BufferedImage loadImage(Path loadPath) {
        if (loadPath == null) {
            throw new IllegalArgumentException("图像路径不能为空!");
        }
        return ImagIO.loadImage(loadPath);
    }

    public static boolean saveImage(BufferedImage image, String extension, Path savePath) {
        if (image == null || extension == null || savePath == null) {
            throw new IllegalArgumentException("图像不能为空!");
        }
        return ImagIO.saveImage(image, extension, savePath);
    }

    public static BufferedImage convertOriginalImageToGrayImage(BufferedImage image, boolean isOriginalImageColor) {
        if (image == null) {
            throw new IllegalArgumentException("图像不能为空!");
        }
        if (isOriginalImageColor) {
            return ImagConverter.convertColorImageToGrayImage(image);
        } else {
            return image;
        }
    }

    public static Integer[][] convertGrayImageToGrayScale(BufferedImage image) {
        if (image == null) {
            throw new IllegalArgumentException("图像不能为空!");
        }
        return ImagConverter.convertGrayImageToGrayScale(image);
    }

    public static BufferedImage convertGrayImageToBinaryImage(BufferedImage image, int threshold) {
        if (image == null) {
            throw new IllegalArgumentException("图像不能为空!");
        }
        return ImagConverter.convertGrayImageToBinaryImage(image, threshold);
    }
}
