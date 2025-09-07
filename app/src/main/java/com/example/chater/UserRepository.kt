package com.example.chater

import android.util.Log
import com.example.chater.Data.Result
import com.example.chater.Data.User
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepository(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
){
    suspend fun signUp(
        email: String, password: String, nick: String): Result<Boolean> =
        try {
            auth.createUserWithEmailAndPassword(email,password).await()
            val user = User(nick,email)
            saveUserToFirestore(user)
            Result.Success(true)
        } catch (e: Exception){
            Result.Error(e)
        }

    suspend fun login(email: String, password: String): Result<Boolean> =
        try {
            val authResult = auth.signInWithEmailAndPassword(email,password).await()
            if(authResult.user?.email == email){
                Result.Success(true)
            }else{
                auth.signOut()
                Result.Error(Exception("Auth failed"))
            }
        } catch(e: Exception){
            Result.Error(e)
        }

    private suspend fun saveUserToFirestore(user: User){
        firestore.collection("users").document(user.email).set(user).await()
    }

    suspend fun getCurrentUser(): Result<User> = try {
        val uid = auth.currentUser?.email
        if (uid != null) {
            val userDocument = firestore.collection("users").document(uid).get().await()
            val user = userDocument.toObject(User::class.java)
            if (user != null) {
                Log.d("user2","$uid")
                Result.Success(user)
            } else {
                Result.Error(Exception("User data not found"))
            }
        } else {
            Result.Error(Exception("User not authenticated"))
        }
    } catch (e: Exception) {
        Result.Error(e)
    }
}