package top.totoro.swing.widget.util;

import java.awt.*;
import java.awt.image.BufferedImage;

public class PaintUtil {

    private static BufferedImage bi;

    /**
     * 绘制5像素半径的边框
     *
     * @param g     画笔
     * @param color 背景色
     * @param width 主体宽度
     */
    public static void drawBorderFiveRadius(Graphics g, Color color, int width) {
        Color origin = g.getColor();
        g.setColor(color);
        for (int i = 0; i < 30; i++) {
            switch (i) {
                case 0:
                    g.drawLine(3, 0, width - 4, 0);
                    break;
                case 1:
                    g.drawLine(2, 1, width - 3, 1);
                    break;
                case 2:
                    g.drawLine(1, 2, width - 2, 2);
                    break;
                case 27:
                    g.drawLine(1, 27, width - 2, 27);
                    break;
                case 28:
                    g.drawLine(2, 28, width - 3, 28);
                    break;
                case 29:
                    g.drawLine(3, 29, width - 4, 29);
                    break;
                default:
                    g.drawLine(0, i, width - 1, i);
                    break;
            }
        }
        g.setColor(origin);
    }

    /**
     * 绘制15像素半径的边框
     *
     * @param g           画笔
     * @param bg          背景色
     * @param borderColor 边框色
     * @param width       主体宽度
     */
    public static void drawBorderFifteenRadius(Graphics g, Color bg, Color borderColor, int x, int y, int width, int height) {
        System.out.println(x+","+y+","+width+","+height);
        bi = getBufferedImage(x , y , width , height);
        Color left = getColor(bi, 1, 1);
        Color right = getColor(bi, width, height);
        System.out.println(left + "," + right);
        Color origin = g.getColor();
        for (int i = 0; i < 15; i++) {
            g.setColor(bg);
            switch (i) {
                case 0:
                    fillOtherPart(g, left, 0, i, 13);
                    fillOtherPart(g, right, width - 13, i, 13);
                    g.drawLine(13, i, width - 14, i);
                    g.drawLine(13, 29 - i, width - 14, 29 - i);
                    break;
                case 1:
                    fillOtherPart(g, left, 0, i, 12);
                    fillOtherPart(g, right, width - 12, i, 12);
                    drawRowBorder(g, borderColor, 1, width, i, 11);
                    break;
                case 2:
                    fillOtherPart(g, left, 0, i, 11);
                    fillOtherPart(g, right, width - 11, i, 11);
                    drawRowBorder(g, borderColor, 1, width, i, 9);
                    break;
                case 3:
                    fillOtherPart(g, left, 0, i, 10);
                    fillOtherPart(g, right, width - 10, i, 10);
                    drawRowBorder(g, borderColor, 1, width, i, 7);
                    break;
                case 4:
                    fillOtherPart(g, left, 0, i, 9);
                    fillOtherPart(g, right, width - 9, i, 9);
                    drawRowBorder(g, borderColor, 0, width, i, 6);
                    break;
                case 5:
                    fillOtherPart(g, left, 0, i, 8);
                    fillOtherPart(g, right, width - 8, i, 8);
                    drawRowBorder(g, borderColor, 0, width, i, 5);
                    break;
                case 6:
                    fillOtherPart(g, left, 0, i, 7);
                    fillOtherPart(g, right, width - 7, i, 7);
                    drawRowBorder(g, borderColor, 0, width, i, 4);
                    break;
                case 7:
                case 8:
                    fillOtherPart(g, left, 0, i, 5);
                    fillOtherPart(g, right, width - 5, i, 5);
                    drawRowBorder(g, borderColor, 0, width, i, 3);
                    break;
                case 9:
                case 10:
                    fillOtherPart(g, left, 0, i, 3);
                    fillOtherPart(g, right, width - 3, i, 3);
                    drawRowBorder(g, borderColor, 0, width, i, 2);
                    break;
                case 11:
                case 12:
                case 13:
                case 14:
                    fillOtherPart(g, left, 0, i, 1);
                    fillOtherPart(g, right, width - 1, i, 1);
                    drawRowBorder(g, borderColor, 0, width, i, 1);
                    break;
            }
        }
        g.setColor(borderColor);
        g.drawLine(13, 0, width - 14, 0);
        g.drawLine(13, 29, width - 14, 29);
        g.setColor(origin);
    }

    private static void fillOtherPart(Graphics g, Color c, int startX, int y, int width) {
        for (int i = 0; i < width; i++) {
            drawPoint(g, c, startX + i, y);
            drawPoint(g, c, startX + i, 29-y);
        }
    }

    private static void drawRowBorder(Graphics g, Color borderColor, int borderLength, int width, int i, int gap) {
        g.drawLine(gap, i, width - 1 - gap, i);
        g.drawLine(gap, 29 - i, width - 1 - gap, 29 - i);
        g.setColor(borderColor);
        g.drawLine(gap, i, gap + borderLength, i);
        g.drawLine(gap, 29 - i, gap + borderLength, 29 - i);
        g.drawLine(width - 1 - gap, i, width - 1 - gap - borderLength, i);
        g.drawLine(width - 1 - gap, 29 - i, width - 1 - gap - borderLength, 29 - i);
    }

    protected static void drawPoint(Graphics graphics, Color c, int x, int y) {
        Color origin = graphics.getColor();
        graphics.setColor(c);
        graphics.drawLine(x, y, x, y);
        graphics.setColor(origin);
    }

    protected static void drawLine(Graphics graphics, Color c, int x, int y, int width, int height) {
        Color origin = graphics.getColor();
        graphics.setColor(c);
        for (int i = 0; i < width; i++) {
            graphics.drawLine(x + i, y, x + i, y + height);
        }
        graphics.setColor(origin);
    }

    public static BufferedImage getBufferedImage(int x, int y, int width, int height) { // 函数返回值为颜色的RGB值。
        Robot rb = null; // java.awt.image包中的类，可以用来抓取屏幕，即截屏。
        try {
            rb = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
        Toolkit tk = Toolkit.getDefaultToolkit(); // 获取缺省工具包
        Dimension di = tk.getScreenSize(); // 屏幕尺寸规格
        Rectangle rec = new Rectangle(0, 0, di.width, di.height);
        BufferedImage bi = rb.createScreenCapture(rec);
        return bi;
    }

    private static Color getColor(BufferedImage bi, int x, int y) {
        int pixelColor = bi.getRGB(x, y);
        Color color = new Color(16777216 + pixelColor);
        return color; // pixelColor的值为负，经过实践得出：加上颜色最大值就是实际颜色值。
    }
}
