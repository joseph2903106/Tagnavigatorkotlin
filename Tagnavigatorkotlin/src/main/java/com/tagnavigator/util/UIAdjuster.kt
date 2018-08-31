package com.tagnavigator.util

import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.animation.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView


/**
 * JosephWang
 */
object UIAdjuster {

    fun expand(): AnimationSet {
        val expandAndShrink = AnimationSet(true)
        val expand = ScaleAnimation(
                1f, 1.5f,
                1f, 1.5f,
                Animation.RELATIVE_TO_PARENT, 0f,
                Animation.RELATIVE_TO_PARENT, 0f)
        expand.duration = 1000

        expandAndShrink.addAnimation(expand)
        expandAndShrink.fillAfter = true
        expandAndShrink.interpolator = AccelerateInterpolator(1.0f)
        return expandAndShrink
    }

    fun shirnk(): AnimationSet {
        val expandAndShrink = AnimationSet(true)

        val shrink = ScaleAnimation(
                1.5f, 1f,
                1.5f, 1f,
                Animation.RELATIVE_TO_PARENT, 0f,
                Animation.RELATIVE_TO_PARENT, 0f)
        shrink.startOffset = 1000
        shrink.duration = 1000


        expandAndShrink.addAnimation(shrink)
        expandAndShrink.fillAfter = true
        expandAndShrink.interpolator = AccelerateInterpolator(1.0f)
        return expandAndShrink
    }

    fun closeKeyBoardForcely(act: Activity) {
        if (act.currentFocus != null) {
            val imm = act.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
        }
    }

    fun setListViewWidth(listView: ListView, width: Int) {
        val params = listView.layoutParams
        params.width = width
        listView.layoutParams = params
        listView.requestLayout()
    }


    fun getScreenWidth(context: Context): Int {
        return context.resources.displayMetrics.widthPixels
    }

    fun getScreenHeight(context: Context): Int {
        return context.resources.displayMetrics.heightPixels
    }

    fun isEmpty(editor: TextView): Boolean {
        return TextUtils.isEmpty(editor.text.toString())
    }

    fun getText(textiew: TextView?): String {
        return textiew?.text?.toString() ?: ""
    }

    /**
     * 要在 dispatchTouchEvent 使用
     *
     * @param activity
     * @param event
     */
    fun closeKeyBoardOnTouchOutSide(activity: Activity, event: MotionEvent) {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = activity.currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                }
            }
        }
    }

    fun showClickAnimation(v: View) {
        v.clearAnimation()
        val anim = AlphaAnimation(0.5f, 1.2f)
        anim.duration = 120
        anim.isFillEnabled = true
        anim.fillAfter = true
        v.startAnimation(anim)
        anim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {

            }

            override fun onAnimationEnd(animation: Animation) {
                animation.cancel()
                v.clearAnimation()
            }

            override fun onAnimationRepeat(animation: Animation) {

            }
        })
    }

    fun getDrawable(ctx: Context, id: Int): Drawable {
        return ctx.resources.getDrawable(id)
    }

    fun closeKeyBoard(act: Activity?) {
        if (act != null) {
            val imm = act.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (act.currentFocus != null && act.currentFocus!!.windowToken != null) {
                imm.hideSoftInputFromWindow(act.currentFocus!!.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            }
            if (act.window != null) {
                act.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
            }
        }
    }

    fun getDeviceWidth(context: Context): Float {
        val display = (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        return display.width.toFloat()
    }

    fun getDeviceHeight(context: Context): Float {
        val display = (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        return display.height.toFloat()
    }

    fun dip2px(context: Context, dipValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dipValue * scale + 0.5f).toInt()
    }

    @JvmOverloads
    fun expandList(listView: ListView, plus: Int = 0): Int {
        var height = 0
        val listAdapter = listView.adapter ?: return height
        var totalHeight = 0
        for (i in 0 until listAdapter.count) {
            val listItem = listAdapter.getView(i, null, listView)
            listItem.measure(0, 0)
            totalHeight += listItem.measuredHeight
        }
        val params = listView.layoutParams
        params.height = totalHeight + listView.dividerHeight * (listAdapter.count - 1) + listView.paddingTop + listView.paddingBottom
        height = params.height + plus
        listView.layoutParams = params
        listView.requestLayout()
        return height
    }

    fun toggleVisibility(v: View): Boolean {
        when (v.visibility) {
            View.VISIBLE -> v.visibility = View.GONE
            else -> v.visibility = View.VISIBLE
        }
        return v.visibility == View.VISIBLE
    }

    fun getIntText(textiew: TextView?): Int {
        if (textiew != null) {
            val text = textiew.text.toString()
            if (TextUtils.isDigitsOnly(text)) {
                try {
                    return Integer.parseInt(text)
                } catch (e: NumberFormatException) {
                    e.printStackTrace()
                }

            }
        }
        return -1
    }

    fun getIntText(res: String): Int {
        if (!TextUtils.isEmpty(res) && TextUtils.isDigitsOnly(res)) {
            try {
                return Integer.parseInt(res)
            } catch (e: NumberFormatException) {
                e.printStackTrace()
            }

        }
        return -1
    }

    fun expand(v: View) {
        v.measure(0, 0)
        expand(v, 400, v.measuredHeight)
    }

    fun collapse(v: View) {
        v.measure(0, 0)
        collapse(v, 400, v.measuredHeight)
    }

    fun expand(v: View, duration: Int, targetHeight: Int) {
        val prevHeight = v.height

        v.visibility = View.VISIBLE
        val valueAnimator = ValueAnimator.ofInt(prevHeight, targetHeight)
        valueAnimator.addUpdateListener { animation ->
            v.layoutParams.height = animation.animatedValue as Int
            v.requestLayout()
        }
        valueAnimator.interpolator = DecelerateInterpolator()
        valueAnimator.duration = duration.toLong()
        valueAnimator.start()
    }

    fun collapse(v: View, duration: Int, targetHeight: Int) {
        val prevHeight = v.height
        val valueAnimator = ValueAnimator.ofInt(prevHeight, targetHeight)
        valueAnimator.interpolator = DecelerateInterpolator()
        valueAnimator.addUpdateListener { animation ->
            v.layoutParams.height = animation.animatedValue as Int
            v.requestLayout()
        }
        valueAnimator.interpolator = DecelerateInterpolator()
        valueAnimator.duration = duration.toLong()
        valueAnimator.start()
    }

    fun toggleVisibilityAnimation(v: View) {
        when (v.visibility) {
            View.VISIBLE -> collapse(v)
            else -> expand(v)
        }
    }
}