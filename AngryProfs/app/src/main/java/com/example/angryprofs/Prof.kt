package com.example.angryprofs

import android.graphics.*
import android.media.Image

interface Prof {
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

    fun launch(angle: Double, v : Float, finCanon: PointF)

    fun draw(canvas: Canvas)

    fun update(interval : Double)

    fun myMove()

    fun resetProf()

    fun follow(v: Float)

    fun checkImpact(hitbox: RectF, vuln : Boolean)

    fun bounce(axe : String, interval : Double)

}