package com.example.angryprofs

import android.graphics.*
import kotlin.math.pow

class Haelterman(var x: Float,
    var y: Float,
    val view: CanonView
) : Prof {
    override val width = 150f //a changer pour une valeur exacte
    override var r = RectF(x, y, x + width, y + width * 1.2f)
    override var vx = 0f
    override var vy = 0f
    override val gravity = 150
    override var profOnScreen = false
    override var currentHP: Int = 4
    override val name = "Haelterman" //inutile je pense
    override val image =
        view.getResources()
            .getIdentifier("haelterman", "drawable", view.getContext().getPackageName())

    override val bmp = BitmapFactory.decodeResource(view.getResources(), image)

    var laserTime : Long? = null
    lateinit var laser : RectF
    val laserPaint = Paint()
    var x_laser : Float = x + width
    var y_laser : Float = (y + 0.25 * 1.2 * width).toFloat() + 40f
    var waitTime = 0

    init {
        laserPaint.color = Color.RED
    }

    override fun launch(angle: Double, v : Float, finCanon : PointF) {
        x = finCanon.x
        y = finCanon.y - width
        vx = (v * Math.sin(angle)).toFloat()
        vy = (-v * Math.cos(angle)).toFloat()
        profOnScreen = true
    }

    override fun draw(canvas: Canvas) {
        canvas.drawBitmap(bmp, null, r, null)
        if (laserTime != null)
            canvas.drawRect(laser, laserPaint)
    }

    override fun update(interval: Double) {
        if (profOnScreen) {
            vy += (interval * (gravity)).toFloat()
            x_laser += (interval * vx).toFloat()  //pour changer la position de tir du laser
            y_laser += (interval * vy).toFloat()
            r.offset((interval * vx).toFloat(), (interval * vy).toFloat()) //permet de ne pas changer directement les x et y => plus facile de reset les positions a la fin du tir
            //il faut ecrire le code de detection de chocs ici
            checkImpact(r, true)
            if (currentHP == 0) {
                profOnScreen = false
            }
            if (laserTime != null) {
                if (System.nanoTime().toDouble() > (laserTime!! + 1f * 10.toDouble()
                        .pow(8))
                ) {
                    laserTime = null
                }
            }
        }
    }

    override fun resetProf() {
        profOnScreen = false
    }

    override fun myMove() {     //tire des lasers par les yeux (aurait pu etre le pouvoir de Mr. Gorza)
        laser = RectF(x_laser, y_laser, x_laser + 10000f, y_laser + 10f) //le laser
        laserTime = System.nanoTime()
        checkImpact(laser, false)
        view.playLaserSound()
    }

    override fun follow(v: Float) {
        vx += -v
    }

    override fun checkImpact(hitbox: RectF, vuln : Boolean) {       //verifie s'il y a une intersection avec un obstacle/etudiant
        var toRemove : MutableList<Obstacle> = mutableListOf()      //important de passer par cette liste, sinon on modifie la liste en meme temps qu'on l'itere -> ConcurrentModificationException
        for (d in view.lesObstacles) {
            if (RectF.intersects(hitbox, d.r)) {
                if (d.choc(this, vuln))
                    toRemove.add(d)
                if (vuln) {
                    currentHP -= 1
                    break
                }
            }
        }
        if (toRemove.size > 0) {
            while (view.drawRunning) {  //on attend que le draw s'arrete pour modifier la liste des obstacles, sinon on risque CME PAS OPTIMISE ON PERD PLEIN DE TEMPS DE CALCUL
                waitTime += 1
            }
            waitTime = 0
            view.removeObstacles(toRemove)
        }

    }

    override fun bounce(axe: String, interval : Double) {
        when (axe) {
            "y" -> {vy = vy * -1
                    r.offset(0f, (vy * interval).toFloat() * 2)}
            "x" -> {vx = vx * -1 - 2 * view.cameraSpeed  //il faut incrementer de -2 cameraSpeed, si la camera bouge deja alors vx = 0 donc faire * -1 ne change rien
                    r.offset((vx * interval).toFloat() * 2, 0f)
                    view.cameraFollows(vx)}
        }
    }

}