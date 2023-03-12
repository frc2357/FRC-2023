#ifndef GRIDBUTTONS_H
#define GRIDBUTTONS_H

#include <Arduino.h>
#include <Adafruit_Keypad.h>
#include <Adafruit_NeoPXL8.h>

#define ALLIANCE_UNSET           -1
#define ALLIANCE_RED             0
#define ALLIANCE_BLUE            1

#define GRID_ROWS                3
#define GRID_COLS                9
#define NODE_COUNT               (GRID_ROWS * GRID_COLS)

#define NODE_EMPTY               '-'
#define NODE_CONE                'A'
#define NODE_CUBE                'O'

enum NodeState {
  NodeDisconnected,
  NodeEmpty,
  NodeCone,
  NodeCube,
  NodeLinkCone,
  NodeLinkCube,
  NodeLinkOpportunity,
  NodeTargetCone,
  NodeTargetCube
};

class GridButtons {
public:
  GridButtons(Adafruit_NeoPXL8 &neoPixels, size_t pixelOffset, Adafruit_Keypad &keypad)
    : m_neoPixels(neoPixels),
      m_pixelOffset(pixelOffset),
      m_keypad(keypad),
      m_alliance(ALLIANCE_UNSET),
      m_ledsChanged(false),
      m_selectedRow(-1),
      m_selectedCol(-1)
  {
    memset(m_nodes, 0, GRID_ROWS * GRID_COLS);
    memset(m_pixelColors, 0, GRID_ROWS * GRID_COLS);
  }

  void begin() {
    clearGridData();
  }

  void update() {
    updatePixelColors();
    if (isConnected()) {
      scanKeypad(m_keypad);
    }
  }

  bool updateLEDs() {
    if (m_ledsChanged) {
      m_ledsChanged = false;
      return true;
    } else {
      return false;
    }
  }

  void updateGridData(const char *gridData) {
    if (strlen(gridData) < 27) {
      clearGridData();
      return;
    }

    size_t index = 0;
    for (size_t row = 0; row < GRID_ROWS; row++) {
      for (size_t col = 0; col < GRID_COLS; col++) {
        m_nodes[row][col] = calculateGridValue(row, col, gridData[index]);
        index++;
      }
    }
  }

  NodeState calculateGridValue(size_t row, size_t col, char gridValue) {
    // TODO: Add target status
    // TODO: Add link status
    if (gridValue == NODE_CONE) {
      return NodeCone;
    } else if (gridValue == NODE_CUBE) {
      return NodeCube;
    } else {
      return NodeEmpty;
    }
  }

  void clearGridData() {
    for (int row = 0; row < GRID_ROWS; row++) {
      for (int col = 0; col < GRID_COLS; col++) {
        m_nodes[row][col] = NodeDisconnected;
      }
    }
  }

  void setAlliance(int8_t alliance) {
    m_alliance = alliance;
  }

  bool isConnected() {
    return m_nodes[0][0] != NodeDisconnected;
  }

  uint8_t getSelectedRow() {
    return m_selectedRow;
  }

  uint8_t getSelectedCol() {
    return m_selectedCol;
  }

  void clearSelected() {
    m_selectedRow = -1;
    m_selectedCol = -1;
  }

private:
  NodeState m_nodes[GRID_ROWS][GRID_COLS];
  uint32_t m_pixelColors[GRID_ROWS * GRID_COLS];
  Adafruit_NeoPXL8 &m_neoPixels;
  size_t m_pixelOffset;
  Adafruit_Keypad &m_keypad;
  int8_t m_alliance;
  bool m_ledsChanged;
  int8_t m_selectedRow;
  int8_t m_selectedCol;
  bool m_selectedIsCube;
  uint32_t m_keypressMillis;

  void scanKeypad(Adafruit_Keypad &keypad) {
    while (keypad.available()) {
      keypadEvent e = keypad.read();
      uint8_t row = e.bit.ROW;
      uint8_t col = e.bit.COL;
      if (e.bit.EVENT == KEY_JUST_PRESSED) {
        uint16_t keynum;
        if (row % 2 == 0) { // even row
          keynum = row * GRID_COLS + col;
        } else { // odd row the neopixels go BACKWARDS!
          keynum = row * GRID_COLS + (5 - col);
        }
        onKeyPress(row, col);
      }
      else if(e.bit.EVENT == KEY_JUST_RELEASED) {
        onKeyRelease(row, col);
      }
    }
  }

  void onKeyPress(uint8_t row, uint8_t col) {
    if (m_selectedCol == col && m_selectedRow == row) {
      // We already selected this key.
      if (row == 2 && m_selectedIsCube) {
        // It's a hybrid node selected for cube, switch to cone
        m_selectedIsCube = false;
      } else {
        // Otherwise clear it
        clearSelected();
      }
      return;
    }

    m_selectedRow = row;
    m_selectedCol = col;
    m_selectedIsCube = (row == 2 || col == 1 || col == 4 || col == 7);
    m_keypressMillis = millis();
  }

