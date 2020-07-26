package top.totoro.swing.widget.context;

import top.totoro.swing.widget.base.BaseLayout;
import top.totoro.swing.widget.interfaces.ContextWrapper;
import top.totoro.swing.widget.layout.LinearLayout;
import top.totoro.swing.widget.listener.InvalidateListener;
import top.totoro.swing.widget.manager.LinearLayoutManager;
import top.totoro.swing.widget.view.View;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class Context implements ContextWrapper {

    private Point location;
    private Dimension size;
    private BaseLayout mainView = new LinearLayout(null);
    protected LinearLayoutManager layoutManager = new LinearLayoutManager();
    protected List<InvalidateListener> invalidateListenerList = new ArrayList<>();

    public void requestInvalidateListener(InvalidateListener listener) {
        invalidateListenerList.remove(listener);
        invalidateListenerList.add(listener);
    }

    public Context() {
        mainView.setContext(this);
    }

    public void startActivity(Context context, Class<? extends Context> target) {
        try {
            Object object = target.newInstance();
            target.getMethod("setLocation", Point.class).invoke(object, context.location);
            target.getMethod("setSize", Dimension.class).invoke(object, context.size);
            target.getMethod("onCreate").invoke(object);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public void startActivity(Class<? extends Context> target) {
        try {
            Object object = target.newInstance();
            target.getMethod("setLocation", Point.class).invoke(object, location);
            target.getMethod("setSize", Dimension.class).invoke(object, size);
            target.getMethod("onCreate").invoke(object);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }
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

    public void setLocation(Point location) {
        this.location = location;
    }

    public void setLocation(int x, int y) {
        this.location = new Point(x, y);
    }

    public Point getLocation() {
        return location;
    }

    public Dimension getSize() {
        return size;
    }

    public void setSize(Dimension size) {
        this.size = size;
    }

    public void setSize(int width, int height) {
        this.size = new Dimension(width, height);
    }
}
