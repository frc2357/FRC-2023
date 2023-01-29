#include <XInput.h>
#include "Adafruit_Keypad.h"

const byte ROWS = 4; // rows
const byte COLS = 4; // columns
//define the symbols on the buttons of the keypads
char keys[ROWS][COLS] = {
  {'1','2','3','4'},
  {'5','6','7','8'},
  {'9','0','A','B'},
  {'C','D','E','F'}
};
byte rowPins[ROWS] = {5, 4, 3, 2}; //connect to the row pinouts of the keypad
byte colPins[COLS] = {11, 10, 9, 8}; //connect to the column pinouts of the keypad

//initialize an instance of class NewKeypad
Adafruit_Keypad customKeypad = Adafruit_Keypad( makeKeymap(keys), rowPins, colPins, ROWS, COLS);

const int JoyMax = 32767;
void setup() {
  XInput.begin();
  customKeypad.begin();
}

void loop() {
  int axis_x = 0;
  int axis_y = 0;
  customKeypad.tick();

  while(customKeypad.available()){
    keypadEvent e = customKeypad.read();
    if(e.bit.EVENT == KEY_JUST_PRESSED){
      axis_x = (e.bit.COL/10)*JoyMax;
      axis_y = (e.bit.ROW/10)*JoyMax;
  XInput.setJoystick(JOY_RIGHT, axis_x, axis_y);
    }
    else if(e.bit.EVENT == KEY_JUST_RELEASED) XInput.setJoystick(JOY_RIGHT, 0, 0);
  }
  
}