package com.dialog.queue;


import androidx.lifecycle.Lifecycle;

import java.lang.ref.WeakReference;
import java.util.HashMap;

/**
 * 弹窗管理类
 */
public class DialogManager {
    private static DialogManager mInstance;

    public static DialogManager getInstance() {
        if (null == mInstance) {
            synchronized (DialogManager.class) {
                if (null == mInstance) {
                    mInstance = new DialogManager();
                }
            }
        }
        return mInstance;
    }

    private HashMap<String, WeakReference<ActivityController>> mLifeQueue = new HashMap<>();

    /**
     * @param activityController 添加activity的生命周期
     * @return
     */
    public WeakReference<ActivityController> addLifecycle(ActivityController activityController) {
        WeakReference<ActivityController> activityControllerImpl = addLifecycle(activityController, true);
        return activityControllerImpl;
    }

    /**
     * @param activityController 添加activity的生命周期
     * @param autoExec           结束后是否是自动执行
     * @return
     */
    public WeakReference<ActivityController> addLifecycle(ActivityController activityController, boolean autoExec) {
        ActivityControllerImpl activityControllerImpl = new ActivityControllerImpl();
        activityControllerImpl.addObserver(activityController.getControllerLifecycle());
        activityControllerImpl.setAutoExec(autoExec);
        WeakReference<ActivityController> activityControllerWeakReference = new WeakReference<>(activityControllerImpl);
        mLifeQueue.put(activityController.getControllerClass().getSimpleName(), activityControllerWeakReference);
        return activityControllerWeakReference;
    }

    /**
     * 移除activity到生命周期
     *
     * @param activityController
     */
    public void removeLifecycle(ActivityController activityController) {
        String simpleName = activityController.getControllerClass().getSimpleName();
        WeakReference<ActivityController> activityControllerWeakReference = mLifeQueue.remove(simpleName);
        if (null == activityControllerWeakReference) return;
        ActivityControllerImpl activityControllerImpl = (ActivityControllerImpl) activityControllerWeakReference.get();
        if (null == activityControllerImpl) return;
        Lifecycle controllerLifecycle = activityControllerImpl.getControllerLifecycle();
        if (null == controllerLifecycle) return;
        controllerLifecycle.removeObserver(activityControllerImpl);
        controllerLifecycle = null;
        activityControllerImpl = null;
    }

    /**
     * 添加弹窗到队列中
     *
     * @param priority           优先级 ，越小，优先级越高
     * @param dialogController   弹窗的控制类
     * @param activityController activity的控制类
     */
    public void addQueue(int priority, DialogController dialogController, ActivityController activityController) {
        addQueue(priority, true, dialogController, activityController);
    }

    /**
     * 添加弹窗到队列中
     *
     * @param priority           优先级 ，越小，优先级越高
     * @param execution          是否是立即执行
     * @param dialogController   弹窗的控制类
     * @param activityController activity的控制类
     */
    public void addQueue(int priority, boolean execution, DialogController dialogController, ActivityController activityController) {
        WeakReference<ActivityController> activityControllerWeakReference = mLifeQueue.get(activityController.getControllerClass().getSimpleName());
        if (null == activityControllerWeakReference) {
            activityControllerWeakReference = addLifecycle(activityController);
        }
        ActivityControllerImpl mActivityControllerImpl = (ActivityControllerImpl) activityControllerWeakReference.get();
        mActivityControllerImpl.addQueue(priority, execution, dialogController, activityController.getControllerFragmentManager());
    }

    /**
     * 立即去执行，可用来检索列表里有没有可以弹出的弹窗
     *
     * @param activityController
     */
    public void execution(ActivityController activityController) {
        WeakReference<ActivityController> activityControllerWeakReference = mLifeQueue.get(activityController.getControllerClass().getSimpleName());
        if (null == activityControllerWeakReference) return;
        ActivityControllerImpl mActivityControllerImpl = (ActivityControllerImpl) activityControllerWeakReference.get();
        if (null != mActivityControllerImpl) {
            mActivityControllerImpl.execution();
        }
    }

    /**
     * 释放单例
     */
    public void release() {
        mLifeQueue.clear();
        mLifeQueue = null;
        mInstance = null;
    }

}
