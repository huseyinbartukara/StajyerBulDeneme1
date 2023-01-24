package com.example.stajyerbuldeneme1.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.example.stajyerbuldeneme1.R
import com.example.stajyerbuldeneme1.databinding.FragmentStajilanDetaySirketBinding
import com.squareup.picasso.Picasso


class StajilanDetaySirketFragment : Fragment() {

    private lateinit var tasarim : FragmentStajilanDetaySirketBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        tasarim = FragmentStajilanDetaySirketBinding.inflate(inflater,container,false)
        val view = tasarim.root

        val bundle:StajilanDetaySirketFragmentArgs by navArgs()
        val gelenIlan = bundle.ilanSirket

        tasarim.textViewStajDetaySirketAdS.text = gelenIlan.sirketAd
        tasarim.editTextStajSirketProfilEmailS.setText(gelenIlan.sirketEmail)
        tasarim.editTextStajDetaySirketSehirS.setText(gelenIlan.sirketSehir)
        tasarim.editTextStajDetaySirketTelefonS.setText(gelenIlan.sirketTelefon)
        tasarim.editTextStajDetayilanAciklamaS.setText(gelenIlan.sirketAciklama)
        tasarim.editTextStajStajCesitS.setText(gelenIlan.sirketStajCesit)
        Picasso.get().load(gelenIlan.sirketResim).into(tasarim.imageViewStajDetaySirketProfileS)

        tasarim.buttonStajIlanDetayIptalS.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.mainFragmentGecis2)
        }




        return view
    }
}