package com.example.angryprofs

import android.graphics.*

class Terrain(
    val x: Float,
    val y: Float,
    val length: Float,
    val height: Float,
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

        paint.color = Color.GREEN
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
        /*val section = ((balle.profP.y - cible.top) / longueurPiece).toInt()
        if (section >= 0 && section < CIBLE_PIECES && !cibleTouchee[section]) {
            cibleTouchee[section] = true
            balle.resetProf()
            view.increaseTimeLeft()
            if (++nbreCiblesTouchees == CIBLE_PIECES) view.gameOver()
            view.playCibleSound()
        } else {
            view.reduceTimeLeft()
            balle.resetProf()
        }*/
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