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
    val canon = Canon(0f, 0f, 0f, this)
    val lesObstaclesDestructibles: Array<ObstacleDestructible>
    val leTerrain: Array<Terrain>
    val lesEtudiants: Array<Etudiant>
    var lesObstacles: MutableList<Obstacle>   //la liste avec les objets encore presents a l'ecran
    lateinit var prof: Prof
    val activity = context as FragmentActivity
    var totalElapsedTime : Double
    val soundPool1: SoundPool
    val soundMap: SparseIntArray
    var fired: Boolean = false
    var moveUsed: Boolean = false
    var timeShot: Long? = null
    var cameraMoves: Boolean = false
    var score: Int = 0
    val scoreVictoire = 10000       //score limite de victoire
    var turn : Int = 1
    var name : String = "Haelterman"    //on selectionne haelterman comme prof par defaut si l'utilisateur n'appuie sur aucun bouton avant de tirer
    var cameraSpeed = 0f

    init {
        totalElapsedTime = 0.0
        backgroundPaint.color = Color.rgb(128,234,255)
        textPaint.textSize = screenWidth / 20
        textPaint.color = Color.BLACK
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool1 = SoundPool.Builder()
            .setMaxStreams(3)
            .setAudioAttributes(audioAttributes)
            .build()
        soundMap = SparseIntArray(7)
        soundMap.put(0, soundPool1.load(context, R.raw.target_hit, 1))
        soundMap.put(1, soundPool1.load(context, R.raw.canon_fire, 1))
        soundMap.put(2, soundPool1.load(context, R.raw.blocker_hit, 1))
        soundMap.put(3, soundPool1.load(context, R.raw.explosion, 1))
        soundMap.put(4, soundPool1.load(context, R.raw.blaster, 1))
        soundMap.put(5, soundPool1.load(context, R.raw.wilhelm_scream, 1))
        soundMap.put(6, soundPool1.load(context, R.raw.sad_trombone, 1))

        //on initialise l'ensemble des obstacles du terrain de jeu ici
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
        lesEtudiants = arrayOf(stud1  , stud2, stud3, stud4, stud5, stud6, stud7, stud8, stud9 )    //9 etudiants et 17 obstacles -> score max possible : 10700
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
        textPaint.setTextSize(w / 20f)
        textPaint.isAntiAlias = true
    }

    fun playTerrainSound() {
        soundPool1.play(soundMap.get(2), 0.5f, 0.5f, 1, 0, 1f)
    }

    fun playObstacleSound() {
        soundPool1.play(soundMap.get(0), 0.5f, 0.5f, 1, 0, 1f)
    }

    fun playBoomSound() {
        soundPool1.play(soundMap.get(3), 1f, 1f, 1, 0, 1f)
    }

    fun playLaserSound() {
        soundPool1.play(soundMap.get(4), 1f, 1f, 1, 0, 1f)
    }

    fun playEtudiantDeathSound() {
        soundPool1.play(soundMap.get(5), 1f, 1f, 1, 0, 1f)
    }

    fun playSadSound() {
        soundPool1.play(soundMap.get(6), 1f, 1f, 1, 0, 1f)
    }

    fun draw() {    //methode responsable de l'affichage de tous les objets presents sur la view
        if (holder.surface.isValid) {
            canvas = holder.lockCanvas()
            canvas.drawRect(
                0f, 0f, canvas.width.toFloat(),
                canvas.height.toFloat(), backgroundPaint
            )
            canon.draw(canvas)
            if (fired) {
                if (prof.profOnScreen) {
                    prof.draw(canvas)
                }
            }
            //on passe par les listes initiales avant de verifier si les elements sont toujours dans la liste d'objets encore presents a lecran
            //ceci permet d'eviter d'iterer sur la liste lesObstacles qui peut etre modifiee en meme temps par un checkImpact d'un prof
            //ce qui menait souvent a des ConcurrentModificationException
            for (d in lesObstaclesDestructibles) {
                if (d in lesObstacles)
                    d.draw(canvas)
            }
            for (d in leTerrain)    //pas besoin de la verification pour les terrains car ils ne peuvent pas disparaitre
                d.draw(canvas)
            for (d in lesEtudiants)
                if (d in lesObstacles)
                    d.draw(canvas)
            val formatted = String.format("%1d", score)     //on dessine le compteur de points a la fin pour qu'il se dessine au-dessus des autres objets
            canvas.drawText(
                getResources().getString(R.string.current_score) + formatted,
                30f, 100f, textPaint
            )
            holder.unlockCanvasAndPost(canvas)
        }
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        //s'occupe de verifier quels inputs l'utilisateur rentre sur la view
        val action = e.action
        if (!fired) {   //avant que le prof ne soit tiré
            when (action) {
                MotionEvent.ACTION_DOWN -> {    //on vise tant que le doigt n'est pas relache
                    alignCanon(e)
                }
                MotionEvent.ACTION_MOVE -> {
                    alignCanon(e)
                }
                MotionEvent.ACTION_UP -> {      //on tire le prof
                    spawnProf(canon.finCanon)
                    fireProf(e)
                }
            }
        }
        if (fired && !moveUsed) {
            when (action) {
                MotionEvent.ACTION_DOWN -> {
                    moveUsed = true  //on empeche le pouvoir d'etre utilisé plus d'une fois par tir
                    prof.myMove() //actionne le pouvoir
                }
            }
        }
        return true
    }

    fun fireProf(event: MotionEvent) {  //pour tirer le prof
        if (!prof.profOnScreen) {    //on verifie qu'il n'y a pas de prof à l'ecran
            val angle = alignCanon(event)
            prof.launch(angle, canon.v, canon.finCanon)     //on lance le prof
            fired = true
            soundPool1.play(soundMap.get(1), 1f, 1f, 1, 0, 1f)
            timeShot = System.nanoTime()
        }
    }

    fun addScore(bonus : Int) {     //on incremente le score, sert simplement à encapsuler le score (donc a ne pas le modifier directement dans .choc( , ) des obstacles)
        score += bonus
    }

    fun alignCanon(event: MotionEvent): Double {   //pour aligner le canon avec le point de visee
        val touchPoint = Point(event.x.toInt(), event.y.toInt())
        val centerMinusY = screenHeight - 100f - touchPoint.y //le -100f pour que l'angle soit pris par rapport a la base du canon
        var angle = 0.0     //L'ANGLE N'EST PAS HABITUEL, PART DE OY VERS OX! -> les cos et sin sont inverses par rapport a un repere habituel
        if (centerMinusY != 0.0f)
            angle = Math.atan((touchPoint.x).toDouble() / centerMinusY)
        canon.align(angle)
        return angle
    }

    fun updatePositions(elapsedTimeMS: Double) {    //methode principale de la view, met a jour les positions de chaque element pour pouvoir
                                                    //ensuite les afficher avec draw() dans leurs nouvelles positions
        val interval = elapsedTimeMS / 1000.0
        canon.update(interval)
        if (fired) {
            prof.update(interval)
        }
        //on passe par les listes initiales avant de verifier si les elements sont toujours dans la liste d'objets encore presents a lecran
        //ceci permet d'eviter d'iterer sur la liste lesObstacles qui peut etre modifiee en meme temps par un checkImpact d'un prof
        //ce qui menait souvent a des ConcurrentModificationException
        for (d in lesObstaclesDestructibles)
            if (d in lesObstacles)
                    d.update(interval)
        for (d in leTerrain)    //pas besoin de la verification pour les terrains car ils ne peuvent pas disparaitre
            d.update(interval)
        for (d in lesEtudiants)
            if (d in lesObstacles)
                d.update(interval)

        if (timeShot != null) {
            if (System.nanoTime().toDouble() > timeShot!!.toDouble() + 2 * 10.toDouble()    //2 secondes apres que le prof ait ete tire, on declenche le suivi de la "camera"
                    .pow(9) && !cameraMoves
            ) {
                cameraMoves = true
                timeShot = null
                cameraFollows(prof.vx)
            }
        }
    }

    fun nextTurn() {    //pour passer au prochain tour, reinitialise la position de tous les obstacles, appelé par les profs quand ils n'ont plus de points de vie
        draw()      //pour que le prof ne s'affiche plus quand on fait la pause
        Thread.sleep(1000)  //pour faire un petit arret sur la position du prof, eviter de tirer le prof par erreur
        if (turn < 4) {
            reset()
        }
        else {
            drawing = false
            if (score < scoreVictoire) {
                showGameOverDialog(R.string.lose) }
            else {
                showGameOverDialog(R.string.win) }
        }
    }

    fun showGameOverDialog(messageId: Int) {    //creation et affichage du dialogue de fin de partie
        class GameResult : DialogFragment() {
            override fun onCreateDialog(bundle: Bundle?): Dialog {
                val builder = AlertDialog.Builder(getActivity())
                builder.setTitle(resources.getString(messageId))
                builder.setMessage(
                    resources.getString(    //affichage du score final obtenu
                        R.string.results_format, score
                    )
                )
                builder.setPositiveButton(R.string.reset_game,
                    DialogInterface.OnClickListener { _, _ -> newGame() }    //lancement d'une nouvelle partie quand le joueur appuye sur le bouton correspondant
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
        timeShot = null
        prof.follow(v)
        canon.follow(v)
        for (d in lesObstacles)
            d.follow(v)
    }

    fun newGame() { //lance une nouvelle partie
        lesObstacles = mutableListOf(
            *lesObstaclesDestructibles,
            *leTerrain, *lesEtudiants     //reinitialisation de la liste des obstacles pour une nouvelle partie
        )
        reset()     //reinitialisation de la position de tous les obstacles
        score = 0
        turn = 1
        drawing = true
        thread = Thread(this)
        thread.start()
    }

    fun spawnProf(point : PointF) { //on initialise le prof choisi à l'aide des boutons (ou haelterman par defaut, cf. initialisation de name)
        when (name) {
            "Haelterman" -> prof = Haelterman(point.x, point.y - 150f * 1.2f,this)
            "Bersini" -> prof = Bersini(point.x, point.y - 150f * 1.2f,this)
            "Bogaerts" -> prof = Bogaerts(point.x, point.y - 150f * 1.2f,this)
            "Sparenberg" -> prof = Sparenberg(point.x, point.y - 150f * 1.2f,this)
        }
    }

    fun reset() {
        timeShot = null     //empeche la camera de commencer a bouger juste apres que le prof meurt s'il meurt en moins de 2 secondes
        cameraMoves = false
        cameraFollows(-cameraSpeed) //on arrete le deplacement de la camera
        cameraSpeed = 0f
        turn += 1
        fired = false
        moveUsed = false
        for (d in lesObstacles) //on reinitialise tous les obstacles qui sont encore en jeu
            d.reset()
        canon.reset()
    }

    fun removeObstacles(list : MutableList<Obstacle>) {  //sert a encapsuler lesObstacles, a ne pas la modifier directement dans un checkImpact() des profs
        lesObstacles.removeAll(list)
    }

    fun removeObstacles(obstacle : Obstacle){   //surcharge de la methode, permet d'economiser un tout petit peu de temps de calcul pour les profs qui ne touchent qu'un
        //obstacle a la fois, dans ce cas, il n'y a pas besoin de ranger l'unique obstacle touché dans une MutableList<Obstacle> pour ensuite la passer a la methode ci-dessus
        //on peut alors directement passer l'obstacle ici
        lesObstacles.remove(obstacle)
    }

}
