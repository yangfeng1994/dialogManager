package com.dialog.queue;


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

    private HashMap<String, ActivityController> mLifeQueue = new HashMap<>();

    /**
     *
     * @param activityController 添加activity的生命周期
     * @return
     */
    public ActivityControllerImpl addLifecycle(ActivityController activityController) {
        ActivityControllerImpl activityControllerImpl = addLifecycle(activityController, true);
        return activityControllerImpl;
    }

    /**
     * @param activityController 添加activity的生命周期
     * @param autoExec           结束后是否是自动执行
     * @return
     */
    public ActivityControllerImpl addLifecycle(ActivityController activityController, boolean autoExec) {
        ActivityControllerImpl activityControllerImpl = new ActivityControllerImpl();
        activityControllerImpl.addObserver(activityController.getControllerLifecycle());
        activityControllerImpl.setAutoExec(autoExec);
        mLifeQueue.put(activityController.getControllerClass().getSimpleName(), activityControllerImpl);
        return activityControllerImpl;
    }

    /**
     * 移除activity到生命周期
     *
     * @param activityController
     */
    public void removeLifecycle(ActivityController activityController) {
        mLifeQueue.remove(activityController);
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
        ActivityControllerImpl mActivityControllerImpl = (ActivityControllerImpl) mLifeQueue.get(activityController.getControllerClass().getSimpleName());
        if (null == mActivityControllerImpl) {
            mActivityControllerImpl = addLifecycle(activityController);
        }
        mActivityControllerImpl.addQueue(priority, execution, dialogController, activityController.getControllerFragmentManager());
    }

    /**
     * 立即去执行，可用来检索列表里有没有可以弹出的弹窗
     *
     * @param activityController
     */
    public void execution(ActivityController activityController) {
        ActivityControllerImpl mActivityControllerImpl = (ActivityControllerImpl) mLifeQueue.get(activityController.getControllerClass().getSimpleName());
        if (null != mActivityControllerImpl) {
            mActivityControllerImpl.execution();
        }
    }

}
