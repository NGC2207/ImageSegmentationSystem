package view.gui.panel;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageDisplayPanel extends JPanel {
    private JLabel imageLabel;

    public ImageDisplayPanel() {
        setLayout(new GridBagLayout());
        this.imageLabel = new JLabel();
        this.add(this.imageLabel);
    }

    public void displayImage(BufferedImage image) {
        if (image == null) {
            throw new IllegalArgumentException("图像不能为空!");
        }
        int panelWidth = this.getWidth();
        int panelHeight = this.getHeight();
        double panelAspectRatio = (double) panelWidth / panelHeight;
        double imageAspectRatio = (double) image.getWidth() / image.getHeight();
        int scaledWidth;
        int scaledHeight;
        if (panelAspectRatio > imageAspectRatio) {
            // 面板更宽，按照高度等比例缩放
            scaledHeight = panelHeight;
            scaledWidth = (int) (scaledHeight * imageAspectRatio);
        } else {
            // 面板更高，按照宽度等比例缩放
            scaledWidth = panelWidth;
            scaledHeight = (int) (scaledWidth / imageAspectRatio);
        }
        Image scaledImage = image.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
        ImageIcon icon = new ImageIcon(scaledImage);
        // 设置图像图标到标签中
        this.imageLabel.setIcon(icon);
        // 重新绘制和重新验证面板
        imageLabel.repaint();
        this.repaint();
        this.revalidate();
    }
}
