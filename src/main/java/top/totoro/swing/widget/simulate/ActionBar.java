package top.totoro.swing.widget.simulate;

import javax.swing.*;
import java.awt.*;

public class ActionBar extends JComponent {

    // ActionBar的显示方向， HORIZONTAL : 水平， VERTICAL : 竖直
    public static final int HORIZONTAL = 1, VERTICAL = 2;

    private int orientation = HORIZONTAL;
    private Height height = Height.MID;
    private String title = "";

    /**
     * 设置ActionBar的高度：MIN ==> 允许的最小值, MID ==> 默认值, MAX ==> 允许的最大值
     *
     * @param height 期望高度
     */
    public void setHeight(Height height) {
        this.height = height;
    }

    // 不支持自定义宽度，必须占满父窗口的宽度
//    public void setWidth(int width){ }

    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 设置ActionBar的边框
     *
     * @param pixel 边框像素
     * @param color 边框颜色
     */
    public void setBorder(int pixel, Color color) {
        if (orientation == HORIZONTAL) {
            super.setBorder(BorderFactory.createMatteBorder(0, 0, pixel, 0, color));
        } else if (orientation == VERTICAL) {
            super.setBorder(BorderFactory.createMatteBorder(0, 0, 0, pixel, color));
        }
    }

    public enum Height {
        MIN, MID, MAX
    }
}
