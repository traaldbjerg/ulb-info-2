package com.example.angryprofs

import android.graphics.*
import kotlin.math.sign
import kotlin.random.Random

class Sparenberg(
    var x: Float,
    var y: Float,
    val view: CanonView
) : Prof {
    override val width = 150f
    override var r = RectF(x, y, x + width, y + width * 1.2f)
    override var vx = 0f
    override var vy = 0f
    override val gravity = 150
    override var profOnScreen = false
    override var currentHP: Int = 4
    override val name = "sparenberg"
    override val image =
        view.getResources()
            .getIdentifier(name, "drawable", view.getContext().getPackageName())

    override val bmp = BitmapFactory.decodeResource(view.getResources(), image)

    override fun launch(angle: Double, v: Float, finCanon: PointF) {
        x = finCanon.x
        y = finCanon.y - width / 2 * 1.2f  //pour que le milieu du prof soit a la hauteur du milieu du canon lors du tir
        vx = (v * Math.sin(angle)).toFloat()
        vy = (-v * Math.cos(angle)).toFloat()
        profOnScreen = true     //on declenche l'affichage du prof dans view.draw()
    }

    override fun draw(canvas: Canvas) {     //affichage le prof
        canvas.drawBitmap(bmp, null, r, null)
    }

    override fun update(interval: Double) {     //mise a jour de la position du prof
        if (profOnScreen) {     //on arrete si le prof n'est pas a l'ecran (s'il n'a plus de points de vie)
            vy += (interval * (gravity)).toFloat()
            r.offset(
                (interval * vx).toFloat(),
                (interval * vy).toFloat()
            ) //permet de ne pas changer directement les x et y => plus facile de reset les positions a la fin du tir
            checkImpact(r, true)    //on verifie a chaque update si le prof vient de rentrer en contact avec un objet
            if (currentHP == 0) {
                profOnScreen = false     //on arrete d'afficher le prof
                view.nextTurn()    //on passe au prochain tour
            }
        }
    }

    override fun myMove() {     //se teleporte par effet tunnel, on vous epargne les calculs mais la reflexion sur la barriere de potentiel a une probabilite de 0.5 !
        val reflexion = Random.nextInt(0, 2)
        if (reflexion == 0)
            r.offset(600f * view.cameraSpeed.sign + vx.sign * 600f, 0f)  //si vx est nul, alors view.cameraSpeed ne l'est pas et vice versa -> se teleporte toujours dans le bon sens
        else {
            bounce("x", 0.0)
            view.playSadSound() //c'est quand meme pas de chance :(
        }
    }

    override fun follow(v: Float) {
        vx += -v
    }

    override fun checkImpact(
        hitbox: RectF,
        vuln: Boolean
    ) {       //verifie s'il y a une intersection avec un obstacle
        for (d in view.lesObstacles) {
            if (RectF.intersects(hitbox, d.r)) {
                currentHP -= 1
                if (d.choc(this, vuln)) {  //
                    view.removeObstacles(d)
                }
                break       //on part du principe que les profs sans projectiles ne peuvent toucher qu'un obstacle a la fois, break epargne du temps de calcul
            }
        }
    }

    override fun bounce(axe: String, interval: Double) {
        when (axe) {
            "y" -> {
                vy = vy * -1
                r.offset(0f, (vy * interval).toFloat() * 2)
            }
            "x" -> {
                vx =
                    vx * -1 - 2 * view.cameraSpeed  //il faut incrementer de -2 cameraSpeed, si la camera bouge deja alors vx = 0 donc faire * -1 ne change rien
                r.offset((vx * interval).toFloat() * 2, 0f)
                view.cameraFollows(vx)   //l'incrementation de -2 cameraSpeed permet de la faire osciller entre ses 2 valeurs (car elle change de signe a chaque fois)
            }
        }
    }

}
