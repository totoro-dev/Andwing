package top.totoro.swing.widget.view;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

public class RecyclerView extends JScrollPane {

    public static final int HORIZONTAL = 1, VERTICAL = 2;
    private JPanel parent; // 放置这个RecyclerView的容器
    private JPanel container; // 在这个RecyclerView中放置子控件的容器
    private int orientation; // 这个RecyclerView的布局方向
    private Adapter adapter; // 这个RecyclerView持有的适配器
    /* 适配器对应的所有RecyclerView实例，用于在适配器的数据集发生改变时进行通知这些RecyclerView实例
     * 使用Weak的弱引用模式，确保在适配器不再有RecyclerView持有时，从映射关系中自动删除并GC*/
    private static final WeakHashMap<Adapter, List<RecyclerView>> instances = new WeakHashMap<>();

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
        parent.setLayout(null);
        parent.add(this);
        container = new JPanel(null);
        getViewport().add(container);
        setOrientation(VERTICAL); // 设置默认布局为垂直方向
        setNoneBorder(); // 设置默认为无边框样式
    }

    /**
     * 设置布局的滚动分向
     *
     * @param orientation 布局方向，对应：HORIZONTAL = 1, VERTICAL = 2;
     */
    public void setOrientation(int orientation) {
        this.orientation = orientation;
        if (orientation == HORIZONTAL) {
            this.setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_AS_NEEDED);
            this.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_NEVER);
        } else if (orientation == VERTICAL) {
            this.setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER);
            this.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_AS_NEEDED);
        }
        /* 如果当前的RecyclerView已经持有适配器，需要重置布局方向*/
        if (adapter != null) setAdapter(adapter);
    }

    /**
     * 设置横向滚动的RecyclerView对应的子控件的适配器
     */
    private void setHorizontalAdapter() {
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
        container.setPreferredSize(new Dimension(width, height));
    }

    /**
     * 设置垂直滚动的RecyclerView对应的子控件的适配器
     */
    private void setVerticalAdapter() {
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
        container.setPreferredSize(new Dimension(width, height));
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
        if (list == null) list = new ArrayList();
        if (!list.contains(this)) list.add(this);
        instances.put(adapter, list);
        setBounds(0, 0, parent.getWidth(), parent.getHeight());
        if (orientation == HORIZONTAL) {
            setHorizontalAdapter();
        } else if (orientation == VERTICAL) {
            setVerticalAdapter();
        }
    }

    /**
     * 当设配器未被设置时，显示的是RecyclerView的背景色，设置之后显示的是container
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

}
