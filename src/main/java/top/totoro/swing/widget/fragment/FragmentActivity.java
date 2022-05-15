package top.totoro.swing.widget.fragment;

import top.totoro.swing.widget.context.Activity;
import top.totoro.swing.widget.manager.FragmentManager;

public class FragmentActivity extends Activity {
    private FragmentManager fragmentManager;

    public FragmentManager getFragmentManager() {
        if (fragmentManager == null) {
            synchronized (this) {
                if (fragmentManager == null) {
                    fragmentManager = new FragmentManager();
                }
            }
        }
        return fragmentManager;
    }
}
