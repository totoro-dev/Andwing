package top.totoro.swing.widget.view;

import top.totoro.swing.widget.base.BaseAttribute;
import top.totoro.swing.widget.bean.ViewAttribute;
import top.totoro.swing.widget.context.Context;
import top.totoro.swing.widget.context.PopupWindow;
import top.totoro.swing.widget.event.MotionEvent;
import top.totoro.swing.widget.listener.OnClickListener;
import top.totoro.swing.widget.manager.LayoutManager;
import top.totoro.swing.widget.manager.RightClickMenuManager;
import top.totoro.swing.widget.util.SLog;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("unused")
public class View<Attribute extends BaseAttribute, Component extends JComponent> implements MouseListener, MouseMotionListener {

    private static final String DEFAULT_MENU_WINDOW_RES_FILE = "default_menu_window.swing";

    private int minWidth = 0;
    private int minHeight = 0;

    private View<?, ?> parent;
    private View<?, ?> preView; // 这个View的前一个View，用来处理margin属性
    private String parentId = "";
    private String id;
    private final LinkedList<View<?, ?>> sonViews = new LinkedList<>();
    private final Map<String, View<?, ?>> containViewsById = new ConcurrentHashMap<>();
    private LayoutManager layoutManager;
    private OnClickListener clickListener;
    private boolean listenClickEvent = false;
    private Cursor enterCursor = new Cursor(Cursor.DEFAULT_CURSOR);
    protected Attribute attribute;
    protected Component component;
    protected Context context;
    /* 当前正在显示的下拉框 */
    public static Spinner mShowingSpinner;
    /**
     * 是否可以右键弹出菜单选项
     */
    protected boolean showMenuAble = false;

    public View(View<?, ?> parent) {
        this.parent = parent;
        if (parent != null && parent.attribute != null) {
            parentId = parent.attribute.getId();
            setContext(parent.context);
        }
    }

    public View<?, ?> getPreView() {
        return preView;
    }

    public void setPreView(View<?, ?> preView) {
        this.preView = preView;
    }

    public void addOnClickListener(OnClickListener clickListener) {
        this.clickListener = clickListener;
        listenClickEvent = clickListener != null;
    }

    public void removeAllSon() {
        containViewsById.clear();
        sonViews.clear();
        component.removeAll();
        component.repaint();
    }

    public LinkedList<View<?, ?>> getSonViews() {
        return sonViews;
    }

    public Map<String, View<?, ?>> getContainViewsId() {
        return containViewsById;
    }

    public LayoutManager getLayoutManager() {
        return layoutManager;
    }

