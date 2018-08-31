package com.tagnavigatorkotlin.parent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.tagnavigator.framework.JParentFragment
import com.tagnavigatorkotlin.MainActivity
import com.tagnavigatorkotlin.R
import com.tagnavigatorkotlin.child.WineCategoryChildFragment

/**
 * Created by josephwang on 2017/5/8.
 */

class WineCategoryFragment : JParentFragment<MainActivity>() {
    @BindView(R.id.back)
    lateinit var back: RelativeLayout

    @BindView(R.id.status)
    lateinit var status: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val content = inflater.inflate(R.layout.parent_main, container, false)
        ButterKnife.bind(this, content)
        status.text = TAG
        status.setOnClickListener { commitChildFragment(WineCategoryChildFragment()) }
        return content
    }
}
