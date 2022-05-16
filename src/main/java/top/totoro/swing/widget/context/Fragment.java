package top.totoro.swing.widget.context;

import top.totoro.swing.widget.base.BaseLayout;
import top.totoro.swing.widget.manager.LayoutManager;
import top.totoro.swing.widget.view.Span;
import top.totoro.swing.widget.view.View;

public class Fragment extends Context {

    /**
     * 在layout节点上是否可见
     */
    private boolean visible = false;
    /**
     * commit的时候是否需要执行操作
     */
    private boolean committable = false;
    /**
     * 是否需要重新创建视图 {@link Fragment#onCreateView(LayoutManager, BaseLayout)}
     */
    private boolean reCreateView = false;
    /**
     * 当前创建了的View
     */
    public View<?, ?> root;

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isCommittable() {
        return committable;
    }

    public void setCommittable(boolean committable) {
        this.committable = committable;
    }

    public boolean isReCreateView() {
        return reCreateView;
    }

    public void setReCreateView(boolean reCreateView) {
        this.reCreateView = reCreateView;
    }

    /**
     * 加载fragment时，需要动态创建布局，
     *
     * @param parent 当前fragment的布局的父布局，
     *               其实就是context的mainView。
     *               因为动态加载需要从父布局开始加载，
     *               所以fragment的布局创建必须建立在parent之上，
     *               否则无法正常显示。
     * @return 动态创建的布局
     */
    public View<?, ?> onCreateView(LayoutManager layoutManager, BaseLayout parent) {
        return new Span(parent);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +
                "{" +
                "visible=" + visible +
                ", committable=" + committable +
                '}';
    }
}
