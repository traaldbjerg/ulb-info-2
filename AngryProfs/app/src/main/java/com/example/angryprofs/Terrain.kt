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
    }

    override fun choc(prof: Prof, vuln : Boolean) : Boolean {
        if (vuln)
            prof.vy *= -1
        return false //si on retire l'objet des obstacles, important pour certains profs sinon ConcurrentModificationException
    }

}