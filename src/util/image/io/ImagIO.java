package util.image.io;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;

public class ImagIO {
    public static BufferedImage loadImage(Path loadPath) {
        try {
            return ImageIO.read(loadPath.toFile());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean saveImage(BufferedImage image, String extension, Path savePath) {
        try {
            return ImageIO.write(image, extension, savePath.toFile());
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
