package top.totoro.swing.test;

import swing.R;
import top.totoro.swing.widget.base.Size;
import top.totoro.swing.widget.context.Activity;
import top.totoro.swing.widget.listener.OnClickListener;
import top.totoro.swing.widget.view.Button;
import top.totoro.swing.widget.view.TextView;
import top.totoro.swing.widget.view.View;

import static top.totoro.swing.widget.base.BaseAttribute.GONE;


public class MainActivity extends Activity {
    @Override
    public void onCreate() {
        super.onCreate();
        setContentView(R.layout.activity_main);
        ((TextView) findViewById(R.id.tv_test)).setText("这是第一个页面");
        ((Button) findViewById(R.id.img_test)).setText("跳转第二个页面");
        findViewById(R.id.img_test).addOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                startActivity(MainActivity.this, TargetActivity.class);
            }
        });
    }

    public void onClick(View view) {
        startActivity(this, TargetActivity.class);
    }

    public static class TargetActivity extends Activity {
        @Override
        public void onCreate() {
            super.onCreate();
            setContentView(R.layout.activity_main);
            ((TextView) findViewById(R.id.tv_test)).setText("这是第二个页面");
            findViewById(R.id.img_test).setVisible(GONE);
        }
    }

    public static void main(String[] args) {
        /* 可以指定窗体的大小 */
        newInstance(new Size(500, 500)).startActivity(MainActivity.class);
    }

}
