package top.totoro.swing.widget.bean;

import top.totoro.swing.widget.base.BaseAttribute;

public class LayoutAttribute extends BaseAttribute {

    public static final int VERTICAL = 0;
    public static final int HORIZONTAL = 1;
    private int orientation = VERTICAL; // 组件方向

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    @Override
    public String toString() {
        return "LayoutAttribute{" +
                super.toString() + '\n' +
                "\t\t orientation=" + orientation +
                '}';
    }
}
