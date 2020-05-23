#include "EEPROM.h"
#include "time.h"
#include <BLEDevice.h>
#include <BLEServer.h>
#include <BLEUtils.h>
#include <BLE2902.h>
#include <BLEScan.h>
#include <BLEAdvertisedDevice.h>
#include <FirebaseESP32.h>
#include <WiFi.h>
#include <Wire.h>
#include "Adafruit_MPR121.h"
#define EEPROM_SIZE 400
#define FIREBASE_HOST "lyoko-smartlock.firebaseio.com"
#define FIREBASE_AUTH "85viJDSNb1ClLOu5yULeAorEXhaEXfebxJroE2dy"

#define LED_GREEN_PIN 2
#define LED_RED_PIN 4
#define SPEAKER_PIN 5
#define LED_BLUE_PIN 15
#define RELAY_PIN 23

#define SERVICE_LYOKO_UUID                      "7060da7f-1ce6-43d1-b58c-2c595f8f9a56"
#define CHARACTERISTIC_WIFI_CREDENTIAL_UUID     "b82150b1-48e9-4a1b-a18c-3f2c140a8104"
#define CHARACTERISTIC_RESPONSE_UUID            "459d013b-061c-430b-a4fe-734cc22012cb"
#define CHARACTERISTIC_OWNER_PHONE_NUMBER_UUID  "e7608c36-76e7-4e9c-9263-1786f7a2f854"
#define CHARACTERISTIC_CHIP_ID_UUID             "95374a93-101c-4f2c-a590-2e8fcb3dfd37"
#define CHARACTERISTIC_APPLICATION_COMMAND_UUID "c6014093-e175-41e8-a6bf-96964c7dfa71"

Adafruit_MPR121 cap = Adafruit_MPR121();

// for rtc
const char* ntpServer = "pool.ntp.org";
const long  gmtOffset_sec = 7 * 3600;
const int   daylightOffset_sec = 0;

// new device characteristic
BLECharacteristic* wifiCredential = NULL;
BLECharacteristic* ownerPhoneNumber = NULL;
BLECharacteristic* deviceChipID = NULL;
BLECharacteristic* response = NULL;
BLECharacteristic* command = NULL;
uint8_t reponseCode = 0;

// scan device
BLEScan* pBLEScan;
static BLEAddress *pScanAddress;

// keypad state save
uint16_t lasttouched = 0;
uint16_t currtouched = 0;
String keypad[] = {"*", "7", "4", "1", "0", "8", "5", "2", "#", "9", "6", "3"};

// device eeprom info address
const int modeAddress = 399;
const int onwerPhoneNumberAddress = 1;
const int delayAddress = 12;
const int otpAddress = 14;
const int deviceTrustedAddress[] = {21, 41, 61};
const int deviceTNameAddress[] = {81, 101, 121};
const int emergencyCodeAddress = 143;
const int availableDevicesCountAddress = 160;
const int deviceNameAddress = 170;
const int otpLimitEntryAddress = 210;
const int wifiCredentialAddress = 256;
const int chipIDAddress = 370;



// db path
String parentPath;
String lock_state_path;
String lock_otp_path;
String lock_delay_path;
String histories_path;
String trusted_devices_address_path;
String trusted_devices_name_path;
String update_code_path;
String alert_code_path;
String device_name_path;
String otp_limit_entry_path;
String device_type = "lock/";

// TIME DELAYTION
int typingDelay = 10000;
int devicesScanDelay = 10000;
int updateDataDelay = 1200;
unsigned long devicesScanPrevMillis;
unsigned long typingPrevMillis;
unsigned long updateDataPrevMillis;
unsigned long stateUnlockPrevMillis;
unsigned long ledMillis;

// device eeprom info
int modeIndex;
String wifi_ssid;
String wifi_password;
String onwerPhoneNumber;
String chipID;
String emergencyCode;

int stateUnlockDelay;
int otp_limit_entry = 4;
int state, _state;
String otp ;
String device_name;
String trusted_devices_name[3];
String trusted_devices_address[3];
boolean typing = false;
boolean otpWasConfirm = false;
boolean ledState = false;
int error_count = 0;
int update_code = -99;
String resetDeviceCode = "143";
String openConfigCode = "000";
int available_devices_count = 0;

