package com.example.angryprofs

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.RectF

class Terrain(
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
        paint.color = Color.GREEN
        r = RectF(x, y,
        x + length, y + height)
        vx = 0f
        lastInterval = 0.0
    }

    override fun choc(prof: Prof, vuln : Boolean) : Boolean {
        if (vuln) {
            if (prof.r.bottom - prof.vy * lastInterval <= r.top || prof.r.top - prof.vy * lastInterval >= r.bottom) //on verifie si le prof vient de traverser le haut ou le bas de l'obstacle
                prof.bounce("y", lastInterval)
            else {
                prof.bounce("x", lastInterval)
            }
        }
        return false //represente si on retire l'objet des obstacles, important pour certains profs sinon ConcurrentModificationException
    }

}