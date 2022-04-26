package com.yf.dialogmanager;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.dialog.queue.DialogController;

public class FistDialog extends Dialog implements DialogController, DialogInterface.OnDismissListener {

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
    public void onDismiss(DialogInterface dialog) {
    }

    @Override
    public String getTagName() {
        return "FistRDialog";
    }

    @Override
    public boolean unique() {
        return true;
    }
}
