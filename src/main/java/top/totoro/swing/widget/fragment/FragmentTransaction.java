package top.totoro.swing.widget.fragment;

import top.totoro.swing.widget.bean.LayoutAttribute;
import top.totoro.swing.widget.context.Fragment;
import top.totoro.swing.widget.layout.FrameLayout;
import top.totoro.swing.widget.manager.FragmentManager;
import top.totoro.swing.widget.util.Log;
import top.totoro.swing.widget.view.View;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class FragmentTransaction {

    private Map<FrameLayout, List<Fragment>> mFragmentMap;
    private volatile Boolean isCommitting = false;

    private final FragmentManager fragmentManager;

    public FragmentTransaction(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    /**
     * 添加要执行fragment的切换事务。
     *
     * @param frameLayout 执行切换的布局节点id
     * @param fragment    要切换到的fragment
     */
    public void add(FrameLayout frameLayout, Fragment fragment) {
        // 确保在fragment切换过程中不会有其它切换的事务加入
        // 如果不同步处理的话有可能出现布局加载中断（被其它布局抢占）等风险
        synchronized (this) {
            if (mFragmentMap == null) {
                this.mFragmentMap = new HashMap<>();
            }
            if (frameLayout != null && fragment != null) {
                List<Fragment> fragments = this.mFragmentMap.computeIfAbsent(frameLayout, key -> new LinkedList<>());
                fragments.remove(fragment);
                fragments.add(fragment);
            }
        }
    }

    public void show(Fragment fragment) {
        if (fragment == null) {
            throw new IllegalArgumentException("show fragment is null");
        }
        if (mFragmentMap == null) {
            throw new IllegalStateException(fragment + " never add");
        }
        // 将fragment移到所在列表中的最后一个
        mFragmentMap.forEach((frameLayout, fragments) -> {
            if (frameLayout != null && fragments != null) {
                int match = -1;
                for (int i = 0; i < fragments.size(); i++) {
                    Fragment f = fragments.get(i);
                    if (f == fragment) {
                        match = i;
                        break;
                    }
                }
                if (match != -1) {
                    fragments.remove(match);
                    fragments.add(fragment);
                }
            }
        });
        fragment.setVisible(true);
    }

    /**
     * 正式执行一个fragment的切换事务
     */
    public void commit() {
        if (isCommitting) {
            throw new IllegalStateException("commit 事务正在执行中");
        }
        isCommitting = true;
        if (mFragmentMap == null || fragmentManager == null) {
            String msg = "提交的fragment切换事务无法执行：" +
                    "mFragmentMap = " + (mFragmentMap == null ? "null" : mFragmentMap) +
                    ", fragmentManager = " + (fragmentManager == null ? "null" : fragmentManager);
            isCommitting = false;
            throw new IllegalStateException(msg);
        }

        mFragmentMap.forEach((frameLayout, fragments) -> {
            if (frameLayout != null && fragments != null && !fragments.isEmpty()) {
                Fragment show = fragments.get(fragments.size() - 1);
                if (show.isVisible()) {
                    frameLayout.setCurrFragment(show);
                    commitInternal(frameLayout, show);
                }
            }
        });

        isCommitting = false;
    }

    private void commitInternal(FrameLayout frameLayout, Fragment fragment) {
        // 1) 清除frame layout原本fragment的布局，添加新的fragment的布局
        // 清空Layout的子View，防止View的积累
        frameLayout.removeAllSon();
        fragment.getMainView().removeAllSon();

        // 2) 动态创建fragment的布局
        // 将该layout节点的属性复制给fragment的父视图，决定了fragment的显示
        LayoutAttribute layoutAttr = frameLayout.getAttribute();
        fragment.getMainView().setAttribute(new LayoutAttribute());
        fragment.getMainView().setSize(layoutAttr.getWidth(), layoutAttr.getHeight());
        fragment.getMainView().getComponent().setSize(frameLayout.getComponent().getSize());
        View<?, ?> root = fragment.onCreateView(fragment.getLayoutManager(), fragment.getMainView());
        if (root == null) {
            Log.e(this, "commit fail for fragment view is null");
            return;
        }

        // 3) 添加fragment的布局到该frame layout节点中
        // 实现局部布局切换的基础
        frameLayout.addChildView(root);
        frameLayout.setCurrFragment(fragment);

        // 4) 加载fragment布局
        // 需要对fragment进行加载，以确定大小和位置
        frameLayout.invalidate();
    }

}
