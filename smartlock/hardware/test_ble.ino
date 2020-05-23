/*
    Based on Neil Kolban example for IDF: https://github.com/nkolban/esp32-snippets/blob/master/cpp_utils/tests/BLE%20Tests/SampleServer.cpp
    Ported to Arduino ESP32 by Evandro Copercini
    updates by chegewara
*/

#include <BLEDevice.h>
#include <BLEUtils.h>
#include <BLEServer.h>

// See the following for generating UUIDs:
// https://www.uuidgenerator.net/

void setup() {
  pinMode(2, OUTPUT);
  Serial.begin(115200);
  BLEDevice::init("Trusted Test Device");
  BLEServer *pServer = BLEDevice::createServer();
  BLEAdvertising *pAdvertising = pServer->getAdvertising();  // this still is working for backward compatibility
  pAdvertising->setScanResponse(false);
  pAdvertising->setMinPreferred(0x12);
  BLEDevice::startAdvertising();
  digitalWrite(2, true);
}

void loop() {
  // put your main code here, to run repeatedly:

}
