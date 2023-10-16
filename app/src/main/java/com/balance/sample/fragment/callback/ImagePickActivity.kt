package com.balance.sample.fragment.callback

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.balance.sample.databinding.ActivityImagePickBinding
import kotlinx.parcelize.Parcelize

class ImagePickActivity : AppCompatActivity() {
    private lateinit var binding: ActivityImagePickBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImagePickBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.close.setOnClickListener {
            setResult(RESULT_OK, Intent().apply {
                putParcelableArrayListExtra(
                    "data", arrayListOf<ImageItem>(
                        ImageItem("https://1.png"),
                        ImageItem("https://2.png")
                    )
                )
            })
            finish()
        }
    }

    companion object {
        fun start(fragmentActivity: FragmentActivity, callback: CallBackAction) {
            var fragment =
                fragmentActivity.supportFragmentManager.findFragmentByTag(CallbackFragment.fragmentTag) as? CallbackFragment
            if (fragment == null) {
                fragment = CallbackFragment()
            }
            fragment?.let {
//                it.addListener(callback)
                it.callback = callback
                if (!it.isAdded) {
                    fragmentActivity.supportFragmentManager.beginTransaction().add(
                        fragment,
                        CallbackFragment.fragmentTag
                    ).commitNow()
                }
                it.startActivityForResult(
                    Intent(
                        fragmentActivity,
                        ImagePickActivity::class.java
                    ), CallbackFragment.imageRequestCode
                )
            }
        }
    }
}

@Parcelize
data class ImageItem(val url: String) : Parcelable
