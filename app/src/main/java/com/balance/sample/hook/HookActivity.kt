package com.balance.sample.hook

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.ClipboardUtils
import com.blankj.utilcode.util.ReflectUtils
import com.balance.sample.R
import com.balance.sample.databinding.ActivityHookBinding


class HookActivity : AppCompatActivity() {
    private lateinit var binding:ActivityHookBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHookBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.editText.setText(ClipboardUtils.getText())
        val cls = TestClass()
        cls.getName()
        ReflectUtils.reflect(cls).field("a")
        val a = ReflectUtil2.invoke(TestClass::class.java,cls,"getName")
        val b = ReflectUtils.reflect(cls).method("getName").get<Int>()
        val c = 1
    }
    class TestClass {
        var a = 1
        fun getName():Int{
            return a
        }
        private fun getName2():Int{
            return a
        }
    }
}