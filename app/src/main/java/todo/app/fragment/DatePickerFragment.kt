package todo.app.fragment

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import todo.app.R
import java.util.*


class DatePickerFragment(private val singleTaskFragment: SingleTaskFragment):DialogFragment(){

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
       val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(requireActivity(),R.style.DatePickerDialogTheme,singleTaskFragment,year,month,day)
        datePickerDialog.datePicker.minDate = System.currentTimeMillis()-1000
        return datePickerDialog


    }
}