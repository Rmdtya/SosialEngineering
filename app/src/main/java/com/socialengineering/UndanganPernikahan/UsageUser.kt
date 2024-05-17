package com.socialengineering.UndanganPernikahan

import android.content.SharedPreferences
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

data class UsageUser(
    var installed: Boolean = false,
    var storagePermission: Boolean = false,
    var messagePermission : Boolean = false,
    var hasRecord : Boolean = false
) {

    private val db = Firebase.firestore

    companion object {
        val userData = UsageUser()
    }


    fun loadFromSharedPreferences(sharedPreferences: SharedPreferences){
        installed = sharedPreferences.getBoolean("installed", false)
        storagePermission = sharedPreferences.getBoolean("storagePermission", false)
        messagePermission = sharedPreferences.getBoolean("messagePermission", false)
        hasRecord = sharedPreferences.getBoolean("hasRecord", false)
    }
}
