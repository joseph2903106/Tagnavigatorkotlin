package com.tagnavigator.framework


import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.ProgressDialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.support.annotation.IdRes
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.view.KeyEvent
import android.view.View
import android.widget.TextView
import com.tagnavigator.R
import com.tagnavigator.util.JDialog
import com.tagnavigator.util.JLog
import com.tagnavigator.util.JUtil
import com.tagnavigator.util.UIAdjuster
import java.util.*


open abstract class JFragment : Fragment(), View.OnKeyListener {
    var TAG: String
    protected lateinit var savedInstanceState: Bundle
    protected var loading: ProgressDialog? = null
    private lateinit var settings: SharedPreferences

    val handler = Handler()

    val isReclaim: Boolean
        get() = activity == null || activity!!.isFinishing

    @Suppress("DEPRECATION")
    val isLastOneActivity: Boolean
        get() {
            val am = activity!!.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val tasks = am.getRunningTasks(1)
            return if (!tasks.isEmpty()) {
                tasks[0].numActivities == 1
            } else false
        }

    init {
        TAG = (this as Any).javaClass.simpleName
        retainInstance = true
    }

    fun <T : JActivity> getJActivity(): T {
        return activity as T
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

    override fun onCreate(savedInstanceState: Bundle?) {
        JLog.d(JLog.JosephWang, "$TAG onCreate")
        super.onCreate(savedInstanceState)
        settings = PreferenceManager.getDefaultSharedPreferences(getJActivity())
        if (savedInstanceState != null) {
            this.savedInstanceState = savedInstanceState
        }
        UIAdjuster.closeKeyBoard(activity)
        // // 註冊監聽
    }

    fun hasInternet(): Boolean {
        return getJActivity<JActivity>()!!.hasInternet()
    }

    override fun onResume() {
        JLog.d(JLog.JosephWang, "$TAG onResume")
        super.onResume()

        UIAdjuster.closeKeyBoard(activity)
    }

    override fun onDestroy() {
        JLog.d(JLog.JosephWang, "$TAG onDestroy")
        cancelLoading()
        UIAdjuster.closeKeyBoard(activity)
        handler.removeCallbacks(null)
        super.onDestroy()
    }

    @JvmOverloads
    fun showLoading(text: String = "資料交換中\n請稍候"): Boolean {
        if (loading == null && activity != null) {
            loading = JDialog.showProgressDialog(activity!!, "" + text, false)
        }
        return true
    }

    protected fun setProgressInLoading(progress: String) {
        if (loading != null && activity != null) {
            (loading!!.findViewById(R.id.progressTitle) as TextView).text = "" + progress
        }
    }

    /**
     * dismiss ProgressDialog
     */
    fun cancelLoading() {
        if (!isReclaim &&
                loading != null && loading!!.isShowing) {
            loading!!.dismiss()
            loading = null
        }
    }

    /**
     *
     */
    override fun onPause() {
        JLog.d(JLog.JosephWang, "$TAG onPause")
        super.onPause()
        cancelLoading()
        // unregisterAllBundledAction();
    }

    fun onFinish() {
        JLog.d("josephWang", "onFinish " + javaClass.simpleName)
    }

    override fun onAttach(activity: Activity?) {
        JLog.d(JLog.JosephWang, "$TAG onAttach")
        super.onAttach(activity)
    }

    override fun onDetach() {
        JLog.d(JLog.JosephWang, "$TAG onDetach")
        super.onDetach()
        cancelLoading()
    }

    fun finish() {
        onFinish()
        cancelLoading()
        if (notEmpyOtherFragments()) {
            activity!!.supportFragmentManager.popBackStack()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        JLog.d(JLog.JosephWang, "$TAG onViewCreated")
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onStart() {
        JLog.d(JLog.JosephWang, "$TAG onStart")
        super.onStart()
    }

    override fun onStop() {
        JLog.d(JLog.JosephWang, "$TAG onStop")
        super.onStop()
    }

    fun <T : View> getView(@IdRes id: Int): T {
        return getJActivity<JActivity>()!!.getView(id) as T
    }

    override fun onDestroyView() {
        handler.removeCallbacks(null)
        JLog.d(JLog.JosephWang, "$TAG onDestroyView")
        cancelLoading()
        super.onDestroyView()
    }

    @JvmOverloads
    fun takeScreenShot(rootView: View = activity!!.findViewById<View>(android.R.id.content).rootView): Bitmap {
        rootView.destroyDrawingCache()
        rootView.buildDrawingCache()
        rootView.invalidate()
        rootView.isDrawingCacheEnabled = true
        val map = rootView.getDrawingCache(true).copy(Bitmap.Config.ARGB_8888, false)
        rootView.destroyDrawingCache()
        return map
    }

    @JvmOverloads
    fun commitOtherChildFragment(replaceId: Int, fragment: Fragment, type: FragmentAnimationType = FragmentAnimationType.None) {
        commitOtherChildFragment(replaceId, fragment, fragment.javaClass.simpleName, type)
    }

    fun commitOtherChildFragment(replaceId: Int, fragment: Fragment, tag: String, type: FragmentAnimationType) {
        if (getJActivity<JActivity>() != null && !getJActivity<JActivity>()!!.isFinishing && getJActivity<JActivity>()!!.isAlive && childFragmentManager != null) {
            val transaction = childFragmentManager.beginTransaction()
            /***Test */
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            /***Test */
            when (type) {
                FragmentAnimationType.LeftIn -> transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                FragmentAnimationType.RightIn -> transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                FragmentAnimationType.TopDown -> transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
                FragmentAnimationType.BottomRise -> transaction.setCustomAnimations(R.anim.slide_down, R.anim.slide_up)
                FragmentAnimationType.None -> {
                }
            }
            if (hasOtherChildsSameFragment(fragment)) {
                val historyFragment = getOtherChildsHistoryFragment(fragment.javaClass)
                transaction.replace(replaceId, historyFragment, tag)
            } else {
                transaction.replace(replaceId, fragment, tag)
            }

            transaction.addToBackStack(null)
            // transaction.commit();
            /*******
             * 修正Bug java.lang.IllegalStateException: Can not perform this action
             * after onSaveInstanceState
             */
            transaction.commitAllowingStateLoss()
        }
    }

    fun commitOtherChildNewFragment(replaceId: Int, fragment: Fragment) {
        if (!getJActivity<JActivity>()!!.isFinishing && childFragmentManager != null) {
            val transaction = childFragmentManager.beginTransaction()
            transaction.replace(replaceId, fragment, fragment.javaClass.simpleName)

            transaction.addToBackStack(null)
            // transaction.commit();
            /*******
             * 修正Bug java.lang.IllegalStateException: Can not perform this action
             * after onSaveInstanceState
             */
            transaction.commitAllowingStateLoss()
        }
    }

    fun showDialogFragment(dialog: DialogFragment) {
        val tag = dialog.javaClass.simpleName
        val ft = childFragmentManager.beginTransaction()
        val prev = childFragmentManager.findFragmentByTag(tag)
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

    fun <T : Fragment> getOtherChildsHistoryFragment(fragment: Class<T>): T? {
        return getOtherChildsHistoryFragment(fragment.simpleName)
    }

    fun <T : Fragment> getOtherChildsHistoryFragment(tag: String): T? {
        if (childFragmentManager != null) {
            val current = childFragmentManager.findFragmentByTag(tag)
            if (current != null ) {
                return current as T
            }
        }
        return null
    }

    fun clearOtherChildsFragment(fragment: Class<out Fragment>) {
        if (childFragmentManager != null) {
            val current = childFragmentManager.findFragmentByTag(fragment.simpleName)
            if (current != null) {
                JLog.d(TAG + " clearFragment Fragment " + current.javaClass.simpleName)
                current.onDestroyView()
            }
        }
    }

    fun hasOtherChildsSameFragment(fragment: Fragment): Boolean {
        if (childFragmentManager != null) {
            val current = childFragmentManager.findFragmentByTag(fragment.javaClass.simpleName)
            if (current != null) {
                return true
            }
        }
        return false
    }

    fun hasOtherChildsSameFragment(fragment: Class<out Fragment>): Boolean {
        if (childFragmentManager != null) {
            val current = childFragmentManager.findFragmentByTag(fragment.simpleName)
            if (current != null) {
                return true
            }
        }
        return false
    }

    @JvmOverloads
    fun removeOtherChildsFragment(fragments: Class<out Fragment>, clear: Boolean = true) {
        if (notEmpyOtherFragments()) {
            val fragment = childFragmentManager.findFragmentByTag(fragments.simpleName)
            JLog.d(JLog.JosephWang, TAG + " removeFragment " + (fragment != null))
            if (fragment != null) {
                childFragmentManager.beginTransaction().remove(fragment).commit()
            }

            if (clear) {
                childFragmentManager.fragments.clear()
            }
        }
    }

    @SuppressLint("RestrictedApi")
    fun clearOtherChildsAllFragment() {
        if (notEmpyOtherFragments()) {
            childFragmentManager.fragments.clear()
        }
    }

    @SuppressLint("RestrictedApi")
    fun isCurrentOtherFragmentVisible(fragment: Fragment): Boolean {
        if (notEmpyOtherFragments()) {
            val current = childFragmentManager.fragments[0]
            return current != null && current.javaClass.simpleName == fragment.javaClass.simpleName
        }
        return false
    }

    fun isCurrentOtherFragmentVisible(fragment: Class<out Fragment>): Boolean {
        if (notEmpyOtherFragments()) {
            val current = childFragmentManager.fragments[0]
            return current != null && current.javaClass.simpleName == fragment.simpleName
        }
        return false
    }

    fun notEmpyOtherFragments(): Boolean {
        return childFragmentManager != null && JUtil.notEmpty(childFragmentManager.fragments)
    }

    fun isLastOneOtherFragmentVisible(fragment: Class<out Fragment>): Boolean {
        if (notEmpyOtherFragments()) {
            val current = childFragmentManager.fragments[childFragmentManager.fragments.size - 1]
            return current != null && current.javaClass.simpleName == fragment.simpleName
        }
        return false
    }

    fun isCurrentOtherFragmentExist(fragment: Fragment): Boolean {
        if (childFragmentManager != null) {
            val current = childFragmentManager.findFragmentByTag(fragment.javaClass.simpleName)
            if (current != null) {
                return true
            }
        }
        return false
    }

    fun isCurrentOtherFragmentExist(fragment: Class<out Fragment>): Boolean {
        if (childFragmentManager != null) {
            val current = childFragmentManager.findFragmentByTag(fragment.simpleName)
            if (current != null) {
                return true
            }
        }
        return false
    }

    fun <T : View> getChildView(@IdRes id: Int): T {
        return activity?.window?.findViewById<View>(id) as T
    }

    override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK || event.action == KeyEvent.KEYCODE_BACK) {
            backAction()
            return true
        }
        return onKey(v, keyCode, event)
    }

    fun backAction() {
        if (childFragmentManager.backStackEntryCount > 0) {
            childFragmentManager.popBackStack()
        }
    }
}
