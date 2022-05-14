package top.totoro.swing.widget.manager;

import top.totoro.swing.widget.fragment.FragmentTransaction;

@SuppressWarnings("unused")
public class FragmentManager {
    public FragmentManager() {
        // 一个Activity全局只能拥有一个实例，因为fragment的切换是原子性的事物
    }

    public FragmentTransaction beginTransaction() {
        return new FragmentTransaction(this);
    }
}
