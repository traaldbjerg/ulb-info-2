package com.example.angryprofs

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF

interface ObstacleInter {
    val r : RectF
    val paint : Paint
    var vx: Float

    fun draw(canvas: Canvas)

    fun setRect()

    fun update(interval: Double)

    fun choc(prof: ProfInter)

    fun resetCible()

    fun follow(v: Float)

}