package com.example.angryprofs

import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.Color
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
    val visPaint = Paint()
    var finCanon = PointF(canonLongueur, view.screenHeight - hauteur)
    var pointVis1: PointF? = PointF(0f, 0f)    //juste pour avoir une idée d'où on vise
    var pointVis2: PointF? = PointF(0f, 0f)
    var pointVis3: PointF? = PointF(0f, 0f)
    var pointVis4: PointF? = PointF(0f, 0f)
    val gravity = 100
    var vx = 0f
    var v0 = 200f
    var v = v0
    var currentTime = 0.0

    init {
        visPaint.color = Color.RED
    }

    fun update(interval : Double) {
        finCanon.offset(vx * interval.toFloat() , 0f)
        currentTime += interval
        v = v0 * Math.cos(currentTime * Math.PI).toFloat().pow(2) // devrait simuler effet de la jauge, ne fonctionne pas
    }

    fun draw(canvas: Canvas) {
        canonPaint.strokeWidth = largeur * 1.5f
        canvas.drawLine(
            0f, view.screenHeight - 50f, finCanon.x,
            finCanon.y, canonPaint
        )
        canvas.drawCircle(
            0f, view.screenHeight - 50f, canonBaseRadius,
            canonPaint)
        if (!view.fired) {
            canvas.drawCircle(pointVis1!!.x, pointVis1!!.y, 10f, visPaint)
            canvas.drawCircle(pointVis2!!.x, pointVis2!!.y, 10f, visPaint)
            canvas.drawCircle(pointVis3!!.x, pointVis3!!.y, 10f, visPaint)
            canvas.drawCircle(pointVis4!!.x, pointVis4!!.y, 10f, visPaint)
        }
    }

    fun setFinCanon(hauteur: Float) {
        finCanon.set(canonLongueur, hauteur)
    }

    fun align(angle: Double) {
        finCanon.x = (canonLongueur * Math.sin(angle)).toFloat()
        finCanon.y = (-canonLongueur * Math.cos(angle)
                + view.screenHeight
                ).toFloat()

        //on calcule les positions pour les 4 points de visee, formules de tir oblique
        val dx = (Math.sin(angle) * v).toFloat()
        pointVis1 = PointF(finCanon.x + dx, finCanon.y + trajTirOblique(angle, dx))
        pointVis2 = PointF(finCanon.x + 2 * dx, finCanon.y + trajTirOblique(angle, 2 * dx))
        pointVis3 = PointF(finCanon.x + 3 * dx, finCanon.y + trajTirOblique(angle, 3 * dx))
        pointVis4 = PointF(finCanon.x + 4 * dx, finCanon.y + trajTirOblique(angle, 4 * dx))
    }

    private fun trajTirOblique(angle : Double, x: Float) : Float {
        val y = ((gravity * x.pow(2))/(2 * Math.sin(angle).pow(2) * v.pow(2) + 0.0000000000000001) - Math.tan(angle).pow(-1) * x).toFloat()
        //le 0.000000000000001 sert juste a empecher la division par 0
        return y
    }

    fun follow(v: Float) {
        vx += -v
    }

}
