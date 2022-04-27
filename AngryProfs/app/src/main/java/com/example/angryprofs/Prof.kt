package com.example.angryprofs

import android.graphics.*

open class Prof(var x: Float, var y: Float, val width: Float, var view: CanonView, val obstacle: Obstacle, val cible: Cible, var prof : Int) {
    var r = RectF(x, y, x + width, y + width * 1.2f)
    var v0 = 0f
    var vx = 0f
    var vy = 0f
    var profOnScreen = true
    var profSize = 0f
    var raint = Paint()
    var currentHP : Int = 0
    val hpList = listOf(4,4,4,4)
    val nameList = listOf("Marc Haelterman", "Jean-Marc Sparenberg", "Hugues Bersini", "Philippe Bogaerts") //probablement a rajouter dans un string.xml
    val image =
        view.getResources().getIdentifier("NOMDUPROF", "drawable", view.getContext().getPackageName())

    //pour plus general, mettre le nom de la ressource en attribut, facilement changeable a la creation d'objet
    val bmp = BitmapFactory.decodeResource(view.getResources(), image)

    val gravity = 1800

    init {
        raint.color = Color.RED
    }

    fun launch(angle: Double) {
        x = profSize
        y = view.screenHeight / 2f
        vx = (v0 * Math.sin(angle)).toFloat()
        vy = (-v0 * Math.cos(angle)).toFloat()
        profOnScreen = true
    }

    fun draw(canvas: Canvas) {
        canvas.drawBitmap(bmp, null, r, null)
    }

    fun update(interval: Double) {
        if (profOnScreen) {
            vy += (interval * (gravity)).toFloat()
            r = RectF(x, y, x + width, y + width * 1.2f)
            r.offset((interval * vx).toFloat(), (interval * vy).toFloat())
            x += (interval * vx).toFloat()
            y += (interval * vy).toFloat()

            /* Vérifions si la balle touche l'obstacle ou pas */
            if (x + profSize > obstacle.obstacle.left && x - profSize < obstacle.obstacle.right
                && y + profSize > obstacle.obstacle.top
                && y - profSize < obstacle.obstacle.bottom
            ) {
                vx *= -1
                r.offset((3 * vx * interval).toFloat(), 0f)
                view.reduceTimeLeft()
                view.playObstacleSound()
            }
            // Si elle sort de l'écran
            else if (x + profSize > view.screenWidth
                || x - profSize < 0
            ) {
                profOnScreen = false
            } else if (y + profSize > view.screenHeight
                || y - profSize < 0
            ) {
                profOnScreen = false
            } else if (x + profSize > cible.cible.left
                && y + profSize > cible.cible.top
                && y - profSize < cible.cible.bottom
            ) {
                cible.detectChoc(this)

                /* Pour l'instant rien ne se passe lorsque balle heurte la cible */
            }
        }
    }

    open fun MyMove() {

    }

    fun resetProf() {
        profOnScreen = false
    }
}
