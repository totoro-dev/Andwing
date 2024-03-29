package top.totoro.swing.widget.util;

import java.awt.*;
import java.awt.image.BufferedImage;

@SuppressWarnings("unused")
public class PaintUtil {

    private static final int MIN_ALPHA = 0;
    private static final int MAX_ALPHA = 100;

    protected Color calculateAlphaValue(Color c1, Color c2, int alpha) {
        if (alpha < MIN_ALPHA)
            alpha = MIN_ALPHA;
        else if (alpha > MAX_ALPHA)
            alpha = MAX_ALPHA;
        int R = (c1.getRed() * (MAX_ALPHA - alpha) + c2.getRed() * alpha) / MAX_ALPHA;
        int G = (c1.getGreen() * (MAX_ALPHA - alpha) + c2.getGreen() * alpha) / MAX_ALPHA;
        int B = (c1.getBlue() * (MAX_ALPHA - alpha) + c2.getBlue() * alpha) / MAX_ALPHA;
        return new Color(R, G, B);
    }

    public static void drawRadius(Graphics g, Color color, int radius, int startX, int startY) {
        Color origin = g.getColor();
        g.setColor(color);
        int r = radius;
        for (int i = 0; i < 2; i++) {
            for (int j = -radius; j < 0; j++) {
                int x = j;
                int y1 = (int) Math.sqrt(radius * radius - x * x);
                int y2;
                x += startX + radius;
                y1 = startY + radius - y1;
                y2 = startY + 2 * radius - (y1 - startY);
                drawPoint(g, color, x, y1);
                drawPoint(g, color, x, y2);
            }
            startX++;
            startY++;
            radius--;
        }
        g.setColor(origin);
    }

    /**
     * 绘制高度为30的5像素半径的边框
     *
     * @param g     画笔
     * @param color 背景色
     * @param width 主体宽度
     */
    public static void drawButtonRadius(Graphics g, Color color, int radius, int width, int height) {
        Color origin = g.getColor();
        g.setColor(color);
        Color[] radiusColors = new Color[radius];
        for (int i = 0; i < radius/2; i++) {
            float op = (float)(2*i) / (float)(radius);
            radiusColors[i] = ColorUtil.transparencyColor(color, Math.min(1, op));
        }
        for (int i = radius/2; i < radius; i++) {
            float op = (float)(2*radius - 2*i) / (float)(radius);
            radiusColors[i] = ColorUtil.transparencyColor(color, Math.min(1, op));
        }
        for (int i = 0; i < height; i++) {
            if (i < radius) {
                for (int i1 = 0; i1 < (radius - i); i1++) {
                    drawPoint(g, radiusColors[i1], i1, i);
                    drawPoint(g, radiusColors[i1], width - i1 - 1, i);
//                    drawPoint(g, radiusColors[i], i1, i);
//                    drawPoint(g, radiusColors[i], width - i1 - 1, i);
                }
                g.setColor(color);
                g.drawLine(radius - i, i, width - radius + i -1, i);
            } else if (i >= height - radius + 1) {
                for (int i1 = 0; i1 < (radius - height + i); i1++) {
                    drawPoint(g, radiusColors[height - i], i1, i);
                    drawPoint(g, radiusColors[height - i], width - i1 - 1, i);
                }
                g.setColor(color);
                g.drawLine((radius - height + i), i, width - (radius - height + i) -1, i);
            } else {
                g.drawLine(0, i, width - 1, i);
            }
        }
        g.setColor(origin);
    }

    /**
     * 绘制按钮的小圆角
     *
     * @param g      画笔
     * @param bg     背景色
     * @param width  主体宽度
     * @param height 主体高度
     */
    public static void drawButtonRadius(Graphics g, Color bg, int width, int height) {
        Color origin = g.getColor();
        Color c180 = ColorUtil.transparencyColor(bg, 0.25F);
        Color c32 = ColorUtil.transparencyColor(bg, 0.5F);
        Color c16 = ColorUtil.transparencyColor(bg, 0.75F);
        final int t2 = height - 2, t1 = height - 1;
        g.setColor(bg);
        for (int i = 0; i < height; i++) {
            switch (i) {
                case 0:
                    drawPoint(g, c180, 0, 0);
                    drawPoint(g, c180, width - 1, 0);
                    drawPoint(g, c32, 1, 0);
                    drawPoint(g, c32, width - 2, 0);
                    g.setColor(bg);
                    g.drawLine(2, 0, width - 3, 0);
                    continue;
                case 1:
                    drawPoint(g, c32, 0, 1);
                    drawPoint(g, c32, width - 1, 1);
                    g.setColor(bg);
                    g.drawLine(1, 1, width - 2, 1);
                    continue;
            }
            if (i == t2) {
                drawPoint(g, c32, 0, t2);
                drawPoint(g, c32, width - 1, t2);
                g.setColor(bg);
                g.drawLine(1, t2, width - 2, t2);
            } else if (i == t1) {
                drawPoint(g, c180, 0, t1);
                drawPoint(g, c180, width - 1, t1);
                drawPoint(g, c32, 1, t1);
                drawPoint(g, c32, width - 2, t1);
                g.setColor(bg);
                g.drawLine(2, t1, width - 3, t1);
            } else {
                drawPoint(g, c16, 0, i);
                drawPoint(g, c16, width - 1, i);
                g.setColor(bg);
                g.drawLine(1, i, width - 2, i);
            }
        }
        g.setColor(origin);
    }

