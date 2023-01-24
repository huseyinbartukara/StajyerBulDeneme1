package com.example.stajyerbuldeneme1.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.Navigation
import com.example.stajyerbuldeneme1.R
import com.example.stajyerbuldeneme1.databinding.FragmentSirketProfileDuzenleBinding
import com.example.stajyerbuldeneme1.databinding.FragmentStajyerProfileDuzenleBinding
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

class StajyerProfileDuzenleFragment : Fragment() {

    private lateinit var tasarim : FragmentStajyerProfileDuzenleBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var firestore : FirebaseFirestore
    private lateinit var storge : FirebaseStorage
    private lateinit var stajyerUsers : StajyerUsers
    private lateinit var teknoKentSecim : String
    private lateinit var stajyerProfilResim : String
    private lateinit var stajyerCvUrl : String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        tasarim = FragmentStajyerProfileDuzenleBinding.inflate(inflater,container,false)
        val view = tasarim.root

        auth = Firebase.auth
        firestore = Firebase.firestore
        storge = Firebase.storage

        getDataStajyerUsers()

        tasarim.editKullaniciProfilEmailDuzenle.isEnabled = false
        tasarim.editTextCvUrlDuzenle.isEnabled = false



        tasarim.buttonStajyerProfileKaydet.setOnClickListener {

            val yeniStajyerAd = tasarim.editTextKullaniciProfileAdDuzenle.text.toString()
            //val yeniSirketResim = sirketProfilResim
            val yeniStajyerEgitim = tasarim.editTextKullaniciProfilEgitimDuzenle.text.toString()
            val yeniStajyerSehir = tasarim.editTextKullaniciProfileSehirDuzenle.text.toString()
            val yeniStajyerSinif = tasarim.editTextKullaniciProfileSinifDuzenle.text.toString()
            //val yeniSirketEmail = tasarim.editSirketProfilEmailDuzenle.text.toString()


            // burada kayıt yapılacak ve profil sayfgasına geri dönülücek.
            val useruid = auth.currentUser!!.uid

            val profileAdRef = firestore.collection("StajyerUsers").document(useruid)
            profileAdRef
                .update(        "stajyerIsim",yeniStajyerAd,
                    "stajyerOkul",yeniStajyerEgitim,
                    "stajyerSehir",yeniStajyerSehir,
                    "stajyerSinif",yeniStajyerSinif)
                .addOnSuccessListener {
                    Toast.makeText(requireActivity(),"Güncelleme Başarılı",Toast.LENGTH_LONG).show()
                    Navigation.findNavController(tasarim.buttonStajyerProfileKaydet).navigate(R.id.kullaniciProfileFragmentGecis)

                }
                .addOnFailureListener {
                    Toast.makeText(requireActivity(),"Bir hata oluştu.",Toast.LENGTH_LONG).show()

                }
        }

        tasarim.buttonStajyerProfileIptal.setOnClickListener {
            // burada işlem iptal edilecek ve profile sayfasoona geri dönülücek.
            Navigation.findNavController(tasarim.buttonStajyerProfileIptal).navigate(R.id.kullaniciProfileFragmentGecis)
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

                                tasarim.editTextKullaniciProfileAdDuzenle.setText(stajyerUsers.stajyerAd)
                                tasarim.editTextKullaniciProfilEgitimDuzenle.setText(stajyerUsers.stajyerOkul)
                                tasarim.editTextKullaniciProfileSinifDuzenle.setText(stajyerUsers.stajyerSinif)
                                tasarim.editKullaniciProfilEmailDuzenle.setText(stajyerUsers.stajyerEmail)
                                tasarim.editTextKullaniciProfileSehirDuzenle.setText(stajyerUsers.stajyerSehir)
                                tasarim.editTextCvUrlDuzenle.setText(stajyerUsers.stajyerCvUrl)
                                Picasso.get().load(stajyerUsers.stajyerResim).into(tasarim.imageViewKullaniciProfileDuzenle)
                            }
                        }
                    }
                }
            }
        }

    }



}