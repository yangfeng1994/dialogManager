package com.yf.dialogmanager;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.dialog.queue.DialogController;
import com.dialog.queue.DialogDismissCallback;

public class FistDialog extends Dialog implements DialogController, DialogInterface.OnDismissListener {
    private DialogDismissCallback callback;

    public FistDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.dialog_fister);
        setOnDismissListener(this);
    }

    public FistDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        setContentView(R.layout.dialog_fister);
        setOnDismissListener(this);
    }

    protected FistDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        setContentView(R.layout.dialog_fister);
        setOnDismissListener(this);
    }

    @Override
    public void doShow(@NonNull FragmentManager fragmentManager) {
        show();
    }

    @Override
    public void doDismiss(DialogDismissCallback callback) {
        this.callback = callback;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (null != callback) {
            callback.onDismiss();
        }
    }
}
