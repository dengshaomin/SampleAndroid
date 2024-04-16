package com.balance.sample

import android.view.LayoutInflater
import androidx.activity.ComponentActivity
import androidx.databinding.ViewDataBinding
import androidx.viewbinding.ViewBinding

fun <VB : ViewBinding> ComponentActivity.viewBinding(inflate: (LayoutInflater) -> VB) = lazy {
    inflate(layoutInflater).also { binding ->
        setContentView(binding.root)
        if (binding is ViewDataBinding) binding.lifecycleOwner = this
    }
}