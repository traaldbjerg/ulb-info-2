package com.example.angryprofs

import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.PointF
import android.graphics.RectF

class Bersini(
    val view: CanonView
) : ProfInter {
    override var x = 0f
    override var y = 0f
    override val width = 200f //a changer pour une valeur exacte
    override var r = RectF(x, y, x + width, y + width * 1.2f)
    override var vx = 0f
    override var vy = 0f
    override val gravity = 100
    override var profOnScreen = false
    override var currentHP: Int = 4
    override val name = "Bersini"
    override val image =
        view.getResources()
            .getIdentifier("NOMDUPROF", "drawable", view.getContext().getPackageName())

    //pour plus general, mettre le nom de la ressource en attribut, facilement changeable a la creation d'objet
    override val bmp = BitmapFactory.decodeResource(view.getResources(), image)

    override fun launch(angle: Double, v : Float, finCanon : PointF) {
        x = finCanon.x
        y = finCanon.y - width / 2
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
            //r.offset((interval * vx).toFloat(), (interval * vy).toFloat())    ligne inutile? on change deja la position en x,y du rectangle
            x += (interval * vx).toFloat()
            y += (interval * vy).toFloat()
            r = RectF(x, y, x + width, y + width * 1.2f)

            //il faut ecrire le code de detection de chocs ici
            checkImpact(r, true)
            if (currentHP == 0) {
                profOnScreen = false
            }

        }
    }

    override fun resetProf() {
        profOnScreen = false
    }

    override fun myMove() {

    }

    override fun follow(v: Float) {
        vx += -v
    }

    override fun checkImpact(hitbox: RectF, vuln : Boolean) {       //verifie s'il y a une intersection avec un obstacle, sera etendu aux etudiants par la suite
        for (d in view.lesObstacles) {       //pas forcement super optimise s'il y a collision avec un des derniers elements
                if (RectF.intersects(hitbox, d.r)) {
                    d.choc(this)
                    currentHP -= 1
                    break
                }
            }
    }

}