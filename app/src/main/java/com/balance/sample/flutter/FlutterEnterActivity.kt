package com.balance.sample.flutter

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.balance.sample.R
import com.balance.sample.databinding.ActivityFlutterEnterBinding
import io.flutter.embedding.android.FlutterActivity

class FlutterEnterActivity : AppCompatActivity() {
    private lateinit var binding:ActivityFlutterEnterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFlutterEnterBinding.inflate(layoutInflater)
        Log.e("balance",intent?.data?.toString()?:"")
        NativeMethodChannel.instance.init(this.applicationContext)
        setContentView(binding.root)
        binding.defaultRoute.setOnClickListener {
            startActivity(FlutterActivity.withCachedEngine("cache_0").build(this))
        }
        binding.targetRoute.setOnClickListener {
            startActivity(FlutterActivity.withNewEngine().initialRoute("video").build(this))
        }
        binding.community.setOnClickListener {
            startActivity(Intent(this,CommunityActivity::class.java))
        }
//        startActivityForResult(Intent(this,CommunityActivity::class.java),1000)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        setResult(resultCode,data)
        finish()
    }
}