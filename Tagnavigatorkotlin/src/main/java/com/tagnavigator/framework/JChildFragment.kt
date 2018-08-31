package com.tagnavigator.framework


import android.os.Bundle
import com.tagnavigator.util.JLog
import com.tagnavigator.util.JUtil
import java.lang.reflect.ParameterizedType

/**
 * Created by josephwang on 2017/3/28.
 */

open abstract class JChildFragment<Parent : JParentFragment<*>> : JFragment() {
    open var jParentFragment: Parent? = null
        get() {
            val tab = getJTabActivity<JTabActivity>()
            val sooper = javaClass.getGenericSuperclass()
            if (sooper !is ParameterizedType) {
                throw IllegalArgumentException("ChildFragment must have been assigned to JParentFragment with Generic name !!!")
            } else {
                if (JUtil.notEmpty((sooper).actualTypeArguments)) {
                    val parentType = (sooper).actualTypeArguments[0]
                    val name = parentType.toString()
                    val className = name.substring(name.lastIndexOf(".") + 1)
                    if (tab.getHistoryFragment(className) != null) {
                        return tab.getHistoryFragment(className) as Parent
                    } else {
                        return null
                    }
                } else {
                    throw IllegalArgumentException("ChildFragment must have been assigned to JParentFragment with Generic name !!!")
                }
            }
        }

    fun getJParentClass(): Class<out JParentFragment<JTabActivity>> {
        val sooper = this.javaClass.getGenericSuperclass()
        val parentType = (sooper as ParameterizedType).actualTypeArguments[0]
        return parentType.javaClass as Class<out JParentFragment<JTabActivity>>
    }

    open val currentFragment: JFragment
        get() {
            val parent = jParentFragment
            return if (parent?.currentFragment != null) {
                parent.currentFragment
            } else {
                this
            }
        }

    fun <T : JTabActivity> getJTabActivity(): T {
        return activity as T
    }

    fun <T : JFragment> getJChildsHistoryFragment(classes: Class<T>): T {
        if (jParentFragment != null) {
            return jParentFragment!!.getJChildsHistoryFragment(classes)
        } else {
            var tab = activity as JTabActivity
            var fragment = tab.getHistoryFragment(classes)
            try {
                if (fragment == null) {
                    fragment = classes.newInstance()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return fragment as T
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (activity !is JTabActivity) {
            throw IllegalArgumentException("Must be combined with JTabActivity!!!")
        }
        //        checkParent();
    }

    private fun checkParent() {
        val sooper = javaClass.getGenericSuperclass() as? ParameterizedType
                ?: throw IllegalArgumentException("ChildFragment must have been assigned to JParentFragment with Generic name !!!")
    }

    fun hasChildFragment(): Boolean {
        return jParentFragment != null && jParentFragment!!.hasChildFragment()
    }

    @JvmOverloads
    fun commitChildFragment(fragment: JFragment, isSelfAddToHistory: Boolean = true) {
        val parent = jParentFragment
        parent?.commitChildFragment(fragment, isSelfAddToHistory)
    }

    fun clearHistory() {

        val parent = jParentFragment
        parent?.clearHistory()
    }

    fun backToPreviousFragment(fragmentClass: Class<JFragment>? = null) {
        val parent = jParentFragment
        JLog.d(TAG, "JParentFragment (parent != null) " + (parent != null))
        parent?.backToPreviousFragment(fragmentClass)
    }
}