package top.totoro.swing.widget.context;

import top.totoro.swing.widget.base.Location;
import top.totoro.swing.widget.bean.LayoutAttribute;
import top.totoro.swing.widget.view.View;

import javax.swing.*;
import java.awt.*;

public class PopupWindow extends Context {
    protected JWindow mDropDownWindow;
    private LayoutAttribute containerAttr;
    private View<?, ?> dropTarget;

    public static PopupWindow mShowingPopupWindow;

    public PopupWindow() {
        mDropDownWindow = new JWindow();
        mDropDownWindow.getContentPane().setBackground(Color.white);
        containerAttr = new LayoutAttribute();
    }

    public PopupWindow(int width, int height) {
        this();
        setSize(width, height);
    }

    public PopupWindow(String layoutId, int width, int height) {
        this(width, height);
        layoutManager.inflate(getMainView(), layoutId);
        layoutManager.setMainLayout(getMainView());
    }

    /**
     * 刷新下拉框的显示位置
     */
    public void refreshLocation() {
        if (dropTarget == null) return;
        if (dropTarget.getComponent() == null) return;

        Location location = null;
        if (dropTarget.getComponent().isVisible()) {
            location = Location.getLocation(dropTarget.getComponent());
        }
        if (location == null) return;
        location.yOnScreen += dropTarget.getHeight() + 1;
        mDropDownWindow.setLocation(location.xOnScreen, location.yOnScreen);

    }

    public void setSize(int width, int height) {
        mDropDownWindow.setSize(width, height);
        containerAttr.setWidth(width);
        containerAttr.setHeight(height);
        getMainView().setAttribute(containerAttr);
    }

    public void showAsDrop(View<?, ?> dropTarget) {
        prepareShow();
        this.dropTarget = dropTarget;
        refreshLocation();

        layoutManager.invalidate();

        mDropDownWindow.getContentPane().add(getMainView().getComponent());
        mDropDownWindow.setVisible(true);
        mShowingPopupWindow = this;
    }

    public void prepareShow() {
        if (mShowingPopupWindow != null) {
            mShowingPopupWindow.dismiss();
            mShowingPopupWindow = null;
        }
    }

    public void dismiss() {
        if (mShowingPopupWindow != null && mShowingPopupWindow != this) {
            mShowingPopupWindow.dismiss();
            mShowingPopupWindow = null;
        }
        mDropDownWindow.dispose();
    }
}
