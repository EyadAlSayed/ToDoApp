package todo.app.model

import java.io.Serializable


class TaskModel(val name: String,val description:String, val priority: Int, val date: String, val time: String):Serializable  {
    constructor():this("","",0,"","")
}