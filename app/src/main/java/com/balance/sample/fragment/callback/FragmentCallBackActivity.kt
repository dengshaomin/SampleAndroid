package com.balance.sample.fragment.callback

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import com.balance.sample.R
import com.balance.sample.databinding.ActivityFragmentCallbackBinding
import com.balance.sample.databinding.ActivityFragmentNavigationBinding
import com.google.gson.Gson

class FragmentCallBackActivity : AppCompatActivity(), CallBackAction {
    private lateinit var binding: ActivityFragmentCallbackBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFragmentCallbackBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.jump.setOnClickListener {
            ImagePickActivity.start(this, this)
        }
    }

    override fun actionSuccess(data: MutableList<String>) {
        binding.cbContent.text = Gson().toJson(data)
    }

}