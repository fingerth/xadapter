package com.fingerth.xadapter

import android.animation.Animator
import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.LinearInterpolator
import com.fingerth.xadapter.AnModel

interface ImAnimation {
    /**
     * 自定义动画
     */
    fun getAnimators(view: View?): Array<Animator> {
        val scaleX = ObjectAnimator.ofFloat(view, "scaleX", 0.9f, 1f).apply {
            duration = 300
            interpolator = LinearInterpolator()
        }
        val scaleY = ObjectAnimator.ofFloat(view, "scaleY", 0.6f, 1f).apply {
            duration = 300
            interpolator = LinearInterpolator()
        }
        return arrayOf(scaleX, scaleY)
    }

    /**
     * AnModel.NORMAL =普通, AnModel.ONLY_DOWN,AnModel.ONLY_UP  =只有上拉或下拉时才有动画
     */
    fun getAnModel(): AnModel = AnModel.NORMAL

    /**
     * 从第几个开始有动画(比如:进来界面展示了5个,前5个不需要动画)
     */
    fun getStart(): Int = -1
}