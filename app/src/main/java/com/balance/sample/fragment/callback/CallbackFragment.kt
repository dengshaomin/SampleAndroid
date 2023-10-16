package com.balance.sample.fragment.callback

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.Fragment

class CallbackFragment : Fragment() {
    var callback: CallBackAction? = null

    companion object {
        val fragmentTag = "callback_fragment"
        val imageRequestCode = 1000

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == imageRequestCode && resultCode == Activity.RESULT_OK) {
            val images =
                data?.getSerializableExtra("data") as? MutableList<String> ?: mutableListOf()
            //当前demo是从activity里面直接打开的，interface为activity，防止后台被杀；
            //最好是通过自定义view打开，更好复用，自定义view里面在走生命周期时判断fragment是否已经存在，被杀时重新设置callback
            (callback ?: (activity as? CallBackAction))?.actionSuccess(images)
        }
    }
}

interface CallBackAction {
    fun actionSuccess(data: MutableList<String>)
}