package com.example.stajyerbuldeneme1.ui.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavArgs
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.example.stajyerbuldeneme1.R
import com.example.stajyerbuldeneme1.databinding.FragmentSirketProfileBinding
import com.example.stajyerbuldeneme1.databinding.FragmentStajilanDetayBinding
import com.squareup.picasso.Picasso

class StajilanDetayFragment : Fragment() {

    private lateinit var tasarim : FragmentStajilanDetayBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        tasarim = FragmentStajilanDetayBinding.inflate(inflater,container,false)
        val view = tasarim.root

        val bundle:StajilanDetayFragmentArgs by navArgs()
        val gelenIlan = bundle.ilanStaj

        tasarim.textViewStajDetaySirketAd.text = gelenIlan.sirketAd
        tasarim.editTextStajSirketProfilEmail.setText(gelenIlan.sirketEmail)
        tasarim.editTextStajDetaySirketSehir.setText(gelenIlan.sirketSehir)
        tasarim.editTextStajDetaySirketTelefon.setText(gelenIlan.sirketTelefon)
        tasarim.editTextStajDetayilanAciklama.setText(gelenIlan.sirketAciklama)
        tasarim.editTextStajStajCesit.setText(gelenIlan.sirketStajCesit)
        Picasso.get().load(gelenIlan.sirketResim).into(tasarim.imageViewStajDetaySirketProfile)

        tasarim.buttonStajIlanDetayIptal.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.mainFragmentGecis)
        }

        tasarim.buttonStajIlanDetayBasvur.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SEND)
            emailIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            emailIntent.type = "vnd.android.cursor.item/email"
            emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(gelenIlan.sirketEmail))
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Stajyer İlan Başvurusu")
            startActivity(Intent.createChooser(emailIntent, "Send mail using..."))
        }



        return view

    }
}