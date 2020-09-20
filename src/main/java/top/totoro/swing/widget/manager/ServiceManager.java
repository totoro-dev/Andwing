package top.totoro.swing.widget.manager;

import top.totoro.swing.widget.context.Context;
import top.totoro.swing.widget.context.Service;
import top.totoro.swing.widget.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 使用同步锁来处理服务的同步和记录、删除
 */
public class ServiceManager {

    private static final String TAG = ServiceManager.class.getSimpleName();

    // 使用startService启动的服务
    private static final Map<Context, List<Service>> mStartedServices = new HashMap<>();
    // 使用bindService启动的服务
    private static final Map<Context, List<Service>> mBoundServices = new HashMap<>();

    private static final Lock mLock = new ReentrantLock();

    public static void putStartedService(Context context, Service service) {
        try {
            mLock.lock();
            if (mStartedServices.computeIfAbsent(context, key -> new ArrayList<>()).contains(service)) return;
            mStartedServices.computeIfAbsent(context, key -> new ArrayList<>()).add(service);
        } finally {
            mLock.unlock();
        }
    }

    public static void putBoundService(Context context, Service service) {
        try {
            mLock.lock();
            if (mBoundServices.computeIfAbsent(context, key -> new ArrayList<>()).contains(service)) return;
            mBoundServices.computeIfAbsent(context, key -> new ArrayList<>()).add(service);
        } finally {
            mLock.unlock();
        }
    }

    public static boolean removeStartedService(Context context, Service service) {
        try {
            mLock.lock();
            List<Service> list = mStartedServices.get(context);
            if (list != null) {
                list.remove(service);
                if (list.size() == 0) {
                    mStartedServices.remove(context);
                }
                return true;
            }
            return false;
        } finally {
            Log.d(TAG, "removeStartedService() service = " + service);
            mLock.unlock();
        }
    }

    public static boolean removeBoundService(Context context, Service service) {
        try {
            mLock.lock();
            return mBoundServices.computeIfAbsent(context, key -> new ArrayList<>()).remove(service);
        } finally {
            mLock.unlock();
        }
    }

    public static List<Service> getStartedServices(Context context) {
        try {
            mLock.lock();
            return new ArrayList<>(mStartedServices.computeIfAbsent(context, key -> new ArrayList<>()));
        } finally {
            mLock.unlock();
        }
    }

    public static List<Service> getBoundServices(Context context) {
        try {
            mLock.lock();
            return new ArrayList<>(mBoundServices.computeIfAbsent(context, key -> new ArrayList<>()));
        } finally {
            mLock.unlock();
        }
    }

    public static boolean isEmpty() {
        try {
            mLock.lock();
            return mStartedServices.isEmpty();
        } finally {
            mLock.unlock();
        }
    }
}
