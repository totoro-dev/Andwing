package top.totoro.swing.widget.simulate;

import top.totoro.swing.widget.interfaces.ContextWrapper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Context implements ContextWrapper {

    public void startActivity(Context context, Class<? extends ContextWrapper> target) {
        try {
            Object object = target.newInstance();
            Method method = target.getMethod("onCreate");
            method.invoke(object);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
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

}
