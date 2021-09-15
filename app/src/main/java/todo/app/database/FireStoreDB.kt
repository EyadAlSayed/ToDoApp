package todo.app.database

import com.google.firebase.firestore.FirebaseFirestore

class FireStoreDB {
    companion object {
        public fun getInstance() = FirebaseFirestore.getInstance()
        public fun getCollectionRef() = FirebaseFirestore.getInstance().collection("Tasks")
    }


}