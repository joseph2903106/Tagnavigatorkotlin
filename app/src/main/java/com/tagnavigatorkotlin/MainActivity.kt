package com.tagnavigatorkotlin

/**
 * Created by JosephWang on 2018/8/17.
 */

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import butterknife.BindView
import butterknife.ButterKnife
import com.tagnavigator.framework.JTabActivity
import com.tagnavigator.util.JDialog
import com.tagnavigatorkotlin.parent.*

class MainActivity : JTabActivity() {

    @BindView(R.id.main_page_button)
    lateinit var mainPageButton: RadioButton

    @BindView(R.id.wine_button)
    lateinit var wineButton: RadioButton

    @BindView(R.id.discover_button)
    lateinit var discoverButton: RadioButton

    @BindView(R.id.mylist_button)
    lateinit var mylistButton: RadioButton

    @BindView(R.id.account_button)
    lateinit var accountButton: RadioButton

    override val fragmentId: Int
        get() = R.id.content

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_tab)
        ButterKnife.bind(this)
        initListener()
    }

    private fun initListener() {
        mainPageButton.setOnClickListener(clickListener)
        wineButton.setOnClickListener(clickListener)
        discoverButton.setOnClickListener(clickListener)
        mylistButton.setOnClickListener(clickListener)
        accountButton.setOnClickListener(clickListener)
        discoverButton.performClick()
    }

    private val clickListener = View.OnClickListener { v ->
        if (v is RadioButton) {
            val type = FunctionType.toogleFunction(this@MainActivity, v)
            when (type) {
                FunctionType.MainPage -> commitCurrentFragmentByParent(MainPageFragment())
                FunctionType.Wine -> commitCurrentFragmentByParent(WineCategoryFragment())
                FunctionType. Discover -> commitCurrentFragmentByParent(DiscoveryMainFragment())
                FunctionType.MyList -> commitCurrentFragmentByParent(MyListMainFragment())
                FunctionType.Account -> commitCurrentFragmentByParent(LoginFragment())
            }
        }
    }

    override fun showExitReminid() {
        JDialog.showMessage(this, "Message", "Exit or not ?", "confirm", "cancel",
                DialogInterface.OnClickListener { dialog, which ->
                    dialog.dismiss()
                    exitApp()
                },
                DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
    }
}
