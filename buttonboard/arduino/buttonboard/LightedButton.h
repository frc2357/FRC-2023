#ifndef LIGHTEDBUTTON_H
#define LIGHTEDBUTTON_H

#include <Arduino.h>
#include <Adafruit_MCP23X17.h>

enum LEDMode {
  LED_OFF,
  LED_ON,
  LED_FLASH_FAST,
};

class LightedButton {
public:

  LightedButton(Adafruit_MCP23X17 &mcp, uint8_t ledPin, uint8_t buttonPin)
    : m_mcp(mcp),
      m_ledPin(ledPin),
      m_buttonPin(buttonPin),
      m_lastButtonValue(HIGH)
  {
    setLEDMode(LED_OFF);
  }

  LEDMode getLEDMode() {
    return m_ledMode;
  }

  void setLEDMode(LEDMode mode) {
    m_ledMode = mode;
  }

  bool update() {
    uint8_t buttonValue = m_mcp.digitalRead(m_buttonPin);
    bool wasPressed = false;

    if (buttonValue == LOW && m_lastButtonValue == HIGH) {
      wasPressed = true;
    }
    m_lastButtonValue = buttonValue;

    updateLED();

    return wasPressed;
  }

  void updateLED() {
    switch (m_ledMode) {
      case LED_OFF:
        m_mcp.digitalWrite(m_ledPin, LOW);
        break;
      case LED_ON:
        m_mcp.digitalWrite(m_ledPin, HIGH);
        break;
      case LED_FLASH_FAST:
        uint32_t cycle = millis() % 250;
        m_mcp.digitalWrite(m_ledPin, cycle < 125 ? HIGH : LOW);
        break;
    }
  }

private:
  Adafruit_MCP23X17 &m_mcp;
  uint8_t m_ledPin;
  LEDMode m_ledMode;
  uint8_t m_buttonPin;
  uint8_t m_lastButtonValue;
};

#endif /* LIGHTEDBUTTON_H */