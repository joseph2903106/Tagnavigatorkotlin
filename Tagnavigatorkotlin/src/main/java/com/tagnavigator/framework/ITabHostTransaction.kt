package com.tagnavigator.framework

interface ITabHostTransaction {
    fun commitFragmentTransaction(fragment: JFragment, type: FragmentAnimationType)
}
