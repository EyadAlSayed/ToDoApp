package todo.app.fragment

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import org.koin.android.ext.android.inject
import java.util.*



class TimePickerFragment :DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)
        val addTaskFragment:AddTaskFragment by inject()


        return TimePickerDialog(activity,addTaskFragment, hour, minute, false)
    }
}