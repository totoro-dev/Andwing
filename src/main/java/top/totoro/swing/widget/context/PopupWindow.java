package top.totoro.swing.widget.context;

import top.totoro.swing.widget.base.Location;
import top.totoro.swing.widget.bean.LayoutAttribute;
import top.totoro.swing.widget.view.View;

import javax.swing.*;
import java.awt.*;

/**
 * 悬浮框
 */
public class PopupWindow extends Context {

    public static PopupWindow mShowingPopupWindow;

    protected JWindow mDropDownWindow;
    private final LayoutAttribute containerAttr;
    private View<?, ?> target;
    private Location location;

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
        setContentView(layoutId);
    }

    /**
     * 通过布局id加载内容到悬浮框中
     *
     * @param layoutId 布局id
     */
    public void setContentView(String layoutId) {
        getMainView().setLayoutManager(layoutManager);
        layoutManager.setMainLayout(getMainView());
        layoutManager.inflate(getMainView(), layoutId);
    }

    /**
     * 刷新悬浮框的显示位置
     */
    public void refreshLocation() {
        Location location = null;
        if (target != null && this.location == null) {
            // 1) 在target组件的正下方显示悬浮框
            if (target.getComponent() == null) return;
            if (target.getComponent().isVisible()) {
                location = Location.getLocation(target.getComponent());
                if (location == null) return;
                location.yOnScreen += target.getHeight() + 1;
            }
        } else if (this.location != null && target == null) {
            // 2) 在屏幕的固定位置显示悬浮框
            location = this.location;
        } else if (target != null) {
            // 3) 在target组件的左上偏移位置显示悬浮框
            if (target.getComponent() == null) return;
            if (target.getComponent().isVisible()) {
                location = Location.getLocation(target.getComponent());
                if (location == null) return;
                location.xOnScreen += this.location.xOnParent;
                location.yOnScreen += this.location.yOnParent;
            }
        } else {
            return;
        }

        if (location == null) return;
        mDropDownWindow.setLocation(location.xOnScreen, location.yOnScreen);

    }

    /**
     * 设置悬浮框的大小
     *
     * @param width  宽度
     * @param height 高度
     */
    @Override
    public void setSize(int width, int height) {
        super.setSize(width, height);
        setSizeInternal(width, height);
    }

    private void setSizeInternal(int width, int height) {
        super.setSize(width, height);
        mDropDownWindow.setSize(width, height);
        containerAttr.setWidth(width);
        containerAttr.setHeight(height);
        getMainView().setAttribute(containerAttr);
    }

    /**
     * 在指定的控件下方显示悬浮框
     *
     * @param dropTarget 目标控件
     */
    @SuppressWarnings("unused")
    public void showAsDrop(View<?, ?> dropTarget) {
        prepareShow();
        this.target = dropTarget;
        this.location = null;
        show();
    }

    /**
     * 在指定的屏幕坐标显示悬浮框
     *
     * @param location 包含了目标位置的屏幕坐标({@link Location#xOnScreen}, {@link Location#yOnScreen})
     */
    @SuppressWarnings("unused")
    public void showAsLocation(Location location) {
        prepareShow();
        this.target = null;
        this.location = location;
        show();
    }

    /**
     * 在组件相对内部坐标开始显示悬浮框
     *
     * @param insideTarget 相对父组件
     * @param gapLocation  与父组件的左上顶点距离({@link Location#xOnParent}, {@link Location#yOnParent})
     */
    public void showAsInside(View<?, ?> insideTarget, Location gapLocation) {
        prepareShow();
        this.target = insideTarget;
        this.location = gapLocation;
        show();
    }

    private void show() {
        refreshLocation();
        layoutManager.invalidate(getMainView());

        mDropDownWindow.getContentPane().add(getMainView().getComponent());
        mDropDownWindow.setVisible(true);
        mShowingPopupWindow = this;
    }

    private void prepareShow() {
        if (mShowingPopupWindow != null) {
            mShowingPopupWindow.dismiss();
            mShowingPopupWindow = null;
        }
    }

    /**
     * 销毁悬浮框
     */
    public void dismiss() {
        if (mShowingPopupWindow != null && mShowingPopupWindow != this) {
            mShowingPopupWindow.dismiss();
            mShowingPopupWindow = null;
        }
        mDropDownWindow.setVisible(false);
        mDropDownWindow.dispose();
    }

    @SuppressWarnings("unused")
    public boolean isVisible() {
        return mDropDownWindow != null && mDropDownWindow.isVisible();
    }
}
