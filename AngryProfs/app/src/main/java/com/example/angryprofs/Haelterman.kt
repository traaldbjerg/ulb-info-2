package com.example.angryprofs

import android.graphics.*
import kotlin.math.pow

class Haelterman(
    val view: CanonView
) : ProfInter {
    override var x = 600f
    override var y = 600f
    override val width = 200f //a changer pour une valeur exacte
    override var r = RectF(x, y, x + width, y + width * 1.2f)
    override var vx = 0f
    override var vy = 0f
    override val gravity = 100
    override var profOnScreen = false
    override var currentHP: Int = 4
    override val name = "Haelterman" //inutile je pense
    override val image =
        view.getResources()
            .getIdentifier("haelterman", "drawable", view.getContext().getPackageName())
    //pour plus general, mettre le nom de la ressource en attribut, facilement changeable a la creation d'objet
    override val bmp = BitmapFactory.decodeResource(view.getResources(), image)

    var laserTime : Long? = null
    lateinit var laser : RectF

    override fun launch(angle: Double, v : Float, finCanon : PointF) {
        x = finCanon.x
        y = finCanon.y - width
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
            if (laserTime != null) {
                if (System.nanoTime().toDouble() > (laserTime!! + 5f * 10.toDouble()
                        .pow(8))
                ) {
                    view.lesRectangles.remove(laser)
                }
            }
        }
    }

    override fun resetProf() {
        profOnScreen = false
    }

    override fun myMove() {
        laser = RectF(x, y, x + 10000f, y + 10f) //le laser
        view.lesRectangles.add(laser)
        laserTime = System.nanoTime()
        checkImpact(laser, false)
    }

    override fun follow(v: Float) {
        vx += -v
    }

    override fun checkImpact(hitbox: RectF, vuln : Boolean) {       //verifie s'il y a une intersection avec un obstacle, sera etendu aux etudiants par la suite
        for (d in view.lesObstacles) {       //pas forcement super optimise s'il y a collision avec un des derniers elements de l'array
            if (RectF.intersects(hitbox, d.r)) {
                d.choc(this)
                if (vuln) {
                    currentHP -= 1
                    break
                }
            }
        }
    }

}
