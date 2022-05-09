package com.example.angryprofs

import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF

class ObstacleDestructible(
    override val x: Float,
    override val y: Float,
    override val length: Float,
    override val height: Float,
    override val view: CanonView
) : Obstacle(
    x,
    y,
    length,
    height,
    view
) {

    init {
        paint.color = Color.GRAY
        r = RectF(x, y,
        x + length, y + height)
        vx = 0f
        lastInterval = 0.0
    }

    override fun choc(prof: Prof, vuln : Boolean) : Boolean {
        if (vuln) {
            if (prof.r.bottom - prof.vy * lastInterval <= r.top || prof.r.top - prof.vy * lastInterval >= r.bottom) //on verifie si le prof vient de traverser le haut ou le bas de l'obstacle
                //pas besoin de differencier les 2 cas car s'il traverse le bas, vitesse y negative et s'il traverse le haut, vitesse y positive
                prof.bounce("y", lastInterval)
            else
                prof.bounce("x", lastInterval)
        }

        view.addScore(100)
        view.playObstacleSound()
        return true //si on retire l'objet des obstacles, important pour certains profs sinon ConcurrentModificationException
    }

}