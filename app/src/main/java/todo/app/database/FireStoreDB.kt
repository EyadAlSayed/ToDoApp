package todo.app.database

import com.google.firebase.firestore.FirebaseFirestore
import todo.app.model.TaskModel

object FireStoreDB {

        private const val collectionPath = "Tasks"
        fun getInstance() = FirebaseFirestore.getInstance()
        fun getCollectionRef() = FirebaseFirestore.getInstance().collection(collectionPath)
        fun deleteDoc(docID:String) = FirebaseFirestore.getInstance().collection(collectionPath).document(docID).delete()
        fun setDoc(docID: String,task:TaskModel) = FirebaseFirestore.getInstance().collection(collectionPath).document(docID).set(task)

}