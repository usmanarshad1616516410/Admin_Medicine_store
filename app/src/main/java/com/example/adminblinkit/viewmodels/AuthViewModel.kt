package com.example.adminblinkit.viewmodels

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.adminblinkit.models.Users
import com.example.adminblinkit.utils.Utils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.flow.MutableStateFlow

class AuthViewModel() : ViewModel() {

    private val auth : FirebaseAuth=FirebaseAuth.getInstance()


    private var _isSignInSuccessfully = MutableStateFlow(false)
    var isSignInSuccessfully = _isSignInSuccessfully

    private var _isSignUpSuccessfully = MutableStateFlow(false)
    var isSignUpSuccessfully = _isSignUpSuccessfully


    private var _isPasswordReset = MutableLiveData(false)
    var isPasswordReset = _isPasswordReset

    private var _isCurrentUser = MutableStateFlow(false)
    var isCurrentUser = _isCurrentUser

    init {
        if (Utils.getAuthInstance().currentUser != null){
            isCurrentUser.value = true
        }
//        Utils.getAuthInstance().currentUser?.let {
//            isCurrentUser.value = true
//        }

    }


    // For Email Authentication===========================



    suspend fun createUserWithEmail(email: String, password: String, users: Users) {

        FirebaseMessaging.getInstance().token.addOnCompleteListener{
            users.adminToken=it.result

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        users.uid=Utils.currentUser()
                        // Sign in success, update UI with the signed-in user's information
                        FirebaseDatabase.getInstance().getReference("Admins")
                            .child(Utils.currentUser()!!).child("AdminInfo").setValue(users)
                        _isSignUpSuccessfully.value = true
                        Log.d("GGG", "createUserWithEmail:${users.uid}")

                    } else {
                        _isSignUpSuccessfully.value = false
                        // If sign in fails, display a message to the user.
                        Log.w(ContentValues.TAG, "createUserWithEmail:failure", it.exception)

                    }
                }
        }
    }


    fun signInWithEmail(email: String, password: String) {

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    _isSignInSuccessfully.value = true
                    Log.d(ContentValues.TAG, "signInWithEmail:success")


                } else {
                    _isSignInSuccessfully.value = false
                    // If sign in fails, display a message to the user.
                    Log.w(ContentValues.TAG, "signInWithEmail:failure", task.exception)

                }
            }


    }


    fun resetPassword(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Password reset email sent successfully
                    // You can handle success or perform additional actions here
                    _isPasswordReset.value = true
                } else {
                    _isPasswordReset.value = false
                    // Password reset email sending failed
                    // You can handle failure or display an error message to the user
                }
            }
    }

}





