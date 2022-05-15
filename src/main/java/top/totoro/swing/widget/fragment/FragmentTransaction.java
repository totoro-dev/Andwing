package top.totoro.swing.widget.fragment;

import top.totoro.swing.widget.bean.LayoutAttribute;
import top.totoro.swing.widget.context.Fragment;
import top.totoro.swing.widget.layout.FrameLayout;
import top.totoro.swing.widget.manager.FragmentManager;
import top.totoro.swing.widget.util.Log;
import top.totoro.swing.widget.view.View;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

@SuppressWarnings("unused")
public class FragmentTransaction {

    private volatile Map<FrameLayout, List<Fragment>> mFragmentMap;
    private volatile boolean isCommitting = false;

    private final FragmentManager fragmentManager;

    public FragmentTransaction(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    /**
     * 如果map还没初始化则首先初始化
     */
    private void checkFragmentMap() {
        if (mFragmentMap == null) {
            synchronized (this) {
                if (mFragmentMap == null) {
                    this.mFragmentMap = new WeakHashMap<>();
                }
            }
        }
    }

    /**
     * 添加可以被执行切换事务的fragment。
     *
     * @param frameLayout 执行切换的布局节点
     * @param fragment    有效的fragment
     */
    public void add(FrameLayout frameLayout, Fragment fragment) {
        // 确保在fragment添加过程中不会有其它添加的事务加入
        // 如果不同步处理的话有可能出现布局重复的冲突
        if (frameLayout != null && fragment != null) {
            checkFragmentMap();
            synchronized (this) {
                List<Fragment> fragments = this.mFragmentMap.computeIfAbsent(frameLayout, key -> new LinkedList<>());
                fragments.remove(fragment);
                fragments.add(fragment);
            }
        }
    }

    /**
     * 将Fragment从可执行的列表中移除
     *
     * @param fragment 移除的布局
     */
    public void remove(Fragment fragment) {
        if (fragment == null) {
            throw new IllegalArgumentException("remove fragment is null");
        }
        checkFragmentMap();
        synchronized (this) {
            mFragmentMap.forEach((frameLayout, fragments) -> fragments.remove(fragment));
        }
    }

    /**
     * 在所有已知layout节点上显示指定的布局
     *
     * @param fragment 布局
     */
    public void show(Fragment fragment) {
        checkInputFragment(fragment);
        if (fragment.isVisible()) {
            Log.d(this, "fragment is visible, not necessary show again");
            return;
        }
        // 将fragment移到所在列表中的最后一个
        mFragmentMap.forEach((frameLayout, fragments) -> {
            if (frameLayout == null/*显示布局的节点不能为空*/ || fragments == null || fragments.isEmpty()) {
                // 继续下一个节点的查找
                return;
            }
            for (Fragment item : fragments) {
                // 将所有显示中的fragment置为不可见
                if (item.isVisible()) {
                    item.setVisible(false);
                    // 需要在下次commit的时候执行不可见操作
                    item.setCommittable(true);
                }
            }
            // 最后一个显示的Fragment，如果最后一个和目前要显示的不匹配，则需要将其置换到列表尾部
            Fragment lastShow = fragments.get(fragments.size() - 1);
            if (lastShow != fragment) {
                fragments.remove(fragment);
                fragments.add(fragment);
            }
        });
        // 标志fragment是可见的
        fragment.setVisible(true);
        fragment.setCommittable(true);
    }

    /**
     * 在所有已知layout节点上隐藏指定的布局
     *
     * @param fragment 布局
     */
    public void hide(Fragment fragment) {
        checkInputFragment(fragment);
        if (fragment.isVisible()) {
            // 设置fragment不可见了，并且在下次commit时需要做处理
            fragment.setVisible(false);
            fragment.setCommittable(true);
        }
    }

    private void checkInputFragment(Fragment fragment) {
        if (fragment == null) {
            throw new IllegalArgumentException("show fragment is null");
        }
        if (mFragmentMap == null) {
            throw new IllegalStateException(fragment + " never add");
        }
    }

    /**
     * 提交执行一个fragment的切换事务
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
                for (int i = 0; i < fragments.size(); i++) {
                    Fragment fragment = fragments.get(i);
                    Log.d(this, "commit fragment " + fragment);
                    if (!fragment.isCommittable()) {
                        continue;
                    }
                    commitInternal(frameLayout, fragment, i, fragments.size());
                }
            }
        });

        isCommitting = false;
    }

    private void commitInternal(FrameLayout frameLayout, Fragment fragment, int index, int size) {
        if (!fragment.isVisible()) {
            if (frameLayout.getCurrFragment() == fragment) {
                commitInternalHide(frameLayout, fragment);
            }
        } else if (index == size - 1) {
            frameLayout.setCurrFragment(fragment);
            commitInternalShow(frameLayout, fragment);
        }
        fragment.setCommittable(false);
    }

    private void commitInternalHide(FrameLayout frameLayout, Fragment fragment) {
        frameLayout.removeAllSon();
        frameLayout.setCurrFragment(null);
        frameLayout.invalidate();
    }

    private void commitInternalShow(FrameLayout frameLayout, Fragment fragment) {
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
