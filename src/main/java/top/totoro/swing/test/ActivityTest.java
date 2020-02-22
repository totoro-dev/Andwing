package top.totoro.swing.test;

import top.totoro.swing.widget.simulate.Activity;

public class ActivityTest extends Activity {
    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("onCreate() " + getClass().getSimpleName());
    }
}
