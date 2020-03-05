package top.totoro.swing.widget.listener.deafultImpl;

import top.totoro.swing.widget.context.Activity;
import top.totoro.swing.widget.listener.OnActivityResizeListener;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * 窗口拉伸、收缩的接口默认实现
 */
public class DefaultActivityResizeMouseListener implements MouseListener, MouseMotionListener {

    private Activity activity;
    private OnActivityResizeListener resizeListener;
    private Rectangle leftTop, leftBottom, rightTop, rightBottom, left, right, top, bottom;
    private boolean prepareResize = false, resizing = false;
    private int resizeType = 0;

    public void init(Activity activity) {
        this.activity = activity;
        resetFrameBoundRect();
        activity.getFrame().addMouseListener(this);
        activity.getFrame().addMouseMotionListener(this);
    }

    public void resetFrameBoundRect() {
        int w = activity.getFrame().getWidth(), h = activity.getFrame().getHeight();
        leftTop = new Rectangle(0, 0, 10, 10);
        leftBottom = new Rectangle(0, h - 10, 10, 10);
        rightTop = new Rectangle(w - 10, 0, 10, 10);
        rightBottom = new Rectangle(w - 10, h - 10, 10, 10);
        left = new Rectangle(0, 10, 10, h - 20);
        right = new Rectangle(w - 10, 10, 10, h - 20);
        top = new Rectangle(10, 0, w - 20, 10);
        bottom = new Rectangle(10, h - 10, w - 20, 10);
    }

    public void setOnActivityResizeListener(OnActivityResizeListener resizeListener) {
        this.resizeListener = resizeListener;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (prepareResize) {
            resizing = true;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (prepareResize && resizing) {
            prepareResize = false;
            resizing = false;
            activity.getFrame().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (prepareResize && resizing && resizeListener != null) {
            resizeListener.onResizeDoing(resizeType, e.getX(), e.getY());
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (activity.isResizeable()) {
            Point point = e.getPoint();
            prepareResize = true;
            if (leftTop.contains(point)) {
                resizeType = OnActivityResizeListener.LEFT_TOP;
                activity.getFrame().setCursor(new Cursor(Cursor.NW_RESIZE_CURSOR));
            } else if (leftBottom.contains(point)) {
                resizeType = OnActivityResizeListener.LEFT_BOTTOM;
                activity.getFrame().setCursor(new Cursor(Cursor.SW_RESIZE_CURSOR));
            } else if (rightTop.contains(point)) {
                resizeType = OnActivityResizeListener.RIGHT_TOP;
                activity.getFrame().setCursor(new Cursor(Cursor.NE_RESIZE_CURSOR));
            } else if (rightBottom.contains(point)) {
                resizeType = OnActivityResizeListener.RIGHT_BOTTOM;
                activity.getFrame().setCursor(new Cursor(Cursor.SE_RESIZE_CURSOR));
            } else if (left.contains(point)) {
                resizeType = OnActivityResizeListener.LEFT;
                activity.getFrame().setCursor(new Cursor(Cursor.W_RESIZE_CURSOR));
            } else if (right.contains(point)) {
                resizeType = OnActivityResizeListener.RIGHT;
                activity.getFrame().setCursor(new Cursor(Cursor.E_RESIZE_CURSOR));
            } else if (top.contains(point)) {
                resizeType = OnActivityResizeListener.TOP;
                activity.getFrame().setCursor(new Cursor(Cursor.N_RESIZE_CURSOR));
            } else if (bottom.contains(point)) {
                resizeType = OnActivityResizeListener.BOTTOM;
                activity.getFrame().setCursor(new Cursor(Cursor.S_RESIZE_CURSOR));
            } else {
                prepareResize = false;
                activity.getFrame().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        }
    }

    public final OnActivityResizeListener DEFAULT_RESIZE_LISTENER = new OnActivityResizeListener() {
        @Override
        public void onResizeDoing(int type, int x, int y) {
            int lx = activity.getFrame().getX(), ly = activity.getFrame().getY(), w = activity.getFrame().getWidth(), h = activity.getFrame().getHeight();
            switch (type) {
                case LEFT_TOP:
                    activity.getFrame().setLocation(lx + x, ly + y);
                    activity.resetSize(w - x, h - y);
                    break;
                case LEFT_BOTTOM:
                    activity.getFrame().setLocation(lx + x, ly);
                    activity.resetSize(w - x, y);
                    break;
                case RIGHT_TOP:
                    activity.getFrame().setLocation(lx, ly + y);
                    activity.resetSize(x, h - y);
                    break;
                case RIGHT_BOTTOM:
                    activity.resetSize(x, y);
                    break;
                case LEFT:
                    activity.resetLocation(lx + x, ly);
                    activity.resetSize(w - x, h);
                    break;
                case RIGHT:
                    activity.resetSize(x, h);
                    break;
                case TOP:
                    activity.resetLocation(lx, ly + y);
                    activity.resetSize(w, h - y);
                    break;
                case BOTTOM:
                    activity.resetSize(w, y);
                    break;
            }
            resetFrameBoundRect();
        }
    };

}
