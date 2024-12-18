package com.iea.esp32bluetooth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.iea.esp32bluetooth.databinding.ActivityDeviceControlBinding

class DeviceControlActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDeviceControlBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_device_control)

        binding = ActivityDeviceControlBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val macAddress = intent.getStringExtra("DEVICE_ADDRESS")
        binding.connected.text = "Conectado a: $macAddress"
    }
}
