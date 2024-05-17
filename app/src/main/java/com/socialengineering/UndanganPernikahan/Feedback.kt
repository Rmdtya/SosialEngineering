package com.socialengineering.UndanganPernikahan

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.socialengineering.UndanganPernikahan.databinding.FragmentFeedbackBinding


class Feedback : Fragment() {
    private lateinit var binding : FragmentFeedbackBinding
    private val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFeedbackBinding.inflate(inflater, container, false)
        val rootView = binding.root

        binding.btnConfirm.setOnClickListener {
            if(binding.inp.text.isNotEmpty()){
                WriteFeedBack(binding.inp.text.toString())
            }else{
                Toast.makeText(requireContext(), "Feedback Tidak Boleh Kosong", Toast.LENGTH_SHORT).show()
            }
        }
        return rootView
    }

    private fun WriteFeedBack(text : String){
        val userDocRef = db.collection("users").document("statistik")

        userDocRef.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val feedback = documentSnapshot.get("feedback") as? ArrayList<String>

                    if (feedback != null) {
                        feedback.add(text)
                        userDocRef.update("feedback", feedback)
                    }

                    Toast.makeText(requireContext(), "Feedback Berhasil Dikirim", Toast.LENGTH_SHORT).show()

                    val fragmentManager = requireActivity().supportFragmentManager
                    fragmentManager.popBackStack()

                }
            }
            .addOnFailureListener {e ->
                //Toast.makeText(this, "Gagal: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("FirestoreError", "Gagal: ${e.message}") // Menampilkan pesan kesalahan ke Logcat
            }
    }

}