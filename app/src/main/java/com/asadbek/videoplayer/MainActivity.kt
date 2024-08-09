package com.asadbek.videoplayer

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.SeekBar
import android.widget.Toast
import android.widget.VideoView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    lateinit var videoView: VideoView
    lateinit var btnStart:Button
    lateinit var btnStop:Button
    lateinit var btnPause:Button
    lateinit var btnResume:Button
    lateinit var liner:SeekBar
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        videoView = findViewById(R.id.videoView)
        btnStart = findViewById(R.id.btnStart)
        btnStop = findViewById(R.id.btnStop)
        btnPause = findViewById(R.id.btnPause)
        btnResume = findViewById(R.id.btnContinue)
        liner = findViewById(R.id.liner)

        // videLinki
        videoView.setVideoURI(Uri.parse("https://firebasestorage.googleapis.com/v0/b/ethicalhackinguz-1560f.appspot.com/o/lessons%2F1720935518140.mp4?alt=media&token=4c4feea0-40b5-47d5-a95f-693024e94df0"))

        liner.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser){
                   
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })

        btnStart.setOnClickListener {
            playin()
            videoView.start() // videoni boshlash
        }
        btnStop.setOnClickListener {
            videoView.stopPlayback() // videoni qayta boshlash yoki to`xtatish
        }
        btnPause.setOnClickListener {
            videoView.pause() // pauza qilish
        }
        btnResume.setOnClickListener {
            videoView.resume() // davom ettirish
        }
    }

    // seekbar uchun fun
    private fun playin(){
        val handler = Handler()
        handler.postDelayed({
            liner.progress = videoView.currentPosition
            Toast.makeText(this, "${videoView.duration}", Toast.LENGTH_SHORT).show()
            playin()
        },1000)
    }
}