package todo.app.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import todo.app.activity.AlarmActivity
import todo.app.enumValue.Keys

class AlarmBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        val alarmIntent = Intent(context, AlarmActivity::class.java).apply {
            this.putExtra(Keys.TASK_NAME.value, intent!!.getStringExtra(Keys.TASK_NAME.value))
            this.putExtra(Keys.TASK_DESC.value, intent.getStringExtra(Keys.TASK_DESC.value))
            this.putExtra(Keys.REQUEST_CODE.value, intent.getIntExtra(Keys.REQUEST_CODE.value, -1))

            /**
             * for more info about the task object that send via intent
             *
             *
             *
             *  this.putExtra(Keys.TASK_PRIO.value,intent.getIntExtra(Keys.TASK_PRIO.value,-1))
             *  this.putExtra(Keys.TASK_DATE.value,intent.getStringExtra(Keys.TASK_DATE.value))
             *  this.putExtra(Keys.TASK_TIME.value,intent.getStringExtra(Keys.TASK_TIME.value))
             *
             * */

            this.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context?.startActivity(alarmIntent)
    }
}
