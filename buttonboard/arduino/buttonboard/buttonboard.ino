/*
 * Button Board Arduino Sketch
 *
 * This handles all of the buttons and LEDs from the button board
 * and connects them to the Driver Station via the Sensor2357 USB JSON library
 * 
 */
#include <Sensor2357.h>
#include <Adafruit_Keypad.h>
#include <Adafruit_NeoPXL8.h>
#include "GridButtons.h"

// --- Pin Definitions ---
#define NEOPIXEL_PIN             16

uint8_t rowPins[GRID_ROWS] = {5, 6, 9};
uint8_t colPins[GRID_COLS] = {26, 27, 28, 29, 24, 25, 14, 15, 8};
int8_t neoPixelPins[8] = { 16, 17, 18, 19, 20, 21, 22, 23 };  // Can be used with NeoPXL8
// ----------------------- 

// --- Constants ---
#define LOOP_DELAY_MS            10
#define TARGET_NONE              -1
#define TARGET_CUBE              0
#define TARGET_CONE              1
#define TARGET_GRID_NONE         -1
#define COLOR_ORDER              NEO_GRB // NeoPixel color format (see Adafruit_NeoPixel)
#define NEOPIXEL_STRIP_MAXLEN    27      // The longest neopixel "strip" we have
const size_t TARGET_COL_INDEX = 0;
const size_t TARGET_ROW_INDEX = 1;
const size_t TARGET_TYPE_INDEX = 2;
char keys[GRID_ROWS][GRID_COLS] = {
  {'a','b','c','d','e','f','g','h','i'},
  {'j','k','l','m','n','o','p','q','r'},
  {'s','t','u','v','w','x','y','z','@'}
};
// -----------------

// --- Globals ---
char grid[NODE_COUNT + 1];
int8_t target[2];
const char *alliance;
Adafruit_NeoPXL8 neoPixels(NEOPIXEL_STRIP_MAXLEN, neoPixelPins, COLOR_ORDER);
Adafruit_Keypad keypad = Adafruit_Keypad(makeKeymap(keys), rowPins, colPins, GRID_ROWS, GRID_COLS);
SensorDevice_Adafruit_RP2040_Scorpio<3> device("buttonboard");
GridButtons gridButtons(neoPixels, 0, keypad);
// ---------------

// "alliance" sensor
const char *updateAlliance(const SensorSettings& settings) {
  const char *nextAlliance = settings["value"].asString();
  if (strcmp(nextAlliance, ALLIANCE_RED) == 0) {
    alliance = ALLIANCE_RED;
  } else if (strcmp(nextAlliance, ALLIANCE_BLUE) == 0) {
    alliance = ALLIANCE_BLUE;
  } else {
    alliance = ALLIANCE_UNSET;
  }
  gridButtons.setAlliance(alliance);
  return nextAlliance;
}

// "grid" sensor
const char *updateGrid(const SensorSettings& settings) {
  const char *nextGrid = settings["value"].asString();
  if (strncmp(grid, nextGrid, NODE_COUNT) != 0) {
    strncpy(grid, nextGrid, NODE_COUNT);
    gridButtons.updateGridData(grid);
  }
  return grid;
}

void setTarget() {
  if (gridButtons.getSelectedCol() == -1) {
    clearTarget();
    return;
  }
  target[TARGET_COL_INDEX] = gridButtons.getSelectedCol();
  target[TARGET_ROW_INDEX] = gridButtons.getSelectedRow();
  target[TARGET_TYPE_INDEX] = gridButtons.isSelectedCube() ? TARGET_CUBE : TARGET_CONE;
}

void clearTarget() {
  gridButtons.clearSelected();
  target[TARGET_COL_INDEX] = -1;
  target[TARGET_ROW_INDEX] = -1;
  target[TARGET_TYPE_INDEX] = -1;
}

void updateTargetCol(int8_t prevCol, int8_t ntCol) {
  int8_t buttonCol = gridButtons.getSelectedCol();

  if (buttonCol != prevCol) {
    // Button was pressed
    setTarget();
  }

  if (ntCol != prevCol && ntCol == -1) {
    // Robot cleared our target (probably scored it just now)
    clearTarget();
  }
}

void updateTargetRow(int8_t prevRow, int8_t ntRow) {
  int8_t buttonRow = gridButtons.getSelectedRow();

  if (buttonRow != prevRow) {
    // Button was pressed
    setTarget();
  }
}

// "target" sensor
int readTarget(size_t index, const SensorSettings &settings) {
  JsonElement &el = settings["array"];
  if (index == TARGET_COL_INDEX) {
    int8_t prevCol = target[TARGET_COL_INDEX];
    int ntCol = el[TARGET_COL_INDEX].asInt();
    updateTargetCol(prevCol, ntCol);
    return target[TARGET_COL_INDEX];

  } else if (index == TARGET_ROW_INDEX) {
    int8_t prevRow = target[TARGET_ROW_INDEX];
    int ntRow = el[TARGET_ROW_INDEX].asInt();
    updateTargetRow(prevRow, ntRow);
    return target[TARGET_ROW_INDEX];

  } else if (index == TARGET_TYPE_INDEX) {
    int8_t prevType = target[TARGET_TYPE_INDEX];
    int8_t buttonType = gridButtons.isSelectedCube() ? TARGET_CUBE : TARGET_CONE;

    if (buttonType != prevType) {
      // Button was pressed
      setTarget();
    }
    return target[TARGET_TYPE_INDEX];
  }
  return -1;
}

void setup() {
  memset(grid, 0, NODE_COUNT + 1);

  // Set up hardware
  neoPixels.begin();
  neoPixels.setBrightness(255);
  neoPixels.clear();
  neoPixels.show();

  keypad.begin();

  gridButtons.begin();

  // Set up JSON device
  device.initSensor("alliance", updateAlliance, (size_t) 5);
  device.initSensor("grid", updateGrid, (size_t) (NODE_COUNT + 1));
  device.initSensor("target", readTarget, (size_t) 3);
  device.begin();
}

void loop() {
  bool ledsChanged = false;

  keypad.tick();
  gridButtons.update();

  if (gridButtons.updateLEDs()) {
    ledsChanged = true;
  }
  if (ledsChanged) {
    neoPixels.show();
  }

  device.update();

  delay(LOOP_DELAY_MS);
}
