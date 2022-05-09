package com.example.angryprofs

import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.PointF
import android.graphics.RectF
import android.widget.Toast

class Bersini(
    var x: Float,
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
    override val name = "bersini"
    override val image =
        view.getResources()
            .getIdentifier(name, "drawable", view.getContext().getPackageName())

    override val bmp = BitmapFactory.decodeResource(view.getResources(), image)

    override fun launch(angle: Double, v: Float, finCanon: PointF) {
        x = finCanon.x
        y = finCanon.y - width / 2 * 1.2f
        vx = (v * Math.sin(angle)).toFloat()
        vy = (-v * Math.cos(angle)).toFloat()
        profOnScreen = true
    }

    override fun draw(canvas: Canvas) {
        canvas.drawBitmap(bmp, null, r, null)
    }

    override fun update(interval: Double) {
        if (profOnScreen) {
            vy += (interval * (gravity)).toFloat()
            r.offset(
                (interval * vx).toFloat(),
                (interval * vy).toFloat()
            ) //permet de ne pas changer directement les x et y => plus facile de reset les positions a la fin du tir
            checkImpact(r, true)
            if (currentHP == 0) {
                profOnScreen = false    // on arreter d'afficher le prof
                view.nextTurn()    //on passe au prochain tour
            }
        }
    }

    override fun myMove() {     //trouve une faille (intentionnelle) dans le code de ce jeu et elimine tous les etudiants instantanement
        val toRemove: MutableList<Obstacle> = mutableListOf()
        for (d in view.lesEtudiants) {
            if (d in view.lesObstacles) {  //pour empecher Mr. Bersini de reeliminer des etudiants qui sont deja K.O.
                d.choc(this, false)
                toRemove.add(d)
            }
        }
        if (toRemove.size > 0) {
            val text = view.getResources().getString(R.string.tech_bersini1)
            val duration = Toast.LENGTH_LONG
            val toast = Toast.makeText(view.activity, text, duration)
            toast.show()
            view.removeObstacles(toRemove)
        } else {
            val text = view.getResources().getString(R.string.tech_bersini2)
            val duration = Toast.LENGTH_LONG
            val toast = Toast.makeText(view.activity, text, duration)
            toast.show()
        }
    }

    override fun follow(v: Float) {     //change la vitesse du professeur afin de simuler le suivi du prof par la camera
        vx += -v
    }

    override fun checkImpact(   //verifie s'il y a une intersection avec un obstacle
        hitbox: RectF,
        vuln: Boolean
    ) {
        for (d in view.lesObstacles) {       //pas forcement super optimise s'il y a collision avec un des derniers elements de l'array
            if (RectF.intersects(hitbox, d.r)) {
                currentHP -= 1
                if (d.choc(this, vuln)) {
                    view.removeObstacles(d)
                }
                break       //on part du principe que les profs sans projectiles ne peuvent toucher qu'un obstacle a la fois
            }
        }
    }

    override fun bounce(axe: String, interval: Double) {
        when (axe) {
            "y" -> {
                vy = vy * -1
                r.offset(0f, (vy * interval).toFloat())
            }
            "x" -> {
                vx =
                    vx * -1 - 2 * view.cameraSpeed  //il faut incrementer de -2 cameraSpeed, si la camera bouge deja alors vx = 0 donc faire * -1 ne change rien
                r.offset((vx * interval).toFloat(), 0f)
                view.cameraFollows(vx)
            }
        }
    }

}