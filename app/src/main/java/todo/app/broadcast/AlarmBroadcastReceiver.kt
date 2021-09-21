package todo.app.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import todo.app.activity.AlarmActivity

class AlarmBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        val alarmIntent = Intent(context, AlarmActivity::class.java).apply {
            this.putExtra("NAME", intent!!.getStringExtra("NAME"))
            this.putExtra("DESC", intent.getStringExtra("DESC"))
            this.putExtra("ID", intent.getIntExtra("ID", -1))

            /**
             * for more info about the task object that send via intent
             *
             *
             *
             *  this.putExtra("PRIO",intent.getIntExtra("PRIO",-1))
             *  this.putExtra("DATE",intent.getStringExtra("DATE"))
             *  this.putExtra("TIME",intent.getStringExtra("TIME"))
             *
             * */

            this.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context?.startActivity(alarmIntent)
    }
}
