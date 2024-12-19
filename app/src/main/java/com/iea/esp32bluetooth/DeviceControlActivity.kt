package com.iea.esp32bluetooth

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.iea.esp32bluetooth.databinding.ActivityDeviceControlBinding
import java.io.IOException
import java.io.InputStream
import java.util.*

class DeviceControlActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDeviceControlBinding
    private var bluetoothSocket: BluetoothSocket? = null
    private var isConnected = false
    private lateinit var deviceAddress: String

    companion object {
        // UUID estándar para comunicación Bluetooth Serial
        private val UUID_BT_SERIAL: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeviceControlBinding.inflate(layoutInflater)
        setContentView(binding.root)

        deviceAddress = intent.getStringExtra("DEVICE_ADDRESS") ?: return
        connectToDevice()

        binding.disconnectButton.setOnClickListener {
            disconnect()
        }
        binding.cleanConsole.setOnClickListener {
            // Limpia el contenido de la consola
            binding.console.text = ""
        }
    }

    private fun connectToDevice() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permiso BLUETOOTH_CONNECT no otorgado", Toast.LENGTH_SHORT).show()
            return
        }

        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        val device: BluetoothDevice = bluetoothAdapter.getRemoteDevice(deviceAddress)

        try {
            bluetoothSocket = device.createRfcommSocketToServiceRecord(UUID_BT_SERIAL)
            bluetoothSocket?.connect()
            isConnected = true
            Toast.makeText(this, "Conectado a $deviceAddress", Toast.LENGTH_SHORT).show()

            startListeningToDevice()
        } catch (e: IOException) {
            Log.e("Bluetooth", "Error al conectar al dispositivo", e)
            Toast.makeText(this, "Error al conectar a $deviceAddress", Toast.LENGTH_SHORT).show()
        }
    }


    private fun startListeningToDevice() {
        val inputStream: InputStream? = bluetoothSocket?.inputStream
        Thread {
            try {
                val buffer = ByteArray(1024)
                while (isConnected) {
                    val bytesRead = inputStream?.read(buffer) ?: -1
                    if (bytesRead > 0) {
                        val receivedMessage = String(buffer, 0, bytesRead)
                        runOnUiThread {
                            binding.console.append("$receivedMessage\n")
                            binding.consoleLog.post {
                                binding.consoleLog.smoothScrollTo(0, binding.console.bottom)
                            }
                        }
                    }
                }
            } catch (e: IOException) {
                Log.e("Bluetooth", "Error al leer datos del dispositivo", e)
            }
        }.start()
    }

    private fun disconnect() {
        try {
            isConnected = false
            bluetoothSocket?.close()
            Toast.makeText(this, "Desconectado", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            Log.e("Bluetooth", "Error al desconectar", e)
        }
        finish()
    }
}