// SPEAKER_PIN setting
int freq = 16000;
int channel = 0;
int resolution = 8;
int successSound = 3;
int touchSound = 1;
int otp_null = 2;
FirebaseData firebaseData;
FirebaseJson json;

void stateCheck() {
  if (state == _state) return;
  state = _state;
  switch (state) {
    case 1: openTheGate(); break;
    case 2: holdOn(); break;
    default: lockTheGate(); break;
  }
}
void stateChange(int _s) {
  if (_s != 2) {
    digitalWrite(RELAY_PIN, _s);
  }
  if (modeIndex == 1)
    Firebase.setInt(firebaseData, lock_state_path, _s);
}
void lockTheGate() {
  Serial.println("Đóng chặt lắm rồi này");
  stateChange(0);
  state = 0;
  lasttouched = 0;
  devicesScanPrevMillis = millis();
}

void openTheGate() {
  Serial.println("Mở rồi này");
  sound(successSound) ;
  stateChange(1);
  state = 1;
  stateUnlockPrevMillis = millis();

}
void holdOn() {
  state = 2;
  stateChange(state);
}

void historySave(String unlock_name, String unlock_type) {
  Serial.println("Saving History.....");
  char timeStringBuff[50];
  struct tm timeinfo;
  if (!getLocalTime(&timeinfo)) return;
  strftime(timeStringBuff, sizeof(timeStringBuff), "%A, %B %d %Y %H:%M:%S", &timeinfo);
  json.add("unlock_name", unlock_name); json.add("unlock_type", unlock_type); json.add("unlock_time", String(timeStringBuff));
  Firebase.pushJSON(firebaseData, histories_path, json);
  json.clear();
}

void sound(int soundCode) {
  for (int i = 0; i < soundCode; i++ ) {
    ledcWrite(channel, 255);
    delay(25);
    ledcWrite(channel, 0);
    delay(25);
  }
}
void checkOTP(String s) {
  if (emergencyCode.equals(s)) {
    openTheGate();
    otpWasConfirm = true;
  }
  else if (otp.equals(s))
  {
    openTheGate(); otp = ""; error_count = 0; otpWasConfirm = true;
    historySave("Người có mã otp", "otp");
    if (modeIndex == 1)Firebase.setString(firebaseData, lock_otp_path, otp);
  } else {
    sound(2);
    otpWasConfirm = false;
  }
}

void ConfirmTypingCode(String _typingCode) {
  String systemCode, pass;
  if (_typingCode.length() == 6)
  {
    checkOTP(_typingCode);
  }
  else
  {
    systemCode = _typingCode.substring(0, _typingCode.indexOf("*"));
    pass = _typingCode.substring(systemCode.length() + 1 );
    Serial.print("systemCode: ");
    Serial.println(systemCode);
    Serial.print("pass: ");
    Serial.println(pass);
    if (pass.equals(emergencyCode)) {
      if (systemCode.equals(resetDeviceCode) ) {
        for (int i = 0 ; i < EEPROM_SIZE ; i++) {
          EEPROM.write(i, 0);
        }
        EEPROM.commit();
        EEPROM.end();
        Serial.print("RESET: ");

      }
      else if (systemCode.equals(openConfigCode)) {
        modeIndex = 3;
        EEPROM.write(modeAddress, modeIndex);
        EEPROM.commit();
        delay(500);
        ESP.restart();
      }
    } else
    {
      otpWasConfirm = false;
    }
  }
  typing = false;
}

//-------------------------------------------------------------------------------------
class wifiCredentailCallBack: public BLECharacteristicCallbacks {
    void onWrite(BLECharacteristic *wifiCredential) {
      std::string rxValue = wifiCredential->getValue();
      String s;
      unsigned long count;
      if (rxValue.length() > 0) {
        for (int i = 0; i <= rxValue.length() - 1; i++)  s = s + rxValue[i];
        Serial.print("wifiCredentailCallBack: ");
        Serial.println(s);
        EEPROM.writeString(wifiCredentialAddress, s);
        String s = EEPROM.readString(wifiCredentialAddress);
        if (s.length() > 0) {
          wifi_ssid = s.substring(0, s.indexOf("|"));
          wifi_password = s.substring(wifi_ssid.length() + 1 );
        }

        //        WiFi.begin(wifi_ssid.c_str(), wifi_password.c_str());
        //        count = millis();
        //
        //        while (WiFi.status() != WL_CONNECTED ) {
        //          if (millis() - count >= 10000)
        //          {
        //            reponseCode = 9;
        //            response->setValue(&reponseCode, 1)
        //            ;break; break;
        //
        //          } else {
        //            delay(500);
        //            Serial.print(".");
        //          }
        //
        //        }
        EEPROM.commit();
        reponseCode = 1;
        response->setValue(&reponseCode, 1);



      }
    }
};

