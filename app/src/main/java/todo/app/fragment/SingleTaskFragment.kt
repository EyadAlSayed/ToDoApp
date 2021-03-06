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
import todo.app.data.ItemList
import todo.app.database.FireStoreDB
import todo.app.enumValue.ErrorMessage
import todo.app.enumValue.InfoMessage
import todo.app.enumValue.Keys
import todo.app.model.TaskModel
import java.util.*
import javax.inject.Inject

class SingleTaskFragment @Inject constructor() : Fragment(), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {

    private lateinit var v: View
    private lateinit var edName: EditText
    private lateinit var edDesc: EditText
    private lateinit var edPrio: EditText
    private lateinit var taskDate: String
    private lateinit var taskTime: String
    private lateinit var calendar: Calendar
    private var intentFlag : Int? = null
    private lateinit var intentTaskModel: TaskModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        v = inflater.inflate(R.layout.single_task_fragment, container, false)

        intentFlag = requireArguments().getInt(Keys.FLAG.value)
        if(intentFlag != 0) intentTaskModel = requireArguments().getSerializable(Keys.TASK_MODEL.value) as TaskModel

        calendar = Calendar.getInstance()
        initViews()
        changeActionBarTitle()
        return v
    }

    private val onAddClick = View.OnClickListener {
        if (checkInput()) {
            val id = FireStoreDB.getTaskCollectionRef().document().id
            val task = TaskModel(
                id,
                edName.text.toString(),
                edDesc.text.toString(),
                edPrio.text.toString().toInt(),
                taskDate,
                taskTime
            )

            FireStoreDB.setTaskDoc(id, task)
            ItemList.addItem(task)
            startNewAlarm(task)
            clearView()
            hideKeyBoard()
            showSnackBar(InfoMessage.ADD_TASK.message)
        }
    }

    private val onUpdateClick = View.OnClickListener {
       if (checkInput()) {
            val id = intentTaskModel.id
            val task = TaskModel(
                id,
                edName.text.toString(),
                edDesc.text.toString(),
                edPrio.text.toString().toInt(),
                taskDate,
                taskTime
            )
            FireStoreDB.setTaskDoc(id, task)
            startNewAlarm(task)
            clearView()
            hideKeyBoard()
            showSnackBar(InfoMessage.UPDATE_TASK.message)
        }

    }

    private val onDatePickerClick = View.OnClickListener {
        val datePicker = DatePickerFragment(this)
        datePicker.show(requireActivity().supportFragmentManager, "datePicker")
    }

    private val onTimePickerClick = View.OnClickListener {
        val timePicker = TimePickerFragment(this)
        timePicker.show(requireActivity().supportFragmentManager, "timePicker")
    }

    private fun initViews() {

        v.findViewById<Button>(R.id.pick_date_btn).setOnClickListener(onDatePickerClick)
        v.findViewById<Button>(R.id.pick_time_btn).setOnClickListener(onTimePickerClick)

        edName = v.findViewById(R.id.ed_name)
        edDesc = v.findViewById(R.id.ed_desc)
        edPrio = v.findViewById(R.id.ed_priority)

        if (intentFlag == 1) {
            initUpdateView()
            showUpdateTaskButton()
        }
        else showAddTaskButton()

    }

    private fun initUpdateView() {

        edName.setText(intentTaskModel.name)
        edDesc.setText(intentTaskModel.description)
        edPrio.setText(intentTaskModel.priority.toString())
        taskDate = intentTaskModel.date
        taskTime = intentTaskModel.time
    }

    private fun changeActionBarTitle() {
        val appCompatActivity: AppCompatActivity = requireActivity() as AppCompatActivity
        val actionBarTitle = if(intentFlag == 0) "New Task" else "Update Task"
        appCompatActivity.supportActionBar?.title = actionBarTitle
    }

    private fun showAddTaskButton() {
        val button = v.findViewById<Button>(R.id.add_task_btn)
        button.visibility = View.VISIBLE
        button.setOnClickListener(onAddClick)
        v.findViewById<Button>(R.id.update_task_btn).visibility = View.GONE
    }

    private fun showUpdateTaskButton() {
        val button = v.findViewById<Button>(R.id.update_task_btn)
        button.visibility = View.VISIBLE
        button.setOnClickListener(onUpdateClick)
        v.findViewById<Button>(R.id.add_task_btn).visibility = View.GONE
    }

    private fun checkInput(): Boolean {
        var isOk = true

        if (edName.text.isEmpty()) {
            edName.error = ErrorMessage.FIELD_REQUIRED.message
            isOk = false
        }
        if (edDesc.text.isEmpty()) {
            edDesc.error = ErrorMessage.FIELD_REQUIRED.message
            isOk = false
        }
        if (edPrio.text.isEmpty()) {
            edPrio.error = ErrorMessage.FIELD_REQUIRED.message
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
        changePickerButtonRes(v.findViewById(R.id.pick_date_btn), R.drawable.rounded_shape)
        changePickerButtonRes(v.findViewById(R.id.pick_time_btn), R.drawable.rounded_shape)
    }

    private fun startNewAlarm(taskModel: TaskModel) {

        val reqCode = ItemList.getItemListSize()
        val alarmManager = requireActivity().getSystemService(ALARM_SERVICE) as AlarmManager?
        val intent = Intent(
            requireActivity(),
            AlarmBroadcastReceiver::class.java
        ).apply { this.putExtra(Keys.REQUEST_CODE.value, reqCode) }

        sendTaskObject(intent, taskModel)
        val pendingIntent = PendingIntent.getBroadcast(requireActivity(), reqCode, intent, 0)
        alarmManager?.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
    }

    private fun hideKeyBoard() {

        val manager = context?.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        manager.hideSoftInputFromWindow(edName.applicationWindowToken, 0)
        manager.hideSoftInputFromWindow(edDesc.applicationWindowToken, 0)
        manager.hideSoftInputFromWindow(edPrio.applicationWindowToken, 0)

    }

    private fun showSnackBar(message: String) {
        val sb = Snackbar.make(v, message, Snackbar.LENGTH_SHORT)
        sb.view.setBackgroundResource(R.drawable.rounded_shape_snackbar)
        sb.show()
    }

    private fun sendTaskObject(intent: Intent, taskModel: TaskModel) {
        intent.putExtra(Keys.TASK_NAME.value, taskModel.name)
        intent.putExtra(Keys.TASK_DESC.value, taskModel.description)
        intent.putExtra(Keys.TASK_PRIO.value, taskModel.priority)
        intent.putExtra(Keys.TASK_DATE.value, taskModel.date)
        intent.putExtra(Keys.TASK_TIME.value, taskModel.time)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        taskDate = "$year-${month + 1}-$dayOfMonth"

        changePickerButtonRes(v.findViewById(R.id.pick_date_btn), R.color.orange)

        calendar[Calendar.YEAR] = year
        calendar[Calendar.MONTH] = month
        calendar[Calendar.DAY_OF_MONTH] = dayOfMonth
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        taskTime = "${getTwoDigitNum(hourOfDay)}:${getTwoDigitNum(minute)}:00"

        changePickerButtonRes(v.findViewById(R.id.pick_time_btn), R.color.orange)

        calendar[Calendar.HOUR_OF_DAY] = hourOfDay
        calendar[Calendar.MINUTE] = minute
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MILLISECOND] = 0
    }

    private fun changePickerButtonRes(button: Button, res: Int) {
        button.setBackgroundResource(res)
    }
    private fun getTwoDigitNum(num:Int):String{
        if(num <= 9) return  "0$num"
        return num.toString()
    }
}
