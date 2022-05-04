package com.example.angryprofs

import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.PointF
import kotlin.math.pow

class Canon(
    var canonBaseRadius: Float,
    var canonLongueur: Float,
    hauteur: Float,
    var largeur: Float,
    val view: CanonView
) {
    val canonPaint = Paint()
    var finCanon = PointF(canonLongueur, hauteur)
    var pointVis1: PointF? = PointF(0f, 0f)
    var pointVis2: PointF? = PointF(0f, 0f)
    var pointVis3: PointF? = PointF(0f, 0f)
    var pointVis4: PointF? = PointF(0f, 0f)
    val gravity = 1800
    var fired = false


    fun draw(canvas: Canvas) {
        canonPaint.strokeWidth = largeur * 1.5f
        canvas.drawLine(
            0f, view.screenHeight / 2, finCanon.x,
            finCanon.y, canonPaint
        )
        canvas.drawCircle(
            0f, view.screenHeight / 2, canonBaseRadius,
            canonPaint)
        if (!fired) {
            canvas.drawCircle(pointVis1!!.x, pointVis1!!.y, 5f, canonPaint)
            canvas.drawCircle(pointVis2!!.x, pointVis2!!.y, 5f, canonPaint)
            canvas.drawCircle(pointVis3!!.x, pointVis3!!.y, 5f, canonPaint)
            canvas.drawCircle(pointVis4!!.x, pointVis4!!.y, 5f, canonPaint)
        }
    }

    fun setFinCanon(hauteur: Float) {
        finCanon.set(canonLongueur, hauteur)
    }

    fun align(angle: Double, v0: Float) {
        finCanon.x = (canonLongueur * Math.sin(angle)).toFloat()
        finCanon.y = (-canonLongueur * Math.cos(angle)
                + view.screenHeight / 2).toFloat()

        //on calcule les positions pour les 4 points de visee, formules de tir oblique
        val dx = (Math.cos(angle) * v0).toFloat()
        pointVis1 = PointF(finCanon.x + dx, finCanon.y + trajTirOblique(angle, v0, dx))
        pointVis2 = PointF(finCanon.x + 2 * dx, finCanon.y + trajTirOblique(angle, v0, 2 * dx))
        pointVis3 = PointF(finCanon.x + 3 * dx, finCanon.y + trajTirOblique(angle, v0, 3 * dx))
        pointVis4 = PointF(finCanon.x + 4 * dx, finCanon.y + trajTirOblique(angle, v0, 4 * dx))

    }

    private fun trajTirOblique(angle : Double, v0 : Float, x: Float) : Float {
        val y = ((gravity * x.pow(2))/(2 * Math.cos(angle).pow(2) * v0.pow(2)) - Math.tan(angle) * x).toFloat()
        return y
    }

    fun follow(v: Float) {

    }
}
