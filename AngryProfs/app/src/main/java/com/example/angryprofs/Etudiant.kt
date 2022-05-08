package com.example.angryprofs

import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.RectF

class Etudiant(name: String,
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

    val image =
        view.getResources()
            .getIdentifier(name, "drawable", view.getContext().getPackageName())

    val bmp = BitmapFactory.decodeResource(view.getResources(), image)

    init {
        r = RectF(x, y,
        x + length, y + height)
        vx = 0f
        lastInterval = 0.0
    }

    override fun draw(canvas: Canvas) {
        canvas.drawBitmap(bmp, null, r, null)
    }

    override fun choc(prof: Prof, vuln : Boolean) : Boolean {
        view.addScore(1000)
        return true //represente si on retire l'objet des obstacles, important pour certains profs sinon ConcurrentModificationException
    }

}