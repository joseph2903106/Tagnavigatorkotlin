package com.tagnavigator.framework
import android.support.v4.app.Fragment
import android.view.KeyEvent
import com.tagnavigator.util.JLog


/**
 * Created by josephwang on 2017/3/28.
 */

abstract class JTabActivity : JActivity(), ITabHostTransaction {

    abstract val fragmentId: Int
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK || event.action == KeyEvent.KEYCODE_BACK) {
            val fragment = getCurrentFragment<Fragment>()
            JLog.d(JLog.TAG, "onKeyDown fragment != null " + (fragment != null))
            if (fragment != null) {
                JLog.d(JLog.TAG, "onKeyDown fragment instanceof JChildFragment " + (fragment is JChildFragment<*>))
                JLog.d(JLog.TAG, "onKeyDown fragment getSimpleName " + fragment.javaClass.simpleName)
                if (fragment is JChildFragment<*>) {
                    val child = fragment
                    JLog.d(JLog.TAG, "onKeyDown child.hasChildFragment " + child.hasChildFragment())
                    if (child.hasChildFragment()) {
                        child.backToPreviousFragment()
                        return true
                    }
                    else
                    {
                        showExitReminid()
                        return true
                    }
                }
            } else return true
        }
        return super.onKeyDown(keyCode, event)
    }

    @JvmOverloads
    fun commitCurrentFragmentByParent(fragment: JParentFragment<*>, type: FragmentAnimationType = FragmentAnimationType.None) {
        if (getHistoryFragment(fragment.javaClass) != null) {
            val parent = getHistoryFragment(fragment.javaClass) as JParentFragment<*>
            commitFragmentTransaction(parent.currentFragment, type)
        } else {
            commitFragmentTransaction(fragment, type)
        }
    }

    @JvmOverloads
    fun commitParentFragment(fragment: JParentFragment<*>, type: FragmentAnimationType = FragmentAnimationType.None) =
            if (getHistoryFragment(fragment.javaClass) != null) {
                val parent = getHistoryFragment(fragment.javaClass) as JParentFragment<*>
                if (parent != null) {
                    parent.clearHistory()
                    parent.currentFragment = parent
                    commitFragmentTransaction(parent, type)
                } else {

                }

            } else {
                commitFragmentTransaction(fragment, type)
            }

    override fun commitFragmentTransaction(fragment: JFragment, type: FragmentAnimationType) {
        commitFragment(fragmentId, fragment, type)
    }


    open fun showExitReminid() {}
}