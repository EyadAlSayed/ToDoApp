package todo.app.activity

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import todo.app.R
import todo.app.broadcast.AlarmBroadcastReceiver


class AlarmActivity : AppCompatActivity() {

    private lateinit var img: ImageView
    private lateinit var txtAlarmTaskName: TextView
    private lateinit var txtAlarmDesc: TextView
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var view: View


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        setContentView(R.layout.alram_layout)
        view = findViewById(R.id.alarm_parent_layout)

        initActionBar()
        initMediaPlayer()
        assignViewById()
        loadingGIFImage()

        txtAlarmTaskName.text = intent.getStringExtra("NAME")
        txtAlarmDesc.text = intent.getStringExtra("DESC")

        mediaPlayer!!.start()

    }

    private val onOkClick = View.OnClickListener {
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager?
        val intent = Intent(this, AlarmBroadcastReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0)
        alarmManager!!.cancel(pendingIntent)
        stopPlayer()
        finish()
    }

    private fun initMediaPlayer() {

        mediaPlayer = MediaPlayer.create(this, R.raw.ringtone)
        mediaPlayer!!.setOnCompletionListener {
            mediaPlayer!!.start()
        }
    }

    private fun initActionBar() {

        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#FEA82F")))
        supportActionBar?.title = "Alarm Task"
    }

    private fun assignViewById() {
        img = findViewById(R.id.alarm_img)
        txtAlarmTaskName = findViewById(R.id.txt_alarm_task_name)
        txtAlarmDesc = findViewById(R.id.txt_alarm_description)
        findViewById<Button>(R.id.ok_btn).setOnClickListener(onOkClick)
    }

    private fun loadingGIFImage() {
        Glide.with(this).load(R.drawable.task_alarm).into(img)
    }

    private fun stopPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer!!.release()
            mediaPlayer = null
            Snackbar.make(view, "The ringtone stopped", Snackbar.LENGTH_SHORT).show()
        }
    }
    override fun onStop() {
        super.onStop()
        stopPlayer()

    }
}