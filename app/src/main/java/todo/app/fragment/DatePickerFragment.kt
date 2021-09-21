package todo.app.fragment

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import org.koin.android.ext.android.inject
import todo.app.R
import java.util.*


class DatePickerFragment:DialogFragment(){


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
       val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val addTaskFragment:AddTaskFragment by inject()

        return DatePickerDialog(requireActivity(),R.style.DatePickerDialogTheme,addTaskFragment,year,month,day)


    }
}