#include <BluetoothSerial.h>

BluetoothSerial SerialBT;  // Objeto para el Bluetooth Serial

void setup() {
  // Inicia el puerto serie de la ESP32
  Serial.begin(115200);
  
  // Inicia el Bluetooth
  SerialBT.begin("ESP32Test");
  Serial.println("Bluetooth iniciado correctamente");
}

void loop() {
  // Envía el mensaje "Hola Mundo" cada medio segundo
  SerialBT.println("Hola Mundo!");
  delay(1000);  // Espera medio segundo antes de enviar el siguiente mensaje
}