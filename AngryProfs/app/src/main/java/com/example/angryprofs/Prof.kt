package com.example.angryprofs

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF

class Prof(var view: CanonView, val obstacle: Obstacle, val cible: Cible, var prof : Int) {
    var profP = PointF()
    var profVitesse = 0f
    var profVitesseX = 0f
    var profVitesseY = 0f
    var profOnScreen = true
    var profSize = 0f
    var profPaint = Paint()
    var currentHP : Int = 0
    val hpList = listOf(4,4,4,4)
    val nameList = listOf("Marc Haelterman", "Jean-Marc Sparenberg", "Hugues Bersini", "Philippe Bogaerts") //probablement a rajouter dans un string.xml

    val gravity = 1800

    init {
        profPaint.color = Color.RED
    }

    fun launch(angle: Double) {
        profP.x = profSize
        profP.y = view.screenHeight / 2f
        profVitesseX = (profVitesse * Math.sin(angle)).toFloat()
        profVitesseY = (-profVitesse * Math.cos(angle)).toFloat()
        profOnScreen = true
    }

    fun draw(canvas: Canvas) {
        canvas.drawCircle(
            profP.x, profP.y, profSize,
            profPaint
        )
    }

    fun update(interval: Double) {
        if (profOnScreen) {
            profVitesseY += (interval * (gravity)).toFloat()
            profP.x += (interval * profVitesseX).toFloat()
            profP.y += (interval * profVitesseY).toFloat()
            /* Vérifions si la balle touche l'obstacle ou pas */
            if (profP.x + profSize > obstacle.obstacle.left && profP.x - profSize < obstacle.obstacle.right
                && profP.y + profSize > obstacle.obstacle.top
                && profP.y - profSize < obstacle.obstacle.bottom
            ) {
                profVitesseX *= -1
                profP.offset((3 * profVitesseX * interval).toFloat(), 0f)
                view.reduceTimeLeft()
                view.playObstacleSound()
            }
            // Si elle sort de l'écran
            else if (profP.x + profSize > view.screenWidth
                || profP.x - profSize < 0
            ) {
                profOnScreen = false
            } else if (profP.y + profSize > view.screenHeight
                || profP.y - profSize < 0
            ) {
                profOnScreen = false
            } else if (profP.x + profSize > cible.cible.left
                && profP.y + profSize > cible.cible.top
                && profP.y - profSize < cible.cible.bottom
            ) {
                cible.detectChoc(this)

                /* Pour l'instant rien ne se passe lorsque balle heurte la cible */
            }
        }
    }

    fun resetProf() {
        profOnScreen = false
    }
}
