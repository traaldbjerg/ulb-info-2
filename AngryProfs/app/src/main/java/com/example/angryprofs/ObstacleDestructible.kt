package com.example.angryprofs

import android.graphics.*

class ObstacleDestructible(
    var x: Float,
    var y: Float,
    var length: Float,
    var height: Float,
    var view: CanonView
) : ObstacleInter {

    override val r = RectF(x, y,
        x + length, y + height)
    override val paint = Paint()
    override val longueurPiece = 300f
    override var vx = 0f

    override fun draw(canvas: Canvas) {
        val currentPoint = PointF()
        currentPoint.x = r.left
        currentPoint.y = r.top

        paint.color = Color.GRAY
        canvas.drawRect(
            currentPoint.x, currentPoint.y, r.right,
            currentPoint.y + longueurPiece, paint
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
        view.score += 500
        prof.lesObstacles.remove(this)
    }

    override fun resetCible() {
        vx = 0f
        r.set(x, y,
        x + length, y + height)
    }

    override fun follow(v: Float) {
        vx += v
    }

}