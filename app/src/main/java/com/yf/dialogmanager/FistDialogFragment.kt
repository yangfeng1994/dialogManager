package com.yf.dialogmanager

import android.content.DialogInterface
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import com.dialog.queue.DialogController
import com.dialog.queue.DialogDismissCallback

class FistDialogFragment : BaseDialogFragment(), DialogController {
    var callback: DialogDismissCallback? = null
    var number: Int = 1
    val mTVNumberDialog by lazy { view?.findViewById<TextView>(R.id.mTVNumberDialog) }

    companion object {
        fun newInstance(number: Int = 1) = FistDialogFragment().apply {
            this.number = number
        }
    }

    override val layoutRes: Int
        get() = R.layout.dialog_fragment_fister

    override fun onInitFastData() {
        mTVNumberDialog?.text = "第${number}个DialogFragment弹窗"
    }

    override fun doShow(fragmentManager: FragmentManager) {
        show(fragmentManager, javaClass.simpleName)
    }

    override fun doDismiss(callback: DialogDismissCallback?) {
        this.callback = callback
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        callback?.onDismiss()
    }

}