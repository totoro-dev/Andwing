package top.totoro.swing.widget.simulate;

import top.totoro.swing.widget.view.BaseScrollBar;
import top.totoro.swing.widget.view.HorizontalScrollBar;
import top.totoro.swing.widget.view.VerticalScrollBar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

public class RecyclerView extends JComponent {

    public static final int HORIZONTAL = 1, VERTICAL = 2;
    private JPanel parent; // 放置这个RecyclerView的容器
    private JPanel container; // 在这个RecyclerView中放置子控件的容器
    private BaseScrollBar.Vertical verticalScrollBar; // 垂直滚动条
    private BaseScrollBar.Horizontal horizontalScrollBar; // 水平滚动条
    private int orientation; // 这个RecyclerView的布局方向
    private Adapter adapter; // 这个RecyclerView持有的适配器
    /* 适配器对应的所有RecyclerView实例，用于在适配器的数据集发生改变时进行通知这些RecyclerView实例
     * 使用Weak的弱引用模式，确保在适配器不再有RecyclerView持有时，从映射关系中自动删除并GC*/
    private static final WeakHashMap<Adapter, List<RecyclerView>> instances = new WeakHashMap<>();

    private boolean mousePressing; // 鼠标是否按住
    private boolean shiftPressing; // Shift键是否按住

    private int verticalBarHeight = 0;
    private int verticalBarY = 0, clickY = 0;
    private int maxVerticalBarY = 0;

    private int horizontalBarWidth = 0;
    private int horizontalBarX = 0, clickX = 0;
    private int maxHorizontalBarX = 0;

    public static abstract class Adapter<ViewHolder extends JPanel> {

        public abstract ViewHolder onCreateViewHolder(JPanel container);

        public abstract void onBindViewHolder(ViewHolder holder, int position);

        public abstract int getItemCount();

        /**
         * 通知持有该适配器的所有RecyclerView，数据集发生改变
         * 修改这些RecyclerView的内容
         * 如果正在浏览位置前的数据集数量变少，会导致浏览的位置后移。
         * 如果正在浏览位置前的数据集数量变多，会导致浏览的位置前移。
         * 如果正在浏览位置前的数据集数量不变，不改变正在浏览的位置。
         */
        public void notifyDataSetChange() {
            List<RecyclerView> list = instances.get(this);
            if (list == null || list.size() == 0) return;
            for (RecyclerView instance :
                    list) {
                if (instance == null) continue;
                instance.setAdapter(this);
                instance.validate(); // 更新滚动条状态
            }
        }
    }

    public RecyclerView(JPanel parent) {
        this.parent = parent;
        setLayout(null);
        parent.setLayout(null);
        parent.add(this);
        container = new JPanel(null);
        verticalScrollBar = new VerticalScrollBar();
        horizontalScrollBar = new HorizontalScrollBar();
        setNoneBorder(); // 设置默认为无边框样式
        setOrientation(VERTICAL); // 设置默认布局为垂直方向
        setFocusable(true); // 使得RecyclerView可以监听键盘事件
        initMainListener();
        add(container);
    }

    /**
     * 设置布局的滚动分向
     *
     * @param orientation 布局方向，对应：HORIZONTAL = 1, VERTICAL = 2;
     */
    public void setOrientation(int orientation) {
        this.orientation = orientation;
        if (orientation == HORIZONTAL) {
            initHorizontalListener();
        } else if (orientation == VERTICAL) {
            initVerticalListener();
        }
        /* 如果当前的RecyclerView已经持有适配器，需要重置布局方向*/
        if (adapter != null) setAdapter(adapter);
    }

    /**
     * 设置RecyclerView的适配器
     *
     * @param adapter 适配器
     */
    public void setAdapter(Adapter adapter) {
        this.adapter = adapter;
        container.removeAll();
        List<RecyclerView> list = instances.get(adapter);
        if (list == null) {
            if (list == null) list = new ArrayList<>();
            list.add(this);
            instances.put(adapter, list);
        } else if (!list.contains(this)) {
            list.add(this);
        }
        setBounds(0, 0, parent.getWidth(), parent.getHeight());
        if (orientation == HORIZONTAL) {
            setHorizontalAdapter();
        } else if (orientation == VERTICAL) {
            setVerticalAdapter();
        }
        verticalScrollBar.setVisible(false);
        horizontalScrollBar.setVisible(false);
        repaint();
    }

    /**
     * 设置自定义的垂直滚动条
     *
     * @param verticalScrollBar 自定义的滚动条
     */
    public void setVerticalScrollBar(BaseScrollBar.Vertical verticalScrollBar) {
        this.verticalScrollBar = verticalScrollBar;
        initVerticalListener();
    }