class ownerPhoneNumberCallback: public BLECharacteristicCallbacks {
    void onWrite(BLECharacteristic *ownerPhoneNumber) {
      std::string value = ownerPhoneNumber->getValue();
      String s;
      if (value.length() > 0) {
        for (int i = 0; i <= value.length() - 1; i++) s = s + value[i];
        Serial.print("ownerPhoneNumberCallback: ");
        Serial.println(s);
        onwerPhoneNumber = s.substring(0, s.indexOf("|"));
        emergencyCode = s.substring(onwerPhoneNumber.length() + 1, s.indexOf("*") );
        device_name =  s.substring(s.indexOf("*") + 1);
        EEPROM.writeString(deviceNameAddress, device_name);
        EEPROM.writeString(onwerPhoneNumberAddress, onwerPhoneNumber);
        EEPROM.writeString(emergencyCodeAddress, emergencyCode);
        EEPROM.commit();
        reponseCode = 2;
        response->setValue(&reponseCode, 1);
      }
    }
};
class commandCallback: public BLECharacteristicCallbacks {
    void onWrite(BLECharacteristic *ownerPhoneNumber) {
      std::string value = command->getValue();
      String commandData;
      if (value.length() > 0) {
        for (int i = 0; i <= value.length() - 1; i++) commandData = commandData + value[i];
        Serial.print("Reponse: ");
        Serial.println(commandData);
        if (commandData.equals(resetDeviceCode)) {
          EEPROM.writeInt(delayAddress, 10);
          EEPROM.writeInt(otpLimitEntryAddress, 3);
          EEPROM.writeString(chipIDAddress, WiFi.macAddress());
          EEPROM.writeInt(availableDevicesCountAddress, 0);
          EEPROM.commit();
        } 
        getDataCredentail();
        reponseCode = 3;
        response->setValue(&reponseCode, 1);


      }
    }
};


void newDeviceTask() {
  BLEDevice::init("Lyoko Smart Lock");                                        // Create Device
  BLEServer* server = BLEDevice::createServer();                              // Create Server
  BLEService* lyoko_service = server->createService(SERVICE_LYOKO_UUID);

  // Create a BLE Characteristic
  wifiCredential = lyoko_service->createCharacteristic(
                     CHARACTERISTIC_WIFI_CREDENTIAL_UUID,
                     BLECharacteristic::PROPERTY_WRITE |
                     BLECharacteristic::PROPERTY_READ
                   );

  ownerPhoneNumber = lyoko_service->createCharacteristic(
                       CHARACTERISTIC_OWNER_PHONE_NUMBER_UUID,
                       BLECharacteristic::PROPERTY_WRITE
                     );

  deviceChipID = lyoko_service->createCharacteristic(
                   CHARACTERISTIC_CHIP_ID_UUID,
                   BLECharacteristic::PROPERTY_READ
                 );

  response = lyoko_service->createCharacteristic(
               CHARACTERISTIC_RESPONSE_UUID,
               BLECharacteristic::PROPERTY_READ |
               BLECharacteristic::PROPERTY_NOTIFY
             );

  command = lyoko_service->createCharacteristic(
              CHARACTERISTIC_APPLICATION_COMMAND_UUID,
              BLECharacteristic::PROPERTY_WRITE
            );

  // Create a BLE Descriptor
  response->addDescriptor(new BLE2902());
  deviceChipID->setValue(WiFi.macAddress().c_str());
  wifiCredential->setCallbacks(new wifiCredentailCallBack());
  ownerPhoneNumber->setCallbacks(new ownerPhoneNumberCallback());
  command->setCallbacks(new commandCallback());

  // start the service
  lyoko_service->start();
  // Start advertising
  BLEAdvertising* pAdvertising = BLEDevice::getAdvertising();
  pAdvertising->addServiceUUID(SERVICE_LYOKO_UUID);
  pAdvertising->setScanResponse(false);
  pAdvertising->setMinPreferred(0x12);  // set value to 0x00 to not advertise this parameter
  BLEDevice::startAdvertising();
}


