package todo.app.activity

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import todo.app.R
import todo.app.broadcast.AlarmBroadcastReceiver
import todo.app.enumValue.InfoMessage
import todo.app.enumValue.Keys


class AlarmActivity : AppCompatActivity() {

    private lateinit var img: ImageView
    private lateinit var txtAlarmTaskName: TextView
    private lateinit var txtAlarmDesc: TextView
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var view: View


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFullScreenMode()
        setContentView(R.layout.alram_layout)
        view = findViewById(R.id.alarm_parent_layout)

        initViews()
        initMediaPlayer()
        loadingGIFImage()


        txtAlarmTaskName.text = intent.getStringExtra(Keys.TASK_NAME.value)
        txtAlarmDesc.text = intent.getStringExtra(Keys.TASK_DESC.value)

        mediaPlayer!!.start()

    }

    private fun setFullScreenMode() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
    }

    private val onOkClick = View.OnClickListener {
        cancelAlarm()
        stopPlayer()
        finish()
    }

    private fun cancelAlarm() {
        val reqCode = intent.getIntExtra("ID", -1)
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager?
        val intent = Intent(this, AlarmBroadcastReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, reqCode, intent, 0)
        alarmManager!!.cancel(pendingIntent)
    }

    private fun initViews() {
        img = findViewById(R.id.alarm_img)
        txtAlarmTaskName = findViewById(R.id.txt_alarm_task_name)
        txtAlarmDesc = findViewById(R.id.txt_alarm_description)
        findViewById<Button>(R.id.ok_btn).setOnClickListener(onOkClick)
    }

    private fun initMediaPlayer() {
        mediaPlayer = MediaPlayer.create(this, R.raw.ringtone)
        mediaPlayer!!.setOnCompletionListener {
            mediaPlayer!!.start()
        }
    }

    private fun loadingGIFImage() {
        Glide.with(this).load(R.drawable.task_alarm).into(img)
    }

    private fun stopPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer!!.release()
            mediaPlayer = null
        }
        showSnackBar(InfoMessage.RINGTONE_STOP.message)
    }

    private fun showSnackBar(message: String) {
        val sb = Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
        sb.view.setBackgroundResource(R.color.orange)
        sb.show()
    }

    override fun onStop() {
        super.onStop()
        stopPlayer()
    }

}