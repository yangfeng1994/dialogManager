package com.dialog.queue;

import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import java.util.ArrayList;

public class ActivityControllerImpl implements DefaultLifecycleObserver, ActivityController, DialogDismissCallback {

    private Lifecycle.State mState;

    /**
     * 判断当前是否显示
     */
    private boolean isShow;

    private boolean autoExec;
    /**
     * 添加到队列中的dialog，等待执行
     */
    private ArrayList<Pair<Integer, DialogController>> mDialogQueue = new ArrayList();

    /**
     * 已显示的dialog 用来关闭界面，移除监听的
     */
    private ArrayList<Pair<Integer, DialogController>> mExecutedDialogQueue = new ArrayList();
    private Lifecycle lifecycle;
    private FragmentManager mFragmentManager;

    public void addObserver(Lifecycle lifecycle) {
        this.lifecycle = lifecycle;
        lifecycle.addObserver(this);
    }

    /**
     * 弹窗结束后，是否自动执行
     *
     * @param autoExec
     */
    public void setAutoExec(boolean autoExec) {
        this.autoExec = autoExec;
    }

    @Override
    public void onCreate(@NonNull LifecycleOwner owner) {
        mState = owner.getLifecycle().getCurrentState();
    }

    @Override
    public void onStart(@NonNull LifecycleOwner owner) {
        mState = owner.getLifecycle().getCurrentState();
    }

    @Override
    public void onResume(@NonNull LifecycleOwner owner) {
        mState = owner.getLifecycle().getCurrentState();
        checkDialogQueue();
    }

    @Override
    public void onPause(@NonNull LifecycleOwner owner) {
        mState = owner.getLifecycle().getCurrentState();
    }

    @Override
    public void onStop(@NonNull LifecycleOwner owner) {
        mState = owner.getLifecycle().getCurrentState();
    }

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        mState = owner.getLifecycle().getCurrentState();
        cleanDialogCallBack(mDialogQueue);
        cleanDialogCallBack(mExecutedDialogQueue);
    }

    @Override
    public Lifecycle getControllerLifecycle() {
        return lifecycle;
    }

    @Override
    public Class getControllerClass() {
        return null;
    }

    @Override
    public FragmentManager getControllerFragmentManager() {
        return mFragmentManager;
    }

    public void addQueue(int priority, boolean execution, DialogController dialogController, FragmentManager fragmentManager) {
        this.mFragmentManager = fragmentManager;
        mDialogQueue.add(0, new Pair(priority, dialogController));
        if (mState == Lifecycle.State.RESUMED) {
            if (execution) {
                execute();
            }
        }
    }

    /**
     * 检测队列中是否还有dialog
     */
    private void checkDialogQueue() {
        if (mDialogQueue.isEmpty()) return;
        execute();
    }


    /**
     * 移除dialog的监听
     */
    private void cleanDialogCallBack(ArrayList<Pair<Integer, DialogController>> dialogQueue) {
        for (Pair<Integer, DialogController> dialogController : dialogQueue) {
            DialogController dialog = dialogController.second;
            dialog.doDismiss(null);
        }
        dialogQueue.clear();
    }

    /**
     * 显示弹窗
     * priority 越小，优先级越高
     */
    private void execute() {
        if (isShow) return;
        int size = mDialogQueue.size();
        int priority = 0;
        int index = 0;
        for (int i = 0; i < size; i++) {
            Pair<Integer, DialogController> item = mDialogQueue.get(i);
            Integer first = item.first;
            if (first <= priority) {
                index = i;
                priority = first;
            }
        }
        Pair<Integer, DialogController> dialogController = mDialogQueue.get(index);
        DialogController dialog = dialogController.second;
        dialog.doShow(getControllerFragmentManager());
        dialog.doDismiss(this);
        mDialogQueue.remove(dialogController);
        mExecutedDialogQueue.add(dialogController);
        isShow = true;
    }

    public void execution() {
        if (mState == Lifecycle.State.RESUMED) {
            checkDialogQueue();
        }
    }

    /**
     * 当前弹窗关闭，关闭后，再次从队列中查询，是否还有弹窗准备弹出
     */
    @Override
    public void onDismiss() {
        isShow = false;
        if (autoExec) {
            execution();
        }
    }

}
