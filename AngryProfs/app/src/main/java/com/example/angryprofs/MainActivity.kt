package com.example.angryprofs

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.Button

class MainActivity: AppCompatActivity() {
    lateinit var canonView: CanonView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_main)
        canonView = findViewById<CanonView>(R.id.vMain)

        val haelt : Button = findViewById(R.id.Haelti_button)
        val spar : Button = findViewById(R.id.Spar_button)
        val bog : Button = findViewById(R.id.Bog_button)
        val bers : Button = findViewById(R.id.Bers_button)

        haelt.setOnClickListener{
            canonView.name = "Haelterman"
        }

        spar.setOnClickListener{
            canonView.name = "Sparenberg"
        }

        bog.setOnClickListener{
            canonView.name = "Bogaerts"
        }

        bers.setOnClickListener{
            canonView.name = "Bersini"
        }

    }


    override fun onPause() {
        super.onPause()
        canonView.pause()
    }

    override fun onResume() {
        super.onResume()
        canonView.resume()
    }
}