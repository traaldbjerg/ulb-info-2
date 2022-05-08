package com.example.angryprofs

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF

abstract class Obstacle(
    open val x: Float,
    open val y: Float,
    open val length: Float,
    open val height: Float,
    open val view: CanonView
) {
    open var r = RectF(x, y,
        x + length, y + height)
    open val paint = Paint()
    open var vx = 0f

    open fun draw(canvas: Canvas) {
        canvas.drawRect(
            r.left, r.top, r.right,
            r.bottom, paint
        )
    }

    open fun setRect() {
        r.set(x, y,
        x + length, y + height)
    }

    open fun update(interval: Double) {
        if (vx != 0f) {
            var move = (interval * vx).toFloat()
            r.offset(move, 0f)
        }
    }

    abstract fun choc(prof: Prof, vuln : Boolean) : Boolean

    open fun follow(v: Float) {
        vx += -v
    }

    open fun reset() {
        r = RectF(x, y,
        x + length, y + height)
        vx = 0f
    }

}