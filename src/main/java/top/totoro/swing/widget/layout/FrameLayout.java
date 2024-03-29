package top.totoro.swing.widget.layout;

import top.totoro.swing.widget.context.Fragment;
import top.totoro.swing.widget.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * 为fragment提供布局节点
 * 允许在这个节点中替换子布局，从而切换布局
 */
@SuppressWarnings("unused")
public class FrameLayout extends LinearLayout {

    private final List<Fragment> containFragments = new ArrayList<>();
    private Boolean addFragmentEnd = true;
    private Fragment currFragment;

    public Boolean getAddFragmentEnd() {
        return addFragmentEnd;
    }

    public Fragment getCurrFragment() {
        return currFragment;
    }

    public void setCurrFragment(Fragment currFragment) {
        this.currFragment = currFragment;
    }

    public FrameLayout(View<?, ?> parent) {
        super(parent);
    }

    /**
     * 向该frame layout节点添加一个fragment，
     * 需要通过FragmentManager{@link top.totoro.swing.widget.manager.FragmentManager}
     * 执行transaction方法从而创建一个fragment切换事务，否则即使添加成功，也无法得到切换fragment的目的
     *
     * @param fragment 需要被添加并执行切换的窗口碎片
     */
    public void addFragment(Fragment fragment) {
        if (checkFragment(fragment)) return;
        synchronized (containFragments) {
            addFragmentEnd = false;
            containFragments.add(fragment);
            addFragmentEnd = true;
        }
    }

    /**
     * 检查fragment是否已经被添加到当前的frame layout节点
     *
     * @param fragment 需要检查的fragment布局
     * @return 是：已经添加过了，或是fragment为空；否：未曾添加
     */
    public boolean checkFragment(Fragment fragment) {
        return fragment == null || containFragments.contains(fragment);
    }

    @Override
    public void invalidate() {
        super.invalidate();
        if (currFragment != null) {
            // 自身刷新的同时要刷新当前显示中的Fragment
            currFragment.invalidate();
        }
    }

    @Override
    public String toString() {
        return "FrameLayout{" +
                "containFragments=" + containFragments +
                '}';
    }
}
