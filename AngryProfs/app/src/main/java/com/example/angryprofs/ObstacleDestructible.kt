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
    }

    override fun choc(prof: Prof, vuln : Boolean) : Boolean {
        view.addScore(100)
        return true //si on retire l'objet des obstacles, important pour certains profs sinon ConcurrentModificationException
    }

}