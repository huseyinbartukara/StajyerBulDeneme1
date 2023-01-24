package com.example.stajyerbuldeneme1.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.navigation.Navigation
import com.example.stajyerbuldeneme1.R
import com.example.stajyerbuldeneme1.databinding.FragmentGirisYapBinding
import com.example.stajyerbuldeneme1.databinding.FragmentSirketProfileDuzenleBinding
import com.example.stajyerbuldeneme1.ui.model.SirketUsers
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso

class SirketProfileDuzenleFragment : Fragment() {

    private lateinit var tasarim : FragmentSirketProfileDuzenleBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var firestore : FirebaseFirestore
    private lateinit var storge : FirebaseStorage
    private lateinit var sirketUsers : SirketUsers
    private lateinit var teknoKentSecim : String
    private lateinit var sirketProfilResim : String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        tasarim = FragmentSirketProfileDuzenleBinding.inflate(inflater,container,false)
        val view = tasarim.root

        auth = Firebase.auth
        firestore = Firebase.firestore
        storge = Firebase.storage

        tasarim.editSirketProfilEmailDuzenle.isEnabled = false

        getDataSirketUsers()

        var teknokentName = resources.getStringArray(R.array.teknokentName)

        if(tasarim.spinnerSirketProfileDuzenle != null){
            val adapter: ArrayAdapter<String> = ArrayAdapter(requireContext(),android.R.layout.simple_spinner_item,teknokentName)
            tasarim.spinnerSirketProfileDuzenle.adapter = adapter

            tasarim.spinnerSirketProfileDuzenle.onItemSelectedListener = object  : AdapterView.OnItemSelectedListener{
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    // secilen değeri veriyor
                    teknoKentSecim = teknokentName[p2]
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }
            }
        }







        tasarim.buttonSirketProfileKaydet.setOnClickListener {
            val yeniSirketAd = tasarim.editTextSirketProfileAdDuzenle.text.toString()
            //val yeniSirketResim = sirketProfilResim
            val yeniSirketHakkinda = tasarim.editTextSirketProfileAciklamaDuzenle.text.toString()
            val yeniTeknoKent = teknoKentSecim
            val yeniSirketTelefon = tasarim.editTextSirketTelefonDuzenle.text.toString()
            //val yeniSirketEmail = tasarim.editSirketProfilEmailDuzenle.text.toString()
            val useruuid = auth.currentUser!!.uid


            val profileSirketRef = firestore.collection("SirketUsers").document(useruuid)

            profileSirketRef.update("sirketAd",yeniSirketAd
                ,"sirketTelefon",yeniSirketTelefon
                ,"sirketSehir",yeniTeknoKent
                ,"sirketHakkinda",yeniSirketHakkinda)
                .addOnSuccessListener { // kayıt olursa
                    Toast.makeText(requireActivity(),"Güncelleme başarılı",Toast.LENGTH_LONG).show()
                    Navigation.findNavController(tasarim.buttonSirketProfileKaydet).navigate(R.id.sirketProfileGecis)
                }
                .addOnFailureListener {
                    Toast.makeText(requireActivity(),"Bir Sorun Oluştu.",Toast.LENGTH_LONG).show()
                }
        }

        tasarim.buttonSirketProfileIptal.setOnClickListener {
            // değişiklik  olmucagını belirtiyor ve bizi profil sayfasına geri götürücek.
            Navigation.findNavController(it).navigate(R.id.sirketProfileGecis)
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
                            sirketProfilResim = documentSirketProfile.get("downloadUrlSirketProfil") as String


                            sirketUsers = SirketUsers(sirketAd,sirketHakkinda,sirketSehir,sirketTelefon,sirketEmail,sirketProfilResim)



                            val userEmailActive = auth.currentUser?.email as String

                            if(sirketUsers.sirketEmail.equals(userEmailActive)){
                                tasarim.editSirketProfilEmailDuzenle.setText(sirketUsers.sirketEmail)
                                tasarim.editTextSirketProfileAciklamaDuzenle.setText(sirketUsers.sirketHakkinda)
                                tasarim.editTextSirketProfileAdDuzenle.setText(sirketUsers.sirketAd)
                                //tasarim.editTextSirketSehir.setText(sirketUsers.sirketSehir)
                                tasarim.editTextSirketTelefonDuzenle.setText(sirketUsers.sirketTelefon)
                                Picasso.get().load(sirketUsers.sirketResim).into(tasarim.imageViewSirketProfileDuzenle)

                            }
                        }
                    }
                }
            }
        }

    }

}