package com.tagnavigator.framework


import android.os.Bundle
import android.os.Parcelable
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v4.view.PagerAdapter
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import com.tagnavigator.util.JLog
import java.util.*

/**
 * Implementation of [PagerAdapter] that
 * uses a [Fragment] to manage each page. This class also handles
 * saving and restoring of fragment's state.
 *
 *
 *
 * This version of the pager is more useful when there are a large number
 * of pages, working more like a list view.  When pages are not visible to
 * the user, their entire fragment may be destroyed, only keeping the saved
 * state of that fragment.  This allows the pager to hold on to much less
 * memory associated with each visited page as compared to
 * [android.support.v4.app.FragmentPagerAdapter] at the cost of potentially more overhead when
 * switching between pages.
 *
 *
 *
 * When using FragmentPagerAdapter the host ViewPager must have a
 * valid ID set.
 *
 *
 *
 * Subclasses only need to implement [.getItem]
 * and [.getCount] to have a working adapter.
 *
 *
 *
 * Here is an example implementation of a pager containing fragments of
 * lists:
 *
 *
 * {@sample development/samples/Support13Demos/src/com/example/android/supportv13/app/FragmentStatePagerSupport.java
 * * complete}
 *
 *
 *
 * The `R.layout.fragment_pager` resource of the top-level fragment is:
 *
 *
 * {@sample development/samples/Support13Demos/res/layout/fragment_pager.xml
 * * complete}
 *
 *
 *
 * The `R.layout.fragment_pager_list` resource containing each
 * individual fragment's layout is:
 *
 *
 * {@sample development/samples/Support13Demos/res/layout/fragment_pager_list.xml
 * * complete}
 */
open abstract class FixedFragmentStatePagerAdapter : PagerAdapter {

    private val mFragmentManager: FragmentManager
    private var mCurTransaction: FragmentTransaction? = null

    private val mSavedState = ArrayList<Fragment.SavedState?>()
    private var mSavedFragmentTags = ArrayList<String?>()
    private var mFragments = ArrayList<Fragment?>()
    private var mCurrentPrimaryItem: Fragment? = null
    private var title: Array<String?>? = null

    constructor(fragmenet: JFragment, title: Array<String?>) {
        mFragmentManager = fragmenet.childFragmentManager
        this.title = title
        JLog.v(TAG, "FixedFragmentStatePagerAdapter title.size #${title!!.size}:")
    }

    constructor(activity: FragmentActivity?, title: Array<String?>) {
        mFragmentManager = activity!!.supportFragmentManager
        this.title = title
        JLog.v(TAG, "FixedFragmentStatePagerAdapter title.size #${title!!.size}:")
    }

    constructor(fragmenet: JFragment, titles: ArrayList<String>) {
        mFragmentManager = fragmenet.childFragmentManager

        val array = arrayOfNulls<String>(titles.size)
        titles.toArray(array)
        this.title = array

        JLog.v(TAG, "FixedFragmentStatePagerAdapter title.size #${title!!.size}:")
    }

    constructor(activity: FragmentActivity?, titles: ArrayList<String>) {
        mFragmentManager = activity!!.supportFragmentManager
        val array = arrayOfNulls<String>(titles.size)
        titles.toArray(array)
        this.title = array
        JLog.v(TAG, "FixedFragmentStatePagerAdapter title.size #${title!!.size}:")
    }

    /**
     * Return the Fragment associated with a specified position.
     */
    abstract fun getItem(position: Int): Fragment

    fun getTag(position: Int): String? {
        return null
    }

    override fun startUpdate(container: ViewGroup) {}

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        // If we already have this item instantiated, there is nothing
        // to do.  This can happen when we are restoring the entire pager
        // from its saved state, where the fragment manager has already
        // taken care of restoring the fragments we previously had instantiated.

        JLog.d(TAG, "Adding item #$position:")
        print("Adding item " + position)
        if (mFragments.size > position) {
            val f = mFragments[position]
            if (f != null) {
                return f
            }
        }

        if (mCurTransaction == null) {
            mCurTransaction = mFragmentManager.beginTransaction()
        }

        val fragment = getItem(position)
        val fragmentTag = getTag(position)

        JLog.v(TAG, "Adding item #$position: f=$fragment t=$fragmentTag")

