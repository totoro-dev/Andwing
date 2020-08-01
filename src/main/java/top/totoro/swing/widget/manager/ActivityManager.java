package top.totoro.swing.widget.manager;

import top.totoro.swing.widget.base.Location;
import top.totoro.swing.widget.base.Size;
import top.totoro.swing.widget.context.Activity;
import top.totoro.swing.widget.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class ActivityManager {
    private static final String TAG = ActivityManager.class.getSimpleName();
    private static Activity mTopActivity = null;
    private static final Map<Class<? extends Activity>, Object> CREATED_ACTIVITY = new ConcurrentHashMap<>();

    /**
     * ���õ�ǰ�Ķ��㴰�ڣ������µĴ��ڱ��벻Ϊ�գ���Ȼ�µĴ��ڲ��ᱻ��Ϊ���㴰��
     *
     * @param activity �µĴ���
     */
    public static void setTopActivity(Activity activity) {
        if (activity == null) return;
        mTopActivity = activity;
    }

    /**
     * ͨ���÷������Ի�ȡ����ǰӦ�ô��ڵĴ��ڶ���Activity��
     * ������ڻ�û�б��������Ļ���������ṩ��target������һ����Ӧ���͵Ĵ��ڶ���
     * �������ڶ�����ӵ�CREATED_ACTIVITY�С�
     *
     * @param target Ŀ�괰�ڵ���
     * @param <A>    Ҫ��ȡ�Ĵ��ڵ����Ͷ���
     * @return ƥ��target���͵Ĵ��ڶ���
     */
    @SuppressWarnings("unchecked")
    public static <A extends Activity> A getOrCreateActivity(Class<A> target) {
        AtomicBoolean isNewActivity = new AtomicBoolean(false);
        A targetActivity = (A) CREATED_ACTIVITY.computeIfAbsent(target, targetActivityType -> {
            Log.d(TAG, "getOrCreateActivity create a new activity type = " + target);
            isNewActivity.set(true);
            // CREATED_ACTIVITY�в�����target���͵Ĵ��� ��Ҫ���´���һ�����ڲ���ӵ�CREATED_ACTIVITY�С�
            A activity = null;
            if (mTopActivity == null) {
                // ��ǰ�������κδ��ڣ�����һ��û��ָ����С��λ�õĴ���
                try {
                    activity = target.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    // ���ݶ��㴰�ڵ�λ�úʹ�С����һ���µĴ���
                    activity = target.newInstance();
                    target.getMethod("setLocation", Location.class).invoke(activity, mTopActivity.getLocation());
                    target.getMethod("setSize", Size.class).invoke(activity, mTopActivity.getSize());
//                    target.getMethod("onCreate").invoke(object);
                } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
            return activity;
        });
        Log.d(TAG, "get activity is " + targetActivity);
        if (targetActivity != null)
            targetActivity.setOnRestart(!isNewActivity.get());
        return targetActivity;
    }

    /**
     * ���Ի�ȡ��ָ����С�ĵĴ��ڶ���
     * ��ͨ��CREATED_ACTIVITY�����Ƿ����ͬһ���͵Ĵ��ڣ��������򴴽��µĴ���
     * �������׼����ɣ�������ָ���Ĵ�С
     *
     * @param target     Ŀ�괰�ڵ���
     * @param targetSize ���ڵ�Ŀ���С
     * @param <A>        Ҫ��ȡ�Ĵ��ڵ����Ͷ���
     * @return һ������ָ����С�Ĵ���
     */
    public static <A extends Activity> A getOrCreateActivityWithSize(Class<A> target, Size targetSize) {
        A targetActivity = getOrCreateActivity(target);
        if (targetActivity != null) {
            targetActivity.setSize(targetSize);
        }
        return targetActivity;
    }

    /**
     * ���Ի�ȡ��ָ��λ�õĵĴ��ڶ���
     * ��ͨ��CREATED_ACTIVITY�����Ƿ����ͬһ���͵Ĵ��ڣ��������򴴽��µĴ���
     * �������׼����ɣ�������ָ����λ��
     *
     * @param target         Ŀ�괰�ڵ���
     * @param targetLocation ���ڵ�Ŀ��λ��
     * @param <A>            Ҫ��ȡ�Ĵ��ڵ����Ͷ���
     * @return һ������ָ��λ�õĴ���
     */
    public static <A extends Activity> A getOrCreateActivityWithLocation(Class<A> target, Location targetLocation) {
        A targetActivity = getOrCreateActivity(target);
        if (targetActivity != null) {
            targetActivity.setLocation(targetLocation);
        }
        return targetActivity;
    }

    /**
     * ���Ի�ȡ��ָ����С��λ�õĵĴ��ڶ���
     * ��ͨ��CREATED_ACTIVITY�����Ƿ����ͬһ���͵Ĵ��ڣ��������򴴽��µĴ���
     * �������׼����ɣ�������ָ���Ĵ�С��λ��
     *
     * @param target         Ŀ�괰�ڵ���
     * @param targetLocation ���ڵ�Ŀ���С��λ��
     * @param <A>            Ҫ��ȡ�Ĵ��ڵ����Ͷ���
     * @return һ������ָ����С��λ�õĴ���
     */
    public static <A extends Activity> A getOrCreateActivityWithSizeAndLocation(Class<A> target, Size targetSize, Location targetLocation) {
        // �ȴ����ڵĴ�С
        A targetActivity = getOrCreateActivityWithSize(target, targetSize);
        if (targetActivity != null) {
            targetActivity.setLocation(targetLocation);
        }
        return targetActivity;
    }

    public static void removeActivity(Activity removeActivity) {
        CREATED_ACTIVITY.remove(removeActivity.getClass());
    }
}
