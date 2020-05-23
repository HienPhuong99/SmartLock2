#include "EEPROM.h"
#define EEPROM_SIZE 256
void setup() {
  Serial.begin(115200);
  if (!EEPROM.begin(EEPROM_SIZE)) {
    delay(1000);
  }
  for (int i = 0 ; i < EEPROM_SIZE ; i++) {
    Serial.print(EEPROM.read(i));
  }
  delay(500); Serial.println();
  for (int i = 0 ; i < EEPROM_SIZE ; i++) {
    EEPROM.write(i, 0);
  }
 EEPROM.commit();
 for (int i = 0 ; i < EEPROM_SIZE ; i++) {
    Serial.print(EEPROM.read(i));
  }
  EEPROM.end();
}

void loop() {
  // put your main code here, to run repeatedly:

}