  void onKeyRelease(uint8_t row, uint8_t col) {
  }

  void setPixelColor(uint16_t index, uint32_t color) {
    if (m_pixelColors[index] != color) {
      m_pixelColors[index] = color;
      m_neoPixels.setPixelColor(index, color);
      m_ledsChanged = true;
    }
  }

  void updatePixelColors() {
    uint32_t colorEmptyCoop = getColorEmptyCoop();
    uint32_t colorEmptyAlliance;

    if (m_alliance == ALLIANCE_RED) {
      colorEmptyAlliance = getColorEmptyRed();
    } else if (m_alliance == ALLIANCE_BLUE) {
      colorEmptyAlliance = getColorEmptyBlue();
    } else {
      colorEmptyAlliance = getColorEmptyUnsetAlliance();
    }

    for (uint8_t row = 0; row < GRID_ROWS; row++) {
      for (uint8_t col = 0; col < GRID_COLS; col++) {
        size_t ledIndex = getLEDIndex(row, col);

        if (m_selectedRow == row && m_selectedCol == col) {
          setPixelColor(ledIndex, m_selectedIsCube ? getColorTargetCube() : getColorTargetCone());
          continue;
        }

        switch (m_nodes[row][col]) {
          case NodeEmpty:
            if (col >= 3 && col <=5) {
              setPixelColor(ledIndex, colorEmptyCoop);
            } else {
              setPixelColor(ledIndex, colorEmptyAlliance);
            }
            break;
          case NodeCone:
            setPixelColor(ledIndex, getColorCone());
            break;
          case NodeCube:
            setPixelColor(ledIndex, getColorCube());
            break;
          case NodeLinkCone:
            setPixelColor(ledIndex, getColorLinkCone());
            break;
          case NodeLinkCube:
            setPixelColor(ledIndex, getColorLinkCube());
            break;
          case NodeLinkOpportunity:
            setPixelColor(ledIndex, getColorLinkOpportunity());
            break;
          case NodeDisconnected:
          default:
            setPixelColor(ledIndex, getColorDisconnected());
            break;
        }
      }
    }
  }

  size_t getLEDIndex(uint8_t row, uint8_t col) {
    size_t index; 

    if (row % 2 == 0) { // even row
      index = row * GRID_COLS + col;
    } else { // odd row the neopixels go BACKWARDS!
      index = row * GRID_COLS + ((GRID_COLS - 1) - col);
    }

    return index;
  }

  uint32_t getColorEmptyUnsetAlliance() {
    unsigned long cycleMillis = millis() % 2000;

    if (cycleMillis < 1000) {
      float factor = ((float)cycleMillis) / 1000.0;
      return m_neoPixels.Color(255 * factor, 255 * factor, 255 * factor);
    } else {
      float factor = ((float)2000 - cycleMillis) / 1000.0;
      return m_neoPixels.Color(255 * factor, 255 * factor, 255 * factor);
    }
  }

  uint32_t getColorEmptyRed() {
    return m_neoPixels.Color(4, 0, 0);
  }

  uint32_t getColorEmptyBlue() {
    return m_neoPixels.Color(0, 0, 4);
  }

  uint32_t getColorEmptyCoop() {
    return m_neoPixels.Color(2, 2, 2);
  }

  uint32_t getColorDisconnected() {
    return m_neoPixels.Color(125, 125, 125);
  }

  uint32_t getColorCone() {
    return m_neoPixels.Color(175, 125, 0);
  }

  uint32_t getColorCube() {
    return m_neoPixels.Color(100, 0, 200);
  }

  uint32_t getColorLinkCone() {
    return m_neoPixels.Color(87, 62, 0);
  }

  uint32_t getColorLinkCube() {
    return m_neoPixels.Color(60, 0, 100);
  }

  uint32_t getColorLinkOpportunity() {
    return m_neoPixels.Color(255, 255, 255);
  }

  uint32_t getColorTargetCone() {
    return getSelectionFlash(255, 182, 0);
  }

  uint32_t getColorTargetCube() {
    return getSelectionFlash(128, 0, 255);
  }

  uint32_t getSelectionFlash(uint8_t r, uint8_t g, uint8_t b) {
    unsigned long cycleMillis = (millis() - m_keypressMillis) % 500;

    if (cycleMillis < 250) {
      float factor = ((float)cycleMillis) / 250.0;
      return m_neoPixels.Color(r * factor, g * factor, b * factor);
    } else {
      float factor = ((float)500 - cycleMillis) / 250.0;
      return m_neoPixels.Color(r * factor, g * factor, b * factor);
    }
  }
};

#endif // GRIDBUTTONS_H