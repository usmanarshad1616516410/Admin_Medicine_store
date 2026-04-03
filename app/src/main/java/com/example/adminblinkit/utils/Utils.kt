package com.example.adminblinkit.utils

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.Toast
import com.example.adminblinkit.databinding.ProgressDialogBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

object Utils {

    private var dialog : AlertDialog? = null

    fun showDialog(context: Context, message: String){
        val progress = ProgressDialogBinding.inflate(LayoutInflater.from(context))
        progress.tvMessage.text = message
        dialog = AlertDialog.Builder(context).setView(progress.root).setCancelable(false).create()
        dialog!!.show()
    }

    fun hideDialog(){
        dialog?.dismiss()
    }

    fun showToast(context: Context, message: String){
        Toast.makeText(context,message, Toast.LENGTH_SHORT).show()
    }
    private var firebaseAuthInstance: FirebaseAuth? = null
    fun getAuthInstance(): FirebaseAuth {
        if (firebaseAuthInstance  == null){
            firebaseAuthInstance = FirebaseAuth.getInstance()
        }
        return firebaseAuthInstance!!
    }

    fun currentUser(): String? {
        val user = FirebaseAuth.getInstance().currentUser?.uid
        return user
    }

    fun getRandomId(): String{
        return (1 .. 25).map{(('A' .. 'Z') + ('a' .. 'z') + ('0' .. '9')).random()}.joinToString("")
    }

    fun itemPushKey():String{
        // product push key
         val productRef = FirebaseDatabase.getInstance().getReference("Admins")
         val newItemKey: String? = productRef.push().key
        return newItemKey!!
    }

    fun timeStamp(): Long {
        val timestamp = System.currentTimeMillis()
        return timestamp
    }

}