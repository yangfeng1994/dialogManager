package com.dialog.queue;

import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;

public interface ActivityController {

    Lifecycle getControllerLifecycle();

    Class getControllerClass();

    FragmentManager getControllerFragmentManager();

}
