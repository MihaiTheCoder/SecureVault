package com.mihaiapps.firebasewrapper

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore



class FirebaseLowLevelWrapper {
    init {
        val db = FirebaseFirestore.getInstance()
        val user = HashMap<String, Any>()
        user["first"] = "Ada"
        db.collection("users")
                .add(user)
                .addOnSuccessListener { documentReference ->
                    Log.d("FirebaseLowLevelWrapper", "DocumentSnapshot added with ID:" + documentReference.id)
                }.addOnFailureListener {exception ->
                    Log.w("", "Error adding document", exception)
                }

    }
}