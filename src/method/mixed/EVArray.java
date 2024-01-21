package method.mixed;

import util.array.ArrayHelper;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;

public class EVArray {
    //顶点集信息:
    private final int widthOfVertexes;
    private final int heightOfVertexes;
    private final Integer[][] vertexes;
    private final double varianceOfVertexes;
    //EV集信息:(EV集为顶点集和边集的集合)
    private final int cols;
    private final int rows;
    private final double[][] vertexWithEdgeArray;
    private final Integer[][] classificationArray;
    private ArrayList<ArrayList<classifiedInformation>> classifiedInformationArrayList;
    private final BufferedImage originalColorImage;
    private final BufferedImage classifiedColorFillImage;
    private final BufferedImage classifiedEdgeSegmentationImage;

    public BufferedImage getClassifiedEdgeSegmentationImage() {
        return classifiedEdgeSegmentationImage;
    }

    public BufferedImage getClassifiedColorFillImage() {
        return classifiedColorFillImage;
    }

    private final double threshold;

    public EVArray(BufferedImage originalImage, Integer[][] grayScale) {
        this.originalColorImage = originalImage;
        this.vertexes = grayScale;
        this.widthOfVertexes = vertexes[0].length;
        this.heightOfVertexes = vertexes.length;
        this.varianceOfVertexes = ArrayHelper.getVariance(vertexes);
        this.cols = widthOfVertexes * 2 - 1;
        this.rows = heightOfVertexes * 2 - 1;
        this.vertexWithEdgeArray = createVertexWithEdgeArray();
        this.threshold = getAverageEdgeWeights();
        this.classificationArray = getClassificationArray(threshold);
        this.classifiedColorFillImage = classifiedColorFillImage_FirstPoint();
//        this.classifiedColorFillImage = classifiedColorFillImage_AveragePoint();
        this.classifiedEdgeSegmentationImage = classifiedEdgeSegmentationImage();
    }

    private double getAverageEdgeWeights() {
        double sum = 0;
        int count = 0;
        for (int y = 0; y < rows; y += 2) {
            for (int x = 1; x < cols - 1; x += 2) {
                sum += vertexWithEdgeArray[y][x];
                count++;
            }
        }
        for (int x = 0; x < cols; x += 2) {
            for (int y = 1; y < rows - 1; y += 2) {
                sum += vertexWithEdgeArray[y][x];
                count++;
            }
        }
        return sum / count;
    }

    private double[][] createVertexWithEdgeArray() {
        double[][] vertexWithEdgeArray = new double[rows][cols];
        //设置顶点信息
        for (int y = 0; y < heightOfVertexes; y++) {
            for (int x = 0; x < widthOfVertexes; x++) {
                vertexWithEdgeArray[2 * y][2 * x] = vertexes[y][x];
            }
        }
        //设置边信息
        //横边
        for (int y = 0; y < rows; y += 2) {
            for (int x = 1; x < cols - 1; x += 2) {
                vertexWithEdgeArray[y][x] = getEdgeWeight(vertexWithEdgeArray[y][x - 1], vertexWithEdgeArray[y][x + 1]);
            }
        }
        //竖边
        for (int x = 0; x < cols; x += 2) {
            for (int y = 1; y < rows - 1; y += 2) {
                vertexWithEdgeArray[y][x] = getEdgeWeight(vertexWithEdgeArray[y - 1][x], vertexWithEdgeArray[y + 1][x]);
            }
        }
        return vertexWithEdgeArray;
    }

    private double getEdgeWeight(double grayValue1, double grayValue2) {
        return Math.exp(-Math.pow(grayValue1 - grayValue2, 2) / varianceOfVertexes);
    }

    private Integer[][] getClassificationArray(double threshold) {
        Integer[][] classification = new Integer[heightOfVertexes][widthOfVertexes];
        boolean[][] classified = new boolean[heightOfVertexes][widthOfVertexes];
        int label = 0;
        classifiedInformationArrayList = new ArrayList<>();
        //定义一个队列,从vertexWithEdgeArray[0][0]开始,将其标记为label,并将其连接的未标记的顶点加入队列
        //一个顶点最多与上下左右四个顶点相邻,判断是否连接的依据为边的权重是否大于threshold
        //如果队列为空,则从vertexWithEdgeArray中找到第一个未标记的顶点,将其标记为当前label+1,并将其连接的未标记的顶点加入队列
        //重复上述过程,直到所有顶点都被标记
        for (int y = 0; y < heightOfVertexes; y++) {
            for (int x = 0; x < widthOfVertexes; x++) {
                if (!classified[y][x]) {
                    classifiedInformationArrayList.add(new ArrayList<>());
                    classify(vertexWithEdgeArray, classification, classified, y * widthOfVertexes + x, label, threshold);
                    label++;
                }
            }
        }
        return classification;
    }

