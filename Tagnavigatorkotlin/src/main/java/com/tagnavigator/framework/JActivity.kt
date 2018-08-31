package com.tagnavigator.framework
import android.annotation.SuppressLint
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.support.annotation.IdRes
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.widget.TextView
import com.tagnavigator.R
import com.tagnavigator.util.*
import java.lang.ref.WeakReference
import java.util.*


/**
 * middle-ware of real Activity
 *
 * @author JosephWang
 */
open class JActivity : FragmentActivity() {
    protected var TAG = (this as Any).javaClass.simpleName
    protected var loading: ProgressDialog? = null
    private var messageDialg: Dialog? = null
    private lateinit var settings: SharedPreferences

    var isFrontVisible = false
        private set
    val handler = Handler()
    var isAlive = true
        private set

    val isLastOneActivity: Boolean
        get() = AppManager.isLastOneActivity(this@JActivity)

    //		return isFinishing() || isDestroyed();
    val isReclaim: Boolean
        get() = isFinishing

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settings = PreferenceManager.getDefaultSharedPreferences(this)
        isAlive = true
        AppManager.addActivity(this@JActivity)
        JUtil.allowNetWorkRunOnUI()
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        UIAdjuster.closeKeyBoard(this@JActivity)
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        UIAdjuster.closeKeyBoardOnTouchOutSide(this@JActivity, event)
        return super.dispatchTouchEvent(event)
    }

    @JvmOverloads
    fun putIntegerToPreference(key: String, value: Int = 0) {
        settings.edit().putInt(key, value).apply()
    }

    fun putLongToPreference(key: String, value: Long) {
        settings.edit().putLong(key, value).apply()
    }

    fun putStringToPreference(key: String, value: String) {
        settings.edit().putString(key, value).apply()
    }

    fun putBooleanToPreference(key: String, value: Boolean) {
        settings.edit().putBoolean(key, value).apply()
    }

    private fun convertToArray(string: String): ArrayList<String?> {
        return ArrayList<String?>(Arrays.asList(*string.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()))
    }

    fun getStringFromPreference(key: String, defaultValues: String): String? {
        return settings.getString(key, defaultValues)
    }

    @JvmOverloads
    fun getIntegerFromPreference(key: String, defaultValues: Int = 0): Int {
        return settings.getInt(key, defaultValues)
    }

    @JvmOverloads
    fun getLongFromPreference(key: String, defaultValues: Long = 0): Long? {
        return settings.getLong(key, defaultValues)
    }

    /**
     * 從SharedPreference裡取出對應至該key的值
     *
     * @param key
     * @return 對應至該key的boolean值，若不存在則回傳defaultValues
     */
    fun getBooleanFromPreference(key: String, defaultValues: Boolean): Boolean {
        return settings.getBoolean(key, defaultValues)
    }

    /**
     * 從SharedPreference裡取出對應至該key的值
     *
     * @param key
     * @return 對應至該key的值，若不存在則回傳空字串(非null)
     */
    fun getStringFromPreference(key: String): String {
        return settings.getString(key, "")
    }

    protected fun setProgressInLoading(progress: String) {
        if (loading != null) {
            (loading!!.findViewById(R.id.progressTitle) as TextView).text = "" + progress
        }
    }

    override fun onResume() {
        super.onResume()
        isFrontVisible = true
        UIAdjuster.closeKeyBoard(this@JActivity)
    }

    protected fun runOnHandler() {
        handler.post(object : Runnable {
            override fun run() {
                handler.removeCallbacks(this)
            }
        })
    }

    private fun showErrorMessage(intent: Intent?, listener: DialogInterface.OnClickListener?) {
        if (JUtil.hasIntentExtras(intent)) {
            val msg = intent!!.extras!!.getString(Const.SERVER_MESSAGE_TAG)
            dissmissMessageDialog()
            if (listener != null) {
                messageDialg = JDialog.showMessage(this@JActivity, "錯誤", msg, listener)
            } else {
                messageDialg = JDialog.showMessage(this@JActivity, "錯誤", msg)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        isFrontVisible = false
    }

    fun dissmissMessageDialog() {
        if (messageDialg != null) {
            messageDialg!!.dismiss()
        }
    }

    override fun onDestroy() {
        isAlive = false
        cancelLoading()
        UIAdjuster.closeKeyBoard(this@JActivity)
        handler.removeCallbacks(null)
        try {
            super.onDestroy() // I use try catch and it dosen't crash any more
            WeakReference(this@JActivity).clear()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun startClearTopIntent(intent: Intent, isCloseStartActivity: Boolean) {
        startClearTopIntent(intent, Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK, isCloseStartActivity)
    }

    fun startClearTopIntent(inclass: Class<out FragmentActivity>, isCloseStartActivity: Boolean) {
        startClearTopIntent(Intent(this@JActivity, inclass), Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK, isCloseStartActivity)
    }

    fun startClearTopIntent(inclass: Class<out FragmentActivity>) {
        startClearTopIntent(Intent(this@JActivity, inclass), Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK, true)
    }

    protected fun closeRepeatActivity() {
        if (intent.flags and Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT != 0) {
            finish()
        }
    }

    fun startActivity(inclass: Class<out FragmentActivity>) {
        startActivity(Intent(this@JActivity, inclass))
    }

    @JvmOverloads
    fun startClearTopIntent(intent: Intent, action: Int = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK, isCloseStartActivity: Boolean = true) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            intent.flags = action
            startActivity(intent)
        } else {
            val cn = intent.component
            val mainIntent = Intent.makeRestartActivityTask(cn)
            startActivity(mainIntent)
        }
        if (isCloseStartActivity) {
            finish()
        }
    }

    override fun finish() {
        //        clearAllFragment();
        AppManager.removeRecord(this)
        JLog.d(" finish $TAG")
        innerFinish()
    }

    fun backAction() {}

    fun onFinish() {}

    protected fun onErrorMessageClick() {}

    protected fun innerFinish() {
        onFinish()
        cancelLoading()
        //        clearAllFragment();
        super.finish()
        isAlive = false
        if (!isLastOneActivity) {
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
    }

    fun finishWithAnimation(hasAnimation: Boolean) {
        if (hasAnimation) {
            innerFinish()
        } else {
            onFinish()
            super.finish()
        }
    }

    @JvmOverloads
    fun showLoading(text: String = "資料交換中\\n請稍候"): Boolean {
        if (loading == null && baseContext != null && !isFinishing) {
            loading = JDialog.showProgressDialog(this@JActivity, "" + text, false)
        }
        return true
    }

    fun updateLoading(statusText: String) {
        if (loading == null) {
            loading = JDialog.showProgressDialog(this@JActivity, "" + statusText, false)
        }
        val status = loading!!.findViewById(R.id.progressTitle) as TextView
        status.text = statusText
    }

    @JvmOverloads
    fun hasInternet(showAlert: Boolean = true): Boolean {
        val connManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val info = connManager.activeNetworkInfo
        if (info == null || !info.isConnected || !info.isAvailable) {
            cancelLoading()
            if (showAlert) {
                if (messageDialg != null) {
                    messageDialg!!.dismiss()
                }
                messageDialg = JDialog.showMessage(this@JActivity,
                        Const.Message_Title,
                        "無網路連線",
                        DialogInterface.OnClickListener { dialog, which ->
                            onErrorMessageClick()
                            dialog.dismiss()
                        })
            }
            return false
        } else {
            return true
        }
    }


    /**
     * dismiss ProgressDialog
     */
    fun cancelLoading() {
        if (loading != null && loading!!.isShowing) {
            loading!!.dismiss()
            loading = null
        }
    }

    fun cancelLoadingOnUI() {
        handler.post(object : Runnable {
            override fun run() {
                cancelLoading()
                handler.removeCallbacks(this)
            }
        })
    }

    protected fun onError() {
        backPress()
    }

    protected fun backPress(): Boolean {
        finish()
        return true
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK || event.action == KeyEvent.KEYCODE_BACK) {
            backAction()
            if (backPress()) {
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    @JvmOverloads
    fun takeScreenShot(rootView: View = findViewById<View>(android.R.id.content).rootView): Bitmap {
        rootView.destroyDrawingCache()
        rootView.isDrawingCacheEnabled = true
        rootView.buildDrawingCache(true)
        rootView.invalidate()
        val map = rootView.getDrawingCache(true).copy(Bitmap.Config.ARGB_8888, false)
        rootView.isDrawingCacheEnabled = false
        rootView.destroyDrawingCache()
        return map
    }

    @JvmOverloads
    fun commitFragment(replaceId: Int, fragment: Fragment, type: FragmentAnimationType = FragmentAnimationType.None) {
        commitFragment(replaceId, fragment, fragment.javaClass.simpleName, type)
    }

    fun showDialogFragment(dialog: DialogFragment) {
        val tag = dialog.javaClass.simpleName
        val ft = supportFragmentManager.beginTransaction()
        val prev = supportFragmentManager.findFragmentByTag(tag)
        if (prev != null) {
            if (prev is DialogFragment) {
                prev.dismiss()
            }
            ft.remove(prev)
        }
        ft.addToBackStack(null)
        // Create and show the dialog.
        dialog.show(ft, tag)
    }

    @JvmOverloads
    fun commitFragment(replaceId: Int, fragment: Fragment, tag: String, type: FragmentAnimationType, keepInHistory: Boolean = false) {
        if (!isFinishing && isAlive && supportFragmentManager != null) {
            val transaction = supportFragmentManager.beginTransaction()
            when (type) {
                FragmentAnimationType.LeftIn -> transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                FragmentAnimationType.RightIn -> transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                FragmentAnimationType.TopDown -> transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
                FragmentAnimationType.BottomRise -> transaction.setCustomAnimations(R.anim.slide_down, R.anim.slide_up)
                FragmentAnimationType.None -> {
                }
            }
            if (hasSameFragment(fragment)) {
                val historyFragment = getHistoryFragment(fragment.javaClass)
                transaction.replace(replaceId, historyFragment, tag)
            } else {
                if (keepInHistory) {
                    transaction.add(replaceId, fragment, tag)
                } else {
                    transaction.replace(replaceId, fragment, tag)
                }
            }

            if (keepInHistory) {
                transaction.addToBackStack(tag)
            } else {
                transaction.addToBackStack(null)
            }

            // transaction.commit();
            /*****
             * 修正Bug java.lang.IllegalStateException: Can not perform this action
             * after onSaveInstanceState
             */
            transaction.commitAllowingStateLoss()
        }
    }

    fun <T : Fragment> getHistoryFragment(fragment: Class<T>): Fragment? {
        return getHistoryFragment(fragment.simpleName)
    }

    fun getHistoryFragment(tag: String?): JFragment? {
        if (supportFragmentManager != null) {
            val current = supportFragmentManager.findFragmentByTag(tag)
            if (current != null) {
                return current as JFragment
            }
        }
        return null
    }

    fun clearFragment(fragment: Class<out Fragment>) {
        if (supportFragmentManager != null) {
            val current = supportFragmentManager.findFragmentByTag(fragment.simpleName)
            if (current != null) {
                JLog.d(TAG + " clearFragment Fragment " + current.javaClass.simpleName)
                current.onDestroyView()
            }
        }
    }

    fun hasSameFragment(fragment: Fragment): Boolean {
        if (supportFragmentManager != null) {
            val current = supportFragmentManager.findFragmentByTag(fragment.javaClass.simpleName)
            if (current != null) {
                return true
            }
        }
        return false
    }

    fun hasSameFragment(fragment: Class<out Fragment>): Boolean {
        if (supportFragmentManager != null) {
            val current = supportFragmentManager.findFragmentByTag(fragment.simpleName)
            if (current != null) {
                return true
            }
        }
        return false
    }

    @JvmOverloads
    fun removeFragment(fragments: Class<out Fragment>, clear: Boolean = true) {
        if (notEmpyFragments()) {
            val fragment = supportFragmentManager.findFragmentByTag(fragments.simpleName)
            JLog.d(JLog.JosephWang, TAG + " removeFragment " + (fragment != null))
            if (fragment != null) {
                supportFragmentManager.beginTransaction().remove(fragment).commit()
            }

            if (clear) {
                supportFragmentManager.fragments.clear()
            }
        }
    }

    @SuppressLint("RestrictedApi")
    fun clearAllFragment() {
        if (notEmpyFragments()) {
            supportFragmentManager.fragments.clear()
        }
    }

    fun isFragmentVisible(fragment: Fragment): Boolean {
        return isFragmentVisible(fragment.javaClass)
    }

    fun isFragmentVisible(fragment: Class<out Fragment>): Boolean {
        if (notEmpyFragments()) {
            val current = getHistoryFragment(fragment.javaClass.simpleName)
            return if (current != null && current.isVisible) {
                true
            } else {
                false
            }
        }
        return false
    }

    fun notEmpyFragments(): Boolean {
        return supportFragmentManager != null && JUtil.notEmpty(supportFragmentManager.fragments)
    }

    fun <T : View> getView(@IdRes id: Int): T {
        return window.findViewById<View>(id) as T
    }

    fun isCurrentFragmentExist(fragment: Fragment): Boolean {
        if (supportFragmentManager != null) {
            val current = supportFragmentManager.findFragmentByTag(fragment.javaClass.simpleName)
            if (current != null) {
                return true
            }
        }
        return false
    }

    fun isCurrentFragmentExist(fragment: Class<out Fragment>): Boolean {
        if (supportFragmentManager != null) {
            val current = supportFragmentManager.findFragmentByTag(fragment.simpleName)
            if (current != null) {
                return true
            }
        }
        return false
    }

    fun dispatchFragmentDesory() {
        val fragments = supportFragmentManager.fragments
        if (JUtil.notEmpty(fragments)) {
            for (fragment in fragments) {
                fragment?.onDestroy()
            }
            clearAllFragment()
        }
    }

    open fun <T : Fragment> getCurrentFragment(): T? {
        if (supportFragmentManager != null && JUtil.notEmpty(supportFragmentManager.fragments)) {
            val fragments = supportFragmentManager.fragments
            var current: Fragment? = null
            for (i in fragments.indices.reversed()) {
                current = fragments[i]
                if (current != null && current.isVisible) {
                    return current as T?
                }
            }
        }
        return null
    }

    protected fun exitApp() {
        val pid = android.os.Process.myPid()
        android.os.Process.killProcess(pid)
    }
}
