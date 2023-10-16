package com.balance.sample.fragment.navigation

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import com.balance.sample.R

class FragmentC: BaseNavigationFragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.txNavigation.text = "C"
        binding.root.setBackgroundResource(R.color.purple_500)
        binding.txNavigation.setOnClickListener {
            Navigation.findNavController(binding.root).navigate( FragmentCDirections.actionCToA(binding.txNavigation.text.toString()))
        }
    }
}