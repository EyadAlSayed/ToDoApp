package todo.app.data

import todo.app.model.TaskModel

object ItemList {
    private val itemList:ArrayList<TaskModel> = ArrayList()
    fun getList() = itemList
    fun getItemListSize() = itemList.size
    fun getItemByPosition(pos:Int) = itemList[pos]
    fun removeItemByPosition(pos:Int) { itemList.removeAt(pos) }
    fun removeAllItem() = itemList.removeAll(itemList)
    fun addItem(taskModel: TaskModel) { if(taskModel !in itemList) itemList.add(taskModel) }

}