package com.tagnavigator.util


import android.app.AlertDialog
import android.app.AlertDialog.Builder
import android.app.Dialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.DialogInterface.OnClickListener
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.app.FragmentActivity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.tagnavigator.R


/**
 * Central dialog-create factory
 *
 * @author JosephWang
 */
object JDialog {
    fun notShowing(act: FragmentActivity?, dialog: Dialog?): Boolean {
        return act != null &&
                !act!!.isFinishing() &&
                dialog != null &&
                !dialog.isShowing
    }

    /**
     * show loading progress
     *
     * @param context
     * @param msg
     * @param cancelable
     * @return [ProgressDialog]
     */
    fun showProgressDialog(context: FragmentActivity?, msg: String, cancelable: Boolean): ProgressDialog {
        val p = ProgressDialog(context, R.style.ProgressDialogSlide)
        p.requestWindowFeature(Window.FEATURE_NO_TITLE)
        p.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        p.isIndeterminate = true
        p.setCancelable(cancelable)
        p.setMessage(msg)
        p.show()
        val content = LayoutInflater.from(context).inflate(R.layout.progress_dialog, null)
        val spinner = content.findViewById(R.id.progress) as ProgressBar

        spinner.progressDrawable = content!!.getResources().getDrawable(R.drawable.progressbar_states)
        p.setContentView(content)
        (content.findViewById(R.id.progressTitle) as TextView).text = "" + msg

        avoidDismiss(p, false)
        return p
    }

    fun showToast(context: FragmentActivity?, title: Int) {
        Toast.makeText(context, JUtil.getString(context, title), Toast.LENGTH_LONG).show()
    }

