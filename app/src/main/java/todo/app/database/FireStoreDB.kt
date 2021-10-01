package todo.app.database

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import todo.app.model.TaskModel

object FireStoreDB {

    private const val collectionPath = "Users"
    private const val subCollection = "Tasks"

    var lastVisibleDoc: DocumentSnapshot? = null
    var userId:String? = null

    fun getNewTaskCollectionId() = FirebaseFirestore.getInstance().collection(collectionPath).document().id
    fun getTaskCollectionRef() = FirebaseFirestore.getInstance().collection(collectionPath).document(userId!!).collection(subCollection)
    fun getQueryByLimit(limit: Long) = getTaskCollectionRef().orderBy("id").limit(limit)
    fun getQueryStartAfterLastVisibleDocWithLimit(field: String, fieldValue: String, limit: Long) = getTaskCollectionRef().orderBy(field).startAfter(fieldValue).limit(limit)
    fun setTaskDoc(docID: String, task: TaskModel) = getTaskCollectionRef().document(docID).set(task)  // it used to add and update the Task
    fun deleteDoc(docID: String) = getTaskCollectionRef().document(docID).delete()

}