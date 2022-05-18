package top.totoro.swing.widget.manager;

import top.totoro.swing.widget.base.Location;
import top.totoro.swing.widget.context.PopupWindow;
import top.totoro.swing.widget.view.View;

import java.awt.event.MouseEvent;

/**
 * 右键菜单管理
 */
public class RightClickMenuManager {
    private RightClickMenuManager() {
        // do nothing
    }

    private static volatile RightClickMenuManager instance;

    public static RightClickMenuManager getInstance() {
        if (instance == null) {
            synchronized (RightClickMenuManager.class) {
                if (instance == null) {
                    instance = new RightClickMenuManager();
                }
            }
        }
        return instance;
    }

    private PopupWindow menuWindow;
    private View<?, ?> target;

    public void showMenu(MouseEvent e, View<?, ?> target) {
        if (target == null) {
            return;
        }
        if (this.target != target) {
            menuWindow = target.createMenuWindow();
        }
        this.target = target;
        Location gapLocation = Location.getLocation(target.getComponent());
        gapLocation.xOnParent = e.getXOnScreen() - gapLocation.xOnScreen;
        gapLocation.yOnParent = e.getYOnScreen() - gapLocation.yOnScreen;
        menuWindow.showAsInside(target, gapLocation);
    }

    @SuppressWarnings("unused")
    public void hideMenu() {
        if (menuWindow != null) {
            menuWindow.dismiss();
        }
    }

    @SuppressWarnings("unused")
    public PopupWindow getMenuWindow() {
        return menuWindow;
    }
}
