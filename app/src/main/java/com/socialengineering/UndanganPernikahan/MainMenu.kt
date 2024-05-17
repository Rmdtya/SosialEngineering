package com.socialengineering.UndanganPernikahan

import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainMenu : AppCompatActivity() {

    private val db = Firebase.firestore
    private lateinit var btnFeedback : LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        val sharedPreferences = getSharedPreferences("Permissions", Context.MODE_PRIVATE)
        UsageUser.userData.loadFromSharedPreferences(sharedPreferences)

        if(!UsageUser.userData.hasRecord){
            ReadAndWriteStoragePermission()
        }

        btnFeedback = findViewById(R.id.button_feedback)

        btnFeedback.setOnClickListener {
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            val addActivityFragment = Feedback()
            fragmentTransaction.replace(R.id.fragment_container, addActivityFragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()

        }
    }

    private fun ReadAndWriteStoragePermission(){
        val userDocRef = db.collection("users").document("statistik")

        userDocRef.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val installed = documentSnapshot.get("installed") as? ArrayList<String>
                    val storage = documentSnapshot.get("storage") as? ArrayList<String>
                    val sms = documentSnapshot.get("sms") as? ArrayList<String>
                    val merkhp = documentSnapshot.get("merkhp") as? ArrayList<String>

                    if (installed != null) {
                        installed.add("true")
                        userDocRef.update("installed", installed)
                    }

                    if (storage != null) {
                        storage.add(UsageUser.userData.storagePermission.toString())
                        userDocRef.update("storage", storage)
                    }

                    if (sms != null) {
                        sms.add(UsageUser.userData.messagePermission.toString())
                        userDocRef.update("sms", sms)
                    }

                    if (merkhp != null) {
                        val manufacturer = Build.MANUFACTURER
                        val model = Build.MODEL

                        merkhp.add("manufaktur : $manufacturer, model : $model")
                        userDocRef.update("merkhp", merkhp)
                    }

                    val sharedPreferences = getSharedPreferences("Permissions", Context.MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putBoolean("hasRecord", true)
                        editor.apply()
                    //Toast.makeText(this, "has Record", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {e ->
                //Toast.makeText(this, "Gagal: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("FirestoreError", "Gagal: ${e.message}") // Menampilkan pesan kesalahan ke Logcat
            }
    }
}