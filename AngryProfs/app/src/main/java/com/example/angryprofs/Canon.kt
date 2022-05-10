package com.example.angryprofs

import android.graphics.*
import kotlin.math.pow

class Canon(
    var canonBaseRadius: Float,
    var canonLongueur: Float,
    var largeur: Float,
    private val view: CanonView
) {
    private val canonPaint = Paint()
    private val visPaint = Paint()
    var finCanon = PointF(canonLongueur, view.screenHeight - 150f)
    var baseCanon = PointF(0f, view.screenHeight - 100f)
    private var pointVis1: PointF = PointF(0f, 0f)    //juste pour avoir une idée d'où on vise
    private var pointVis2: PointF = PointF(0f, 0f)
    private var pointVis3: PointF = PointF(0f, 0f)
    private var pointVis4: PointF = PointF(0f, 0f)
    private val gravity = 150
    private var vx = 0f
    private var v0 = 400f    //vitesse maximale que peut atteindre le projectile
    var v = v0      //vitesse a laquelle sera tire le projectile, mise a jour a chaque appel de update(interval)
    private var currentTime = 0.0f      //permet de mettre a jour l'effet de jauge dans la methode update(interval)
    private var currentAngle : Double = 0.0   //permet de sauvegarder un angle afin que les points de visee puissent utiliser les formules de tir oblique dans update(interval)
    private var dx = 0f    //represente l'espacement horizontal entre les points de visee (depend de la vitesse v)
    private var jauge = RectF(50f, 400f, 80f, 400f)
    private val jauge_height = 150f

    init {
        visPaint.color = Color.RED
        align(Math.PI / 4)
        update(0.0) //pour que les points de visée commencent au bon endroit
    }

    fun update(interval : Double) {     //sert a mettre a jour les positions des 4 points de visee et la position du canon lorsqu'il faut faire le suivi de camera
        finCanon.offset(vx * interval.toFloat() , 0f)
        baseCanon.offset(vx * interval.toFloat() , 0f)
        currentTime += interval.toFloat()
        v = v0 * Math.abs((Math.sin(currentTime * Math.PI / 6).toFloat())) //simule un effet de jauge, il reste encore à la dessiner
        jauge = RectF(50f, 550f - Math.abs((Math.sin(currentTime * Math.PI / 6).toFloat())) * jauge_height, 80f, 550f)
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
            canvas.drawCircle(pointVis1.x, pointVis1.y, 10f, visPaint)
            canvas.drawCircle(pointVis2.x, pointVis2.y, 10f, visPaint)
            canvas.drawCircle(pointVis3.x, pointVis3.y, 10f, visPaint)
            canvas.drawCircle(pointVis4.x, pointVis4.y, 10f, visPaint)
            canvas.drawRect(jauge, visPaint)
        }
    }

    fun set() {
        finCanon.set(canonLongueur - 50f, view.screenHeight - 150f)
        baseCanon.set(0f, view.screenHeight - 100f)
    }

    fun align(angle: Double) {      //une grosse partie de cette methode est passe dans update afin de permettre la mise a jour en direct des points de visee
        currentAngle = angle  //pas super clean de faire passer par un attribut mais sert a pouvoir garder l'implementation deja ecrite pas le passe
        finCanon.x = (canonLongueur * Math.sin(angle)).toFloat()
        finCanon.y = (-canonLongueur * Math.cos(angle)
                + view.screenHeight - 100f
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
        finCanon = PointF(canonLongueur - 50f, view.screenHeight - 150f)
        baseCanon = PointF(0f, view.screenHeight - 100f)
        vx = 0f
        align(Math.PI / 4)
        update(0.0)
    }

}
