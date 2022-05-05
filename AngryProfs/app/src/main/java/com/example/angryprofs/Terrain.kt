package com.example.angryprofs

import android.graphics.*

class Terrain(
    val x: Float,
    val y: Float,
    val length: Float,
    val height: Float,
    val view: CanonView
) : ObstacleInter {

    override val r = RectF(x, y,
        x + length, y + height)
    override val paint = Paint()
    override var vx = 0f

    override fun draw(canvas: Canvas) {
        paint.color = Color.GREEN
        canvas.drawRect(
            r.left, r.top, r.right,
            r.bottom, paint
        )
    }

    override fun setRect() {
        r.set(x, y,
        x + length, y + height)
    }

    override fun update(interval: Double) {
        if (vx != 0f) {
            var move = (interval * vx).toFloat()
            r.offset(move, 0f)
        }
    }

    override fun choc(prof: ProfInter) {
        prof.vy *= -1f
    }

    override fun resetCible() {
        vx = 0f
        r.set(x, y,
        x + length, y + height)
    }

    override fun follow(v: Float) {
        vx += -v
    }

}