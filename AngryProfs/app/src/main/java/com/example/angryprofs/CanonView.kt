package com.example.angryprofs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.*
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import android.util.AttributeSet
import android.util.SparseIntArray
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import kotlin.math.pow

class CanonView @JvmOverloads constructor(
    context: Context,
    attributes: AttributeSet? = null,
    defStyleAttr: Int = 0
) : SurfaceView(context, attributes, defStyleAttr), SurfaceHolder.Callback, Runnable {

    lateinit var canvas: Canvas
    val backgroundPaint = Paint()
    val textPaint = Paint()
    var screenWidth = 0f
    var screenHeight = 0f
    var drawing = false
    lateinit var thread: Thread
    val canon = Canon(0f, 0f, 0f, 0f, this)
    /*val obstacle = Obstacle(0f, 0f, 0f, 0f, 0f, this)
    val cible = Cible(0f, 0f, 0f, 0f, 0f, this)*/
    val lesObstaclesDestructibles: Array<ObstacleDestructible>
    val leTerrain: Array<Terrain>
    val lesEtudiants: Array<Etudiant>
    var lesObstacles: MutableList<Obstacle>
    lateinit var prof: Prof
    //var shotsFired = 0
    //var gameOver = false
    val activity = context as FragmentActivity
    var totalElapsedTime : Double
    val soundPool: SoundPool
    val soundMap: SparseIntArray
    var fired: Boolean = false
    var moveUsed: Boolean = false
    var timeShot: Long? = null
    var cameraMoves: Boolean = false
    var score: Int = 0
    val scoreVictoire = 10000
    var turn : Int = 1
    var name : String = "Bogaerts"
    var cameraSpeed = 0f
    var drawRunning = false

    init {
        totalElapsedTime = 0.0
        backgroundPaint.color = Color.rgb(128,234,255)
        textPaint.textSize = screenWidth / 20
        textPaint.color = Color.BLACK
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(1)
            .setAudioAttributes(audioAttributes)
            .build()
        soundMap = SparseIntArray(5)
        soundMap.put(0, soundPool.load(context, R.raw.target_hit, 1))
        soundMap.put(1, soundPool.load(context, R.raw.canon_fire, 1))
        soundMap.put(2, soundPool.load(context, R.raw.blocker_hit, 1))
        soundMap.put(3, soundPool.load(context, R.raw.explosion, 1))
        soundMap.put(4, soundPool.load(context, R.raw.blaster, 1))

        // créer tous les obstacles, terrains etudiants et le prof ici????

        val ground = Terrain(-1500f, 800f, 30000f, 100f, this)
        val limite = Terrain(-1500f, 0f, 1480f, 900f, this)
        val pre_butte = Terrain( 400f, 750f, 200f, 50f, this)
        val butte = Terrain( 600f, 600f, 200f, 200f, this)
        val trou = Terrain( 1050f, 600f, 200f, 200f, this)
        val butte2 = Terrain( 1250f, 500f, 200f, 300f, this)
        val butte3 = Terrain( 1450f, 700f, 200f, 100f, this)
        leTerrain = arrayOf(ground, pre_butte, butte, trou, butte2, butte3, limite)
        val obs1 = ObstacleDestructible(800f, 600f, 50f, 200f, this)
        val obs2 = ObstacleDestructible(800f, 550f, 250f, 50f, this)
        val obs3 = ObstacleDestructible(850f, 400f, 50f, 200f, this)
        val obs4 = ObstacleDestructible(850f, 350f, 220f, 50f, this)
        val obs5 = ObstacleDestructible(1020f, 400f, 50f, 200f, this)
        val obs6 = ObstacleDestructible(850f, 150f, 220f, 200f, this)
        val obs7 = ObstacleDestructible(1070f, 450f, 180f, 150f, this)
        val obs8 = ObstacleDestructible(1000f, 600f, 50f, 200f, this)
        val obs9 = ObstacleDestructible(1400f, 450f, 300f, 50f, this)
        val obs10 = ObstacleDestructible(1650f, 500f, 50f,300f, this)
        val obs11 = ObstacleDestructible(1450f, 150f, 200f,300f, this)
        val obs12 = ObstacleDestructible(1050f, 100f, 430f,50f, this)
        val obs13 = ObstacleDestructible(1750f, 200f, 50f,300f, this)
        val obs14 = ObstacleDestructible(1750f, 500f, 50f,300f, this)
        val obs15 = ObstacleDestructible(1850f, 200f, 50f,300f, this)
        val obs16 = ObstacleDestructible(1850f, 500f, 50f,300f, this)
        val obs17 = ObstacleDestructible(1750f, 150f, 150f,50f, this)
        lesObstaclesDestructibles =
            arrayOf(obs1, obs2, obs3, obs4, obs5, obs6, obs7, obs8, obs9, obs10, obs11, obs12, obs13, obs14,
                obs15, obs16, obs17
            )
        val stud1 = Etudiant("yeehaw", 855f, 640f, 140f, 170f, this)
        val stud2 = Etudiant("c_qui_ca",905f, 400f, 120f, 150f, this)
        val stud3 = Etudiant("laiba_la_bg",905f, 0f, 120f, 150f, this)
        val stud4 = Etudiant("yeehaw", 1090f, 300f, 140f, 170f, this)
        val stud5 = Etudiant("c_qui_ca",1270f, 350f, 120f, 150f, this)
        val stud6 = Etudiant("yeehaw", 1480f, 550f, 140f, 170f, this)
        val stud7 = Etudiant("laiba_la_bg", 1480f, 0f, 140f, 170f, this)
        val stud8 = Etudiant("laiba_la_bg",1770f, 0f, 120f, 150f, this)
        val stud9 = Etudiant("c_qui_ca",1950f, 650f, 120f, 150f, this)
        lesEtudiants = arrayOf(stud1  , stud2, stud3, stud4, stud5, stud6, stud7, stud8, stud9 )
        lesObstacles = mutableListOf(*lesObstaclesDestructibles, *leTerrain, *lesEtudiants)
    }

    fun pause() {
        drawing = false
        thread.join()
    }

    fun resume() {
        drawing = true
        thread = Thread(this)
        thread.start()
    }

    override fun run() {
        var previousFrameTime = System.currentTimeMillis()
        while (drawing) {
            val currentTime = System.currentTimeMillis()
            var elapsedTimeMS: Double = (currentTime - previousFrameTime).toDouble()
            totalElapsedTime += elapsedTimeMS / 1000.0
            updatePositions(elapsedTimeMS)
            draw()
            previousFrameTime = currentTime
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        screenWidth = w.toFloat()
        screenHeight = h.toFloat()
        canon.canonBaseRadius = (h / 18f)
        canon.canonLongueur = (w / 8f)
        canon.largeur = (w / 24f)
        canon.set()
        //canon.v0 = (w * 3 / 2f)

        /*obstacle.obstacleDistance = (w * 5 / 8f)
        obstacle.obstacleDebut = (h / 8f)
        obstacle.obstacleFin = (h * 3 / 8f)
        obstacle.width = (w / 24f)
        obstacle.initialObstacleVitesse = (h / 2f)
        obstacle.setRect()

        cible.width = (w / 24f)
        cible.cibleDistance = (w * 7 / 8f)
        cible.cibleDebut = (h / 8f)
        cible.cibleFin = (h * 7 / 8f)
        cible.cibleVitesseInitiale = (-h / 4f)
        cible.setRect()*/
        textPaint.setTextSize(w / 20f)
        textPaint.isAntiAlias = true
    }

    fun playObstacleSound() {
        soundPool.play(soundMap.get(2), 1f, 1f, 1, 0, 1f)
    }

    fun playCibleSound() {
        soundPool.play(soundMap.get(0), 1f, 1f, 1, 0, 1f)
    }

    fun playBoomSound() {
        soundPool.play(soundMap.get(3), 1f, 1f, 1, 0, 1f)
    }

    fun playLaserSound() {
        soundPool.play(soundMap.get(4), 1f, 1f, 1, 0, 1f)
    }

    fun draw() {
        if (holder.surface.isValid) {
            drawRunning = true
            canvas = holder.lockCanvas()
            canvas.drawRect(
                0f, 0f, canvas.width.toFloat(),
                canvas.height.toFloat(), backgroundPaint
            )
            canon.draw(canvas)
            //il faut ecrire le compteur de points ici
            if (fired) {
                if (prof.profOnScreen) {
                    prof.draw(canvas)
                }
            }
            for (d in lesObstacles) {
                d.draw(canvas)
            }
            val formatted = String.format("%1d", score)     //on dessine le score a la fin pour qu'il se superpose sur le reste
            canvas.drawText(
                "Score : " + formatted,
                30f, 100f, textPaint
            )
            holder.unlockCanvasAndPost(canvas)
            drawRunning = false
        }
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        val action = e.action
        if (!fired) {
            when (action) {
                MotionEvent.ACTION_DOWN -> {
                    alignCanon(e)
                }
                MotionEvent.ACTION_MOVE -> {
                    alignCanon(e)
                }
                MotionEvent.ACTION_UP -> {
                    spawnProf(canon.finCanon)
                    fireProf(e)
                }
            }
        }
        if (fired && !moveUsed) {  //on empeche le pouvoir d'etre utilisé plus d'une fois par tir
            when (action) {
                MotionEvent.ACTION_DOWN -> {
                    moveUsed = true
                    prof.myMove() //actionne le pouvoir
                }
            }
        }
        return true
    }

    fun fireProf(event: MotionEvent) {
        if (!prof.profOnScreen) {
            val angle = alignCanon(event)
            prof.launch(angle, canon.v, canon.finCanon)
            fired = true
            soundPool.play(soundMap.get(1), 1f, 1f, 1, 0, 1f)
            timeShot = System.nanoTime()
        }
    }

    fun addScore(bonus : Int) {
        score += bonus
    }

    fun alignCanon(event: MotionEvent): Double {
        val touchPoint = Point(event.x.toInt(), event.y.toInt())
        val centerMinusY = screenHeight - touchPoint.y
        var angle = 0.0     //ATTENTION L'ANGLE N'EST PAS HABITUEL, PART DE oY VERS oX
        if (centerMinusY != 0.0f)
            angle = Math.atan((touchPoint.x).toDouble() / centerMinusY)
        canon.align(angle)
        return angle
    }

    fun updatePositions(elapsedTimeMS: Double) {
        val interval = elapsedTimeMS / 1000.0
        canon.update(interval)
        if (fired) {
            prof.update(interval)
        }
        for (d in lesObstacles) {
            d.update(interval)
        }

        if (timeShot != null) {
            if (System.nanoTime().toDouble() > timeShot!!.toDouble() + 2 * 10.toDouble()
                    .pow(9) && !cameraMoves
            ) {
                cameraMoves = true
                timeShot = null
                cameraFollows(prof.vx)
            }
        }

        if (fired && !prof.profOnScreen) {
            timeShot = null     //devrait empecher la camera de commencer a bouger juste apres que le prof soit mort mais ne fonctionne pas a tous les coups
            cameraMoves = false
            cameraFollows(-cameraSpeed) //on arrete le deplacement de la camera, peut etre a changer pour que ca s'arrete des le premier contact avec un obstacle?
            cameraSpeed = 0f
            turn += 1
            fired = false
            moveUsed = false
            if (turn < 5) {
                reset()
                //showProfSelectionDialog(R.string.choisir_prof)
            }
            else {
                drawing = false
                turn = 1
                //gameOver = true
                if (score < scoreVictoire) {
                    reset()
                    showGameOverDialog(R.string.lose) }
                else {
                    reset()
                    showGameOverDialog(R.string.win) }
            }
        }
    }

    fun showGameOverDialog(messageId: Int) {
        class GameResult : DialogFragment() {
            override fun onCreateDialog(bundle: Bundle?): Dialog {
                val builder = AlertDialog.Builder(getActivity())
                builder.setTitle(resources.getString(messageId))
                builder.setMessage(
                    resources.getString(
                        R.string.results_format, score
                    )
                )
                builder.setPositiveButton(R.string.reset_game,
                    DialogInterface.OnClickListener { _, _ -> newGame() }
                )
                return builder.create()
            }
        }

        activity.runOnUiThread(
            Runnable {
                val ft = activity.supportFragmentManager.beginTransaction()
                val prev =
                    activity.supportFragmentManager.findFragmentByTag("dialog")
                if (prev != null) {
                    ft.remove(prev)
                }
                ft.addToBackStack(null)
                val gameResult = GameResult()
                gameResult.setCancelable(false)
                gameResult.show(ft, "dialog")
            }
        )
    }

    fun showProfSelectionDialog(messageId: Int) {
        class GameResult : DialogFragment() {
            override fun onCreateDialog(bundle: Bundle?): Dialog {
                val builder = AlertDialog.Builder(getActivity())
                builder.setTitle(resources.getString(messageId))
                builder.setMessage(
                    resources.getString(
                        R.string.results_format, score
                    )
                )
                builder.setNeutralButton(R.string.ch_haelti,
                    DialogInterface.OnClickListener { _, _ -> name = "Haelterman" ; }
                )
                builder.setNeutralButton(R.string.ch_bog,
                    DialogInterface.OnClickListener { _, _ -> name = "Bogaerts" ; }
                )
                builder.setNeutralButton(R.string.ch_bers,
                    DialogInterface.OnClickListener { _, _ -> name = "Bersini" ; }
                )
                builder.setNeutralButton(R.string.ch_spar,
                    DialogInterface.OnClickListener { _, _ -> name = "Sparenberg" ; }
                )
                builder.setPositiveButton(R.string.ok,
                    DialogInterface.OnClickListener { _, _ -> }
                )
                return builder.create()
            }
        }

        activity.runOnUiThread(
            Runnable {
                val ft = activity.supportFragmentManager.beginTransaction()
                val prev =
                    activity.supportFragmentManager.findFragmentByTag("dialog")
                if (prev != null) {
                    ft.remove(prev)
                }
                ft.addToBackStack(null)
                val gameResult = GameResult()
                gameResult.setCancelable(false)
                gameResult.show(ft, "dialog")
            }
        )
    }

    override fun surfaceChanged(
        holder: SurfaceHolder, format: Int,
        width: Int, height: Int
    ) {
    }

    override fun surfaceCreated(holder: SurfaceHolder) {}
    override fun surfaceDestroyed(holder: SurfaceHolder) {}

    fun cameraFollows(v: Float) {  //on simule le deplacement d'une camera en faisant se deplacer tous les objets a des vitesses non nulles
        cameraSpeed += v
        prof.follow(v)
        canon.follow(v)
        for (d in lesObstacles)
            d.follow(v)
    }

    /*fun gameOver() {
        drawing = false
        showGameOverDialog(R.string.win)
        gameOver = true
    }*/

    fun newGame() {
        //cible.resetCible()
        //obstacle.resetObstacle()
        //timeLeft = 10.0
        //prof.resetProf()

        //if (gameOver) {
        lesObstacles = mutableListOf(
            *lesObstaclesDestructibles,
            *leTerrain, *lesEtudiants     //on reinitialise la liste des obstacles pour une nouvelle partie
        )
        reset()
        score = 0
        turn = 1

        //gameOver = false
        drawing = true
        thread = Thread(this)
        thread.start()
        //}
    }

    fun spawnProf(point : PointF) {
        when (name) {
            "Haelterman" -> prof = Haelterman(point.x, point.y - 200f,this)
            "Bersini" -> prof = Bersini(point.x, point.y - 200f,this)
            "Bogaerts" -> prof = Bogaerts(point.x, point.y - 200f,this)
            "Sparenberg" -> prof = Sparenberg(point.x, point.y - 200f,this)
        }
    }

    fun reset() {
        for (d in lesObstacles)
            d.reset()
        canon.reset()
    }

    fun removeObstacles(list : MutableList<Obstacle>) {
        lesObstacles.removeAll(list)
    }

}