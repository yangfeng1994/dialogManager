package com.yf.dialogmanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import com.dialog.queue.ActivityController
import com.dialog.queue.DialogManager

class FistActivity : AppCompatActivity(), ActivityController {
    val dialogManager = DialogManager.getInstance()
    val mHandler = Handler(Looper.getMainLooper())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fist)
        dialogManager.addLifecycle(this)
        showDialog()
    }

    override fun getControllerLifecycle(): Lifecycle {
        return lifecycle
    }

    override fun onDestroy() {
        dialogManager.removeLifecycle(this)
        super.onDestroy()
        mHandler.removeCallbacksAndMessages(null)
    }

    override fun getControllerClass(): Class<*> {
        return this.javaClass
    }

    override fun getControllerFragmentManager(): FragmentManager {
        return supportFragmentManager
    }

    private fun showDialog() {
        mHandler.postDelayed({
            val mFistDialogFragment = FistDialogFragment.newInstance(1)
            dialogManager.addQueue(1,false, mFistDialogFragment, this)
        }, 2000)
        mHandler.postDelayed({
            val mFistDialogFragment = FistDialogFragment.newInstance(2)
            dialogManager.addQueue(0, mFistDialogFragment, this)
        }, 3000)
    }
}