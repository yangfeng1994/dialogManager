package com.dialog.queue;



import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

public interface DialogController {
    void doShow(@NonNull FragmentManager fragmentManager);

    void doDismiss(DialogDismissCallback callback);

}
