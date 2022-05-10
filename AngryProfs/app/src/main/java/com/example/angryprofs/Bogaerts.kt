package com.example.angryprofs

import android.graphics.*
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
    override val name = "bogaerts"
    override val image =
        view.getResources()
            .getIdentifier(name, "drawable", view.getContext().getPackageName())

    override val bmp = BitmapFactory.decodeResource(view.getResources(), image)
    lateinit var explosion : RectF
    private val explosion_image : Int = view.getResources().getIdentifier("explosion", "drawable", view.getContext().getPackageName())
    private val explosion_bmp = BitmapFactory.decodeResource(view.getResources(), explosion_image)
    private var explosTime : Long? = null
    private val explosion_width = 500f
    private var x_explosion : Float = x + width / 2 - explosion_width / 2
    private var y_explosion : Float = y + width / 2 * 1.2f - explosion_width / 2

    override fun launch(angle: Double, v : Float, finCanon : PointF) {
        x = finCanon.x
        y = finCanon.y - width / 2 * 1.2f  //pour que le milieu du prof soit a la hauteur du milieu du canon lors du tir
        vx = (v * Math.sin(angle)).toFloat()
        vy = (-v * Math.cos(angle)).toFloat()
        profOnScreen = true
    }

    override fun draw(canvas: Canvas) {
        canvas.drawBitmap(bmp, null, r, null)
        if (explosTime != null) {       //si l'explosion a eu lieu et n'a pas encore expiré
            canvas.drawBitmap(explosion_bmp, null, explosion, null)    //on affiche l'explosion
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
                profOnScreen = false     //on arrete d'afficher le prof
                view.nextTurn()    //on passe au prochain tour
            }
            if (explosTime != null) {
                if (System.nanoTime().toDouble() > (explosTime!! + 5f * 10.toDouble()       //on laisse l'explosion a l'ecran pendant 0.5 secondes
                        .pow(8))
                ) {
                    explosTime = null
                }
            }
        }
    }

    override fun myMove() {     //les chimistes savent tous faire des explosifs, non?
        explosion = RectF(x_explosion, y_explosion, x_explosion + explosion_width, y_explosion + explosion_width)
        explosTime = System.nanoTime()
        checkImpact(explosion, false)
        view.playBoomSound()
    }

    override fun follow(v: Float) {
        vx += -v
    }

    override fun checkImpact(hitbox: RectF, vuln : Boolean) {
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