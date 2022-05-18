package top.totoro.swing.widget.manager;

import top.totoro.swing.widget.base.BaseAttribute;
import top.totoro.swing.widget.event.MotionEvent;
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

    private View<?, ?> menuContainer;

    public void showMenu(MouseEvent e, View<?, ?> listener) {
        if (listener == null) {
            return;
        }
        hideMenu();
        menuContainer = listener.createMenuView();
        menuContainer.invalidateSuper();
    }

    public void hideMenu() {
        if (menuContainer != null) {
            menuContainer.getSonByIndex(1).setVisible(BaseAttribute.GONE);
            menuContainer.invalidateSuper();
        }
    }
}
