package com.example.stajyerbuldeneme1.ui.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import com.example.stajyerbuldeneme1.R
import com.example.stajyerbuldeneme1.databinding.FragmentKayitOlBinding
import com.example.stajyerbuldeneme1.databinding.FragmentKayitOlSirketBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.util.*

class KayitOlSirketFragment : Fragment() {

    private lateinit var tasarim : FragmentKayitOlSirketBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var firestore : FirebaseFirestore
    private lateinit var storge : FirebaseStorage
    private lateinit var activityResultLauncher : ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher : ActivityResultLauncher<String>
    var selectedPicture : Uri? = null

    private lateinit var sirketAdi : String
    private lateinit var sehir : String
    private lateinit var hakkinda : String
    private lateinit var telefon : String
    private lateinit var teknoKentSecim : String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        tasarim = FragmentKayitOlSirketBinding.inflate(inflater,container,false)
        val view = tasarim.root

        auth = Firebase.auth
        firestore = Firebase.firestore
        storge = Firebase.storage

        var teknokentName = resources.getStringArray(R.array.teknokentName)

        if(tasarim.spinnerSirketKaydol != null){
            val adapter: ArrayAdapter<String> = ArrayAdapter(requireContext(),android.R.layout.simple_spinner_item,teknokentName)
            tasarim.spinnerSirketKaydol.adapter = adapter

            tasarim.spinnerSirketKaydol.onItemSelectedListener = object  : AdapterView.OnItemSelectedListener{
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    // secilen değeri veriyor
                    teknoKentSecim = teknokentName[p2]
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }
            }
        }


        registerLauncher()

        tasarim.imageViewKaydolSirketResimEkle.setOnClickListener {
            resimSec(it)
        }

        tasarim.buttonKaydol.setOnClickListener {

             val email = tasarim.sirketKaydolEmail.text.toString()
             val sifre = tasarim.sirketKaydolSifre.text.toString()
             sirketAdi = tasarim.sirketKaydolAd.text.toString()
             sehir  = teknoKentSecim
             hakkinda = tasarim.sirketKaydolHakkinda.text.toString()
             telefon = tasarim.sirketKaydolTelefon.text.toString()


            if(email.equals("") || sifre.equals("") || sirketAdi.equals("") || sehir.equals("") || hakkinda.equals("") || telefon.equals("")){
                Toast.makeText(requireActivity(),"Lütfen Her Alanı Doldurduğunuzdan Emin Olun!", Toast.LENGTH_LONG).show()
            }else{
                auth.createUserWithEmailAndPassword(email,sifre).addOnSuccessListener {
                    // kayıt başarılı olursa geçiş yapılacak.

                    upload(tasarim.buttonKaydol)


                }.addOnFailureListener {
                    // kayıt başarısız olursa
                    Toast.makeText(requireActivity(),"Kayıt Olurken Bir Hata Oluştu", Toast.LENGTH_LONG).show()
                }
            }
            kayitOlSirketButton(it)
        }


        return view
    }

    fun kayitOlSirketButton(it:View){
        Navigation.findNavController(it).navigate(R.id.girisYapFragmentGecisSirket)
    }



    fun upload(view: View){

        val uuid = UUID.randomUUID()
        val imageName = "$uuid.jpg"

        val reference = storge.reference
        val imageReference = reference.child("sirketProfileImages").child(imageName)

        if(selectedPicture != null){
            imageReference.putFile(selectedPicture!!).addOnSuccessListener {
                // upload edilirse
                val uploadPictureReference = storge.reference.child("sirketProfileImages").child(imageName)
                uploadPictureReference.downloadUrl.addOnSuccessListener {
                    // bize resmin storage da nereye kayıtlı oldugunu uri ile veriyor
                    val dowloadUrl = it.toString()


                    val sirketUsers = hashMapOf<String, Any>()

                    sirketUsers.put("downloadUrlSirketProfil",dowloadUrl)
                    sirketUsers.put("sirketEmail",auth.currentUser!!.email!!)
                    sirketUsers.put("sirketAd",sirketAdi)
                    sirketUsers.put("sirketSehir",sehir)
                    sirketUsers.put("sirketHakkinda",hakkinda)
                    sirketUsers.put("sirketTelefon",telefon)


                    firestore.collection("SirketUsers").document(auth.currentUser!!.uid).set(sirketUsers).addOnSuccessListener {
                        // firestore içerisine aktarılırsa

                    }.addOnFailureListener {
                        // fireStore içerisine aktaramazsam
                        Toast.makeText(requireActivity(),it.localizedMessage,Toast.LENGTH_LONG).show()
                    }

                }
            }.addOnFailureListener{
                // UPLOAD başarısız olursa
                Toast.makeText(requireActivity(),it.localizedMessage,Toast.LENGTH_LONG).show()
            }
        }

    }



    fun resimSec(view:View){

        if(ContextCompat.checkSelfPermission(requireActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            // izin yok demek
            if(ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)){
                Snackbar.make(view,"Permission Needed for gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission"){
                    // izin isteme
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }.show()
            }else{
                // izin isteme
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }else{
            // izin var demek galeriye gidicek.
            val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            activityResultLauncher.launch(intentToGallery)
        }
    }


    private fun registerLauncher(){

        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if(result.resultCode == AppCompatActivity.RESULT_OK){
                val intentFromResult = result.data
                if(intentFromResult != null){
                    selectedPicture = intentFromResult.data // galeriden gelen verinin uri si
                    selectedPicture?.let {
                        tasarim.imageViewKaydolSirketResimEkle.setImageURI(it) // kullanıcı arayuzde sectiği resmi görsün diye
                    }
                }
            }
        }

        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){ result ->
            if(result){
                // izin verildi
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)

            }else{
                // izin verilmedi
                Toast.makeText(requireActivity(),"permission needed!", Toast.LENGTH_LONG).show()
            }
        }

    }



}