    /**
     * 设置自定义的水平滚动条
     *
     * @param horizontalScrollBar 自定义的滚动条
     */
    public void setHorizontalScrollBar(BaseScrollBar.Horizontal horizontalScrollBar) {
        this.horizontalScrollBar = horizontalScrollBar;
        initHorizontalListener();
    }

    /**
     * 背景色设置
     *
     * @param bg 背景色
     */
    @Override
    public void setBackground(Color bg) {
        super.setBackground(bg);
        if (this.container != null) this.container.setBackground(bg);
    }

    /**
     * 设置无边框样式
     */
    public void setNoneBorder() {
        setBorder(BorderFactory.createMatteBorder(0, 0, 0, 0, Color.white));
    }

    /**
     * 设置横向滚动的RecyclerView对应的子控件的适配器
     */
    private void setHorizontalAdapter() {
        container.add(horizontalScrollBar);
        int count = adapter.getItemCount();
        int width = 0, height = 0;
        JPanel[] items = new JPanel[count];
        for (int i = 0; i < count; i++) {
            JPanel item = adapter.onCreateViewHolder(container);
            adapter.onBindViewHolder(item, i);
            item.setLocation(width, 0);
            width += item.getWidth();
            if (height < item.getHeight()) height = item.getHeight();
            items[i] = item;
            container.add(item);
        }
        container.setBounds(0, 0, width, getHeight());
        // 垂直方向上，滚轮滚动长度与容器高度的比例
        double horizontal = getWidth() / (double) width;
        horizontalBarWidth = (int) (getWidth() * horizontal);
        maxHorizontalBarX = getWidth() - horizontalBarWidth;
        horizontalScrollBar.setBarWidth(horizontalBarWidth);
        horizontalBarX = 0;
        horizontalScrollBar.setBarX(horizontalBarX);
        horizontalScrollBar.setBounds(0, getHeight() - horizontalScrollBar.getHeight(), getWidth(), horizontalScrollBar.getHeight());
    }

    /**
     * 设置垂直滚动的RecyclerView对应的子控件的适配器
     */
    private void setVerticalAdapter() {
        container.add(verticalScrollBar);
        int count = adapter.getItemCount();
        int width = 0, height = 0;
        JPanel[] items = new JPanel[count];
        for (int i = 0; i < count; i++) {
            JPanel item = adapter.onCreateViewHolder(container);
            adapter.onBindViewHolder(item, i);
            item.setLocation(0, height);
            height += item.getHeight();
            if (width < item.getWidth()) width = item.getWidth();
            items[i] = item;
            container.add(item);
        }
        container.setBounds(0, 0, getWidth(), height);
        // 垂直方向上，滚轮滚动长度与容器高度的比例
        double vertical = getHeight() / (double) height;
        verticalBarHeight = (int) (getHeight() * vertical);
        maxVerticalBarY = getHeight() - verticalBarHeight;
        verticalScrollBar.setBarHeight(verticalBarHeight);
        verticalBarY = 0;
        verticalScrollBar.setBarY(verticalBarY);
        verticalScrollBar.setBounds(getWidth() - verticalScrollBar.getWidth(), 0, verticalScrollBar.getWidth(), getHeight());
    }

