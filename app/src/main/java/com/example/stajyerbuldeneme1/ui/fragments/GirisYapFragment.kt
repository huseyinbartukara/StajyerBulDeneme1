package com.example.stajyerbuldeneme1.ui.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.stajyerbuldeneme1.OlayActivity
import com.example.stajyerbuldeneme1.R
import com.example.stajyerbuldeneme1.SirketOlayActivity
import com.example.stajyerbuldeneme1.databinding.FragmentGirisYapBinding
import com.example.stajyerbuldeneme1.ui.model.SirketUsers
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class GirisYapFragment : Fragment() {

    private lateinit var tasarim : FragmentGirisYapBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var firestore : FirebaseFirestore
    private lateinit var storge : FirebaseStorage
    private lateinit var sirketUsersArrayList : ArrayList<SirketUsers>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        tasarim = FragmentGirisYapBinding.inflate(inflater,container,false)
        val view = tasarim.root

        auth = Firebase.auth
        firestore = Firebase.firestore
        storge = Firebase.storage








        tasarim.textViewKaydol.setOnClickListener {
            kayitOlText(it)
        }

        tasarim.textViewKaydolSirket.setOnClickListener {
            kayitOlSirketText(it)
        }

        tasarim.buttonGirisYap.setOnClickListener {

            val email = tasarim.girisyapEmail.text.toString().trim()
            val sifre = tasarim.girisyapSifre.text.toString().trim()


            if(email.equals("") || sifre.equals("")){
                Toast.makeText(requireActivity(),"Email ya da Şifre Giriniz!",Toast.LENGTH_LONG).show()
            }else{
                auth.signInWithEmailAndPassword(email,sifre).addOnSuccessListener {


                    val user = auth.currentUser!!.uid

                    val refSirket = firestore.collection("SirketUsers").document(user)
                    val refStajyer = firestore.collection("StajyerUsers").document(user)

                    refStajyer!!.get().addOnSuccessListener {

                        val stajyerMail = it.get("stjyerEmail").toString()

                        if(email.equals(stajyerMail)){
                            olayActivityGec(tasarim.buttonGirisYap)
                        }
                    }



                    refSirket!!.get().addOnSuccessListener {

                        val sirketMail = it.get("sirketEmail").toString()

                        if(email.equals(sirketMail)){
                            sirketOlayActivityGec(tasarim.buttonGirisYap)

                        } else{
                            olayActivityGec(tasarim.buttonGirisYap)
                        }


                    }
                }.addOnFailureListener {
                    Toast.makeText(requireActivity(),"Giriş Yaparken Hata Oluştu",Toast.LENGTH_LONG).show()
                }
            }
        }
        return view
    }





    fun kayitOlText(it:View){
        Navigation.findNavController(it).navigate(R.id.kayitOlFragmentGecis)
    }

    fun kayitOlSirketText(it:View){
        Navigation.findNavController(it).navigate(R.id.kayitOlSirketFragmentGecis)
    }

    fun olayActivityGec(it:View){
        val intent = Intent(requireActivity(), OlayActivity::class.java)
        startActivity(intent)
    }

    fun sirketOlayActivityGec(it: View){
        val intent = Intent(requireActivity(),SirketOlayActivity::class.java)
        startActivity(intent)
    }


}