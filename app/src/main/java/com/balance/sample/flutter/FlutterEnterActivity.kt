package com.balance.sample.flutter

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.balance.sample.R
import io.flutter.embedding.android.FlutterActivity
import kotlinx.android.synthetic.main.activity_flutter_enter.*

class FlutterEnterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("balance",intent?.data?.toString()?:"")
        NativeMethodChannel.instance.init(this.applicationContext)
        setContentView(R.layout.activity_flutter_enter)
        default_route.setOnClickListener {
            startActivity(FlutterActivity.withNewEngine().build(this))
        }
        target_route.setOnClickListener {
            startActivity(FlutterActivity.withNewEngine().initialRoute("video").build(this))
        }
        community.setOnClickListener {
            startActivity(Intent(this,CommunityActivity::class.java))
        }
        startActivityForResult(Intent(this,CommunityActivity::class.java),1000)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        setResult(resultCode,data)
        finish()
    }
}