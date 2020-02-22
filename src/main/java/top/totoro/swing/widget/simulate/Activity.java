package top.totoro.swing.widget.simulate;

import top.totoro.swing.test.ActivityTest;

import javax.swing.*;

public class Activity extends Context {

    private JFrame frame;

    @Override
    public void onCreate() {
        super.onCreate();

        frame = new JFrame(){
            @Override
            public void dispose() {
                super.dispose();
                onDestroy();
            }
        };
    }

    public static void main(String[] args) {
        Activity activity = new Activity();
        activity.startActivity(activity, ActivityTest.class);
    }
}
