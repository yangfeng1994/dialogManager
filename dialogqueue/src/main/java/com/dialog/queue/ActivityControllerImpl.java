package com.dialog.queue;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Message;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class ActivityControllerImpl implements DefaultLifecycleObserver, ActivityController, DialogDismissCallback {

    private Lifecycle.State mState;
    private Field mDismissMessageField;
    /**
     * 当前显示的弹窗
     */
    private DialogController showDialog;
    private boolean autoExec;
    /**
     * 添加到队列中的dialog，等待执行
     */
    private ArrayList<Pair<Integer, DialogController>> mDialogQueue = new ArrayList();

    private Lifecycle lifecycle;
    private FragmentManager mFragmentManager;
    private FragmentManager.FragmentLifecycleCallbacks mFragmentLifecycleCallbacks;

    public void addObserver(Lifecycle lifecycle) {
        this.lifecycle = lifecycle;
        if (null != lifecycle) {
            lifecycle.addObserver(this);
        }
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
        mDialogQueue.clear();
        mDialogQueue = null;
        if (null != lifecycle) {
            lifecycle.removeObserver(this);
        }
        if (null != mFragmentLifecycleCallbacks && null != mFragmentManager) {
            mFragmentManager.unregisterFragmentLifecycleCallbacks(mFragmentLifecycleCallbacks);
        }
        mFragmentLifecycleCallbacks = null;
        showDialog = null;
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
        hookDialogFragmentDismiss();
        String tagName = dialogController.getTagName();
        boolean unique = dialogController.unique();
        if (unique) {
            if (null != showDialog) {
                String showTagName = showDialog.getTagName();
                boolean showUnique = showDialog.unique();
                if (showUnique && null != showTagName && null != tagName) {
                    if (tagName.equals(showTagName)) {
                        extracted(execution);
                        return;
                    }
                }
            }
            for (Pair<Integer, DialogController> item : mDialogQueue) {
                DialogController controller = item.second;
                String queueTagName = controller.getTagName();
                boolean queueUnique = dialogController.unique();
                if (queueUnique && null != queueTagName && null != tagName) {
                    if (tagName.equals(queueTagName)) {
                        extracted(execution);
                        return;
                    }
                }
            }
        }
        mDialogQueue.add(0, new Pair(priority, dialogController));
        extracted(execution);
    }

    /**
     * 根據生命周期判斷 是否有可以执行的弹窗
     *
     * @param execution 是否是立即执行
     */
    private void extracted(boolean execution) {
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
     * 显示弹窗
     * priority 越小，优先级越高
     */
    private void execute() {
        if (null != showDialog) return;
        int size = mDialogQueue.size();
        int priority = Integer.MAX_VALUE;
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
        FragmentManager controllerFragmentManager = getControllerFragmentManager();
        if (dialog instanceof DialogFragment) {
            DialogFragment dialogFragment = (DialogFragment) dialog;
            dialogFragment.show(controllerFragmentManager, controllerFragmentManager.getClass().getSimpleName());
        } else if (dialog instanceof Dialog) {
            Dialog dialog1 = (Dialog) dialog;
            dialog1.show();
            hookDialogDismiss(dialog1);
        }
        mDialogQueue.remove(dialogController);
        showDialog = dialog;
    }

    private void hookDialogFragmentDismiss() {
        if (null != mFragmentLifecycleCallbacks) return;
        mFragmentLifecycleCallbacks = new FragmentManager.FragmentLifecycleCallbacks() {
            @Override
            public void onFragmentDestroyed(@NonNull FragmentManager fm, @NonNull Fragment f) {
                super.onFragmentDestroyed(fm, f);
                if (null == showDialog) return;
                if (showDialog instanceof DialogFragment) {
                    if (showDialog == f) {
                        onDismiss();
                    }
                }
            }
        };
        mFragmentManager.registerFragmentLifecycleCallbacks(mFragmentLifecycleCallbacks, true);
    }


    /**
     * hookDialog的dismiss方法
     *
     * @param dialog
     */
    private void hookDialogDismiss(Dialog dialog) {
        if (null == mDismissMessageField) {
            mDismissMessageField = HookUtils.getDeclaredField(Dialog.class, "mDismissMessage");
        }
        DialogInterface.OnDismissListener oldListener = null;
        if (null != mDismissMessageField) {
            Object mDismissMessage = HookUtils.fieldGetValue(mDismissMessageField, dialog);
            if (null != mDismissMessage) {
                if (mDismissMessage instanceof Message) {
                    Object listener = ((Message) mDismissMessage).obj;
                    if (null != listener) {
                        oldListener = ((DialogInterface.OnDismissListener) listener);
                    }
                }
            }
        }
        DialogInterface.OnDismissListener finalOldListener = oldListener;
        dialog.setOnDismissListener(dialog1 -> {
            onDismiss();
            if (null != finalOldListener) {
                finalOldListener.onDismiss(dialog1);
            }
            dialog.setOnDismissListener(null);
        });
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
        showDialog = null;
        if (autoExec) {
            execution();
        }
    }

}