    private MouseListener verticalMouseListener = new MouseListener() {
        @Override
        public void mouseClicked(MouseEvent e) {
        }

        @Override
        public void mousePressed(MouseEvent e) {
            clickY = e.getY();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            verticalScrollBar.setVisible(true);
        }

        @Override
        public void mouseExited(MouseEvent e) {
            if (mousePressing) return;
            verticalScrollBar.setVisible(false);
        }
    };
    private MouseMotionListener verticalMouseMotionLister = new MouseMotionListener() {
        @Override
        public void mouseDragged(MouseEvent e) {
            int gap = e.getY() - clickY;
            if (scrolledVertical(gap)) {
                clickY = e.getY();
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {
        }
    };
    private MouseListener horizontalMouseListener = new MouseListener() {
        @Override
        public void mouseClicked(MouseEvent e) {
        }

        @Override
        public void mousePressed(MouseEvent e) {
            clickX = e.getX();
            mousePressing = true;
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            mousePressing = false;
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            horizontalScrollBar.setVisible(true);
        }

        @Override
        public void mouseExited(MouseEvent e) {
            if (mousePressing) return;
            horizontalScrollBar.setVisible(false);
        }
    };
    private MouseMotionListener horizontalMouseMotionLister = new MouseMotionListener() {
        @Override
        public void mouseDragged(MouseEvent e) {
            int gap = e.getX() - clickX;
            if (scrolledHorizontal(gap)) {
                clickX = e.getX();
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {
        }
    };

    private void initMainListener() {
        addMouseWheelListener(e -> {
            int gap = e.getWheelRotation() * e.getScrollAmount();
            if (verticalScrollBar.getVisible()) {
                scrolledVertical(gap);
            } else if (horizontalScrollBar.getVisible() && shiftPressing) {
                scrolledHorizontal(gap);
            }
        });
        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
                mousePressing = true;
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                mousePressing = false;
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (orientation == VERTICAL) verticalScrollBar.setVisible(true);
                if (orientation == HORIZONTAL) horizontalScrollBar.setVisible(true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (mousePressing) return;
                verticalScrollBar.setVisible(false);
                horizontalScrollBar.setVisible(false);
            }
        });
        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                    shiftPressing = true;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                    shiftPressing = false;
                }
            }
        });
    }

    private void initVerticalListener() {
        verticalScrollBar.setMouseListener(verticalMouseListener);
        verticalScrollBar.setMouseMotionListener(verticalMouseMotionLister);
    }

    private void initHorizontalListener() {
        horizontalScrollBar.setMouseListener(horizontalMouseListener);
        horizontalScrollBar.setMouseMotionListener(horizontalMouseMotionLister);
    }

    /**
     * 垂直方向滚动布局
     *
     * @param gap 滚轮滚动长度
     */
    private boolean scrolledVertical(int gap) {
        int tmp = gap + verticalBarY;
        if (tmp > maxVerticalBarY) {
            // 垂直方向上，设置滚动至底部
            container.setLocation(container.getX(), getHeight() - container.getHeight());
            verticalScrollBar.setLocation(verticalScrollBar.getX(), -container.getY());
            repaint();
        } else if (tmp < 0) {
            // 垂直方向上，设置滚动至顶部
            container.setLocation(container.getX(), 0);
            verticalScrollBar.setLocation(verticalScrollBar.getX(), 0);
            repaint();
        } else if (gap >= 1 || gap <= -1) {
            if (getHeight() == verticalBarHeight) return true;
            // 滚动比例，带符号。"+"：垂直向下滚动；"-"：垂直向上滚动
            // getHeight() - verticalBarHeight可滚动的长度，相当于容器的不可见长度
            double scale = gap / (double) (getHeight() - verticalBarHeight);
            int scrollHeight = (int) ((container.getHeight() - getHeight()) * scale);
            if (container.getY() + container.getHeight() - getHeight() < 0 && scrollHeight <= 0) return true;
            if (container.getY() + getHeight() - container.getHeight() > 0 && scrollHeight >= 0) return true;
            verticalBarY = tmp;
            verticalScrollBar.setBarY(verticalBarY);
            container.setLocation(container.getX(), container.getY() - scrollHeight);
            verticalScrollBar.setLocation(verticalScrollBar.getX(), -container.getY());
            repaint();
            return true;
        }
        return false;
    }

    /**
     * 水平方向滚动布局
     *
     * @param gap 滚轮滚动长度
     */
    private boolean scrolledHorizontal(int gap) {
        int tmp = gap + horizontalBarX;
        if (tmp > maxHorizontalBarX) {
            //水平方向上，设置滚动至右端
            container.setLocation(getWidth() - container.getWidth(), container.getY());
            horizontalScrollBar.setLocation(-container.getX(), horizontalScrollBar.getY());
            repaint();
        } else if (tmp < 0) {
            // 水平方向上，设置滚动至左端
            container.setLocation(0, container.getY());
            horizontalScrollBar.setLocation(-container.getX(), horizontalScrollBar.getY());
            repaint();
        } else if (gap >= 1 || gap <= -1) {
            if (getWidth() == horizontalBarWidth) return true;
            // getWidth() - horizontalBarWidth可滚动的宽度，相当于容器的不可见宽度
            // 滚动比例，带符号。"+"：水平向右滚动；"-"：水平向左滚动
            double scale = gap / (double) (getWidth() - horizontalBarWidth);
            int scrollWidth = (int) ((container.getWidth() - getWidth()) * scale);
            if (container.getX() + container.getWidth() - getWidth() < 0 && scrollWidth <= 0) return true;
            if (container.getX() + getWidth() - container.getWidth() > 0 && scrollWidth >= 0) return true;
            horizontalBarX = tmp;
            horizontalScrollBar.setBarX(horizontalBarX);
            container.setLocation(container.getX() - scrollWidth, container.getY());
            horizontalScrollBar.setLocation(-container.getX(), horizontalScrollBar.getY());
            repaint();
            return true;
        }
        return false;
    }

}
