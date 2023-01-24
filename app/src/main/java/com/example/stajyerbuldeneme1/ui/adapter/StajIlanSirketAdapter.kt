package com.example.stajyerbuldeneme1.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.stajyerbuldeneme1.databinding.SirketStajIlanRowBinding
import com.example.stajyerbuldeneme1.ui.fragments.MainFragmentDirections
import com.example.stajyerbuldeneme1.ui.fragments.MainSirketFragmentDirections
import com.example.stajyerbuldeneme1.ui.model.StajIlan
import com.squareup.picasso.Picasso

class StajIlanSirketAdapter(private val stajIlanSirketList : ArrayList<StajIlan>) : RecyclerView.Adapter<StajIlanSirketAdapter.StajIlanSirketHolder>() {

    class StajIlanSirketHolder(val tasarim : SirketStajIlanRowBinding) : RecyclerView.ViewHolder(tasarim.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StajIlanSirketHolder {
        val tasarim = SirketStajIlanRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return StajIlanSirketHolder(tasarim)
    }

    override fun onBindViewHolder(holder: StajIlanSirketHolder, position: Int) {
        holder.tasarim.textViewMainSirketAd.text = stajIlanSirketList.get(position).sirketAd
        holder.tasarim.textViewMainSirketHakkinda.text = stajIlanSirketList.get(position).sirketAciklama
        Picasso.get().load(stajIlanSirketList.get(position).sirketResim).into(holder.tasarim.imageViewMainSirketResim)

    val ilan = stajIlanSirketList.get(position)

        holder.tasarim.cardView.setOnClickListener {
            // geçiş işlemi yapılacak.
            val gecis = MainSirketFragmentDirections.stajilanDetayFragmentGecis(ilanSirket = ilan)
            Navigation.findNavController(it).navigate(gecis)
        }

    }

    override fun getItemCount(): Int {
        return stajIlanSirketList.size
    }

}