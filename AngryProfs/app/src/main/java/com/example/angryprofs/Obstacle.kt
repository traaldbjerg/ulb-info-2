package com.example.angryprofs

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF

abstract class Obstacle(        //classe mere de tous les types d'obstacle, qui declinent chacun a leur facon choc( , )
    open val x: Float,
    open val y: Float,
    open val length: Float,
    open val height: Float,
    open val view: CanonView
) {
    open var r = RectF(x, y,
        x + length, y + height)
    open val paint = Paint()
    open var vx = 0f
    open var lastInterval = 0.0   //important pour verifier dans les methodes de choc si le prof vient de traverser les cotes horizontaux ou verticaux (cf. Terrain et ObstacleDestructible)

    open fun draw(canvas: Canvas) { //affiche l'obstacle a l'ecran
        canvas.drawRect(
            r.left, r.top, r.right,
            r.bottom, paint
        )
    }

    open fun setRect() {
        r.set(x, y,
        x + length, y + height)
    }

    open fun update(interval: Double) { //change la position de l'obstacle a chaque intervale de temps (important pour simuler le suivi de la camera)
        lastInterval = interval
        if (vx != 0f) {
            var move = (interval * vx).toFloat()
            r.offset(move, 0f)
        }
    }

    abstract fun choc(prof: Prof, vuln : Boolean) : Boolean     //decrit le comportement de l'obstacle lors d'un choc
        //renvoie true s'il faut retirer l'objet de view.lesObstacles, false sinon (cf. les implementations)

    open fun follow(v: Float) {     //on donne une vitesse a l'obstacle pour simuler le suivi de la camera
        vx += -v
    }

    open fun reset() {      // on reinitialise la position de l'obstacle a la fin de chaque tour (pour annuler le deplacement des objets du au suivi de la camera)
        r = RectF(x, y,
        x + length, y + height)
        vx = 0f     //on retire toute vitesse horizontale aux objets, sinon il arrivait apres une view.newGame() que certains obstacles qui avaient disparu avant que view.cameraFollows
                    //ne leur retire leur vitesse horizontale se deplacent tous seuls au debut d'une nouvelle partie
    }

}