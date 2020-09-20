package top.totoro.swing.widget.context;

import top.totoro.swing.widget.manager.ActivityManager;
import top.totoro.swing.widget.manager.ServiceManager;
import top.totoro.swing.widget.util.Log;

import java.awt.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public abstract class Service extends Context {

    private static final ExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadExecutor();

    public long mServiceId = 0L;
    private volatile boolean isBinding = false; // 是否是绑定状态
    private volatile boolean hasStarted = false; // 是否启动过，决定能否重新startService
    private volatile boolean hasBound = false; // 曾经绑定过，决定是否reBind
    private Intent mIntent;
    private Frame mFrame; // 如果是startService启动的话需要创建frame用来防止应用退出
    private Future<?> mStartFuture;
    private Future<?> mBindFuture;

    public abstract void onBind(Intent intent);

    public boolean isBinding() {
        return isBinding;
    }

    public void startService(Intent intent) {
        if (hasStarted) {
            Log.e(this, "startService has started, can not start again");
        } else {
            if (mFrame == null) {
                mFrame = new Frame();
                mFrame.setSize(0, 0);
                mFrame.setUndecorated(true);
                mFrame.setVisible(true);
                mFrame.setVisible(false);
            }
            mStartFuture = EXECUTOR_SERVICE.submit(() -> onStartCommand(intent));
            hasStarted = true;
        }
    }

    public void bindService(Intent intent) {
        if (isBinding) {
            Log.e(this, "bindService has bound, can not bind again");
            return;
        } else {
            isBinding = true;
        }
        mBindFuture = EXECUTOR_SERVICE.submit(() -> {
            onBind(intent);
            if (!hasBound) {
                hasBound = true;
                onRebind(intent);
            }
            onStartCommand(intent);
        });
    }

    public void onStartCommand(Intent intent) {
        this.mIntent = intent;
        onStart(intent);
    }

    private void onStart(Intent intent) {

    }

    public boolean onUnbind() {
        return false;
    }

    public void onRebind(Intent intent) {

    }

    public void stopService() {
        if (isBinding) {
            onUnbind();
            if (mBindFuture != null) {
                mBindFuture.cancel(true);
                mBindFuture = null;
            }
            ServiceManager.removeBoundService(mIntent.getCurrentContext(), this);
            isBinding = false;
        }
        if (hasStarted) {
            if (mStartFuture != null) {
                mStartFuture.cancel(true);
                mStartFuture = null;
            }
            ServiceManager.removeStartedService(mIntent.getCurrentContext(), this);
            hasStarted = false;
        }
        onDestroy();
        if (ServiceManager.isEmpty() /* 没有后台服务了 */
                && ActivityManager.getTopActivity() == null /* 没有前台活动了 */) {
            Log.e(this,"system exit for service");
            System.exit(0);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFrame != null) mFrame.dispose();
    }
}