//-------------------------------------------------------------------------------------
void getDataCredentail() {
  String s = EEPROM.readString(wifiCredentialAddress);
  if (s.length() > 0) {
    wifi_ssid = s.substring(0, s.indexOf("|"));
    wifi_password = s.substring(wifi_ssid.length() + 1 );
  }
  onwerPhoneNumber = EEPROM.readString(onwerPhoneNumberAddress);
  chipID = EEPROM.readString(chipIDAddress);
  stateUnlockDelay = EEPROM.readInt(delayAddress);
  otp = EEPROM.readString(otpAddress);
  otp_limit_entry = EEPROM.readInt(otpLimitEntryAddress);
  emergencyCode = EEPROM.readString(emergencyCodeAddress);
  available_devices_count = EEPROM.readInt(availableDevicesCountAddress);
  device_name = EEPROM.readString(deviceNameAddress);

  for (int i = 0; i < available_devices_count; i++) {
    trusted_devices_address[i] = EEPROM.readString(deviceTrustedAddress[i]);
    trusted_devices_name[i] =  EEPROM.readString(deviceTNameAddress[i]);
    Serial.print("UPDATE DEVICE NO.");
    Serial.print(i);
    Serial.print(": ");
    Serial.print(trusted_devices_name[i]);
    Serial.print("---");
    Serial.println(trusted_devices_address[i]);
  }

  Serial.print("S: ");
  Serial.println(s);
  Serial.print("wifi_ssid: ");
  Serial.println(wifi_ssid);
  Serial.print("wifi_password: ");
  Serial.println(wifi_password);
  Serial.print("device_name: ");
  Serial.println(device_name);
  Serial.print("chipID: ");
  Serial.println(chipID);
  Serial.print("onwerPhoneNumber: ");
  Serial.println(onwerPhoneNumber);
  Serial.print("emergencyCode: ");
  Serial.println(emergencyCode);
  Serial.print("stateUnlockDelay: ");
  Serial.println(stateUnlockDelay);
  Serial.print("otp_limit_entry: ");
  Serial.println(otp_limit_entry);
  Serial.print("otp: ");
  Serial.println(otp);
  Serial.print("available_devices_count: ");
  Serial.println(available_devices_count);


  // SETTING DATABASE PATH
  parentPath = "/lyoko_users/" + onwerPhoneNumber + "/own_devices/" + chipID + "/";
  lock_otp_path = parentPath + device_type + "otp";
  lock_state_path = parentPath + device_type + "state";
  lock_delay_path = parentPath + device_type + "delay";
  histories_path = parentPath + "histories";
  trusted_devices_address_path = parentPath + "trusted_devices_address";
  trusted_devices_name_path = parentPath + "trusted_devices_name";
  update_code_path = parentPath + device_type + "update_code";
  alert_code_path = parentPath + device_type + "alert_code";
  otp_limit_entry_path = parentPath + device_type + "otp_limit_entry";
  device_name_path = parentPath + device_type + "device_name";
}

void wifiBegin() {
  WiFi.begin(wifi_ssid.c_str(), wifi_password.c_str());
  unsigned long wifiConnectMillis = millis();
  boolean isConnect = false;
  while (!isConnect ) {
    while (WiFi.status() != WL_CONNECTED ) {
      if (millis() - wifiConnectMillis > 9000) {
        modeIndex = 2;
        EEPROM.write(modeAddress, modeIndex);
        EEPROM.commit();
        delay(1000);
        ESP.restart();

      }
      delay(500);
      Serial.print(".");
    }
    isConnect = true;
  }
  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
  Firebase.reconnectWiFi(true);
  configTime(gmtOffset_sec, daylightOffset_sec, ntpServer);
  typing = false;
}

class ScanDevicesCallbacks: public BLEAdvertisedDeviceCallbacks {
    void onResult(BLEAdvertisedDevice advertisedDevice) {
      pScanAddress = new BLEAddress(advertisedDevice.getAddress());
      for (int i = 0; i < available_devices_count ; i++)
        if (strcmp(pScanAddress->toString().c_str(), trusted_devices_address[i].c_str()) == 0 && advertisedDevice.getRSSI() >= -60)
        {
          historySave(trusted_devices_name[i], "trusted_devices");
          openTheGate();
          pBLEScan->stop();
        }
    }
};

