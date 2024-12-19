package com.iea.esp32bluetooth

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.iea.esp32bluetooth.databinding.ActivityMainBinding

// Primera actividad - Seleccionar dispositivo Bluetooth

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding // Declaración .xml con ViewBinding
    private lateinit var m_bluetoothAdapter: BluetoothAdapter
    private lateinit var m_pairedDevices: Set<BluetoothDevice>

    companion object {
        const val REQUEST_CODE_BLUETOOTH = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkAndRequestPermissions()
        binding.selectDeviceRefresh.setOnClickListener { pairedDevicesList() }
    }

    // Chequea si tiene los permisos adecuados
    private fun checkAndRequestPermissions() {
        val permissions = arrayOf(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH_CONNECT
        )

        val missingPermissions = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (missingPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, missingPermissions.toTypedArray(), REQUEST_CODE_BLUETOOTH)
        }
    }

    // Actua en base a los permisos
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_BLUETOOTH) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                Log.i("Permissions", "Todos los permisos otorgados")
            } else {
                Log.e("Permissions", "Permisos no otorgados, algunas funciones pueden no funcionar")
            }
        }
    }

    // Hace la lista de dispositivos emparejados y los muestra en la lista
    private fun pairedDevicesList() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e("Permissions", "Permiso BLUETOOTH_CONNECT no otorgado")
            return
        }

        m_pairedDevices = m_bluetoothAdapter.bondedDevices
        val list: ArrayList<BluetoothDevice> = ArrayList()

        if (m_pairedDevices.isNotEmpty()) {
            for (device: BluetoothDevice in m_pairedDevices) {
                list.add(device)
                Log.i("Device", "Dispositivo encontrado: $device")
            }
        }

        // Pasa a la siguiente actividad al elegir dispositivo
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, list)
        binding.selectDeviceList.adapter = adapter
        binding.selectDeviceList.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                val device: BluetoothDevice = list[position]
                val address: String = device.address

                val intent = Intent(this, DeviceControlActivity::class.java)
                intent.putExtra("DEVICE_ADDRESS", address)
                startActivity(intent)
            }
    }
}