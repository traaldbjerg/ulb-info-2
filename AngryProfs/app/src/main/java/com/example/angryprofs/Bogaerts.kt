package com.example.angryprofs

import android.graphics.*
import android.media.AudioAttributes
import android.media.SoundPool
import android.util.SparseIntArray
import kotlin.math.pow

class Bogaerts(var x: Float,
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
    override val name = "Bersini"
    override val image =
        view.getResources()
            .getIdentifier("bogaerts", "drawable", view.getContext().getPackageName())

    override val bmp = BitmapFactory.decodeResource(view.getResources(), image)
    lateinit var explosion : RectF
    val explos_image : Int = view.getResources().getIdentifier("explosion", "drawable", view.getContext().getPackageName())
    val explos_bmp = BitmapFactory.decodeResource(view.getResources(), explos_image)
    var explosTime : Long? = null
    val explos_width = 250f
    var x_explosion : Float = x + width / 2 - explos_width / 2
    var y_explosion : Float = y + width / 2 * 1.2f - explos_width / 2
    var waitTime = 0

    override fun launch(angle: Double, v : Float, finCanon : PointF) {
        x = finCanon.x
        y = finCanon.y - width / 2
        vx = (v * Math.sin(angle)).toFloat()
        vy = (-v * Math.cos(angle)).toFloat()
        profOnScreen = true
    }

    override fun draw(canvas: Canvas) {
        canvas.drawBitmap(bmp, null, r, null)
        if (explosTime != null) {
            canvas.drawBitmap(explos_bmp, null, explosion, null)
        }
    }

    override fun update(interval: Double) {
        if (profOnScreen) {
            vy += (interval * (gravity)).toFloat()
            r.offset((interval * vx).toFloat(), (interval * vy).toFloat()) //permet de ne pas changer directement les x et y => plus facile de reset les positions a la fin du tir
            x_explosion += (interval * vx).toFloat()
            y_explosion += (interval * vy).toFloat()
            checkImpact(r, true)
            if (currentHP == 0) {
                profOnScreen = false
            }
            if (explosTime != null) {
                if (System.nanoTime().toDouble() > (explosTime!! + 5f * 10.toDouble()
                        .pow(8))
                ) {
                    explosTime = null
                }
            }
        }
    }

    override fun resetProf() {
        profOnScreen = false
    }

    override fun myMove() {     //les chimistes savent tous faire des explosifs, non?
        explosion = RectF(x_explosion, y_explosion, x_explosion + explos_width, y_explosion + explos_width)
        explosTime = System.nanoTime()
        checkImpact(explosion, false)
        view.playBoomSound()
    }

    override fun follow(v: Float) {
        vx += -v
    }

    override fun checkImpact(hitbox: RectF, vuln : Boolean) {       //verifie s'il y a une intersection avec un obstacle
        var toRemove : MutableList<Obstacle> = mutableListOf()
        for (d in view.lesObstacles) {
            if (RectF.intersects(hitbox, d.r)) {
                if (d.choc(this, vuln))
                    toRemove.add(d)
                else
                    if (vuln)
                        r.offset(0f, - 10f) //si collision avec le terrain, on rajoute un offset pour eviter que le prof soit trop loin dans le sol et meure instantanement
                if (vuln) {
                    currentHP -= 1
                    break       //on part du principe que si c'est le prof qui touche un obstacle, il ne peut en toucher qu'un à la fois
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