        if (mSavedState.size > position) {
            val savedTag = mSavedFragmentTags[position]
            if (TextUtils.equals(fragmentTag, savedTag)) {
                val fss = mSavedState[position]
                if (fss != null) {
                    fragment.setInitialSavedState(fss)
                }
            }
        }
        while (mFragments.size <= position) {
            mFragments.add(null)
        }
        fragment.setMenuVisibility(false)
        fragment.userVisibleHint = false
        mFragments[position] = fragment
        mCurTransaction!!.add(container.id, fragment, fragmentTag)

        return fragment
    }

    open fun <T : Fragment> getFragment(position: Int): T? {
        return if (mFragments.size > position) {
            mFragments[position] as T
        } else null
    }

    override fun destroyItem(container: ViewGroup, position: Int, data: Any) {
        val fragment = data as Fragment

        if (mCurTransaction == null) {
            mCurTransaction = mFragmentManager.beginTransaction()
        }
        JLog.d(TAG, "Removing item #" + position + ": f=" + fragment
                + " v=" + fragment.view + " t=" + fragment.tag)
        while (mSavedState.size <= position) {
            mSavedState.add(null)
            mSavedFragmentTags.add(null)
        }
        mSavedState[position] = mFragmentManager.saveFragmentInstanceState(fragment)
        mSavedFragmentTags.set(position, fragment.javaClass.simpleName)
        mFragments.set(position, null)

        mCurTransaction!!.remove(fragment)
    }

    override fun setPrimaryItem(container: ViewGroup, position: Int, data: Any) {
        val fragment = data as Fragment
        if (fragment !== mCurrentPrimaryItem) {
            if (mCurrentPrimaryItem != null) {
                mCurrentPrimaryItem!!.setMenuVisibility(false)
                mCurrentPrimaryItem!!.userVisibleHint = false
            }
            if (fragment != null) {
                fragment.setMenuVisibility(true)
                fragment.userVisibleHint = true
            }
            mCurrentPrimaryItem = fragment
        }
    }

    override fun getCount(): Int {
        return title!!.size
    }

    override fun finishUpdate(container: ViewGroup) {
        if (mCurTransaction != null) {
            mCurTransaction!!.commitAllowingStateLoss()
            mCurTransaction = null
            mFragmentManager.executePendingTransactions()
        }
    }

    override fun isViewFromObject(view: View, data: Any): Boolean {
        return (data as Fragment).view === view
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return title!![position]
    }

    override fun saveState(): Parcelable? {
        var state: Bundle? = null
        if (mSavedState.size > 0) {
            state = Bundle()
            val fss = arrayOfNulls<Fragment.SavedState>(mSavedState.size)
            mSavedState.toArray(fss)
            state.putParcelableArray("states", fss)
            state.putStringArrayList("tags", mSavedFragmentTags)
        }
        for (i in mFragments.indices) {
            val f = mFragments[i]
            if (f != null) {
                if (state == null) {
                    state = Bundle()
                }
                val key = "f$i"
                mFragmentManager.putFragment(state, key, f)
            }
        }
        return state
    }

    override fun restoreState(state: Parcelable?, loader: ClassLoader?) {
        if (state != null) {
            val bundle = state as Bundle?
            bundle!!.classLoader = loader
            val fss = bundle.getParcelableArray("states")
            mSavedState.clear()
            mFragments.clear()

            val tags = bundle.getStringArrayList("tags")
            if (tags != null) {
                mSavedFragmentTags = tags
            } else {
                mSavedFragmentTags.clear()
            }
            if (fss != null) {
                for (i in fss.indices) {
                    mSavedState.add(fss[i] as Fragment.SavedState)
                }
            }
            val keys = bundle.keySet()
            for (key in keys) {
                if (key.startsWith("f")) {
                    val index = Integer.parseInt(key.substring(1))
                    val f = mFragmentManager.getFragment(bundle, key)
                    if (f != null) {
                        while (mFragments.size <= index) {
                            mFragments.add(null)
                        }
                        f.setMenuVisibility(false)
                        mFragments[index] = f
                    } else {
                        JLog.w(TAG, "Bad fragment at key $key")
                    }
                }
            }
        }
    }

    companion object {
        private val TAG = FixedFragmentStatePagerAdapter::class.java.simpleName

    }
}
