package com.example.angryprofs

import android.graphics.*

class Sparenberg(var x: Float,
    var y: Float,
    val view: CanonView
) : Prof {
    override val width = 200f //a changer pour une valeur exacte
    override var r = RectF(x, y, x + width, y + width * 1.2f)
    override var vx = 0f
    override var vy = 0f
    override val gravity = 150
    override var profOnScreen = false
    override var currentHP: Int = 4
    override val name = "Sparenberg"
    override val image =
        view.getResources()
            .getIdentifier("sparenberg", "drawable", view.getContext().getPackageName())

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
            r.offset((interval * vx).toFloat(), (interval * vy).toFloat()) //permet de ne pas changer directement les x et y => plus facile de reset les positions a la fin du tir

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

    override fun myMove() {     //se teleporte par effet tunnel, n'a qu'une certaine probabilite de passer la barriere de potentiel!
        r.offset(600f, 0f)
    }

    override fun follow(v: Float) {
        vx += -v
    }

    override fun checkImpact(hitbox: RectF, vuln : Boolean) {       //verifie s'il y a une intersection avec un obstacle
        for (d in view.lesObstacles) {       //pas forcement super optimise s'il y a collision avec un des derniers elements de l'array
            if (RectF.intersects(hitbox, d.r)) {
                if (!d.choc(this, vuln)) //si collision avec le terrain, on rajoute un offset pour eviter que le prof soit trop loin dans le sol et meure instantanement
                    r.offset(0f,  - 10f)
                currentHP -= 1
                break
            }
        }
    }

}
