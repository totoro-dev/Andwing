package top.totoro.swing.widget.simulate;

import top.totoro.swing.widget.view.ToastContent;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Toast extends JWindow {

    private Container context;
    private Graphics graphics;
    private int width = 30;
    private long showTime = 0;
    private ToastContent content;

    public static final long SHORT = 1500;
    public static final long LONG = 3000;
    /* 用于关闭Toast队列的定时服务 */
    private static final ScheduledExecutorService closeService = Executors.newScheduledThreadPool(1);
    private static final ScheduledExecutorService waitService = Executors.newScheduledThreadPool(1);
    private static LinkedList<Toast> waitQueue = new LinkedList<>();
    private static long waitingDelay = 0;

    private Toast() {
    }

    public static Toast makeText(Container context, String text) {
        Toast toast = new Toast();
        toast.context = context;
        toast.width += 16;
        toast.setAlwaysOnTop(true);
        char[] chars = text.toCharArray();
        for (char c :
                chars) {
            if (Integer.valueOf(Integer.toString(c)) < 128) {
                toast.width += 8;
            } else {
                if (String.valueOf(c).matches("。？、“”——")) {
                    toast.width += 10;
                } else {
                    toast.width += 16;
                }
            }
        }
        toast.setLocation(context.getLocation().x + (context.getWidth() - toast.width) / 2, context.getLocation().y + context.getHeight() + 40);
        toast.setSize(toast.width, 30);
        toast.content = new ToastContent(text, context.getLocation().x + (context.getWidth() - toast.width) / 2, context.getLocation().y + context.getHeight() + 40);
        toast.add(toast.content);
        return toast;
    }

    private Runnable waitTask = () -> {
        Toast toast = waitQueue.pollFirst();
        if (toast != null) {
            if (context.isVisible()) {
                toast.setVisible(true);
                closeService.schedule(toast::dispose, toast.showTime - 5, TimeUnit.MILLISECONDS);
            }
        }
    };

    public void show(long delay) {
        this.showTime = delay;
        waitService.schedule(waitTask, waitingDelay, TimeUnit.MILLISECONDS);
        waitingDelay += delay;
        waitQueue.add(this);
    }

    @Override
    public void paint(Graphics g) {
        content.paint(g);
    }

    @Override
    public void dispose() {
        super.dispose();
        waitingDelay -= this.showTime;
    }
}
