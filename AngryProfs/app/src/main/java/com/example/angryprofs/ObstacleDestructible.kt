package com.example.angryprofs

import android.graphics.*

class ObstacleDestructible(
    val x: Float,
    val y: Float,
    val length: Float,
    val height: Float,
    val view: CanonView
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
            var move = (interval * vx).toFloat()
            r.offset(move, 0f)
        }
    }

    override fun choc(prof: ProfInter) {
        //rajouter points?
        view.addScore(100)
        view.lesObstacles.remove(this)
    }

    override fun resetCible() {
        vx = 0f
        r.set(x, y,
        x + length, y + height)
        if (!(this in view.lesObstacles)) {
            view.lesObstacles.add(this as Nothing) //je comprends pas pk il veut Nothing et j'ai peur que ca fasse bugger quand on relance le jeu
        }
    }

    override fun follow(v: Float) {
        vx += -v
    }

}
