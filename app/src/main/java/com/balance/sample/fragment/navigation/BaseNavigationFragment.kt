package com.balance.sample.fragment.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.balance.sample.databinding.FragmentBaseNavigationBinding
import com.blankj.utilcode.util.LogUtils

open class BaseNavigationFragment : Fragment() {
    protected lateinit var binding: FragmentBaseNavigationBinding
    var fromWhere: String? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fromWhere = arguments?.get("from") as? String
        LogUtils.e(fromWhere)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBaseNavigationBinding.inflate(inflater, container, false)
        return binding.root
    }
}