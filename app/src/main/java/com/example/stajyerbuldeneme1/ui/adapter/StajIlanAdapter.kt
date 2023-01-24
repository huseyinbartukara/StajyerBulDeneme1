package com.example.stajyerbuldeneme1.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.stajyerbuldeneme1.R
import com.example.stajyerbuldeneme1.databinding.SirketStajIlanRowBinding
import com.example.stajyerbuldeneme1.ui.fragments.MainFragmentDirections

import com.example.stajyerbuldeneme1.ui.model.StajIlan
import com.squareup.picasso.Picasso

class StajIlanAdapter(private val stajIlanList : ArrayList<StajIlan>) : RecyclerView.Adapter<StajIlanAdapter.StajIlanHolder>() {

    class StajIlanHolder(val tasarim : SirketStajIlanRowBinding) : RecyclerView.ViewHolder(tasarim.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StajIlanHolder {
        val tasarim = SirketStajIlanRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return StajIlanHolder(tasarim)
    }

    override fun onBindViewHolder(holder: StajIlanHolder, position: Int) {
        holder.tasarim.textViewMainSirketAd.text = stajIlanList.get(position).sirketAd
        holder.tasarim.textViewMainSirketHakkinda.text = stajIlanList.get(position).sirketAciklama
        Picasso.get().load(stajIlanList.get(position).sirketResim).into(holder.tasarim.imageViewMainSirketResim)

        val ilan = stajIlanList.get(position)



        holder.tasarim.cardView.setOnClickListener {

            val gecis = MainFragmentDirections.stajIlanGecis(ilanStaj = ilan)
            Navigation.findNavController(it).navigate(gecis)
        }


    }

    override fun getItemCount(): Int {
        return stajIlanList.size
    }


}