package com.balance.sample.viewmodel

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.asynclayoutinflater.view.AsyncLayoutInflater
import androidx.core.view.setPadding
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.balance.sample.R
import com.balance.sample.databinding.ActivityAsyncLoadViewBinding
import com.balance.sample.databinding.ActivityViewModelBinding
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SizeUtils

class ViewModelActivity : AppCompatActivity() {
    private lateinit var binding: ActivityViewModelBinding
    //通过by和provider创建获取的是同一个viewmodel；或者通过hilt也可以达到复用的目的
    val viewModel by viewModels<TestViewModel>()
    lateinit var  providerViewModel :TestViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        providerViewModel = ViewModelProvider(this)[TestViewModel::class.java]
        binding = ActivityViewModelBinding.inflate(layoutInflater)
        setContentView(binding.root)
        LogUtils.e(viewModel, providerViewModel)
        providerViewModel.testData.observe(this){
            LogUtils.e("activity view receive:${it}")
        }
        providerViewModel.testData.postValue(true)
    }
}

class ViewModelView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {
    private var viewModel: TestViewModel
    private var providerViewModel =
        ViewModelProvider(context as FragmentActivity)[TestViewModel::class.java]

    init {
        viewModel = TestViewModel()
        LogUtils.e(
            viewModel,
            viewModel == (context as ViewModelActivity).viewModel,
            viewModel == (context as ViewModelActivity).providerViewModel
        )
        LogUtils.e(
            viewModel,
            providerViewModel == (context as ViewModelActivity).viewModel,
            providerViewModel == (context as ViewModelActivity).providerViewModel
        )
        providerViewModel.testData.observe(context as LifecycleOwner){
            //如果在recyclerview中使用，由于Item的复用，注意compare数据id是否一致
            LogUtils.e("customer view receive:${it}")
        }
    }
}

class TestViewModel : ViewModel() {
    val testData = MutableLiveData<Boolean>()
}
