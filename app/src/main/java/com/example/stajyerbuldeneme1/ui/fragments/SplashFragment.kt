package com.example.stajyerbuldeneme1.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.stajyerbuldeneme1.OlayActivity
import com.example.stajyerbuldeneme1.R
import com.example.stajyerbuldeneme1.SirketOlayActivity
import com.example.stajyerbuldeneme1.databinding.FragmentKayitOlBinding
import com.example.stajyerbuldeneme1.databinding.FragmentSplashBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class SplashFragment : Fragment() {

    private lateinit var tasarim : FragmentSplashBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var firestore : FirebaseFirestore
    private lateinit var storge : FirebaseStorage


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        tasarim = FragmentSplashBinding.inflate(inflater,container,false)
        val view = tasarim.root

        auth = Firebase.auth
        firestore = Firebase.firestore
        storge = Firebase.storage

        val currentUser = auth.currentUser


        Handler(Looper.myLooper()!!).postDelayed({

            if(currentUser != null){

                val currentUserEmail = auth.currentUser!!.email

                // bu if çalışırsa içerde giriş yapılı bir kullanıcı var demektir

                val user = auth.currentUser!!.uid

                val refStajyer = firestore.collection("StajyerUsers").document(user)
                val refSirket = firestore.collection("SirketUsers").document(user)

                refStajyer!!.get().addOnSuccessListener {
                    // acık olan stajyer hesap
                    val stajyerMail = it.get("stjyerEmail").toString()
                    if(currentUserEmail.equals(stajyerMail)){
                        val intent = Intent(requireActivity(), OlayActivity::class.java)
                        startActivity(intent)
                        requireActivity().finish()
                    }
                }

                refSirket!!.get().addOnSuccessListener {
                    // acık olan sirket hesap
                    val sirketMail = it.get("sirketEmail").toString()
                    if(currentUserEmail.equals(sirketMail)){
                        val intent = Intent(requireActivity(), SirketOlayActivity::class.java)
                        startActivity(intent)
                        requireActivity().finish()
                    }
                }
            }else{
                findNavController().navigate(R.id.splashScreenGecis)
            }
        },3000)



        return view

    }
}