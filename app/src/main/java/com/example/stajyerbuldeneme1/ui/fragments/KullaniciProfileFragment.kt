package com.example.stajyerbuldeneme1.ui.fragments

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.isVisible
import androidx.navigation.Navigation
import com.example.stajyerbuldeneme1.MainActivity
import com.example.stajyerbuldeneme1.R
import com.example.stajyerbuldeneme1.databinding.FragmentKullaniciProfileBinding
import com.example.stajyerbuldeneme1.databinding.FragmentMainBinding
import com.example.stajyerbuldeneme1.ui.model.SirketUsers
import com.example.stajyerbuldeneme1.ui.model.StajyerUsers
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import java.net.URL

class KullaniciProfileFragment : Fragment() {

    private lateinit var tasarim : FragmentKullaniciProfileBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var firestore : FirebaseFirestore
    private lateinit var storge : FirebaseStorage
    private lateinit var stajyerUsers : StajyerUsers
    private lateinit var stajyerCvUrl : String


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        tasarim = FragmentKullaniciProfileBinding.inflate(inflater,container,false)
        val view = tasarim.root



        auth = Firebase.auth
        firestore = Firebase.firestore
        storge = Firebase.storage



        val clipboard =  requireActivity().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager

        getDataStajyerUsers()

        tasarim.editTextKullaniciProfileAd.isEnabled = false
        tasarim.editTextKullaniciProfilEgitim.isEnabled = false
        tasarim.editTextKullaniciProfileSehir.isEnabled = false
        tasarim.editTextKullaniciProfileSinif.isEnabled = false
        tasarim.editKullaniciProfilEmail.isEnabled = false
        tasarim.editTextCvUrl.isEnabled = false


        tasarim.imageViewExitStajyer.setOnClickListener {

            auth.signOut()
            val intent = Intent(requireActivity(), MainActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }


        tasarim.imageViewStajyerProfileEdit.setOnClickListener {

            Navigation.findNavController(it).navigate(R.id.stajyerProfileDuzenleGecis)


        }

        tasarim.buttonCvGoster.setOnClickListener {

            // panoya kopyalama işlemi.
            //val clip: ClipData = ClipData.newPlainText("",stajyerUsers.stajyerCvUrl)
            //clipboard.setPrimaryClip(clip)
            //Toast.makeText(requireContext(),"Cv İndirme Linki Panoya Kopyalandı", Toast.LENGTH_SHORT).show()

            val webIntent: Intent = Uri.parse(stajyerUsers.stajyerCvUrl).let { webpage ->
                Intent(Intent.ACTION_VIEW, webpage)
            }

            requireActivity().startActivity(webIntent)


        }


        return view
    }





    private fun getDataStajyerUsers(){ // verileri fireStore den alma fonksiyonu

        firestore.collection("StajyerUsers").addSnapshotListener { value, error ->
            if(error != null){
                // hata var demektir
                Toast.makeText(requireActivity(),"Bir Hata Oluştu!", Toast.LENGTH_LONG).show()
            }else{
                // hata yoksa
                if(value != null){
                    if(!value.isEmpty){
                        // eğer değerler boş değilse

                        val documents = value.documents



                        for(documentStajyerProfile in documents){
                            // artık burada document dediğim sey bu uygulama özelinde bir adet post demek
                            val stajyerEmail = documentStajyerProfile.get("stjyerEmail") as String
                            val stajyerIsim = documentStajyerProfile.get("stajyerIsim") as String
                            val stajyerOkul = documentStajyerProfile.get("stajyerOkul") as String
                            val stajyerSehir = documentStajyerProfile.get("stajyerSehir") as String
                            val stajyerSinif = documentStajyerProfile.get("stajyerSinif") as String
                            val stajyerProfilResim = documentStajyerProfile.get("downloadUrlStajyerProfil") as String
                            stajyerCvUrl = documentStajyerProfile.get("dowloadUrlPdf") as String




                            stajyerUsers = StajyerUsers(stajyerIsim,stajyerOkul,stajyerSehir,stajyerEmail,stajyerSinif,stajyerProfilResim,stajyerCvUrl)



                            val userEmailActive = auth.currentUser?.email as String

                            if(stajyerUsers.stajyerEmail.equals(userEmailActive)){

                                tasarim.editTextKullaniciProfileAd.setText(stajyerUsers.stajyerAd)
                                tasarim.editTextKullaniciProfilEgitim.setText(stajyerUsers.stajyerOkul)
                                tasarim.editTextKullaniciProfileSinif.setText(stajyerUsers.stajyerSinif)
                                tasarim.editKullaniciProfilEmail.setText(stajyerUsers.stajyerEmail)
                                tasarim.editTextKullaniciProfileSehir.setText(stajyerUsers.stajyerSehir)
                                tasarim.editTextCvUrl.setText(stajyerUsers.stajyerCvUrl)
                                Picasso.get().load(stajyerUsers.stajyerResim).into(tasarim.imageViewKullaniciProfile)
                            }
                        }
                    }
                }
            }
        }

    }


}