    /**
     * 绘制提示框的圆角
     *
     * @param g      画笔
     * @param bg     提示框的整体背景颜色
     * @param width  对话框宽度
     * @param height 对话框高度
     */
    public static void drawToastRadius(Graphics g, Color bg, int width, int height) {
        Color origin = g.getColor();
        Color c32 = ColorUtil.transparencyColor(bg, 0.33F);
        Color c16 = ColorUtil.transparencyColor(bg, 0.67F);
        final int t2 = height - 2, t1 = height - 1;
        g.setColor(bg);
        for (int i = 0; i < height; i++) {
            switch (i) {
                case 0:
                    drawPoint(g, c32, 1, 0);
                    drawPoint(g, c32, width - 2, 0);
                    g.setColor(c16);
                    g.drawLine(2, 0, width - 3, 0);
                    continue;
                case 1:
                    drawPoint(g, c32, 0, 1);
                    drawPoint(g, c32, width - 1, 1);
                    g.setColor(bg);
                    g.drawLine(1, 1, width - 2, 1);
                    continue;
            }
            if (i == t2) {
                drawPoint(g, c32, 0, t2);
                drawPoint(g, c32, width - 1, t2);
                g.setColor(bg);
                g.drawLine(1, t2, width - 2, t2);
            } else if (i == t1) {
                drawPoint(g, c32, 1, t1);
                drawPoint(g, c32, width - 2, t1);
                g.setColor(c16);
                g.drawLine(2, t1, width - 3, t1);
            } else {
                drawPoint(g, c16, 0, i);
                drawPoint(g, c16, width - 1, i);
                g.setColor(bg);
                g.drawLine(1, i, width - 2, i);
            }
        }
        g.setColor(origin);
    }

    private static Color getColor(Color origin, int gap) {
        int red = origin.getRed() + gap, green = origin.getGreen() + gap, blue = origin.getBlue() + gap;
        if (red > 255) red = 255;
        if (red < 0) red = 0;
        if (green > 255) green = 255;
        if (green < 0) green = 0;
        if (blue > 255) blue = 255;
        if (blue < 0) blue = 0;
        float[] hbs = new float[3];
        Color.RGBtoHSB(red, green, blue, hbs);
        return Color.getHSBColor(hbs[0], hbs[1], hbs[2]);
    }

    /**
     * 绘制15像素半径的边框
     *
     * @param g           画笔
     * @param bg          背景色
     * @param borderColor 边框色
     * @param width       主体宽度
     * @param height      主题宽度
     */
    public static void drawBorderFifteenRadius(Graphics g, Color bg, Color borderColor, int width, int height) {
        Color origin = g.getColor();
        for (int i = 0; i < height / 2; i++) {
            g.setColor(bg);
            switch (i) {
                case 0:
                    g.drawLine(13, i, width - 14, i);
                    g.drawLine(13, 29 - i, width - 14, 29 - i);
                    break;
                case 1:
                    drawRowBorder(g, borderColor, 1, width, i, 11);
                    break;
                case 2:
                    drawRowBorder(g, borderColor, 1, width, i, 9);
                    break;
                case 3:
                    drawRowBorder(g, borderColor, 1, width, i, 7);
                    break;
                case 4:
                    drawRowBorder(g, borderColor, 0, width, i, 6);
                    break;
                case 5:
                    drawRowBorder(g, borderColor, 0, width, i, 5);
                    break;
                case 6:
                    drawRowBorder(g, borderColor, 0, width, i, 4);
                    break;
                case 7:
                case 8:
                    drawRowBorder(g, borderColor, 0, width, i, 3);
                    break;
                case 9:
                case 10:
                    drawRowBorder(g, borderColor, 0, width, i, 2);
                    break;
                default:
                    drawRowBorder(g, borderColor, 0, width, i, 1);
                    break;
            }
        }
        g.setColor(borderColor);
        g.drawLine(13, 0, width - 14, 0);
        g.drawLine(13, height - 1, width - 14, height - 1);
        g.setColor(origin);
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

    /**
     * 截屏指定屏幕区域
     *
     * @param x      区域x坐标
     * @param y      区域y坐标
     * @param width  区域宽
     * @param height 区域高
     * @return 截屏的图像流
     */
    public static BufferedImage getBufferedImage(int x, int y, int width, int height) {
        Robot rb; // 可以用来抓取屏幕，即截屏。
        try {
            rb = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
            return null;
        }
        Toolkit tk = Toolkit.getDefaultToolkit(); // 获取缺省工具包
        Dimension di = tk.getScreenSize(); // 屏幕尺寸规格
        Rectangle rec = new Rectangle(0, 0, di.width, di.height);
        return rb.createScreenCapture(rec);
    }

    /**
     * 获取指定图像流中某点的颜色
     *
     * @param bi 图像流
     * @param x  点x坐标
     * @param y  点y坐标
     * @return 点的颜色
     */
    private static Color getColor(BufferedImage bi, int x, int y) {
        int pixelColor = bi.getRGB(x, y);
        return new Color(16777216 + pixelColor); // pixelColor的值为负，经过实践得出：加上颜色最大值就是实际颜色值。
    }
}
