package top.totoro.swing.widget.base;

import java.awt.Component;
import java.awt.Point;

public class Location {
    public int xOnParent, yOnParent;
    public int xOnScreen, yOnScreen;

    /**
     * 获取可见组件的位置，并生成其Location
     *
     * @param target 组件
     * @return 如果组件不可见为null，否则返回包含组件在屏幕上位置和相对父组件位置的Location
     */
    public static Location getLocation(Component target) {
        if (target == null || !target.isVisible()) return null;
        Point pointOnParent = target.getLocation();
        Location location = new Location(pointOnParent.x, pointOnParent.y);
        Point locationOnScreen = target.getLocationOnScreen();
        location.xOnScreen = locationOnScreen.x;
        location.yOnScreen = locationOnScreen.y;
        return location;
    }

    public Location(int xOnParent, int yOnParent) {
        this.xOnParent = xOnParent;
        this.yOnParent = yOnParent;
    }

    public Location(int xOnParent, int yOnParent, int xOnScreen, int yOnScreen) {
        this.xOnParent = xOnParent;
        this.yOnParent = yOnParent;
        this.xOnScreen = xOnScreen;
        this.yOnScreen = yOnScreen;
    }
}
