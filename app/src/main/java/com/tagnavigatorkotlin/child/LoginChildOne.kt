package com.tagnavigatorkotlin.child

/**
 * Created by JosephWang on 2018/8/31.
 */


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.tagnavigator.framework.JChildFragment
import com.tagnavigatorkotlin.MainActivity
import com.tagnavigatorkotlin.R
import com.tagnavigatorkotlin.parent.LoginFragment

/**
 * Created by josephwang on 2017/5/8.
 */

class LoginChildOne : JChildFragment<LoginFragment>() {
    @BindView(R.id.back)
    lateinit var back: RelativeLayout

    @BindView(R.id.status)
    lateinit var status: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val content = inflater.inflate(R.layout.parent_main, container, false)
        ButterKnife.bind(this, content)
        status.text = TAG
        back.setOnClickListener { backToPreviousFragment() }
        var main =getJTabActivity<MainActivity>()
        Log.d(TAG, "LoginChildOne " + main.isReclaim)
        status.setOnClickListener { commitChildFragment(LoginChildTwo()) }
        return content
    }

}

