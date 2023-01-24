package com.example.stajyerbuldeneme1.ui.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.Navigation
import com.example.stajyerbuldeneme1.MainActivity
import com.example.stajyerbuldeneme1.R
import com.example.stajyerbuldeneme1.databinding.FragmentKullaniciProfileBinding
import com.example.stajyerbuldeneme1.databinding.FragmentSirketProfileBinding
import com.example.stajyerbuldeneme1.ui.model.SirketUsers
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso

class SirketProfileFragment : Fragment() {

    private lateinit var tasarim : FragmentSirketProfileBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var firestore : FirebaseFirestore
    private lateinit var storge : FirebaseStorage
    private lateinit var sirketUsers : SirketUsers


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        tasarim = FragmentSirketProfileBinding.inflate(inflater,container,false)
            val view = tasarim.root


        auth = Firebase.auth
        firestore = Firebase.firestore
        storge = Firebase.storage

            getDataSirketUsers()



        tasarim.editTextSirketProfileAd.isEnabled = false
        tasarim.editTextSirketTelefon.isEnabled = false
        tasarim.editTextSirketSehir.isEnabled = false
        tasarim.editTextSirketProfileAciklama.isEnabled = false
        tasarim.editSirketProfilEmail.isEnabled = false

        tasarim.imageViewExit.setOnClickListener {
            auth.signOut()
            val intent = Intent(requireActivity(), MainActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        tasarim.imageViewSirketProfileEdit.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.SirketProfileDuzenleGecis)
        }


            return view

    }


    private fun getDataSirketUsers(){ // verileri fireStore den alma fonksiyonu

        firestore.collection("SirketUsers").addSnapshotListener { value, error ->
            if(error != null){
                // hata var demektir
                Toast.makeText(requireActivity(),"Bir Hata Oluştu!", Toast.LENGTH_LONG).show()
            }else{
                // hata yoksa
                if(value != null){
                    if(!value.isEmpty){
                        // eğer değerler boş değilse

                        val documents = value.documents



                        for(documentSirketProfile in documents){
                            // artık burada document dediğim sey bu uygulama özelinde bir adet post demek
                            val sirketEmail = documentSirketProfile.get("sirketEmail") as String
                            val sirketAd = documentSirketProfile.get("sirketAd") as String
                            val sirketHakkinda = documentSirketProfile.get("sirketHakkinda") as String
                            val sirketSehir = documentSirketProfile.get("sirketSehir") as String
                            val sirketTelefon = documentSirketProfile.get("sirketTelefon") as String
                            val sirketProfilResim = documentSirketProfile.get("downloadUrlSirketProfil") as String


                            sirketUsers = SirketUsers(sirketAd,sirketHakkinda,sirketSehir,sirketTelefon,sirketEmail,sirketProfilResim)



                            val userEmailActive = auth.currentUser?.email as String

                            if(sirketUsers.sirketEmail.equals(userEmailActive)){
                                tasarim.editSirketProfilEmail.setText(sirketUsers.sirketEmail)
                                tasarim.editTextSirketProfileAciklama.setText(sirketUsers.sirketHakkinda)
                                tasarim.editTextSirketProfileAd.setText(sirketUsers.sirketAd)
                                tasarim.editTextSirketSehir.setText(sirketUsers.sirketSehir)
                                tasarim.editTextSirketTelefon.setText(sirketUsers.sirketTelefon)
                                Picasso.get().load(sirketUsers.sirketResim).into(tasarim.imageViewSirketProfile)

                            }
                        }
                    }
                }
            }
        }

    }






}