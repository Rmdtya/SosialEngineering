package com.socialengineering.UndanganPernikahan

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    companion object {
        private const val RC_SIGN_IN = 9001
    }

    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser
        if (currentUser != null) {
            // The user is already signed in, navigate to MainActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // finish the current activity to prevent the user from coming back to the SignInActivity using the back button
        }

        val signInButton = findViewById<LinearLayout>(R.id.btn_login)
        signInButton.setOnClickListener {
            signIn()
        }
    }

    private fun signIn() {
        val googleSignInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN)
        googleSignInClient.signOut().addOnCompleteListener {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            val newGoogleSignInClient = GoogleSignIn.getClient(this, gso)
            val signInIntent = newGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                // Ubah pesan toast menjadi memberi tahu bahwa email verifikasi akan dikirim
                Toast.makeText(this, "Email verifikasi akan dikirim ke ${account?.email}", Toast.LENGTH_SHORT).show()
                // Mengirim email verifikasi
                sendEmailVerification(account?.email!!)
                // Melakukan otentikasi Firebase setelah email verifikasi dikirim
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendEmailVerification(email: String) {
        // Kirim email verifikasi ke alamat email yang telah login
        val user = auth.currentUser
        user?.sendEmailVerification()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Email verifikasi telah dikirim", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Gagal mengirim email verifikasi", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    // Tambahkan pengecekan apakah email sudah terverifikasi atau belum
                    if (user?.isEmailVerified == true) {
                        Toast.makeText(this, "Signed in as ${user.displayName}", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "Harap verifikasi alamat email Anda terlebih dahulu", Toast.LENGTH_SHORT).show()
                        auth.signOut()
                    }
                } else {
                    Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun CheckOrCreateUserDocument(userID: String, email: String) {

        try {
            val userDocument = db.collection("users").document(userID)

            userDocument.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    if (document != null && document.exists()) {
                        // Dokumen dengan userID sebagai document ID sudah ada, lewati proses.
                        // Anda dapat menambahkan logika atau tindakan lain di sini jika diperlukan.
                        //LoadDataFromFirestore(userID, email)
                    } else {
                        // Dokumen dengan userID sebagai document ID belum ada, buat dokumen baru.
                        val userData = hashMapOf(
                            "name" to email,
                            "email" to email,
                        )
                        userDocument.set(userData)
                            .addOnSuccessListener {
                                // Berhasil membuat dokumen baru.
                            }
                            .addOnFailureListener { error ->
                                // Gagal membuat dokumen baru, tangani error jika diperlukan.
                                Toast.makeText(this, error.localizedMessage, Toast.LENGTH_SHORT).show()
                            }
                    }
                } else {
                    // Gagal mendapatkan dokumen, tangani error jika diperlukan.
                    Toast.makeText(this, task.exception?.localizedMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }catch (e:Exception){
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
        }

    }
}