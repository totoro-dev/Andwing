package top.totoro.swing.test;

import top.totoro.swing.widget.bar.ActionBar;
import top.totoro.swing.widget.base.Size;
import top.totoro.swing.widget.context.Activity;
import top.totoro.swing.widget.listener.OnTextChangeListener;
import top.totoro.swing.widget.util.AttributeDefaultValue;
import top.totoro.swing.widget.util.Log;
import top.totoro.swing.widget.view.EditText;
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
        // 测试Activity中的ActionBar
        setTitle("Java自适应UI框架");
        setTitleColor(Color.decode("#15a5e5"));
        setActionBarHeight(ActionBar.Height.MID);

        EditText et1 = (EditText) findViewById("et1");
        et1.addOnTextChangeListener(new OnTextChangeListener() {
            @Override
            public void onChange(String currentText) {
//                System.out.println(currentText);
            }
        });
//        setCanBack(true);
        // 测试RecyclerView滑动布局的动态加载
        RecycleViewTestAdapter adapter = new RecycleViewTestAdapter();
        RecyclerView rv = (RecyclerView) findViewById("rv1");
        rv.setAdapter(adapter);
        Executors.newScheduledThreadPool(1).schedule(() -> {
            // 测试窗口的大小动态改变后的内容自适应
            resetSize(800, 500);
            // 测试TextView文本框的属性修改
            TextView tv1 = (TextView) findViewById("tv1");
            tv1.setText("文本框测试：文本改变了!");
            tv1.setTextSize(14);
            tv1.setTextColor(Color.BLACK);
            tv1.setTextStyle(AttributeDefaultValue.MONOSPACED);
            tv1.setTextFont(AttributeDefaultValue.ITALIC);
            // 测试ImageView图片的动态变换
            ((ImageView) findViewById("iv1")).setBackgroundImage("img/collect.png");
        }, 3000, TimeUnit.MILLISECONDS);
        Executors.newScheduledThreadPool(1).schedule(() -> {
            // 测试RecyclerView内容动态更换
            adapter.size = 60;
            adapter.notifyDataSetChange();
        }, 4000, TimeUnit.MILLISECONDS);
    }

    public static void main(String[] args) {
        newInstance(new Size(600, 600)).startActivity(ActivityTest.class);
    }
}
