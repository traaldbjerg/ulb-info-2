package com.example.angryprofs

import android.graphics.*

class Etudiant(
    var x: Float,
    var y: Float,
    var length: Float,
    var height: Float,
    var view: CanonView
) : ObstacleInter {

    override var r = RectF(x, y,
        x + length, y + height)
    override val paint = Paint()
    override var vx = 0f

    override fun draw(canvas: Canvas) {
        paint.color = Color.GRAY
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
            val move = (interval * vx).toFloat()
            r.offset(move, 0f)
        }
    }

    override fun choc(prof: ProfInter) {
        //rajouter points?
        view.addScore(1000)
        view.lesObstacles.remove(this)
        r = RectF(0f, 0f, 0f, 0f)
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