package com.balance.sample.fragment.navigation

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.balance.sample.R

class FragmentB : BaseNavigationFragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.txNavigation.text = "B"
        binding.root.setBackgroundResource(R.color.purple_200)
        binding.txNavigation.setOnClickListener {
            Navigation.findNavController(binding.root)
                .navigate(FragmentBDirections.actionFragmentBToC(binding.txNavigation.text.toString()))
        }
    }
}