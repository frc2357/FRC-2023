#include <JsonState.h>
#include "Adafruit_Keypad.h"

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
void setup() {
  customKeypad.begin();
}

void loop() {
  customKeypad.tick();

  while(customKeypad.available()){
    keypadEvent e = customKeypad.read();
    if(e.bit.EVENT == KEY_JUST_PRESSED){
      Json::Int["keyInfo"][0] = e.bit.ROW;
      Json::Int["keyInfo"][1] = e.bit.COL;
    }
  }
  
}