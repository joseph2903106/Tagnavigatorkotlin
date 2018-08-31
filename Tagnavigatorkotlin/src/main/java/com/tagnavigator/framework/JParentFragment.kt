package com.tagnavigator.framework


import android.os.Bundle
import com.tagnavigator.util.JLog
import com.tagnavigator.util.JUtil
import java.util.*

/**
 * Created by josephwang on 2017/3/28.
 */

open abstract class JParentFragment<HostActivity : JTabActivity> : JFragment() {
    val historyList = ArrayList<String>()
    var current: JFragment? = null

    val jTabActivity: HostActivity
        get() = activity as HostActivity

    var currentFragment: JFragment
        get() = if (current != null) current!! else this
        set(fragment) {
            current = fragment
        }

    val lastFragment: JFragment?
        get() {
            if (JUtil.notEmpty(historyList)) {
                val fragmentName = historyList[historyList.size - 1]
                return jTabActivity.getHistoryFragment(fragmentName)
            } else {
                return this
            }
        }

    fun addHistory(fragment: JFragment) {
        historyList.add(fragment.javaClass.getSimpleName())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (activity !is JTabActivity) {
            throw IllegalArgumentException("Must be combined with JTabActivity!!!")
        }
    }

    fun hasChildFragment(): Boolean {
        return historyList.size > 0
    }

    fun removeHistory(fragment: JFragment) {
        historyList.remove(fragment.javaClass.simpleName)
    }

    fun removeHistory(idnex: Int) {
        historyList.removeAt(idnex)
    }

    fun commitChildFragment(fragment: JFragment, animationType: FragmentAnimationType) {
        commitChildFragment(fragment, true, animationType)
    }

    fun containChildFragment(fragment: JFragment): Boolean {
        return historyList.contains(fragment.javaClass.simpleName)
    }

    @JvmOverloads
    fun commitChildFragment(fragment: JFragment, isSelfAddToHistory: Boolean = true, animationType: FragmentAnimationType = FragmentAnimationType.LeftIn) {
        JLog.d(JLog.TAG + " commitChildFragment historyList.contains fragment  " + historyList.contains(fragment.javaClass.simpleName))
        if (!jTabActivity.isFinishing && jTabActivity.supportFragmentManager != null) {
            val parent = jTabActivity.getHistoryFragment(this.javaClass)
            if (parent != null && parent is JParentFragment<*>) {
                if (parent.empyHistory()) {
                    parent.addHistory(this)
                }

                val lastOne = lastFragment
                if (parent.currentFragment !is JParentFragment<*> || lastOne != null && !lastOne.TAG.equals(fragment.TAG)) {
                    parent.addHistory(fragment)
                }
                if (!isSelfAddToHistory) {
                    removeHistory(currentFragment)
                }
                parent.currentFragment = fragment
                jTabActivity.commitFragment(jTabActivity!!.fragmentId, fragment, animationType)
            }
        }
    }

    fun setCurrentFragment() {
        current = this
    }

    fun clearHistory() {
        historyList.clear()
    }

    fun empyHistory(): Boolean {
        return historyList.isEmpty()
    }

    fun getStackFragment(index: Int): JFragment {
        if (JUtil.notEmpty(historyList)) {
            val fragmentName = historyList[index]
            val fragment = jTabActivity.getHistoryFragment(fragmentName)
            return if (fragment != null) {
                fragment
            } else {
                this
            }
        } else {
            return this
        }
    }

    fun <T : JFragment> getJChildsHistoryFragment(classes: Class<T>): T {
        var fragment = jTabActivity.getHistoryFragment(classes)
        try {
            if (fragment == null) {
                fragment = classes.newInstance()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return fragment as T
    }

    @JvmOverloads
    fun <T : JFragment> backToPreviousFragment(fragmentClass: Class<T>? = null) {
        if (JUtil.notEmpty(historyList)) {
            val lastOne = historyList.size - 2
            if (lastOne >= 0 && lastOne < historyList.size) {
                var fragment = getStackFragment(lastOne)
                fragment = jTabActivity.getHistoryFragment(fragment.javaClass.simpleName)!!
                if (fragment.TAG.equals(TAG)) {
                    commitSelfFragment()
                } else {
                    current = fragment
                    jTabActivity.commitFragment(jTabActivity.fragmentId, fragment, FragmentAnimationType.RightIn)
                    historyList.removeAt(historyList.size - 1)
                }
            } else {
                if (historyList.size == 1) {
                    clearHistory()
                    current = this
                    jTabActivity.finish()
                } else {
                    commitSelfFragment()
                }
            }
        } else {
            if (fragmentClass != null) {
                val jFragment = jTabActivity.getHistoryFragment(fragmentClass) as JFragment
                jTabActivity.commitFragmentTransaction(jFragment, FragmentAnimationType.RightIn)
            }
        }
    }

    fun commitSelfFragment() {
        clearHistory()
        setCurrentFragment()
        val fragment = jTabActivity.getHistoryFragment(javaClass)
        if (fragment != null) {
            jTabActivity.commitFragment(jTabActivity.fragmentId, fragment, FragmentAnimationType.RightIn)
        } else {
            jTabActivity.commitFragment(jTabActivity.fragmentId, this, FragmentAnimationType.RightIn)
        }
    }
}