void updateUnlockDelay() {
  Firebase.getInt(firebaseData, lock_delay_path);
  stateUnlockDelay = firebaseData.intData();
  EEPROM.writeInt(delayAddress, stateUnlockDelay);
  EEPROM.commit();
  Serial.print("UPDATE DELAY: ");
  Serial.println(stateUnlockDelay);
  feedbackUpdate();
}

void updateOTPLimitEntry() {
  Firebase.getInt(firebaseData, otp_limit_entry_path);
  otp_limit_entry = firebaseData.intData();
  EEPROM.writeInt(otpLimitEntryAddress, otp_limit_entry);
  EEPROM.commit();
  Serial.print("OTP LIMIT ENTRY: ");
  Serial.println(otp_limit_entry);
  feedbackUpdate();
}
void updateDeviceName() {
  Firebase.getString(firebaseData, device_name_path);
  device_name = firebaseData.stringData();
  EEPROM.writeString(deviceNameAddress, device_name);
  EEPROM.commit();
  Serial.print("DEVICE NAME: ");
  Serial.println(device_name);
}

void updateOTP() {
  Firebase.getString(firebaseData, lock_otp_path);
  otp = firebaseData.stringData();
  EEPROM.writeString(otpAddress, otp);
  EEPROM.commit();
  Serial.print("UPDATE OTP: ");
  Serial.println(otp);
}

void updateTrustedDevices() {
  Firebase.get(firebaseData, trusted_devices_address_path);
  FirebaseJsonArray &arrAddress = firebaseData.jsonArray();
  for (size_t i = 0; i < arrAddress.size(); i++) {
    FirebaseJsonData &jsonData = firebaseData.jsonData();
    arrAddress.get(jsonData, i);
    trusted_devices_address[i] = jsonData.stringValue;
    EEPROM.writeString(deviceTrustedAddress[i], trusted_devices_address[i]);
    EEPROM.commit();
  }
  Firebase.get(firebaseData, trusted_devices_name_path);
  FirebaseJsonArray &arrName = firebaseData.jsonArray();
  for (size_t i = 0; i < arrName.size(); i++) {
    FirebaseJsonData &jsonData = firebaseData.jsonData();
    arrName.get(jsonData, i);
    trusted_devices_name[i] = jsonData.stringValue;
    EEPROM.writeString(deviceTNameAddress[i], trusted_devices_name[i]);
    EEPROM.commit();
  }
  available_devices_count = arrName.size();
  EEPROM.writeInt(availableDevicesCountAddress, available_devices_count);
  EEPROM.commit();
}
void feedbackUpdate() {
  Firebase.setInt(firebaseData, update_code_path, -99);
}

void listenForUpdateData() {
  boolean notChange = false;
  Serial.print("LISTEN UPDATE....");
  if (Firebase.getInt(firebaseData, update_code_path)) {
    switch (firebaseData.intData()) {
      case 0: _state = 0; stateCheck(); break;
      case 1: _state = 1; stateCheck(); break;
      case 2: _state = 2; stateCheck(); break;
      case 3: updateUnlockDelay(); break;
      case 4: updateOTP(); break;
      case 5: updateTrustedDevices(); break;
      case 6: updateOTPLimitEntry(); break;
      default: notChange = true;
    }
    if (!notChange) feedbackUpdate();
    updateDataPrevMillis = millis();
  } else ESP.restart();
}
void otpState() {
  Serial.print("ERROR COUNT: ");
  Serial.println(error_count);
  Serial.print("typing: ");
  Serial.println(typing);
}

void getTypingOtp() {
  Serial.println("Bắt đầu lấy OTP.....");
  boolean led = false;
  String _otp;
  typingPrevMillis = millis();
  sound(successSound);
  while (typing)
  {
    if (millis() - ledMillis > 400 )    //blink otp getting led code
    {
      led = !led;
      digitalWrite(LED_BLUE_PIN, led);
      ledMillis = millis();
    }
    currtouched = cap.touched();
    if (millis() - typingPrevMillis < typingDelay)
    {
      for (uint8_t i = 0; i < 12; i++)
      {
        if ((currtouched & _BV(i)) && !(lasttouched & _BV(i)) )
        {
          if (i != 8)
          {
            _otp = _otp + keypad[i];
            Serial.print("otp: ");
            Serial.println(_otp);
          }
          else
          {
            ConfirmTypingCode(_otp);
          }
          sound(touchSound);
        }
      }
    }
    lasttouched = currtouched;
  }
  if (!otpWasConfirm) error_count++;
  if (error_count == otp_limit_entry - 1 ) sendMessage();
  otpState(); otpWasConfirm = false;
  Serial.println("Kết thúc lấy OTP");
}
void sendMessage() {
  Serial.println("SET ALERT CODE");
  Firebase.setString(firebaseData, alert_code_path, device_name);
}

