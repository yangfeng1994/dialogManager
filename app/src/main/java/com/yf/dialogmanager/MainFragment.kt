package com.yf.dialogmanager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import com.dialog.queue.ActivityController
import com.dialog.queue.DialogManager

class MainFragment : Fragment(), ActivityController {

    val mBTStart by lazy { view?.findViewById<Button>(R.id.mBTStart) }
    val dialogManager = DialogManager.getInstance()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        dialogManager.addLifecycle(this)
        val itemView = inflater.inflate(R.layout.fragment_main, container, false)
        return itemView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBTStart?.setOnClickListener {
            val mFistDialogFragment = FistDialogFragment.newInstance(3)
            dialogManager.addQueue(1, mFistDialogFragment, this)
        }
    }

    override fun getControllerLifecycle(): Lifecycle {
        return lifecycle
    }

    override fun getControllerClass(): Class<*> {
        return javaClass
    }

    override fun getControllerFragmentManager(): FragmentManager {
        return childFragmentManager
    }

    override fun onDestroyView() {
        super.onDestroyView()
        dialogManager.removeLifecycle(this)
    }

}