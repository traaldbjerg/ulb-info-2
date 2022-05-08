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
    var finCanon = PointF(canonLongueur, view.screenHeight - 50f)
    var baseCanon = PointF(0f, view.screenHeight - 50f)
    var pointVis1: PointF? = PointF(0f, 0f)    //juste pour avoir une idée d'où on vise
    var pointVis2: PointF? = PointF(0f, 0f)
    var pointVis3: PointF? = PointF(0f, 0f)
    var pointVis4: PointF? = PointF(0f, 0f)
    val gravity = 150
    var vx = 0f
    var v0 = 400f
    var v = v0
    var currentTime = 0.0f
    var currentAngle : Double = 0.0
    var dx = 0f

    init {
        visPaint.color = Color.RED
    }

    fun update(interval : Double) {
        finCanon.offset(vx * interval.toFloat() , 0f)
        baseCanon.offset(vx * interval.toFloat() , 0f)
        currentTime = view.totalElapsedTime.toFloat()
        v = v0 * (Math.cos(currentTime * Math.PI / 4).toFloat()).pow(2) //simule un effet de jauge, il reste encore à la dessiner
        dx = (Math.sin(currentAngle) * v).toFloat()
        pointVis1 = PointF(finCanon.x + dx, finCanon.y + trajTirOblique(currentAngle, dx))
        pointVis2 = PointF(finCanon.x + 2 * dx, finCanon.y + trajTirOblique(currentAngle, 2 * dx))
        pointVis3 = PointF(finCanon.x + 3 * dx, finCanon.y + trajTirOblique(currentAngle, 3 * dx))
        pointVis4 = PointF(finCanon.x + 4 * dx, finCanon.y + trajTirOblique(currentAngle, 4 * dx))
    }

    fun draw(canvas: Canvas) {
        canonPaint.strokeWidth = largeur
        canvas.drawLine( baseCanon.x, baseCanon.y, finCanon.x, finCanon.y, canonPaint)
        canvas.drawCircle(baseCanon.x, baseCanon.y, canonBaseRadius, canonPaint)
        if (!view.fired) {
            canvas.drawCircle(pointVis1!!.x, pointVis1!!.y, 10f, visPaint)
            canvas.drawCircle(pointVis2!!.x, pointVis2!!.y, 10f, visPaint)
            canvas.drawCircle(pointVis3!!.x, pointVis3!!.y, 10f, visPaint)
            canvas.drawCircle(pointVis4!!.x, pointVis4!!.y, 10f, visPaint)
        }
    }

    fun set(hauteur: Float) {
        finCanon.set(canonLongueur, view.screenHeight - hauteur - 50f)
        baseCanon.set(0f, view.screenHeight - 50f)
    }

    fun align(angle: Double) {      //une grosse partie de cette methode est passe dans update afin de permettre la mise a jour en direct des points de visee
        currentAngle = angle  //pas super clean de faire passer par un attribut mais sert a pouvoir garder l'implementation deja ecrite pas le passe
        finCanon.x = (canonLongueur * Math.sin(angle)).toFloat()
        finCanon.y = (-canonLongueur * Math.cos(angle)
                + view.screenHeight
                ).toFloat()
    }

    private fun trajTirOblique(angle : Double, x: Float) : Float {
        val y = ((gravity * x.pow(2))/(2 * Math.sin(angle).pow(2) * v.pow(2) + 0.0000000000000001) - Math.tan(angle).pow(-1) * x).toFloat()
        //le 0.000000000000001 sert juste a empecher la division par 0
        return y
    }

    fun follow(v: Float) {
        vx += -v
    }

    fun reset() {
        finCanon = PointF(canonLongueur, view.screenHeight - 50f)
        baseCanon = PointF(0f, view.screenHeight - 50f)
    }

}