void notifyInfo(int j) {
  for (int i = 0; i <= j; i++) {
    digitalWrite(LED_RED_PIN, 200);
    ledcWrite(channel, 255);
    delay(400);
    digitalWrite(LED_RED_PIN, 0);
    ledcWrite(channel, 0);
    delay(100);
  }
}

void scanTrustedDevices() {
  Serial.println("SCANNING DEVICES");
  devicesScanPrevMillis = millis();
  pBLEScan->start(1);
  pBLEScan->stop();

}
void bleScanBegin() {
  BLEDevice::init("");
  pBLEScan = BLEDevice::getScan(); //create new scan
  pBLEScan->setAdvertisedDeviceCallbacks(new ScanDevicesCallbacks());
  pBLEScan->setActiveScan(true); //active scan uses more power, but get results faster
  pBLEScan->setInterval(151);
  pBLEScan->setWindow(150);
}

void setup() {
  Serial.begin(115200);
  pinMode(RELAY_PIN, OUTPUT);
  pinMode(LED_RED_PIN, OUTPUT);
  pinMode(LED_GREEN_PIN, OUTPUT);
  pinMode(LED_BLUE_PIN, OUTPUT);
  if (!cap.begin(0x5A)) {
    Serial.println("NOT");
    digitalWrite(LED_RED_PIN, 1);
    delay(400);
    digitalWrite(LED_RED_PIN, 0);
  }
  else {
    Serial.println("FOUND");
    digitalWrite(LED_BLUE_PIN, 1);
    delay(400);
    digitalWrite(LED_BLUE_PIN, 0);
  }
  ledcSetup(channel, freq, resolution);
  ledcAttachPin(SPEAKER_PIN, channel);
  
  if (!EEPROM.begin(EEPROM_SIZE)) {
    delay(1000);
  }
  modeIndex = EEPROM.readInt(modeAddress);
  Serial.print("modeIndex: ");
  Serial.println(modeIndex);
  if (modeIndex == 2) {
    Serial.println("SCAN DEVICE ONLY");
    bleScanBegin();
  }
  if (modeIndex == 0) {
    Serial.println("NEW DEVICE");
    newDeviceTask();
  }
  if (modeIndex == 3) {
    Serial.println("OFFLINE CONFIG MODE");
    newDeviceTask();
  }
  if (modeIndex == 1) {
    Serial.println("REGISTED DEVICE");
    getDataCredentail();
    wifiBegin();
    digitalWrite(RELAY_PIN, 0);
    Firebase.setInt(firebaseData, lock_state_path, 0);
    bleScanBegin();
  }
}

void loop() {
  if (millis() / 1000 > 1000) ESP.restart();
  while (modeIndex == 0)
    if (millis() - ledMillis > 300)
    {
      ledState = !ledState;
      digitalWrite(LED_BLUE_PIN, ledState);
      digitalWrite(LED_RED_PIN, !ledState);
      ledMillis = millis();
      response->notify();
      if (reponseCode == 3) {
        modeIndex = 1;
        EEPROM.write(modeAddress, modeIndex);
        EEPROM.commit();
        delay(1000);
        ESP.restart();
      }
    }
  while (state == 1) if (millis() - stateUnlockPrevMillis >= stateUnlockDelay * 1000 ) lockTheGate();
  currtouched = cap.touched();
  // IF * touched
  if (!(currtouched & _BV(0)) && (lasttouched & _BV(0)) )
  {
    if (error_count < otp_limit_entry )
    {
      typing = true;
      getTypingOtp();
    } else {
      Serial.println("KHÔNG CHO NHẬP");
      notifyInfo(1);
    }
  }
  lasttouched = currtouched;
  if (modeIndex == 1)
    if (millis() - updateDataPrevMillis > updateDataDelay) listenForUpdateData();
  if (millis() - devicesScanPrevMillis > devicesScanDelay &&  available_devices_count > 0) scanTrustedDevices();
}
