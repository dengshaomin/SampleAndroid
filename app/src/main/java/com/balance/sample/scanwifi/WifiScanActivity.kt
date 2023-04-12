package com.balance.sample.scanwifi

import android.Manifest
import android.content.Context
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.balance.sample.R
import com.blankj.utilcode.util.LogUtils
import com.tbruyelle.rxpermissions3.RxPermissions
import kotlinx.android.synthetic.main.activity_wifi_scan.*

class WifiScanActivity : AppCompatActivity() {
    /**
     * https://developer.android.com/guide/topics/connectivity/wifi-scan?hl=zh-cn#wifi-scan-permissions
     * */
    var low = arrayOf(
        Manifest.permission.ACCESS_WIFI_STATE,
        Manifest.permission.CHANGE_WIFI_STATE,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wifi_scan)
        scan.setOnClickListener {
            RxPermissions(this).request(*low).subscribe {
                if (it) {
                    scan()
                }
            }
        }
    }

    private fun scan() {
        val wifiManager =
            applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

// Check if Wi-Fi is enabled on the device.
        if (wifiManager.isWifiEnabled) {

            // Start scanning for Wi-Fi networks.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val scanResult = wifiManager.startScan()

                // Get a list of nearby Wi-Fi networks.
                val wifiList = wifiManager.scanResults

                // Iterate over the list of Wi-Fi networks.
                for (wifi in wifiList) {

                    // Get the SSID (network name) of each Wi-Fi network.
                    val ssid = wifi.SSID

                    // Do something with the SSID, such as displaying it in a list.
                    LogUtils.e("balance", "Nearby Wi-Fi network: $ssid")
                }
            } else {
                wifiManager.startScan()
                val wifiList = wifiManager.scanResults

                // Iterate over the list of Wi-Fi networks.
                for (wifi in wifiList) {

                    // Get the SSID (network name) of each Wi-Fi network.
                    val ssid = wifi.SSID

                    // Do something with the SSID, such as displaying it in a list.
                    LogUtils.e("balance", "Nearby Wi-Fi network: $ssid")
                }
            }
        } else {
            // Wi-Fi is not enabled on the device. Prompt the user to turn it on.
            Toast.makeText(this, "Please turn on Wi-Fi.", Toast.LENGTH_SHORT).show()
        }
    }
}