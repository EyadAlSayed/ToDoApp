package todo.app.fragment

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        calendar = Calendar.getInstance()
        changeTitle()
        assignViewById()
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
            clearView()

            val x = TaskModel("eyad", "sa", 1, "1111", "1111")
            startNewAlarm(x)
            Snackbar.make(v, "Add Task", Snackbar.LENGTH_SHORT).show()
        }
    }


    private val onDatePickerClick = View.OnClickListener {
        val datePicker = DatePickerFragment()
        datePicker.show(activity?.supportFragmentManager!!, "datePicker")
    }

    private val onTimePickerClick = View.OnClickListener {
        val timePicker = TimePickerFragment()
        timePicker.show(activity?.supportFragmentManager!!, "timePicker")
    }

    private fun changeTitle() {
        val appCompatActivity: AppCompatActivity = activity as AppCompatActivity
        appCompatActivity.title = "Add New Task"
    }

    private fun assignViewById() {
        v.findViewById<Button>(R.id.add_task_btn).setOnClickListener(onAddClick)
        v.findViewById<Button>(R.id.pick_date_btn).setOnClickListener(onDatePickerClick)
        v.findViewById<Button>(R.id.pick_time_btn).setOnClickListener(onTimePickerClick)

        edName = v.findViewById(R.id.ed_name)
        edDesc = v.findViewById(R.id.ed_desc)
        edPrio = v.findViewById(R.id.ed_priority)
    }

    private fun checkInput(): Boolean {
        val errorMessage = "This field is required"
        var isOk = true

        if (edName.length() == 0) {
            edName.error = errorMessage
            isOk = false
        }
        if (edDesc.length() == 0) {
            edDesc.error = errorMessage
            isOk = false
        }
        if (edPrio.length() == 0) {
            edPrio.error = errorMessage
            isOk = false
        }
        if (taskDate.isEmpty()) {
            Snackbar.make(v, "Choose Date", Snackbar.LENGTH_LONG).show()
            isOk = false
        }
        if (taskTime.isEmpty()) {
            Snackbar.make(v, "Choose Time", Snackbar.LENGTH_LONG).show()
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

        val alarmManager = context?.getSystemService(ALARM_SERVICE) as AlarmManager?
        val intent = Intent(
            context,
            AlarmBroadcastReceiver::class.java
        )
        sendTaskObject(intent, taskModel)
        val pendingIntent = PendingIntent.getBroadcast(activity, 0, intent, 0)
        alarmManager?.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
    }

    private fun sendTaskObject(intent: Intent, taskModel: TaskModel) {
        intent.putExtra("NAME", taskModel.name)
        intent.putExtra("DESC", taskModel.description)
        intent.putExtra("PRIO", taskModel.priority)
        intent.putExtra("DATE", taskModel.date)
        intent.putExtra("TIME", taskModel.time)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        taskDate = "$year-$month-$dayOfMonth"

        calendar[Calendar.YEAR] = year
        calendar[Calendar.MONTH] = month - 1 // the months are started from Zero
        calendar[Calendar.DAY_OF_MONTH] = dayOfMonth

    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        taskTime = "$hourOfDay:$minute:00"

        calendar[Calendar.HOUR_OF_DAY] = hourOfDay
        calendar[Calendar.MINUTE] = minute
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MILLISECOND] = 0

    }
}
