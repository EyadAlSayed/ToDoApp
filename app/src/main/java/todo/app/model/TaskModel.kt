package todo.app.model

class TaskModel(val name: String,val description:String, val priority: Int, val date: String, val time: String) {
    constructor():this("","",0,"","")
}