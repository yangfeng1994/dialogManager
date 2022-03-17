package com.yf.dialogmanager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import com.dialog.queue.ActivityController
import com.dialog.queue.DialogManager

class MainActivity : AppCompatActivity(), ActivityController {
    val dialogManager = DialogManager.getInstance()
    val mBTStart by lazy { findViewById<Button>(R.id.mBTStart) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        dialogManager.addLifecycle(this)
        showDialog()
        mBTStart.setOnClickListener {
            val intent = Intent(this, FistActivity::class.java)
            startActivity(intent)
        }
    }

    override fun getControllerLifecycle(): Lifecycle {
        return lifecycle
    }

    override fun getControllerClass(): Class<*> {
        return this.javaClass
    }

    override fun getControllerFragmentManager(): FragmentManager {
        return supportFragmentManager
    }

    override fun onDestroy() {
        super.onDestroy()
        dialogManager.removeLifecycle(this)
    }

    private fun showDialog() {
        val mHandler = Handler(Looper.getMainLooper())
        mHandler.postDelayed({
            val mFistDialogFragment = FistDialog(this)
            dialogManager.addQueue(0,false, mFistDialogFragment, this)
        }, 2000)
        mHandler.postDelayed({
            val mFistDialogFragment = FistDialogFragment.newInstance(2)
            dialogManager.addQueue(1, mFistDialogFragment, this)
        }, 3100)
    }

}