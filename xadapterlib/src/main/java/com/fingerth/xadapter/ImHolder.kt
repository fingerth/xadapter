package com.fingerth.xadapter

import android.view.View
import androidx.annotation.IdRes

interface ImHolder {
    fun <T : View> getView(@IdRes viewId: Int): T
    fun setText(@IdRes viewId: Int, text: String)
    fun setImageResource(@IdRes viewId: Int, drawableId: Int)
}