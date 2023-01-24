package com.example.stajyerbuldeneme1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.stajyerbuldeneme1.databinding.ActivityOlayBinding
import com.example.stajyerbuldeneme1.databinding.ActivitySirketOlayBinding

class SirketOlayActivity : AppCompatActivity() {

    private lateinit var tasarim : ActivitySirketOlayBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tasarim = ActivitySirketOlayBinding.inflate(layoutInflater)
        val view = tasarim.root
        setContentView(view)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFramentSirket) as NavHostFragment

        NavigationUI.setupWithNavController(tasarim.bottomNavSirket, navHostFragment.navController)






    }
}