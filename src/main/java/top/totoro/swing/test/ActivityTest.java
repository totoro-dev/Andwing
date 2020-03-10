package top.totoro.swing.test;

import top.totoro.swing.widget.bar.ActionBar;
import top.totoro.swing.widget.context.Activity;
import top.totoro.swing.widget.util.AttributeDefaultValue;
import top.totoro.swing.widget.util.Log;
import top.totoro.swing.widget.view.ImageView;
import top.totoro.swing.widget.view.RecyclerView;
import top.totoro.swing.widget.view.TextView;

import java.awt.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ActivityTest extends Activity {

    @Override
    public void onCreate() {
        Log.d(this, "onCreate() " + getClass().getSimpleName());
        super.onCreate();
        setResizeable(true);
        setContentView("activity_test.xml");
        setTitle("Java自适应UI框架");
        setTitleColor(Color.decode("#15a5e5"));
//        setActionBarHeight(ActionBar.Height.MIN);
        RecycleViewTestAdapter adapter = new RecycleViewTestAdapter();
        Executors.newScheduledThreadPool(1).schedule(() -> {
//            resetSize(400, 400);
            TextView tv1 = (TextView) findViewById("tv1");
            tv1.setText("文本框测试：文本改变了!");
//            tv1.setTextSize(16);
            tv1.setTextColor(Color.BLACK);
            tv1.setTextStyle(AttributeDefaultValue.MONOSPACED);
            tv1.setTextFont(AttributeDefaultValue.ITALIC);
            ((ImageView) findViewById("iv1")).setBackgroundImage("img/collect.png");
            RecyclerView rv = (RecyclerView) findViewById("rv1");
            rv.setAdapter(adapter);
        }, 500, TimeUnit.MILLISECONDS);
//        Executors.newScheduledThreadPool(1).schedule(() -> {
//            adapter.size = 60;
//            adapter.notifyDataSetChange();
//        }, 3000, TimeUnit.MILLISECONDS);
    }
}
