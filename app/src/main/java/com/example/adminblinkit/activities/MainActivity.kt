package com.example.adminblinkit.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.lifecycle.ReportFragment.Companion.reportFragment
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.example.adminblinkit.R
import com.example.adminblinkit.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

   private val binding : ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.crashButton.setOnClickListener {
            throw RuntimeException("Test Crash") // Force a crash
        }


        NavigationUI.setupWithNavController(binding.menuItem , Navigation.findNavController(this , R.id.fragmentContainer))
    }
}