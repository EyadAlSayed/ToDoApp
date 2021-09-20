package todo.app.database

import com.google.firebase.firestore.FirebaseFirestore

class FireStoreDB {
    companion object {
        fun getInstance() = FirebaseFirestore.getInstance()
        fun getCollectionRef() = FirebaseFirestore.getInstance().collection("Tasks")
    }
}