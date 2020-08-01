package top.totoro.swing.widget.base;

import java.awt.Component;
import java.awt.Point;

public class Location {
    public int xOnParent, yOnParent;
    public int xOnScreen, yOnScreen;

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
}
