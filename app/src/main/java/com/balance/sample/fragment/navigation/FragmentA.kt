package com.balance.sample.fragment.navigation

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import com.balance.sample.R

class FragmentA : BaseNavigationFragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.txNavigation.text = "A"
        binding.txNavigation.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(
                R.id.action_fragment_a_to_b,
                bundleOf("from" to binding.txNavigation.text.toString())
            )
        }
    }
}