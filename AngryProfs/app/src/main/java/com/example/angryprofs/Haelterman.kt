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
    override val name = "haelterman"
    override val image =
        view.getResources()
            .getIdentifier(name, "drawable", view.getContext().getPackageName())

    override val bmp = BitmapFactory.decodeResource(view.getResources(), image)

    private var laserTime : Long? = null
    private lateinit var laser : RectF
    private val laserPaint = Paint()
    private var x_laser : Float = x + width
    private var y_laser : Float = (y + 0.25 * 1.2 * width).toFloat() + 40f

    init {
        laserPaint.color = Color.RED
    }

    override fun launch(angle: Double, v : Float, finCanon : PointF) {
        x = finCanon.x
        y = finCanon.y - width / 2 * 1.2f  //pour que le milieu du prof soit a la hauteur du milieu du canon lors du tir
        vx = (v * Math.sin(angle)).toFloat()
        vy = (-v * Math.cos(angle)).toFloat()
        profOnScreen = true
    }

    override fun draw(canvas: Canvas) {
        if (laserTime != null) {
            canvas.drawRect(laser, laserPaint)
        }
        canvas.drawBitmap(bmp, null, r, null)  //en dessinant Haelterman apres le laser, il se superpose a celui-ci, ce qui donne l'impression que le laser sort de ses yeux!

    }

    override fun update(interval: Double) {
        if (profOnScreen) {
            vy += (interval * (gravity)).toFloat()
            x_laser += (interval * vx).toFloat()  //pour changer la position de tir du laser
            y_laser += (interval * vy).toFloat()
            r.offset((interval * vx).toFloat(), (interval * vy).toFloat()) //permet de ne pas changer directement les x et y => plus facile de reset les positions a la fin du tir
            checkImpact(r, true)
            if (currentHP == 0) {
                profOnScreen = false    // on arrete d'afficher le prof
                view.nextTurn()    //on passe au prochain tour
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

    override fun myMove() {     //tire des lasers par les yeux (aurait pu etre le pouvoir de Mr. Gorza)
        laser = RectF(x_laser, y_laser, x_laser + 10000f, y_laser + 10f) //le laser
        laserTime = System.nanoTime()
        checkImpact(laser, false)
        view.playLaserSound()
        view.changeMoveUsed()
    }

    override fun follow(v: Float) {     //change la vitesse du professeur afin de simuler le suivi du prof par la camera
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
                    break   //on part du principe que le prof ne peut toucher qu'un obstacle a la fois
                }
            }
        }
        if (toRemove.size > 0) {
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