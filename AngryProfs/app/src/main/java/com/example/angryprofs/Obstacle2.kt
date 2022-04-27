package com.example.angryprofs

import android.graphics.*

class Obstacle2(
    var cibleDistance: Float,
    var cibleDebut: Float,
    var cibleFin: Float,
    var cibleVitesseInitiale: Float,
    var width: Float,
    var view: CanonView
) {
    val CIBLE_PIECES = 10
    val cible = RectF(
        cibleDistance, cibleDebut,
        cibleDistance + width, cibleFin
    )
    var cibleTouchee = BooleanArray(CIBLE_PIECES)
    val ciblePaint = Paint()
    var longueurPiece = 0f
    var cibleVitesse = cibleVitesseInitiale
    var nbreCiblesTouchees = 0

    fun draw(canvas: Canvas) {
        val currentPoint = PointF()
        currentPoint.x = cible.left
        currentPoint.y = cible.top
        for (i in 0 until CIBLE_PIECES) {
            if (!cibleTouchee[i]) {
                if (i % 3 == 0) {
                    ciblePaint.color = Color.BLUE
                } else if (i % 3 == 1) {
                    ciblePaint.color = Color.YELLOW
                } else {
                    ciblePaint.color = Color.GREEN
                }
                canvas.drawRect(
                    currentPoint.x, currentPoint.y, cible.right,
                    currentPoint.y + longueurPiece, ciblePaint
                )
            }
            currentPoint.y += longueurPiece
        }
    }

    fun setRect() {
        cible.set(
            cibleDistance, cibleDebut,
            cibleDistance + width, cibleFin
        )
        cibleVitesse = cibleVitesseInitiale
        longueurPiece = (cibleFin - cibleDebut) / CIBLE_PIECES
    }

    fun update(interval: Double) {
        var up = (interval * cibleVitesse).toFloat()
        cible.offset(0f, up)
        if (cible.top < 0 || cible.bottom > view.screenHeight) {
            cibleVitesse *= -1f
            up = (interval * 3 * cibleVitesse).toFloat()
            cible.offset(0f, up)
        }
    }

    fun detectChoc(balle: Prof) {
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

    fun resetCible() {
        for (i in 0 until CIBLE_PIECES)
            cibleTouchee[i] = false
        nbreCiblesTouchees = 0
        cibleVitesse = cibleVitesseInitiale
        cible.set(
            cibleDistance, cibleDebut, cibleDistance + width,
            cibleFin
        )
    }

}