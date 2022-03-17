package com.yf.dialogmanager


import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager


/**
 * dialog的基类
 */

abstract class BaseDialogFragment : DialogFragment() {

    open var isShow = false

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        isShow = false
        mOnDismissListener?.onDismiss(dialog)
    }

    private var mOnDismissListener: DialogInterface.OnDismissListener? = null

    open fun setOnDismissListener(listener: DialogInterface.OnDismissListener?) {
        mOnDismissListener = listener
    }

    protected abstract val layoutRes: Int

    open fun isCancelableOutside(): Boolean {
        return true
    }

    open fun getDialogWidth(): Int {
        return WindowManager.LayoutParams.MATCH_PARENT
    }

    open fun getDialogHeight(): Int {
        return WindowManager.LayoutParams.WRAP_CONTENT
    }


    open fun dimAmount(): Float {
        return 0.3f
    }


    open fun getGravity(): Int {
        return Gravity.CENTER
    }

    open fun animRes(): Int {
        return 0
    }

    open fun getBackgroundDrawable(): Drawable {
        return ColorDrawable(Color.TRANSPARENT)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return if (layoutRes > 0) {
            //调用方通过xml获取view
            inflater.inflate(layoutRes, container, false)
        } else {
            super.onCreateView(inflater, container, savedInstanceState)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.let {
            //如果isCancelable()是false 则会屏蔽物理返回键
            it.setCancelable(isCancelable)
            //如果isCancelableOutside()为false 点击屏幕外Dialog不会消失；反之会消失
            it.setCanceledOnTouchOutside(isCancelableOutside())
            //如果isCancelable()设置的是false 会屏蔽物理返回键
            it.setOnKeyListener { _, keyCode, event -> keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN && !isCancelable }
        }
        onInitFastData()
    }

    /**
     * 初始化数据
     */
    protected abstract fun onInitFastData()


    override fun onStart() {
        super.onStart()
        dialog?.let {
            val window = it.window ?: return
            //设置背景色透明
            window.setBackgroundDrawable(getBackgroundDrawable())
            //设置Dialog动画效果
            if (animRes() > 0) {
                window.setWindowAnimations(animRes())
            }
            val params = window.attributes
            //设置Dialog的Width
            params.width = getDialogWidth()
            //设置Dialog的Height
            params.height = getDialogHeight()
            //设置屏幕透明度 0.0f~1.0f(完全透明~完全不透明)
            params.dimAmount = dimAmount()
            params.gravity = getGravity()
            window.attributes = params
        }
    }

    override fun show(manager: FragmentManager, tag: String?) {
        super.show(manager, tag)
        isShow = true
    }



    open fun isShowing(): Boolean {
        dialog?.let {
            return it.isShowing && !isRemoving
        }
        return false
    }

}
