package todo.app.broadcast


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import todo.app.activity.AlarmActivity

class AlarmBroadcastReceiver : BroadcastReceiver() {



    override fun onReceive(context: Context?, intent: Intent?) {
        val intent = Intent(context, AlarmActivity::class.java)
        context?.startActivity(intent)
    }
}
