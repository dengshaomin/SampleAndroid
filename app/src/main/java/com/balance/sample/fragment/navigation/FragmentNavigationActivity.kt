package com.balance.sample.fragment.navigation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import com.balance.sample.R
import com.balance.sample.databinding.ActivityFragmentNavigationBinding

class FragmentNavigationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFragmentNavigationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFragmentNavigationBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()
        //只有在onstart之后才能获取到
//        binding.fragmentContainer.findNavController().navigate(FragmentADirections.actionFragmentAToC("A"))
    }

    override fun onResume() {
        super.onResume()
        (supportFragmentManager.fragments[0] as? NavHostFragment)?.childFragmentManager?.fragments

    }

    override fun onBackPressed() {
        if (Navigation.findNavController(binding.fragmentContainer).currentDestination?.id == R.id.fragment_b) {
            finish()
        } else {
            super.onBackPressed()
        }
    }
}