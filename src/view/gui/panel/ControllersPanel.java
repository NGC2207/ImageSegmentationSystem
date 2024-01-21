package view.gui.panel;

import entity.BinaryImage;
import entity.GrayImage;
import entity.OriginalImage;
import method.mixed.EVArray;
import util.image.ImageSupporter;
import view.constants.PanelConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.file.Path;

public class ControllersPanel extends JPanel {
    private JButton importImageButton;
    private JButton exportImageButton;
    //下拉栏,里面可以选择PanelConstants中定义的图像类型
    private JComboBox<String> imageTypeComboBox;
    //更新按钮,点击后会根据下拉栏的选择进行处理吗,并显示在ImagePanel中
    private JButton processButton;
    private ImageDisplayPanel imageDisplayPanel;
    //
    private String extension;
    private OriginalImage originalImage;
    private GrayImage grayImage;
    private BinaryImage binaryImage;
    private BufferedImage classifiedColorFillImage;
    private BufferedImage classifiedEdgeImage;
    private Integer[][] grayScale;
    private EVArray evArray;

    public ControllersPanel(ImageDisplayPanel imageDisplayPanel) {
        this.imageDisplayPanel = imageDisplayPanel;
        this.init();
        this.addListener();
    }

    public void init() {
        this.setLayout(new GridBagLayout());
        this.importImageButton = new JButton("导入图片");
        this.exportImageButton = new JButton("导出图片");
        this.processButton = new JButton("更新");
        this.imageTypeComboBox = new JComboBox<>(PanelConstants.getImageType());
        //根据GridBagLayout布局,将导入图片按钮,导出图片按钮,下拉栏和更新按钮从上到下依次放在第0行,第1行,第2行,第3行
        this.add(this.importImageButton, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 10, 0, 10), 0, 0));
        this.add(this.exportImageButton, new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 10, 0, 10), 0, 0));
        this.add(this.imageTypeComboBox, new GridBagConstraints(0, 2, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 10, 0, 10), 0, 0));
        this.add(this.processButton, new GridBagConstraints(0, 3, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 10, 0, 10), 0, 0));
    }

    public void addListener() {
        importImageButton.addActionListener(e -> {
            //清空所有的图像
            this.originalImage = null;
            this.grayImage = null;
            this.binaryImage = null;
            this.classifiedColorFillImage = null;
            this.grayScale = null;
            this.classifiedEdgeImage = null;
            this.evArray = null;
            //创建一个对话框,让用户选择原图类型
            String[] options = {"彩色", "灰色"};
            int choice = JOptionPane.showOptionDialog(this, "请选择图片类型", "选择图片类型", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            boolean isOriginalImageColor;
            if (choice == 0) {
                isOriginalImageColor = true;
            } else if (choice == 1) {
                isOriginalImageColor = false;
            } else {
                return;
            }
            //创建一个文件选择器,让用户选择图片
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                //获取选择的图像的Path
                Path imagePath = fileChooser.getSelectedFile().toPath();
                this.extension = imagePath.getFileName().toString().split("\\.")[1];
                //根据图像的Path创建OriginalImage对象
                this.originalImage = new OriginalImage(imagePath, isOriginalImageColor);
                //将OriginalImage对象中的图像显示在ImageDisplayPanel中
                this.imageDisplayPanel.displayImage(this.originalImage.getOriginalImage());
                //将下拉栏的选择设置为"原图"
                this.imageTypeComboBox.setSelectedIndex(0);
            }
        });
        exportImageButton.addActionListener(e -> {
            //创建一个文件选择器,让用户选择保存的路径
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showSaveDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                //获取用户选择的保存路径
                Path savePath = fileChooser.getSelectedFile().toPath();
                //将图像保存到用户选择的路径
                //根据下拉栏的选择,将图像保存为原图或灰度图或者其他
                switch (this.imageTypeComboBox.getSelectedIndex()) {
                    case 0:
                        ImageSupporter.saveImage(this.originalImage.getOriginalImage(), this.extension, savePath);
                        break;
                    case 1:
                        if (this.grayImage == null) {
                            this.grayImage = new GrayImage(this.originalImage.getOriginalImage(), this.originalImage.isOriginalImageColor());
                        }
                        ImageSupporter.saveImage(this.grayImage.getGrayImage(), this.extension, savePath);
                        break;
                    case 2:
                        if (this.grayScale == null) {
                            if (this.grayImage == null) {
                                this.grayImage = new GrayImage(this.originalImage.getOriginalImage(), this.originalImage.isOriginalImageColor());
                            }
                            this.grayScale = ImageSupporter.convertGrayImageToGrayScale(this.grayImage.getGrayImage());
                        }
                        if (binaryImage == null) {
                            this.binaryImage = new BinaryImage(this.grayScale);
                        }
                        ImageSupporter.saveImage(this.binaryImage.getBinaryImage(), this.extension, savePath);
                        break;
                    case 3://边缘分割图
                        if (this.grayScale == null) {
                            if (this.grayImage == null) {
                                this.grayImage = new GrayImage(this.originalImage.getOriginalImage(), this.originalImage.isOriginalImageColor());
                            }
                            this.grayScale = ImageSupporter.convertGrayImageToGrayScale(this.grayImage.getGrayImage());
                        }
                        if (classifiedEdgeImage == null) {
                            if (this.evArray == null) {
                                this.evArray = new EVArray(this.originalImage.getOriginalImage(), this.grayScale);
                            }
                            classifiedEdgeImage = evArray.getClassifiedEdgeSegmentationImage();
                        }
                        ImageSupporter.saveImage(this.classifiedEdgeImage, this.extension, savePath);
                        break;
                    case 4:
                        if (this.grayScale == null) {
                            if (this.grayImage == null) {
                                this.grayImage = new GrayImage(this.originalImage.getOriginalImage(), this.originalImage.isOriginalImageColor());
                            }
                            this.grayScale = ImageSupporter.convertGrayImageToGrayScale(this.grayImage.getGrayImage());
                        }
                        if (classifiedColorFillImage == null) {
                            if(this.evArray == null){
                                this.evArray = new EVArray(this.originalImage.getOriginalImage(), this.grayScale);
                            }
                            this.classifiedColorFillImage = evArray.getClassifiedColorFillImage();
                        }
                        ImageSupporter.saveImage(this.classifiedColorFillImage, this.extension, savePath);
                }
            }
        });
        processButton.addActionListener(e -> {
            switch (this.imageTypeComboBox.getSelectedIndex()) {
                case 0:
                    this.imageDisplayPanel.displayImage(this.originalImage.getOriginalImage());
                    break;
                case 1:
                    if (this.grayImage == null) {
                        this.grayImage = new GrayImage(this.originalImage.getOriginalImage(), this.originalImage.isOriginalImageColor());
                    }
                    this.imageDisplayPanel.displayImage(this.grayImage.getGrayImage());
                    break;
                case 2:
                    if (this.grayScale == null) {
                        if (this.grayImage == null) {
                            this.grayImage = new GrayImage(this.originalImage.getOriginalImage(), this.originalImage.isOriginalImageColor());
                        }
                        this.grayScale = ImageSupporter.convertGrayImageToGrayScale(this.grayImage.getGrayImage());
                    }
                    if (binaryImage == null) {
                        this.binaryImage = new BinaryImage(this.grayScale);
                    }
                    this.imageDisplayPanel.displayImage(this.binaryImage.getBinaryImage());
                    break;
                case 3:
                    if (this.grayScale == null) {
                        if (this.grayImage == null) {
                            this.grayImage = new GrayImage(this.originalImage.getOriginalImage(), this.originalImage.isOriginalImageColor());
                        }
                        this.grayScale = ImageSupporter.convertGrayImageToGrayScale(this.grayImage.getGrayImage());
                    }
                    if (classifiedEdgeImage == null) {
                        if(this.evArray == null){
                            this.evArray = new EVArray(this.originalImage.getOriginalImage(), this.grayScale);
                        }
                        classifiedEdgeImage = evArray.getClassifiedEdgeSegmentationImage();
                    }
                    this.imageDisplayPanel.displayImage(this.classifiedEdgeImage);
                    break;
                case 4:
                    if (this.grayScale == null) {
                        if (this.grayImage == null) {
                            this.grayImage = new GrayImage(this.originalImage.getOriginalImage(), this.originalImage.isOriginalImageColor());
                        }
                        this.grayScale = ImageSupporter.convertGrayImageToGrayScale(this.grayImage.getGrayImage());
                    }
                    if (classifiedColorFillImage == null) {
                        if(this.evArray == null){
                            this.evArray = new EVArray(this.originalImage.getOriginalImage(), this.grayScale);
                        }
                        this.classifiedColorFillImage = evArray.getClassifiedColorFillImage();
                    }
                    this.imageDisplayPanel.displayImage(this.classifiedColorFillImage);
                    break;
            }
        });
    }

}