    private BufferedImage classifiedEdgeSegmentationImage() {
        //某个像素点的四邻域的label不同,则该像素点为边界点,将其设置为黑色,否则设置为白色
        BufferedImage image = new BufferedImage(widthOfVertexes, heightOfVertexes, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < classificationArray.length; i++) {
            for (int j = 0; j < classificationArray[i].length; j++) {
                if (i >= 1 && !Objects.equals(classificationArray[i][j], classificationArray[i - 1][j])) {
                    image.setRGB(j, i, 0);
                } else if (i < classificationArray.length - 1 && !Objects.equals(classificationArray[i][j], classificationArray[i + 1][j])) {
                    image.setRGB(j, i, 0);
                } else if (j >= 1 && !Objects.equals(classificationArray[i][j], classificationArray[i][j - 1])) {
                    image.setRGB(j, i, 0);
                } else if (j < classificationArray[i].length - 1 && !Objects.equals(classificationArray[i][j], classificationArray[i][j + 1])) {
                    image.setRGB(j, i, 0);
                } else {
                    image.setRGB(j, i, 0xffffff);
                }
            }
        }
        return image;
    }

    private void classify(double[][] vertexWithEdgeArray, Integer[][] classification, boolean[][] classified, int indexOfVertex, int label, double threshold) {
        classifiedInformation classifiedInformation = new classifiedInformation(getRowOfVertex(indexOfVertex), getColOfVertex(indexOfVertex), label, vertexes[getRowOfVertex(indexOfVertex)][getColOfVertex(indexOfVertex)], originalColorImage.getRGB(getColOfVertex(indexOfVertex), getRowOfVertex(indexOfVertex)));
        classifiedInformationArrayList.get(label).add(classifiedInformation);
        classified[getRowOfVertex(indexOfVertex)][getColOfVertex(indexOfVertex)] = true;
        classification[getRowOfVertex(indexOfVertex)][getColOfVertex(indexOfVertex)] = label;
        Queue<Integer> queue = new LinkedList<>();
        queue.offer(indexOfVertex);
        while (!queue.isEmpty()) {
            int indexOfVertexInQueue = queue.poll();
            int rowOfVertexInQueue = getRowOfVertex(indexOfVertexInQueue);
            int colOfVertexInQueue = getColOfVertex(indexOfVertexInQueue);
            //判断上下左右四个顶点是否连接
            //上
            if (rowOfVertexInQueue >= 1) {
                int indexOfUpVertex = indexOfVertexInQueue - widthOfVertexes;
                if (!classified[getRowOfVertex(indexOfUpVertex)][getColOfVertex(indexOfUpVertex)]) {
                    if (vertexWithEdgeArray[2 * rowOfVertexInQueue - 1][2 * colOfVertexInQueue] > threshold) {
                        classifiedInformation upClassifiedInformation = new classifiedInformation(getRowOfVertex(indexOfUpVertex), getColOfVertex(indexOfUpVertex), label, vertexes[getRowOfVertex(indexOfUpVertex)][getColOfVertex(indexOfUpVertex)], originalColorImage.getRGB(getColOfVertex(indexOfUpVertex), getRowOfVertex(indexOfUpVertex)));
                        classifiedInformationArrayList.get(label).add(upClassifiedInformation);
                        classified[getRowOfVertex(indexOfUpVertex)][getColOfVertex(indexOfUpVertex)] = true;
                        classification[getRowOfVertex(indexOfUpVertex)][getColOfVertex(indexOfUpVertex)] = label;
                        queue.offer(indexOfUpVertex);
                    }
                }
            }
            //左
            if (colOfVertexInQueue >= 1) {
                int indexOfLeftVertex = indexOfVertexInQueue - 1;
                if (!classified[getRowOfVertex(indexOfLeftVertex)][getColOfVertex(indexOfLeftVertex)]) {
                    if (vertexWithEdgeArray[2 * rowOfVertexInQueue][2 * colOfVertexInQueue - 1] > threshold) {
                        classifiedInformation leftClassifiedInformation = new classifiedInformation(getRowOfVertex(indexOfLeftVertex), getColOfVertex(indexOfLeftVertex), label, vertexes[getRowOfVertex(indexOfLeftVertex)][getColOfVertex(indexOfLeftVertex)], originalColorImage.getRGB(getColOfVertex(indexOfLeftVertex), getRowOfVertex(indexOfLeftVertex)));
                        classifiedInformationArrayList.get(label).add(leftClassifiedInformation);
                        classified[getRowOfVertex(indexOfLeftVertex)][getColOfVertex(indexOfLeftVertex)] = true;
                        classification[getRowOfVertex(indexOfLeftVertex)][getColOfVertex(indexOfLeftVertex)] = label;
                        queue.offer(indexOfLeftVertex);
                    }
                }
            }
            //右
            if (colOfVertexInQueue < widthOfVertexes - 1) {
                int indexOfRightVertex = indexOfVertexInQueue + 1;
                if (!classified[getRowOfVertex(indexOfRightVertex)][getColOfVertex(indexOfRightVertex)]) {
                    if (vertexWithEdgeArray[2 * rowOfVertexInQueue][2 * colOfVertexInQueue + 1] > threshold) {
                        classifiedInformation rightClassifiedInformation = new classifiedInformation(getRowOfVertex(indexOfRightVertex), getColOfVertex(indexOfRightVertex), label, vertexes[getRowOfVertex(indexOfRightVertex)][getColOfVertex(indexOfRightVertex)], originalColorImage.getRGB(getColOfVertex(indexOfRightVertex), getRowOfVertex(indexOfRightVertex)));
                        classifiedInformationArrayList.get(label).add(rightClassifiedInformation);
                        classified[getRowOfVertex(indexOfRightVertex)][getColOfVertex(indexOfRightVertex)] = true;
                        classification[getRowOfVertex(indexOfRightVertex)][getColOfVertex(indexOfRightVertex)] = label;
                        queue.offer(indexOfRightVertex);
                    }
                }
            }
            //下
            if (rowOfVertexInQueue < heightOfVertexes - 1) {
                int indexOfBottomVertex = indexOfVertexInQueue + widthOfVertexes;
                if (!classified[getRowOfVertex(indexOfBottomVertex)][getColOfVertex(indexOfBottomVertex)]) {
                    if (vertexWithEdgeArray[2 * rowOfVertexInQueue + 1][2 * colOfVertexInQueue] > threshold) {
                        classifiedInformation bottomClassifiedInformation = new classifiedInformation(getRowOfVertex(indexOfBottomVertex), getColOfVertex(indexOfBottomVertex), label, vertexes[getRowOfVertex(indexOfBottomVertex)][getColOfVertex(indexOfBottomVertex)], originalColorImage.getRGB(getColOfVertex(indexOfBottomVertex), getRowOfVertex(indexOfBottomVertex)));
                        classifiedInformationArrayList.get(label).add(bottomClassifiedInformation);
                        classified[getRowOfVertex(indexOfBottomVertex)][getColOfVertex(indexOfBottomVertex)] = true;
                        classification[getRowOfVertex(indexOfBottomVertex)][getColOfVertex(indexOfBottomVertex)] = label;
                        queue.offer(indexOfBottomVertex);
                    }
                }
            }
        }
    }

