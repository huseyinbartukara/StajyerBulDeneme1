package com.example.stajyerbuldeneme1.ui.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import com.example.stajyerbuldeneme1.R
import com.example.stajyerbuldeneme1.databinding.FragmentGirisYapBinding
import com.example.stajyerbuldeneme1.databinding.FragmentKayitOlBinding

import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.util.*
import kotlin.math.log
import kotlin.math.sin

class KayitOlFragment : Fragment() {

    private lateinit var tasarim : FragmentKayitOlBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var firestore : FirebaseFirestore
    private lateinit var storge : FirebaseStorage
    private lateinit var activityResultLauncher : ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher : ActivityResultLauncher<String>
    var selectedPicture : Uri? = null
    private lateinit var isim : String
    private lateinit var sehir : String
    private lateinit var sinif : String
    private lateinit var okul : String
    var selectedPdf : Uri? = null






    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        tasarim = FragmentKayitOlBinding.inflate(inflater,container,false)
        val view = tasarim.root

        auth = Firebase.auth
        firestore = Firebase.firestore
        storge = Firebase.storage

        registerLauncher()


        tasarim.imageViewKaydolResimEkle.setOnClickListener {
            // burada galeriden resim secicez.
            resimSec(it)
        }

        tasarim.buttonCvEkle.setOnClickListener {
            openFile()
            tasarim.textViewPdfUri.text = selectedPdf.toString()

        }

        tasarim.buttonKaydol.setOnClickListener {

            val email = tasarim.editTextKayTOlEmail.text.toString()
            val sifre = tasarim.editTextKayTOlSifre.text.toString()
                isim = tasarim.editTextKayTOlIsim.text.toString()
                sehir = tasarim.editTextKayTOlSehir.text.toString()
                sinif = tasarim.editTextKayTOlSinif.text.toString()
                okul = tasarim.editTextKayTOlOkul.text.toString()


            if(email.equals("") || sifre.equals("") || isim.equals("") || sehir.equals("") || sinif.equals("") || okul.equals("")){
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
            kayıtOlButton(it)
        }








        return view
    }


    fun upload(view: View){

        val uuid = UUID.randomUUID()
        val imageName = "$uuid.jpg"
        val pdfName = "$uuid.pdf"


        val reference = storge.reference
        val imageReference = reference.child("stajyerProfileImages").child(imageName)
        val pdfReference = reference.child("stajyerPdf").child(pdfName)

        if(selectedPicture != null){
            imageReference.putFile(selectedPicture!!).addOnSuccessListener {
                // upload edilirse
                val uploadPictureReference = storge.reference.child("stajyerProfileImages").child(imageName)
                uploadPictureReference.downloadUrl.addOnSuccessListener {
                    // bize resmin storage da nereye kayıtlı oldugunu uri ile veriyor
                    val dowloadUrl = it.toString()


                    if(selectedPdf != null){
                        pdfReference.putFile(selectedPdf!!).addOnSuccessListener {
                            val uploadPdfReference = storge.reference.child("stajyerPdf").child(pdfName)
                            uploadPdfReference.downloadUrl.addOnSuccessListener {
                                val dowloadUrlPdf = it.toString()


                                val stajyerUsers = hashMapOf<String, Any>()

                                stajyerUsers.put("downloadUrlStajyerProfil",dowloadUrl)
                                stajyerUsers.put("stjyerEmail",auth.currentUser!!.email!!)
                                stajyerUsers.put("stajyerIsim",isim)
                                stajyerUsers.put("stajyerSehir",sehir)
                                stajyerUsers.put("stajyerSinif",sinif)
                                stajyerUsers.put("stajyerOkul",okul)
                                stajyerUsers.put("dowloadUrlPdf",dowloadUrlPdf)


                                firestore.collection("StajyerUsers").document(auth.currentUser!!.uid).set(stajyerUsers).addOnSuccessListener {
                                    // firestore içerisine aktarılırsa

                                }.addOnFailureListener {
                                    // fireStore içerisine aktaramazsam
                                    Toast.makeText(requireActivity(),it.localizedMessage,Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    }
                }
            }.addOnFailureListener{
                // UPLOAD başarısız olursa
                Toast.makeText(requireActivity(),it.localizedMessage,Toast.LENGTH_LONG).show()
            }
        }

    }


    fun kayıtOlButton(it:View){
        Navigation.findNavController(it).navigate(R.id.girisYapFragmentGecis)
    }


    //----------------------------------------------------

    val PICK_PDF_FILE = 1

    fun openFile() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/pdf"

            // Optionally, specify a URI for the file that should appear in the
            // system file picker when it loads.
            //putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)
        }

        startActivityForResult(intent, PICK_PDF_FILE)
    }


    override fun onActivityResult(
        requestCode: Int, resultCode: Int, resultData: Intent?) {
        if (requestCode == PICK_PDF_FILE
            && resultCode == Activity.RESULT_OK) {
            // The result data contains a URI for the document or directory that
            // the user selected.
            resultData?.data?.also { uri ->
                // Perform operations on the document using its URI.
                selectedPdf = uri
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
                        tasarim.imageViewKaydolResimEkle.setImageURI(it) // kullanıcı arayuzde sectiği resmi görsün diye
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