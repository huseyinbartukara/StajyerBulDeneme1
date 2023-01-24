package com.example.stajyerbuldeneme1.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stajyerbuldeneme1.R
import com.example.stajyerbuldeneme1.databinding.FragmentGirisYapBinding
import com.example.stajyerbuldeneme1.databinding.FragmentMainSirketBinding
import com.example.stajyerbuldeneme1.ui.adapter.StajIlanAdapter
import com.example.stajyerbuldeneme1.ui.adapter.StajIlanSirketAdapter
import com.example.stajyerbuldeneme1.ui.model.StajIlan
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class MainSirketFragment : Fragment() {

    private lateinit var tasarim : FragmentMainSirketBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var firestore : FirebaseFirestore
    private lateinit var storge : FirebaseStorage
    private lateinit var stajIlanArrayList : ArrayList<StajIlan>
    private lateinit var StajIlanSirketAdapter : StajIlanSirketAdapter
    private var sehir_ad : String = ""
    private lateinit var teknoKentSecim : String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        tasarim = FragmentMainSirketBinding.inflate(inflater,container,false)
        val view = tasarim.root

        auth = Firebase.auth
        firestore = Firebase.firestore
        storge = Firebase.storage

        stajIlanArrayList = ArrayList<StajIlan>()

        tasarim.toolbarMainSirket.title = ""
        (activity as AppCompatActivity).setSupportActionBar(tasarim.toolbarMainSirket)

        getData()

        tasarim.rvMainSirket.layoutManager = LinearLayoutManager(requireActivity())
        StajIlanSirketAdapter = StajIlanSirketAdapter(stajIlanArrayList)
        tasarim.rvMainSirket.adapter = StajIlanSirketAdapter

        //----------------

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.main_fragment_toolbar,menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if (menuItem.itemId == R.id.action_filter) {
                    //----------- filtre button bas

                    val tasarim = LayoutInflater.from(requireContext()).inflate(R.layout.filter_alertview_tasarim,null)
                    //val editTextSehir = tasarim.findViewById(R.id.editTextFilterSehir) as EditText
                    val spinnerAlert = tasarim.findViewById(R.id.spinnerAlert) as Spinner


                    var teknokentName = resources.getStringArray(R.array.teknokentName)

                    if(spinnerAlert != null){
                        val adapter: ArrayAdapter<String> = ArrayAdapter(requireContext(),android.R.layout.simple_spinner_item,teknokentName)
                        spinnerAlert.adapter = adapter

                        spinnerAlert.onItemSelectedListener = object  : AdapterView.OnItemSelectedListener{
                            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                                // secilen değeri veriyor
                                teknoKentSecim = teknokentName[p2]



                            }
                            override fun onNothingSelected(p0: AdapterView<*>?) {
                                TODO("Not yet implemented")
                            }
                        }
                    }

                    val ad = AlertDialog.Builder(requireContext())


                    ad.setTitle("İlan Filtrele")
                    ad.setView(tasarim)
                    ad.setPositiveButton("Filtrele"){dialogInterface, i ->

                        sehir_ad = teknoKentSecim
                        Log.e("gelenMesaj",sehir_ad)
                        firestore.collection("SirketIlan").whereEqualTo("sirketSehir",sehir_ad).addSnapshotListener { value, error ->
                            if(error != null){
                                Toast.makeText(requireActivity(),"Ilan Yayınlarken Bir Hata Oluştu!", Toast.LENGTH_LONG).show()
                            }else{
                                if(value != null){
                                    if(!value.isEmpty){
                                        val documents = value.documents

                                        stajIlanArrayList.clear()

                                        for(document in documents){
                                            val sirketAd = document.get("sirketAd") as String
                                            val sirketMail = document.get("sirketEmail") as String
                                            val sirketTelefon = document.get("sirketTelefon") as String
                                            val sirketAciklama = document.get("sirketstajAciklama") as String
                                            val sirketResim = document.get("downloadUrlSirketilanResim") as String
                                            val sirketSehir = document.get("sirketSehir") as String
                                            val stajCesit = document.get("stajCesit") as String

                                            val ilan = StajIlan(sirketAd,sirketMail,sirketSehir,sirketTelefon,sirketAciklama,sirketResim,stajCesit)
                                            stajIlanArrayList.add(ilan)

                                        }
                                        StajIlanSirketAdapter.notifyDataSetChanged()
                                    }
                                }
                            }
                        }
                    }

                    ad.setNegativeButton("İptal"){dialogInterface, i ->

                    }
                    ad.create().show()




                    //------------------

                }
                return true
            }
        },viewLifecycleOwner, Lifecycle.State.RESUMED)

        //----------------




        return view
    }

    private fun getData(){

        firestore.collection("SirketIlan").addSnapshotListener { value, error ->
            if(error != null){
                Toast.makeText(requireActivity(),"Ilan Yayınlarken Bir Hata Oluştu!", Toast.LENGTH_LONG).show()
            }else{
                if(value != null){
                    if(!value.isEmpty){
                        val documents = value.documents

                        stajIlanArrayList.clear()

                        for(document in documents){
                            val sirketAd = document.get("sirketAd") as String
                            val sirketMail = document.get("sirketEmail") as String
                            val sirketTelefon = document.get("sirketTelefon") as String
                            val sirketAciklama = document.get("sirketstajAciklama") as String
                            val sirketResim = document.get("downloadUrlSirketilanResim") as String
                            val sirketSehir = document.get("sirketSehir") as String
                            val stajCesit = document.get("stajCesit") as String

                            val ilan = StajIlan(sirketAd,sirketMail,sirketSehir,sirketTelefon,sirketAciklama,sirketResim,stajCesit)
                            stajIlanArrayList.add(ilan)
                        }
                        StajIlanSirketAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

}