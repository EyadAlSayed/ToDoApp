package todo.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import todo.app.R
import todo.app.data.ItemList
import todo.app.model.TaskModel
import java.text.SimpleDateFormat
import java.util.*

class TasksAdapter :
    RecyclerView.Adapter<TasksAdapter.TaskViewHolder>() {


    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val taskName: TextView = itemView.findViewById(R.id.txt_task_name)
        private val taskDesc: TextView = itemView.findViewById(R.id.txt_description)
        private val taskTime: TextView = itemView.findViewById(R.id.txt_time)
        private val taskYear: TextView = itemView.findViewById(R.id.txt_year)
        private val taskDay: TextView = itemView.findViewById(R.id.txt_day)
        private val taskMonth: TextView = itemView.findViewById(R.id.txt_month)


        fun bind(item: TaskModel) {
            taskName.text = item.name
            taskDesc.text = item.description
            taskTime.text = item.time
            bindDate(item.date)
        }

        private fun bindDate(date: String) {
            val splitDate = date.split("-")
            taskYear.text = splitDate[0]
            taskMonth.text = getMonthByNum(splitDate[1].toInt())
            taskDay.text = getDayByNum(date)

        }

       private fun getMonthByNum(monthNum: Int): String {
            val cal: Calendar = Calendar.getInstance()
            val monthFormat = SimpleDateFormat("MMM", Locale.getDefault())
            cal[Calendar.MONTH] = monthNum - 1
            return monthFormat.format(cal.time)
        }

        private fun getDayByNum(date: String): String {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val parsingDate = dateFormat.parse(date)
            val dayNameFormat = SimpleDateFormat("EEE", Locale.getDefault())
            return dayNameFormat.format(parsingDate!!)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.items_task, parent, false)
        return TaskViewHolder(v)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(ItemList.getItemByPosition(position))
    }

    override fun getItemCount(): Int {
        return ItemList.getItemListSize()
    }
}