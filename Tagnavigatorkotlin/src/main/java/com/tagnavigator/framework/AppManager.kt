package com.tagnavigator.framework

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.support.v4.util.ArrayMap
import com.tagnavigator.util.JLog


/**
 * @author JosephWang
 */
object AppManager {
    private var activityMap: ArrayMap<String, JActivity>? = null

    fun addActivity(activity: JActivity) {
        if (activityMap == null) {
            activityMap = ArrayMap()
        }
        activityMap!![activity::class.java.simpleName] = activity
    }

    @JvmOverloads
    fun finishActivity(name: Class<out JActivity>, hasAnimation: Boolean = false) {
        if (activityMap != null && activityMap!![name.simpleName] != null) {
            val act = activityMap!![name.simpleName]
            act!!.finishWithAnimation(hasAnimation)
            activityMap!!.remove(name.simpleName)
        }
    }

    fun hasActivity(activity: JActivity): Boolean {
        return activityMap!![activity::class.java.simpleName] != null
    }

    fun getActivity(name: Class<out JActivity>): JActivity? {
        return activityMap!![name.simpleName]
    }


    fun removeRecord(activity: JActivity?) {
        if (activity != null) {
            activityMap!!.remove(activity::class.java.simpleName)
        }
    }

    fun finishAllActivity() {
        for (act in activityMap!!.values) {
            act.finishWithAnimation(false)
        }
        activityMap!!.clear()
    }

    fun appExit() {
        JLog.d(JLog.TAG, "appExit...")
        activityMap!!.clear()
        val pid = android.os.Process.myPid()
        android.os.Process.killProcess(pid)
    }

    fun isLastOneActivity(act: Activity?): Boolean {
        if (act == null) {
            return false
        }
        val manager = act.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        if (Build.VERSION.SDK_INT >= 21) {
            val tasks = manager.runningAppProcesses
            if (tasks != null && !tasks.isEmpty() && tasks.size > 0) {
                if (tasks[0].processName.contains(act.javaClass.simpleName)) {
                    return true
                }
            }
        } else {
            val tasks = manager.getRunningTasks(1)
            if (tasks != null && !tasks.isEmpty()) {
                if (tasks[0].numActivities == 1) {
                    return true
                }
            }
        }
        return false
    }


    fun clearAllInstance() {
        if (activityMap != null) {
            activityMap!!.clear()
        }
    }
}