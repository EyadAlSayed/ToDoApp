package todo.app.model

import java.io.Serializable

data class TaskModel(var id:String, val name: String, val description:String, val priority: Int, val date: String, val time: String):Serializable  {

    constructor():this("","","",0,"","")

    override fun toString(): String {
        return "TaskModel(id='$id', name='$name', description='$description', priority=$priority, date='$date', time='$time')"
    }


}