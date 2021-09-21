package todo.app.fragment

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context.ALARM_SERVICE
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import todo.app.R
import todo.app.broadcast.AlarmBroadcastReceiver
import todo.app.database.FireStoreDB
import todo.app.message.ErrorMessage
import todo.app.message.InfoMessage
import todo.app.model.TaskModel
import java.util.*


class AddTaskFragment : Fragment(), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {

    private lateinit var v: View
    private lateinit var edName: EditText
    private lateinit var edDesc: EditText
    private lateinit var edPrio: EditText
    private lateinit var taskDate: String
    private lateinit var taskTime: String
    private lateinit var calendar: Calendar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        v = inflater.inflate(R.layout.new_task_fragment, container, false)
        initViews()
        calendar = Calendar.getInstance()
        changeTitle()
        return v
    }

    private val onAddClick = View.OnClickListener {
        if (checkInput()) {
            val task = TaskModel(
                edName.text.toString(),
                edDesc.text.toString(),
                edPrio.text.toString().toInt(),
                taskDate,
                taskTime
            )
            FireStoreDB.getCollectionRef().add(task)
            startNewAlarm(task)
            clearView()
            hideKeyBoard()
            showSnackBar(InfoMessage.ADD_TASK.message)
        }

    }


    private val onDatePickerClick = View.OnClickListener {
        val datePicker = DatePickerFragment()
        datePicker.show(requireActivity().supportFragmentManager, "datePicker")
    }

    private val onTimePickerClick = View.OnClickListener {
        val timePicker = TimePickerFragment()
        timePicker.show(requireActivity().supportFragmentManager, "timePicker")
    }

    private fun initViews() {
        v.findViewById<Button>(R.id.add_task_btn).setOnClickListener(onAddClick)
        v.findViewById<Button>(R.id.pick_date_btn).setOnClickListener(onDatePickerClick)
        v.findViewById<Button>(R.id.pick_time_btn).setOnClickListener(onTimePickerClick)

        edName = v.findViewById(R.id.ed_name)
        edDesc = v.findViewById(R.id.ed_desc)
        edPrio = v.findViewById(R.id.ed_priority)
    }

    private fun changeTitle() {
        val appCompatActivity: AppCompatActivity = activity as AppCompatActivity
        appCompatActivity.title = "Add New Task"
    }

    private fun checkInput(): Boolean {
        val errorMessage = "This field is required"
        var isOk = true

        if (edName.text.isEmpty()) {
            edName.error = errorMessage
            isOk = false
        }
        if (edDesc.text.isEmpty()) {
            edDesc.error = errorMessage
            isOk = false
        }
        if (edPrio.text.isEmpty()) {
            edPrio.error = errorMessage
            isOk = false
        }
        if (taskDate.isEmpty()) {
            showSnackBar(ErrorMessage.DATE_NOT_CHOSEN.message)
            isOk = false
        }
        if (taskTime.isEmpty()) {
            showSnackBar(ErrorMessage.TIME_NOT_CHOSEN.message)
            isOk = false
        }
        return isOk
    }

    private fun clearView() {
        edName.text.clear()
        edDesc.text.clear()
        edPrio.text.clear()
        taskTime = ""
        taskDate = ""
    }

    private fun startNewAlarm(taskModel: TaskModel) {

        val reqCode = 0
        val alarmManager = context?.getSystemService(ALARM_SERVICE) as AlarmManager?
        val intent = Intent(
            context,
            AlarmBroadcastReceiver::class.java
        ).apply { this.putExtra("ID",reqCode) }

        sendTaskObject(intent, taskModel)
        val pendingIntent = PendingIntent.getBroadcast(activity, reqCode, intent, 0)
        alarmManager?.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
    }

    private fun hideKeyBoard() {

        val manager = context?.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        manager.hideSoftInputFromWindow(edName.applicationWindowToken, 0)
        manager.hideSoftInputFromWindow(edDesc.applicationWindowToken, 0)
        manager.hideSoftInputFromWindow(edPrio.applicationWindowToken, 0)

    }

    private fun showSnackBar(message:String){
        val sb = Snackbar.make(v, message, Snackbar.LENGTH_SHORT)
        sb.view.setBackgroundResource(R.drawable.rounded_shape_snackbar)
        sb.show()
    }
    private fun sendTaskObject(intent: Intent, taskModel: TaskModel) {
        intent.putExtra("NAME", taskModel.name)
        intent.putExtra("DESC", taskModel.description)
        intent.putExtra("PRIO", taskModel.priority)
        intent.putExtra("DATE", taskModel.date)
        intent.putExtra("TIME", taskModel.time)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        taskDate = "$year-${month+1}-$dayOfMonth"

        changePickerButtonColor(v.findViewById(R.id.pick_date_btn))

        calendar[Calendar.YEAR] = year
        calendar[Calendar.MONTH] = month
        calendar[Calendar.DAY_OF_MONTH] = dayOfMonth
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        taskTime = "$hourOfDay:$minute:00"

        changePickerButtonColor(v.findViewById(R.id.pick_time_btn))

        calendar[Calendar.HOUR_OF_DAY] = hourOfDay
        calendar[Calendar.MINUTE] = minute
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MILLISECOND] = 0
    }

    private fun changePickerButtonColor(button: Button){
        button.setBackgroundResource(R.color.orange)
    }
}