    private BufferedImage classifiedColorFillImage_FirstPoint() {
        BufferedImage image = new BufferedImage(widthOfVertexes, heightOfVertexes, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < classificationArray.length; i++) {
            for (int j = 0; j < classificationArray[i].length; j++) {
                //设置为image.setRGB(j, i, classifiedInformationArrayList.get(classificationArray[i][j]).get(0).colorValue);的反色
//                image.setRGB(j, i, ~classifiedInformationArrayList.get(classificationArray[i][j]).get(0).colorValue);
                image.setRGB(j, i, classifiedInformationArrayList.get(classificationArray[i][j]).get(0).colorValue);
            }
        }
        return image;
    }

    private BufferedImage classifiedColorFillImage_AveragePoint() {
        //将每个label里的像素点对应的彩色像素点(根据classifiedInformation中的row和col)提取,求出整个区域的平均值,将该平均值作为该区域的颜色
        BufferedImage image = new BufferedImage(widthOfVertexes, heightOfVertexes, BufferedImage.TYPE_INT_RGB);
        ArrayList<Integer> classifiedColorValue = new ArrayList<>();
        for (ArrayList<classifiedInformation> classifiedInformationArrayList : classifiedInformationArrayList) {
            int sum = 0;
            for (classifiedInformation classifiedInformation : classifiedInformationArrayList) {
                sum += classifiedInformation.colorValue;
            }
            classifiedColorValue.add(sum / classifiedInformationArrayList.size());
        }
        for (int i = 0; i < classificationArray.length; i++) {
            for (int j = 0; j < classificationArray[i].length; j++) {
                image.setRGB(j, i, classifiedColorValue.get(classificationArray[i][j]));
            }
        }
        return image;
    }


    private int getRowOfVertex(int indexOfVertex) {
        return indexOfVertex / widthOfVertexes;
    }

    private int getColOfVertex(int indexOfVertex) {
        return indexOfVertex % widthOfVertexes;
    }

    static class classifiedInformation {
        int row;
        int col;
        int label;
        int grayValue;
        int colorValue;

        public classifiedInformation(int row, int col, int label, int grayValue, int colorValue) {
            this.row = row;
            this.col = col;
            this.label = label;
            this.grayValue = grayValue;
            this.colorValue = colorValue;
        }
    }
}