    public void setLayoutManager(LayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    public void setEnterCursor(Cursor enterCursor) {
        this.enterCursor = enterCursor;
    }

    /**
     * 当前布局刷新时，确定可以冒泡刷新的最大范围
     */
    public void invalidateSuper() {
        if (context != null) {
            context.invalidate();
        } else if (getLayoutManager() != null) {
            getLayoutManager().invalidate();
        } else if (getParent() != null) {
            getParent().invalidate();
        } else {
            invalidate();
        }
    }

    public void setSize(int width, int height) {
        attribute.setWidth(width);
        attribute.setHeight(height);
        component.setSize(width, height);
        invalidate();
    }

    public int getWidth() {
        return component.getWidth();
    }

    public int getHeight() {
        return component.getHeight();
    }

    /**
     * 添加一个子View，只有Layout需要添加子View
     *
     * @param son 子View
     */
    public void addSon(View<?, ?> son) {
        if (son == null) {
            return;
        }
        sonViews.add(son);
    }

    public void removeSon(View<?, ?> son) {
        if (son == null) {
            return;
        }
        sonViews.remove(son);
    }

    /**
     * 根据这个View下子View的位置，获取这个子View的ID
     *
     * @param index 子View的位置
     * @return 子View的ID，不存在则返回“”
     */
    public View<?, ?> getSonByIndex(int index) {
        return sonViews.size() == 0 ? null : sonViews.get(index);
    }

    /**
     * 获取父节点的ID
     *
     * @return 父节点ID，不存在则返回“”
     */
    public String getParentId() {
        return parentId == null ? "" : parentId;
    }

    /**
     * 获取父View，正常的话是个Layout
     *
     * @return 父视图
     */
    public View<?, ?> getParent() {
        return parent;
    }

    /**
     * 获取这个View的ID
     *
     * @return id，默认是创建这个View的系统时间，或在xml或通过setID指定
     */
    public String getId() {
        return attribute == null ? "" : attribute.getId();
    }

    public Component getComponent() {
        return component;
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setAttribute(Attribute attribute) {
        if (attribute == null) return;
        this.attribute = attribute;
//        setId(attribute.getId());
        setShowMenuAble(attribute.getShowMenuAble() == BaseAttribute.VISIBLE);
        component.setVisible(attribute.getVisible() == ViewAttribute.VISIBLE);
        component.setOpaque(attribute.getOpaque() == ViewAttribute.OPAQUE);
        component.setBackground(attribute.getBackground());
        if (!(attribute.getTopBorder() == 0
                && attribute.getBottomBorder() == 0
                && attribute.getLeftBorder() == 0
                && attribute.getRightBorder() == 0)) {
            setBorder(BorderFactory.createMatteBorder(
                    attribute.getTopBorder(),
                    attribute.getLeftBorder(),
                    attribute.getBottomBorder(),
                    attribute.getRightBorder(),
                    attribute.getBorderColor()));
        }
        component.addMouseListener(this);
        component.addMouseMotionListener(this);
    }

    public void setBorder(Border border) {
        component.setBorder(border);
    }

    public int getMinWidth() {
        return minWidth;
    }

    public int getMinHeight() {
        return minHeight;
    }

    public void setMinWidth(int minWidth) {
        this.minWidth = minWidth;
    }

    public void setMinHeight(int minHeight) {
        this.minHeight = minHeight;
    }

    /**
     * 自定义View的ID
     *
     * @param id 新的ID
     */
    public void setId(String id) {
        if (this.id != null && (id == null || id.isEmpty())) {
            unbindViewWithId(this.id);
            this.id = null;
            return;
        }
        try {
            if (!bindViewWithId(id, this)) {
                throw new IllegalArgumentException("setId时，" + id + "无法被设置。");
            }
            this.id = id;
        } catch (Exception e) {
            SLog.e(this, "setId fail" + e);
        }
    }

    /**
     * 将View和ID进行绑定，成功的话View的所有父节点都将更新这个View的ID
     *
     * @param id   将要绑定的id
     * @param view 将要绑定的View
     * @return 绑定是否成功，如果失败的话，其所有父节点也不会修改这个View的ID
     */
    public boolean bindViewWithId(String id, View<?, ?> view) {
        View<?, ?> v = containViewsById.get(id);
        if (v != null && v != view) {
            return false;
        } else {
            if (parent != null) {
                if (parent.bindViewWithId(id, view)) {
                    containViewsById.put(id, view);
                } else {
                    return false;
                }
            } else {
                containViewsById.put(id, view);
            }
        }
        return true;
    }

    /**
     * 将这个视图的id从所有绑定了的试图中解除
     *
     * @param id 要解除的视图id
     */
    public void unbindViewWithId(String id) {
        if (id != null) {
            containViewsById.remove(id);
            if (parent != null) {
                parent.unbindViewWithId(id);
            }
        }
    }

    public View<?, ?> findViewById(String id) {
        View<?, ?> view = containViewsById.get(id);
        try {
            if (view == null) throw new Exception("找不到id为" + id + "的View");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    public void setBackgroundColor(Color bg) {
        attribute.setBackground(bg.toString());
        component.setBackground(bg);
    }

    public void invalidate() {

    }

    public void setVisible(int visible) {
        /* add by HLM on 2020/7/28 解决无效设置导致布局继续刷新的问题 */
        if (visible == attribute.getVisible()) return;
        /* add end */
        attribute.setVisible(visible);
        component.setVisible(attribute.getVisible() == ViewAttribute.VISIBLE);
        invalidateSuper();
    }

    public boolean getVisible() {
        return attribute.getVisible() == ViewAttribute.VISIBLE;
    }

    public void setShowMenuAble(boolean showMenuAble) {
        this.showMenuAble = showMenuAble;
    }

    public boolean isShowMenuAble() {
        return showMenuAble;
    }

    /**
     * 任何需要右键弹出菜单的View都要适配该方法返回自定义的菜单
     *
     * @return 默认创建系统组件的默认菜单，可以自定义菜单弹框
     */
    public PopupWindow createMenuWindow() {
        return new PopupWindow(DEFAULT_MENU_WINDOW_RES_FILE, 160, 40);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (mShowingSpinner != null && !(this instanceof Spinner)) {
            mShowingSpinner.dismiss();
        }
        if (context != PopupWindow.mShowingPopupWindow && PopupWindow.mShowingPopupWindow != null) {
            // 当前点击的view不属于悬浮框的view时才可以dismiss，相当于点击悬浮框外部才能够销毁
            PopupWindow.mShowingPopupWindow.dismiss();
        }
        if (listenClickEvent) {
            clickListener.onClick(this);
        }
        if (context != null) {
            context.dispatchMotionEvent(this, new MotionEvent(e, MotionEvent.ACTION_CLICKED));
        }
        if (showMenuAble && e.getButton() == MouseEvent.BUTTON3/*鼠标右键*/) {
            RightClickMenuManager.getInstance().showMenu(e, this);
        } else if (parent != null) {
            // 向上冒泡
            parent.mouseClicked(e);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (context != null) {
            context.dispatchMotionEvent(this, new MotionEvent(e, MotionEvent.ACTION_DOWN));
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (context != null) {
            context.dispatchMotionEvent(this, new MotionEvent(e, MotionEvent.ACTION_UP));
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        if (enterCursor != null && clickListener != null) {
            component.setCursor(enterCursor);
        }
        if (parent != null) {
            parent.mouseEntered(e);
        }
        if (context != null) {
            context.dispatchMotionEvent(this, new MotionEvent(e, MotionEvent.ACTION_INSIDE));
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        if (clickListener != null) {
            component.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
        if (parent != null) {
            parent.mouseExited(e);
        }
        if (context != null) {
            context.dispatchMotionEvent(this, new MotionEvent(e, MotionEvent.ACTION_OUTSIDE));
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (context != null) {
            context.dispatchMotionEvent(this, new MotionEvent(e, MotionEvent.ACTION_DRAG));
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (context != null) {
            context.dispatchMotionEvent(this, new MotionEvent(e, MotionEvent.ACTION_MOVE));
        }
    }

    public void setParent(View<?, ?> parent) {
        this.parent = parent;
    }

    public Context getContext() {
        return context;
    }
}
