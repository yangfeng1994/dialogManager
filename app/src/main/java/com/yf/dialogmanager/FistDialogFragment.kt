package com.yf.dialogmanager

import android.widget.Button
import android.widget.TextView
import com.dialog.queue.DialogController

class FistDialogFragment : BaseDialogFragment(), DialogController {
    var number: Int = 1
    val mTVNumberDialog by lazy { view?.findViewById<TextView>(R.id.mTVNumberDialog) }
    val mBTNFinish by lazy { view?.findViewById<Button>(R.id.mBTNFinish) }

    companion object {
        fun newInstance(number: Int = 1) = FistDialogFragment().apply {
            this.number = number
        }
    }

    override val layoutRes: Int
        get() = R.layout.dialog_fragment_fister

    override fun onInitFastData() {
        mTVNumberDialog?.text = "第${number}个DialogFragment弹窗"
        mBTNFinish?.setOnClickListener { activity?.finish() }
    }


}