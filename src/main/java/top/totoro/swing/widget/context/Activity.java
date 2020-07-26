package top.totoro.swing.widget.context;

import top.totoro.swing.test.ActivityTest;
import top.totoro.swing.widget.bar.ActionBar;
import top.totoro.swing.widget.bean.LayoutAttribute;
import top.totoro.swing.widget.listener.OnActionBarClickListener;
import top.totoro.swing.widget.listener.OnActionBarResizeListener;
import top.totoro.swing.widget.listener.OnActivityDragListener;
import top.totoro.swing.widget.listener.OnActivityResizeListener;
import top.totoro.swing.widget.listener.deafultImpl.DefaultActivityResizeMouseListener;
import top.totoro.swing.widget.util.SwingConstants;
import top.totoro.swing.widget.view.View;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Activity extends Context implements OnActionBarClickListener, OnActivityDragListener, OnActionBarResizeListener {

    private ScheduledExecutorService resizeExecutor = Executors.newScheduledThreadPool(1);
    private ScheduledFuture resizeFuture;

    private JFrame frame;
    private OnActivityResizeListener resizeListener;
    private DefaultActivityResizeMouseListener defaultActivityResizeMouseListener;
    private boolean resizeable = true; // 是否允许窗口缩放
    public ActionBar mainBar;
    private JPanel actionBarPanel = new JPanel(null);
    private Point normalLocation;
    private Dimension normalSize;

    public Activity() {
        super();
        defaultActivityResizeMouseListener = new DefaultActivityResizeMouseListener();
        addOnActivityResizeListener(defaultActivityResizeMouseListener.DEFAULT_RESIZE_LISTENER);
        defaultActivityResizeMouseListener.setOnActivityResizeListener(resizeListener);
    }

    public static Activity newInstance(Dimension size) {
        Activity activity = new Activity();
        try {
            if (size != null) {
                activity.setSize(size);
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return activity;
    }

    public static Activity newInstance(Dimension size, Point location) {
        Activity activity = new Activity();
        try {
            if (size != null) {
                activity.setSize(size);
            }
            if (location != null) {
                activity.setLocation(location);
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return activity;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        frame = new JFrame() {
            @Override
            public void dispose() {
                super.dispose();
                onDestroy();
            }
        };

        frame.getContentPane().setLayout(null);
        frame.getContentPane().removeAll();

        frame.setUndecorated(true); // 去除窗体的标题栏

        // 设置窗体大小
        if (getSize() != null) {
            frame.setSize(getSize());
        } else {
            // 默认全屏
            frame.setSize(SwingConstants.getScreenSize());
        }

        // 设置窗体位置
        if (getLocation() != null) {
            frame.setLocation(getLocation());
        } else {
            // 默认居中
            frame.setLocationRelativeTo(null);
            setLocation(frame.getLocation());
        }

        defaultActivityResizeMouseListener.init(this);

        normalLocation = frame.getLocation();
        normalSize = getSize();

        // 设置ActionBar
        actionBarPanel.setSize(frame.getWidth(), 0);
        frame.add(actionBarPanel);
        mainBar = new ActionBar(actionBarPanel);
        resetActionBar();
    }

    @Override
    public void onStart() {
        super.onStart();
        onResume();
    }

    @Override
    public void onRestart() {
        super.onRestart();
        onResume();
    }

    /**
     * 窗体被显示时调用
     */
    @Override
    public void onResume() {
        super.onResume();
        // 设置窗体可见
        if (!frame.isVisible()) {
            frame.setVisible(true);
        }

        mainBar.addOnActionBarClickListener(this);
        mainBar.addOnActivityDragListener(this);
        mainBar.addOnActionBarResizeListener(this);
    }

    /**
     * 该方法会在窗体最小化时调用
     */
    @Override
    public void onStop() {
        super.onStop();
        // 设置窗体不可见
        if (frame.isVisible()) {
            frame.setVisible(false);
        }
    }

    /**
     * 窗体被关闭时调用
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        onPause();
        onStop();
    }

    protected void setActionBarHeight(ActionBar.Height height) {
        mainBar.setHeight(height);
    }

    protected void setActionBarBorderColor(Color color) {
        mainBar.setBorder(1, color);
    }

    protected void setTitle(String title) {
        mainBar.setTitleText(title);
    }

    protected void setTitleColor(Color color) {
        mainBar.setTitleColor(color);
    }

    /**
     * 窗口变化时ActionBar会被重置
     */
    private void resetActionBar() {
        actionBarPanel.setSize(frame.getWidth(), actionBarPanel.getHeight());
        Dimension screenSize = SwingConstants.getScreenSize();
        if (frame.getWidth() < screenSize.width || frame.getHeight() < screenSize.getHeight()) {
            mainBar.canMidScreen(false);
        } else {
            mainBar.canMidScreen(true);
        }
        mainBar.resize();
    }

    /**
     * 窗口是否可以缩放
     *
     * @return true：可以，否则不可以
     */
    public boolean isResizeable() {
        return resizeable;
    }

    /**
     * 设置是否允许窗口缩放
     *
     * @param resizeable 是否允许，true：可以缩放，否则不允许
     */
    public void setResizeable(boolean resizeable) {
        this.resizeable = resizeable;
    }

    /**
     * 重置窗体大小
     *
     * @param width  新的窗体宽度
     * @param height 新的窗体高度
     */
    public void resetSize(int width, int height) {
        frame.setSize(width, height);
        resetSize();
    }

    /**
     * 重置窗体大小
     */
    private void resetSize() {
        resetActionBar();
        try {
            // 持续缩放窗口将不会进行立刻刷新，只有当间隔时间超过25ms才会全局刷新
            if (resizeFuture != null) resizeFuture.cancel(true);
            resizeFuture = resizeExecutor.schedule(resizeTask, 25, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Runnable resizeTask = () -> {
        getMainView().getComponent().setLocation(0, actionBarPanel.getHeight());
        getMainView().getComponent().setSize(frame.getWidth(), frame.getHeight() - actionBarPanel.getHeight());
        invalidate();
        defaultActivityResizeMouseListener.resetFrameBoundRect();
        /* add by HLM on 2020/7/26 解决显示中的下拉框由于窗口大小拉伸而跟随移动的功能 */
        if (View.mShowingSpinner != null) {
            View.mShowingSpinner.moveTo();
        }
    };

    /**
     * 重置窗口位置
     *
     * @param x 横坐标
     * @param y 纵坐标
     */
    public void resetLocation(int x, int y) {
        frame.setLocation(x, y);
        setLocation(frame.getLocation());
        /* add by HLM on 2020/7/26 解决显示中的下拉框跟随窗口移动的功能 */
        if (View.mShowingSpinner != null) {
            View.mShowingSpinner.moveTo();
        }
    }

    /**
     * 获取窗口的主容器
     *
     * @return 当前窗口的容器
     */
    public JFrame getFrame() {
        return frame;
    }

    /**
     * 设置窗口缩放的监听
     *
     * @param resizeListener 窗口缩放监听接口
     */
    public void addOnActivityResizeListener(OnActivityResizeListener resizeListener) {
        this.resizeListener = resizeListener;
        defaultActivityResizeMouseListener.setOnActivityResizeListener(resizeListener);
    }

    /**
     * 设置窗口的布局
     *
     * @param resName 窗口布局的xml文件（eg. activity_main.xml）
     */
    public void setContentView(String resName) {
        LayoutAttribute attribute = new LayoutAttribute();
        attribute.setWidth(LayoutAttribute.MATCH_PARENT);
        attribute.setHeight(LayoutAttribute.MATCH_PARENT);
        getMainView().getComponent().removeAll();
        getMainView().setAttribute(attribute);
        getMainView().getComponent().setLocation(0, actionBarPanel.getHeight());
        getMainView().getComponent().setSize(frame.getWidth(), frame.getHeight() - actionBarPanel.getHeight());
        getMainView().setLayoutManager(layoutManager);
        layoutManager.inflate(getMainView(), resName);
        layoutManager.invalidate();
        frame.getContentPane().add(getMainView().getComponent());
        onStart();
    }

    @Override
    public void onBackClick() {

    }

    @Override
    public void onMinClick() {
        /* add by HLM on 2020/7/27 解决显示中的下拉框在窗体最小化时的隐藏功能 */
        if (View.mShowingSpinner != null) {
            View.mShowingSpinner.dismiss();
        }
        frame.setExtendedState(JFrame.ICONIFIED);
    }

    @Override
    public void onMidClick() {
        resetLocation(normalLocation.x, normalLocation.y);
        resetSize(normalSize.width, normalSize.height);
    }

    @Override
    public void onMaxClick() {
        normalSize = frame.getSize();
        normalLocation = frame.getLocation();
        Dimension screen = SwingConstants.getScreenSize();
        resetLocation(0, 0);
        resetSize(screen.width, screen.height);
    }

    @Override
    public void onCloseClick() {
        /* add by HLM on 2020/7/27 解决显示中的下拉框在窗体销毁时的隐藏功能 */
        if (View.mShowingSpinner != null) {
            View.mShowingSpinner.dismiss();
        }
        frame.dispose();
    }

    @Override
    public void onActivityDrag(Point start, int x, int y) {
        resetLocation(frame.getX() - start.x + x, frame.getY() - start.y + y);
    }

    @Override
    public void onActionBarResize() {
        resetSize();
    }
}
