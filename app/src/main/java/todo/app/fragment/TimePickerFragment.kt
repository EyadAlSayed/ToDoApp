package todo.app.fragment

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import todo.app.R
import java.util.*


class TimePickerFragment(private val singleTaskFragment: SingleTaskFragment) :DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        return TimePickerDialog(requireActivity(),R.style.TimePickerTheme,singleTaskFragment, hour, minute, false)
    }
}