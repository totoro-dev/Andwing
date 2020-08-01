package top.totoro.swing.widget.base;

import java.awt.Component;
import java.awt.Dimension;

public class Size {
    public int width, height;
    public static Size getSize(Component target){
        if (target == null) return null;
        Dimension dimension = target.getSize();
        return new Size(dimension.width, dimension.height);
    }

    public Size(int width, int height) {
        this.width = width;
        this.height = height;
    }
}
