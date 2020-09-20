package top.totoro.swing.widget.context;

import top.totoro.swing.widget.util.Log;

public class Intent {

    private Context currentContext;
    private Context targetContext;
    private Class<? extends Context> targetContextClass;

    public Intent() {
    }

    public Intent(String targetContextPackage) {
        Class<?> targetContext = null;
        try {
            targetContext = Class.forName(targetContextPackage);
            Log.d(this, "targetContext super class  = " + targetContext.getSuperclass().getSuperclass().getSimpleName());
            if (targetContext.getSuperclass().getSuperclass().getSimpleName().equals(Context.class.getSimpleName())) {
                //noinspection unchecked
                this.targetContextClass = (Class<? extends Context>) targetContext;
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Intent(Context currentContext, String targetContextPackage) {
        this(targetContextPackage);
        this.currentContext = currentContext;
    }

    public Intent(Context currentContext, Class<? extends Context> targetContextClass) {
        this.currentContext = currentContext;
        this.targetContextClass = targetContextClass;
    }

    public void setCurrentContext(Context currentContext) {
        this.currentContext = currentContext;
    }

    public void setTargetContextClass(Class<? extends Context> targetContextClass) {
        this.targetContextClass = targetContextClass;
    }

    public Context getCurrentContext() {
        return currentContext;
    }

    public Class<? extends Context> getTargetContextClass() {
        return targetContextClass;
    }

    public synchronized Context getTargetContext() {
        if (targetContext == null) {
            synchronized (Intent.class) {
                if (targetContext == null) {
                    try {
                        targetContext = targetContextClass.newInstance();
                    } catch (InstantiationException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return targetContext;
    }
}
