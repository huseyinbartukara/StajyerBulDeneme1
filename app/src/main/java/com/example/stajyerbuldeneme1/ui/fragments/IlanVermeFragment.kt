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
import com.example.stajyerbuldeneme1.databinding.FragmentIlanVermeBinding
import com.example.stajyerbuldeneme1.ui.model.SirketUsers
import com.example.stajyerbuldeneme1.ui.model.StajIlan
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import java.io.Serializable

class IlanVermeFragment : Fragment() {

    private lateinit var tasarim : FragmentIlanVermeBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var firestore : FirebaseFirestore
    private lateinit var storge : FirebaseStorage
    private lateinit var sirketUsers : SirketUsers
    private lateinit var stajCesit : String
    private lateinit var teknoKentSecim : String


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        tasarim = FragmentIlanVermeBinding.inflate(inflater,container,false)
        val view = tasarim.root


        auth = Firebase.auth
        firestore = Firebase.firestore
        storge = Firebase.storage




        getDataSirketIlan()
        boxKontrol()

        var teknokentName = resources.getStringArray(R.array.teknokentName)

        if(tasarim.spinnerIlanVer != null){
            val adapter:ArrayAdapter<String> = ArrayAdapter(requireContext(),android.R.layout.simple_spinner_item,teknokentName)
            tasarim.spinnerIlanVer.adapter = adapter

            tasarim.spinnerIlanVer.onItemSelectedListener = object  : AdapterView.OnItemSelectedListener{
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    // secilen değeri veriyor
                    teknoKentSecim = teknokentName[p2]
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }
            }
        }



        tasarim.buttonilanVer.setOnClickListener {
            uploadSirketİlan()
        }

        return view
    }

    private fun getDataSirketIlan(){ // verileri fireStore den alma fonksiyonu

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
                                tasarim.textViewStajSirketAd.text = sirketUsers.sirketAd
                                tasarim.textViewStajIlanTelefon.text = sirketUsers.sirketTelefon
                                tasarim.textViewStajIlanEmail.text = sirketUsers.sirketEmail
                                tasarim.textViewimageUrl.text = sirketUsers.sirketResim
                                Picasso.get().load(sirketUsers.sirketResim).into(tasarim.imageViewStajSirketResim)
                            }


                        }
                    }
                }
            }
        }
    }


    fun uploadSirketİlan(){

        if(auth.currentUser != null){
            val ilanMap = hashMapOf<String, Any>()

            val yerindeBox = tasarim.checkBoxYerinde.isChecked

             if(yerindeBox.equals(true)){
                 // staj yerinde
                 stajCesit = "Yerinde"
             }else{
                 // staj uzaktan
                 stajCesit = "Uzaktan"
             }

            ilanMap.put("downloadUrlSirketilanResim",tasarim.textViewimageUrl.text.toString())
            ilanMap.put("sirketEmail",auth.currentUser!!.email!!)
            ilanMap.put("sirketAd",tasarim.textViewStajSirketAd.text.toString())
            ilanMap.put("sirketstajAciklama",tasarim.editTextStajIlanAciklama.text.toString())
            ilanMap.put("sirketSehir",teknoKentSecim)
            ilanMap.put("sirketTelefon",tasarim.textViewStajIlanTelefon.text.toString())
            ilanMap.put("stajCesit",stajCesit)


            firestore.collection("SirketIlan").add(ilanMap).addOnSuccessListener {
                // firestore içerisine aktarılırsa
                Navigation.findNavController(tasarim.buttonilanVer).navigate(R.id.mainSirketFragmentGecis)

            }.addOnFailureListener {
                // fireStore içerisine aktaramazsam
                Toast.makeText(requireActivity(),it.localizedMessage,Toast.LENGTH_LONG).show()
            }
        }
    }

    fun boxKontrol(){
        val uzaktanBox = tasarim.checkBoxUzaktan
        val yerindeBox = tasarim.checkBoxYerinde

        yerindeBox.isChecked = true

        uzaktanBox.setOnClickListener {
            if (yerindeBox.isChecked){
                yerindeBox.isChecked =false
            }

        }
        yerindeBox.setOnClickListener {
            if (uzaktanBox.isChecked){
                uzaktanBox.isChecked =false
            }
        }
    }


}