package top.totoro.swing.widget.context;

import com.sun.istack.internal.NotNull;
import top.totoro.swing.widget.base.BaseLayout;
import top.totoro.swing.widget.base.Location;
import top.totoro.swing.widget.base.Size;
import top.totoro.swing.widget.interfaces.ContextWrapper;
import top.totoro.swing.widget.layout.LinearLayout;
import top.totoro.swing.widget.listener.InvalidateListener;
import top.totoro.swing.widget.manager.ActivityManager;
import top.totoro.swing.widget.manager.LinearLayoutManager;
import top.totoro.swing.widget.util.AnimateUtil;
import top.totoro.swing.widget.util.Log;
import top.totoro.swing.widget.view.View;

import java.util.ArrayList;
import java.util.List;

public class Context implements ContextWrapper {

    private Location location;
    private Size size;
    private BaseLayout mainView = new LinearLayout(null);
    protected LinearLayoutManager layoutManager = new LinearLayoutManager();
    protected List<InvalidateListener> invalidateListenerList = new ArrayList<>();

    public LinearLayoutManager getLayoutManager() {
        return layoutManager;
    }

    public void requestInvalidateListener(InvalidateListener listener) {
        invalidateListenerList.remove(listener);
        invalidateListenerList.add(listener);
    }

    public Context() {
        mainView.setContext(this);
    }

    public void startActivity(@NotNull Context context, Class<? extends Activity> target) {
        Activity activity = ActivityManager.getOrCreateActivity(target);
        ActivityManager.setTopActivity(activity);
        if (context instanceof Activity) {
            activity.setParentActivity((Activity) context);
            activity.resetLocation(((Activity) context).getFrame().getLocation().x, ((Activity) context).getFrame().getLocation().y);
            AnimateUtil.transparentOut((Activity) context, 0.5f, () -> {
                context.onStop();
                if (activity.isOnRestart()) {
                    activity.onStart();
                } else {
                    activity.setLocation(context.location);
                    activity.setSize(context.size);
                    activity.onCreate();
                }
                AnimateUtil.transparentIn(activity, 0.75f, () -> activity.setCanBack(true));
            });
        } else {
            activity.setParentActivity(null);
            activity.setCanBack(false);
            activity.setLocation(null);
            AnimateUtil.zoomIn(activity, new Size(500, 500), 0.75f);
        }
    }

    public void startActivity(Class<? extends Activity> target) {
        Activity activity = ActivityManager.getOrCreateActivity(target);
        ActivityManager.setTopActivity(activity);
        if (activity.isOnRestart()) {
            activity.onStart();
        } else {
            activity.setLocation(location);
            activity.setSize(size);
            activity.onCreate();
        }
        AnimateUtil.transparentIn(activity, 0.75f);
    }

    public View findViewById(String id) {
        return mainView.findViewById(id);
    }

    public BaseLayout getMainView() {
        return mainView;
    }

    public void invalidate() {
        if (mainView != null && layoutManager != null) {
            mainView.invalidate();
            layoutManager.invalidate();
            for (InvalidateListener listener :
                    invalidateListenerList) {
                listener.onInvalidateFinished();
            }
        }
    }

    public void onCreate() {
    }

    public void onStart() {

    }

    public void onRestart() {

    }

    public void onResume() {

    }

    public void onPause() {

    }

    public void onStop() {

    }

    public void onDestroy() {

    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setLocation(int x, int y) {
        this.location = new Location(x, y);
    }

    public Location getLocation() {
        return location;
    }

    public Size getSize() {
        return size;
    }

    public void setSize(Size size) {
        this.size = size;
    }

    public void setSize(int width, int height) {
        this.size = new Size(width, height);
    }
}
