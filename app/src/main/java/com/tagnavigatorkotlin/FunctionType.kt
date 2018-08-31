package com.tagnavigatorkotlin

/**
 * Created by JosephWang on 2018/8/31.
 */


import android.widget.RadioButton
import com.tagnavigator.framework.JActivity

/**
 * Created by josephwang on 2017/5/8.
 */

enum class FunctionType {
    MainPage, Wine, Discover, MyList, Account;

    companion object {

        var LastFunctionType = FunctionType.MainPage

        fun getStatus(index: Int): FunctionType {
            return FunctionType.values()[index]
        }

        fun getTypeById(id: Int): FunctionType? {
            var type: FunctionType? = null
            if (id == R.id.main_page_button || id == R.id.main_page_container) {
                type = MainPage
            } else if (id == R.id.wine_button || id == R.id.wine_container) {
                type = Wine
            } else if (id == R.id.discover_button || id == R.id.discover_container) {
                type = Discover
            } else if (id == R.id.mylist_button || id == R.id.mylist_container) {
                type = MyList
            } else if (id == R.id.account_button || id == R.id.account_container) {
                type = Account
            }
            return type
        }

        fun getLastedChecker(act: JActivity): RadioButton {
            return getCheckerByType(act, LastFunctionType)
        }

        fun getCheckerByType(act: JActivity, type: FunctionType): RadioButton {
            when (type) {
                MainPage -> return act.getView(R.id.main_page_button)
                Wine -> return act.getView(R.id.wine_button)
                Discover -> return act.getView(R.id.discover_button)
                MyList -> return act.getView(R.id.mylist_button)
                Account -> return act.getView(R.id.account_button)
                else -> throw IllegalArgumentException("You have to use one of Enum")
            }
        }

        @Synchronized
        fun toogleFunction(act: JActivity,
                           checker: RadioButton): FunctionType? {
            //        RadioButton lastChecker = getCheckerByType(act, LastFunctionType);
            //        FunctionType currentType = getTypeById(checker.getId());
            //        lastChecker.setChecked(false);
            //        checker.setChecked(true);
            //        LastFunctionType = currentType;

            val currentType = getTypeById(checker.id)
            checker.isChecked = true
            for (i in 0 until FunctionType.values().size) {
                val others = getCheckerByType(act, values()[i])
                if (others.id != checker.id) {
                    others.isChecked = false
                }
            }
            LastFunctionType = currentType!!
            return currentType
        }

    }
}

