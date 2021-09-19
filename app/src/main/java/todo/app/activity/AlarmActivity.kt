package todo.app.activity

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import todo.app.R
import todo.app.broadcast.AlarmBroadcastReceiver

class AlarmActivity: AppCompatActivity() {

    private lateinit var img : ImageView

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
        initActionBar()
        assignViewById()
        loadingGIFImage()

    }

    private fun loadingGIFImage(){
        Glide.with(this).load(R.drawable.task_alarm).into(img)
    }

    private fun assignViewById(){
        img = findViewById(R.id.alarm_img)
        findViewById<Button>(R.id.ok_btn).setOnClickListener(onOkClick)
    }

    private val onOkClick = View.OnClickListener {
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager?
        val intent = Intent(this, AlarmBroadcastReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0)
        alarmManager!!.cancel(pendingIntent)
        finish()
    }
    private fun initActionBar(){
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#FEA82F")))
        supportActionBar?.title = "Alarm"
    }
}