package com.example.angryprofs

import android.graphics.*
import android.media.Image

interface ProfInter {
    var x : Float
    var y : Float
    val width : Float
    var r : RectF
    var v0 : Float
    var vx : Float
    var vy : Float
    var profOnScreen : Boolean
    var profSize : Float
    var currentHP : Int
    val name : String
    val image : Int
    val gravity: Int
    val bmp : Bitmap?
    var lesObstacles : MutableList<out ObstacleInter>

    fun launch(angle: Double)

    fun draw(canvas: Canvas)

    fun update(interval : Double)

    fun myMove()

    fun resetProf()

    fun follow(v: Float)

    fun checkImpact(hitbox: RectF)

}