    @JvmOverloads
    fun showListDialog(context: FragmentActivity?, title: Int, msg: Array<String>, listener: OnClickListener, cancelListener: DialogInterface.OnCancelListener? = null): Dialog {
        val builder = Builder(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
        builder.setItems(msg, listener)
        builder.setTitle(title)
        builder.setNegativeButton(android.R.string.cancel, listener)
        if (cancelListener != null) {
            builder.setOnCancelListener(cancelListener)
        }
        val dialog = builder.create()
        dialog.window!!.setType(WindowManager.LayoutParams.TYPE_TOAST)
        dialog.show()
        return dialog
    }

    fun showListDialog(context: FragmentActivity?, title: String, msg: Array<String>, listener: OnClickListener): Dialog {
        // Builder builder = new AlertDialog.Builder(context,
        // R.style.AlertTheme);
        val builder = Builder(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
        builder.setItems(msg, listener)
        builder.setTitle(title)
        builder.setNegativeButton(android.R.string.cancel, listener)
        val dialog = builder.create()
        dialog.window!!.setType(WindowManager.LayoutParams.TYPE_TOAST)
        dialog.show()
        return dialog
    }

    @JvmOverloads
    fun showMessage(context: FragmentActivity?, title: Int, msg: Int, listener: OnClickListener? = null, cancelable: Boolean = true): Dialog {
        val builder = Builder(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
        builder.setTitle(title)
        builder.setMessage(msg)
        builder.setPositiveButton(android.R.string.ok, listener)

        val dialog = avoidDismiss(builder.create(), cancelable)
        dialog.window!!.setType(WindowManager.LayoutParams.TYPE_TOAST)
        dialog.show()

        return dialog
    }

    fun showMessage(context: FragmentActivity?, title: Int, msg: Int, confirmButtonText: Int, listener: OnClickListener, dismissListener: DialogInterface.OnDismissListener): Dialog {
        val titleStr = JUtil.getString(context, title)
        val msgStr = JUtil.getString(context, msg)
        val confirmButtonTextStr = JUtil.getString(context, confirmButtonText)
        return showMessage(context, titleStr, msgStr, confirmButtonTextStr, listener, dismissListener)
    }

    @JvmOverloads
    fun showMessage(context: FragmentActivity?, title: String, msg: String, confirmButtonText: String, listener: OnClickListener, dismissListener: DialogInterface.OnDismissListener? = null): Dialog {
        val builder = Builder(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
        builder.setTitle(title)
        builder.setMessage(msg)
        builder.setPositiveButton(confirmButtonText, listener)
        if (dismissListener != null) {
            builder.setOnDismissListener(dismissListener)
        }

        val dialog = avoidDismiss(builder.create(), false)
        dialog.window!!.setType(WindowManager.LayoutParams.TYPE_TOAST)
        dialog.show()

        return dialog
    }

    fun showMessage(context: FragmentActivity?, title: Int, msg: Int, confirmButtonText: Int, cancelButtonText: Int, listener: OnClickListener): Dialog {
        val titleStr = JUtil.getString(context, title)
        val msgStr = JUtil.getString(context, msg)
        val confirmButtonTextStr = JUtil.getString(context, confirmButtonText)
        val cancelButtonTextStr = JUtil.getString(context, cancelButtonText)
        return showMessage(context, titleStr, msgStr, confirmButtonTextStr, cancelButtonTextStr, listener, null)
    }

    fun showMessage(context: FragmentActivity?, title: Int, msg: Int, confirmButtonText: Int, cancelButtonText: Int, listener: OnClickListener, cancelListener: OnClickListener): Dialog {
        val titleStr = JUtil.getString(context, title)
        val msgStr = JUtil.getString(context, msg)
        val confirmButtonTextStr = JUtil.getString(context, confirmButtonText)
        val cancelButtonTextStr = JUtil.getString(context, cancelButtonText)
        return showMessage(context, titleStr, msgStr, confirmButtonTextStr, cancelButtonTextStr, listener, cancelListener)
    }

    @JvmOverloads
    fun showMessage(context: FragmentActivity?, title: String, msg: String, confirmButtonText: String, cancelButtonText: String, listener: OnClickListener, cancelListener: OnClickListener? = null,
                    cancelable: Boolean = true): Dialog {
        val builder = Builder(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
        builder.setTitle(title)
        builder.setMessage(msg)
        builder.setPositiveButton(confirmButtonText, listener)
        if (cancelListener != null) {
            builder.setNegativeButton(cancelButtonText, cancelListener)
        } else {
            builder.setNegativeButton(cancelButtonText) { dialog, which -> dialog.dismiss() }
        }
        val dialog = avoidDismiss(builder.create(), cancelable)
        //        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_Tz);
        dialog.show()
        return dialog
    }

    @JvmOverloads
    fun showMessage(context: FragmentActivity?, title: String, msg: String, listener: OnClickListener? = null, dismissListener: DialogInterface.OnDismissListener? = null): Dialog {
        val builder = Builder(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
        builder.setTitle(title)
        builder.setMessage(msg)
        builder.setPositiveButton(android.R.string.ok, listener)
        if (dismissListener != null) {
            builder.setOnDismissListener(dismissListener)
        }
        val dialog = avoidDismiss(builder.create(), true)
        //        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
        dialog.show()
        return dialog
    }

    fun showMessages(context: FragmentActivity?, title: String, msg: String, button: String): Dialog {
        val builder = Builder(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
        builder.setTitle(title)
        builder.setMessage(msg)
        builder.setPositiveButton(button) { dialog, which -> dialog.dismiss() }

        val dialog = avoidDismiss(builder.create(), true)
        //        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
        dialog.show()
        return dialog
    }

    fun showMessageWithCancel(context: FragmentActivity?, title: String, msg: String, listener: OnClickListener): Dialog {
        val builder = Builder(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
        builder.setTitle(title)
        builder.setMessage(msg)
        builder.setPositiveButton(android.R.string.ok, listener)
        builder.setNegativeButton(android.R.string.cancel) { dialog, which -> dialog.dismiss() }

        val dialog = avoidDismiss(builder.create(), true)
        //        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
        dialog.show()
        return dialog
    }

    fun showMessage(context: FragmentActivity?, title: String, msg: String, listener: OnClickListener, isShowing: Boolean): Dialog {
        var isShowing = isShowing
        val builder = Builder(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
        builder.setTitle(title)
        builder.setMessage(msg)
        builder.setPositiveButton(android.R.string.ok, listener)

        val dialog = avoidDismiss(builder.create(), true)
        //        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
        dialog.show()
        isShowing = dialog.isShowing
        return dialog
    }

    /**
     * show message with two button
     *
     * @param context
     * @param title
     * @param msg
     * @param callback
     * @return [Dialog]
     */
    fun showDialog(context: FragmentActivity?, title: String, msg: String, callback: OnClickListener): Dialog {
        val builder = Builder(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
        builder.setTitle(title)
        builder.setMessage(msg)
        builder.setPositiveButton(android.R.string.ok, callback)
        builder.setNegativeButton(android.R.string.cancel) { dialog, which -> dialog.dismiss() }

        val dialog = avoidDismiss(builder.create(), true)
        //        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
        dialog.show()

        return dialog
    }

    fun showDialog(context: FragmentActivity?, title: String, msg: String, confirmAction: OnClickListener, cancelAction: OnClickListener?): Dialog {
        val builder = Builder(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
        builder.setTitle(title)
        builder.setMessage(msg)
        builder.setPositiveButton(android.R.string.ok, confirmAction)
        if (cancelAction != null) {
            builder.setNegativeButton(android.R.string.cancel, cancelAction)
        } else {
            builder.setNegativeButton(android.R.string.cancel) { dialog, which -> dialog.dismiss() }
        }
        val dialog = avoidDismiss(builder.create(), true)
        //        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
        dialog.show()
        return dialog
    }

    /**
     * @param <T>
     * @param t
     * @return Dialog
    </T> */
    fun <T : Dialog> avoidDismiss(t: T, cancelable: Boolean): T {
        t.setCancelable(cancelable)
        t.setCanceledOnTouchOutside(cancelable)
        t.setOnKeyListener { dialog, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_SEARCH && event.repeatCount == 0) {
                true
            } else false
        }
        return t
    }

}