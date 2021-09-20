package todo.app.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import todo.app.activity.AlarmActivity

class AlarmBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        val alarmIntent = Intent(context, AlarmActivity::class.java).apply {
           this.putExtra("NAME",intent!!.getStringExtra("NAME"))
           this.putExtra("DESC",intent.getStringExtra("DESC"))

            // for more info about the task object that send via intent


         /*  this.putExtra("PRIO",intent.getIntExtra("PRIO",0))
           this.putExtra("DATE",intent.getStringExtra("DATE"))
           this.putExtra("TIME",intent.getStringExtra("TIME"))*/
        }
        context?.startActivity(alarmIntent)
    }
}
