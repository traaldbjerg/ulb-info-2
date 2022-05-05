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
    //val lesEtudiants: Array<Etudiant>
    lateinit var lesObstacles: MutableList<out ObstacleInter>
    var lesRectangles: MutableList<RectF> = mutableListOf()
    val rectPaint = Paint()
    lateinit var balle: ProfInter
    var shotsFired = 0
    var gameOver = false
    val activity = context as FragmentActivity
    var totalElapsedTime = 0.0
    val soundPool: SoundPool
    val soundMap: SparseIntArray
    var fired: Boolean = false
    var moveUsed: Boolean = false
    var timeShot: Long? = null
    var cameraMoves: Boolean = false
    var score: Int = 0
    var turn : Int = 1
    var name : String = "Haelterman"
    var cameraSpeed = 0f

    init {
        backgroundPaint.color = Color.BLUE
        rectPaint.color = Color.RED
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
        soundMap = SparseIntArray(3)
        soundMap.put(0, soundPool.load(context, R.raw.target_hit, 1))
        soundMap.put(1, soundPool.load(context, R.raw.canon_fire, 1))
        soundMap.put(2, soundPool.load(context, R.raw.blocker_hit, 1))

        // créer tous les obstacles, terrains etudiants et le prof ici????

        val ground = Terrain(0f, 900f, 30000f, 100f, this)
        leTerrain = arrayOf(ground)
        val obs1 = ObstacleDestructible(1000f, 600f, 200f, 900f, this)
        /*val obs2 = ObstacleDestructible(1000f, 600f, 200f, 300f, this)
        val obs3 = ObstacleDestructible(1000f, 600f, 200f, 300f, this)
        val obs4 = ObstacleDestructible(1000f, 600f, 200f, 300f, this)
        val obs5 = ObstacleDestructible(1000f, 600f, 200f, 300f, this)
        val obs6 = ObstacleDestructible(1000f, 600f, 200f, 300f, this)
        val obs7 = ObstacleDestructible(1000f, 600f, 200f, 300f, this)
        val obs8 = ObstacleDestructible(1000f, 600f, 200f, 300f, this)
        val obs9 = ObstacleDestructible(1000f, 600f, 200f, 300f, this)
        val obs10 = ObstacleDestructible(1000f, 600f, 200f, 300f, this)*/
        lesObstaclesDestructibles =
            arrayOf(obs1/*, obs2, obs3, obs4, obs5, obs6, obs7, obs8, obs9, obs10*/)
        /*val stud1 = Etudiant(3000f, 100f, 200f, 300f, this)
        val stud2 = Etudiant(3000f, 100f, 200f, 300f, this)
        val stud3 = Etudiant(3000f, 100f, 200f, 300f, this)
        val stud4 = Etudiant(3000f, 100f, 200f, 300f, this)
        lesEtudiants = arrayOf(stud1, stud2, stud3, stud4)*/
        //lesObstacles = mutableListOf(*lesObstaclesDestructibles, *leTerrain)
        lesObstacles = mutableListOf(obs1, ground)
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
        canon.setFinCanon(h / 2f)
        canon.setFinCanon(h / 2f)
        canon.v0 = (w * 3 / 2f)

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

    fun draw() {
        if (holder.surface.isValid) {
            canvas = holder.lockCanvas()
            canvas.drawRect(
                0f, 0f, canvas.width.toFloat(),
                canvas.height.toFloat(), backgroundPaint
            )
            canon.draw(canvas)
            //il faut ecrire le compteur de points ici
            val formatted = String.format("%d", score)
            canvas.drawText(
                "Score : " + formatted,
                30f, 50f, textPaint
            )
            if (fired) {
                if (balle.profOnScreen) {
                    balle.draw(canvas)
                }
            }
            for (d in lesObstacles) {
                d.draw(canvas)
            }
            for (rect in lesRectangles) {
                canvas.drawRect(
                    rect, rectPaint
                )
            }
            holder.unlockCanvasAndPost(canvas)
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
                    chooseProf()
                    //potentiellement faire le choix au lancement du jeu plutot qu'au tir?
                    fireProf(e)
                }
            }
        }
        if (fired && !moveUsed) {  //on empeche le pouvoir d'etre utilisé plus d'une fois par tir
            when (action) {
                MotionEvent.ACTION_DOWN -> {
                    moveUsed = true
                    balle.myMove() //actionne le pouvoir
                }
            }
        }
        return true
    }

    fun fireProf(event: MotionEvent) {
        if (!balle.profOnScreen) {
            val angle = alignCanon(event)
            balle.launch(angle, canon.v, canon.finCanon)
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
        //if (touchPoint.y > screenHeight)
            //angle += Math.PI
        canon.align(angle)
        return angle
    }

    fun updatePositions(elapsedTimeMS: Double) {
        val interval = elapsedTimeMS / 1000.0
        if (fired) {
            balle.update(interval)
        }
        for (d in lesObstacles) {
            d.update(interval)
        }
        /*timeLeft -= interval

        ecrire condition d'arret du jeu ici?

        if (timeLeft <= 0) {
            timeLeft = 0.0
            gameOver = true
            drawing = false
            showGameOverDialog(R.string.lose)
        }*/
        if (timeShot != null) {
            if (System.nanoTime().toDouble() > timeShot!!.toDouble() + 2 * 10.toDouble()
                    .pow(9) && !cameraMoves
            ) {
                cameraMoves = true
                timeShot = null
                cameraSpeed = balle.vx
                cameraFollows(cameraSpeed)
            }
        }
        if (fired && !balle.profOnScreen) {
            cameraMoves = false
            cameraFollows(-cameraSpeed) //on arrete le deplacement de la camera, peut etre a changer pour que ca s'arrete des le premier contact avec un obstacle?
            cameraSpeed = 0f
            turn += 1
            fired = false
            moveUsed = false
            if (turn < 4)
                showProfSelectionDialog(R.string.choisir_prof)
            else {
                gameOver = true
                turn = 1
                if (score < 5000)
                    showGameOverDialog(R.string.lose)
                else
                    showGameOverDialog(R.string.win)
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
                    DialogInterface.OnClickListener { _, _ -> name = "Haelterman" }
                )
                builder.setNeutralButton(R.string.ch_bog,
                    DialogInterface.OnClickListener { _, _ -> name = "Bogaerts" }
                )
                builder.setNeutralButton(R.string.ch_bers,
                    DialogInterface.OnClickListener { _, _ -> name = "Bersini" }
                )
                builder.setNeutralButton(R.string.ch_spar,
                    DialogInterface.OnClickListener { _, _ -> name = "Sparenberg" }
                )
                builder.setPositiveButton(R.string.ok,
                    DialogInterface.OnClickListener { _, _ ->  }
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

    fun cameraFollows(v: Float) {
        balle.follow(v)
        canon.follow(v)
        for (d in lesObstacles)
            d.follow(v)
    }

    fun gameOver() {
        drawing = false
        showGameOverDialog(R.string.win)
        gameOver = true
    }

    fun newGame() {
        //cible.resetCible()
        //obstacle.resetObstacle()
        //timeLeft = 10.0
        balle.resetProf()
        shotsFired = 0
        totalElapsedTime = 0.0
        drawing = true
        if (gameOver) {
            gameOver = false
            thread = Thread(this)
            thread.start()
        }
    }

    fun chooseProf() {
        when (name) {
            "Haelterman" -> balle = Haelterman(this)
            "Bersini" -> balle = Bersini(this)
            "Bogaerts" -> balle = Bogaerts(this)
            "Sparenberg" -> balle = Sparenberg(this)
        }
    }

}
