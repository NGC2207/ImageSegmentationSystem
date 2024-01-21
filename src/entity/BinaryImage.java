package entity;

import java.awt.image.BufferedImage;

public class BinaryImage {
    private Integer[][] grayScale;
    private BufferedImage binaryImage;
    private double threshold;

    public BufferedImage getBinaryImage() {
        return binaryImage;
    }

    public BinaryImage(Integer[][] grayScale) {
        this.grayScale = grayScale;
        this.threshold = calculateThreshold();
        this.binaryImage = calculateBinaryScale();
    }

    //阈值为所有灰度值的平均值
    private double calculateThreshold() {
        int sum = 0;
        for (int i = 0; i < grayScale.length; i++) {
            for (int j = 0; j < grayScale[0].length; j++) {
                sum += grayScale[i][j];
            }
        }
        return sum / (grayScale.length * grayScale[0].length);
    }

    private BufferedImage calculateBinaryScale() {
        BufferedImage binaryScale = new BufferedImage(grayScale[0].length, grayScale.length, BufferedImage.TYPE_BYTE_BINARY);
        for (int i = 0; i < grayScale.length; i++) {
            for (int j = 0; j < grayScale[0].length; j++) {
                if (grayScale[i][j] > threshold) {
                    binaryScale.setRGB(j, i, 0xffffff); // 注意这里交换了 i 和 j 的位置
                } else {
                    binaryScale.setRGB(j, i, 0x000000); // 同样的，这里也交换了 i 和 j 的位置
                }
            }
        }
        return binaryScale;
    }

}
