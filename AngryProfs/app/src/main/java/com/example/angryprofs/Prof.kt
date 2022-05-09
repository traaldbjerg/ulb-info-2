package com.example.angryprofs

import android.graphics.*
import android.media.Image

interface Prof {    //classe mere des professeurs individuels, leurs techniques impliquent que la majorite des methodes doivent etre personnalisées donc ce n'est pas
                    //choquant de forcer la redefinition de toutes les methodes a l'aide d'une interface, plutot que d'utiliser une classe abstraite
    val width : Float
    var r : RectF
    var vx : Float
    var vy : Float
    var profOnScreen : Boolean
    var currentHP : Int
    val name : String
    val image : Int
    val gravity: Int
    val bmp : Bitmap?

    fun launch(angle: Double, v : Float, finCanon: PointF)     //pour lancer le prof avec la bonne orientation et la bonne vitesse

    fun draw(canvas: Canvas)    //s'occupe d'afficher le prof et le cas echeant, leur technique

    fun update(interval : Double)   //s'occupe de mettre a jour les positions des profs d'un intervalle a un autre

    fun myMove()    //declenche la technique du professeur, chaque implementation de cette methode est differente

    fun follow(v: Float)   //change la vitesse du professeur afin de simuler le suivi du prof par la camera

    fun checkImpact(hitbox: RectF, vuln : Boolean)  //on verifie si le professeur ou un element de leur technique rentre en contact avec un obstacle
    //hitbox est le rectangle a considerer pour la verification d'intersection : par exemple, on peut lui passer le laser de Mr. Haelterman pour verifier ses intersections
    //vuln represente si le prof est vulnerable lors de l'appel de la methode : si le laser de Mr. Haelterman rencontre un obstacle, il ne devrait pas perdre de points de vie

    fun bounce(axe : String, interval : Double) //inverse la vitesse du prof selon l'axe horizontal ou vertical. les obstacles s'occupent de passer le bon axe a cette methode

}