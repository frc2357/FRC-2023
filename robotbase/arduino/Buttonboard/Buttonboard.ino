#include <Sensor2357.h>
#include "Adafruit_Keypad.h"

#define LOOP_DELAY_MS = 10
const byte ROWS = 3; // rows
const byte COLS = 9; // columns
//define the symbols on the buttons of the keypads
char keys[ROWS][COLS] = {
  {'1','2','3','4','5','6','7','8','9'},
  {'0','A','B','C','D','E','F','G','H'},
  {'I','J','K','L','M','N','O','P','Q'},
};
byte rowPins[ROWS] = {0, 10, 20}; //connect to the row pinouts of the keypad
byte colPins[COLS] = {9, 1, 2, 3,4,5,6,7,8}; //connect to the column pinouts of the keypad
//initialize an instance of class NewKeypad
Adafruit_Keypad customKeypad = Adafruit_Keypad( makeKeymap(keys), rowPins, colPins, ROWS, COLS);
//makes Json variables for what we need, returns 1 if a button was pressed, 0 if not.
int readIntValue(int min, int max){  
  customKeypad.tick();
  while(customKeypad.available()){
    keypadEvent e = customKeypad.read();
    if(e.bit.EVENT == KEY_JUST_PRESSED){
      Json::Int("keyRow",e.bit.ROW);
      Json::Int("keyCol",e.bit.COL);
      return 1;
    }
    return 0;
  }
  return 0;
}
void setup() {
  customKeypad.begin();
  sevice.initSensor("intValue",readIntValue,0,8);
  device.begin();
}

void loop() {
  device.update();
  delay(LOOP_DELAY_MS);
}