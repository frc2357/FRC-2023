#ifndef LIGHTEDBUTTON_H
#define LIGHTEDBUTTON_H

#include <Arduino.h>
#include <Adafruit_MCP23X17.h>

class LightedButton {
public:
  LightedButton(Adafruit_MCP23X17 &mcp, uint8_t ledPin, uint8_t buttonPin)
    : m_mcp(mcp),
      m_ledPin(ledPin),
      m_buttonPin(buttonPin),
      m_lastButtonValue(HIGH)
  {
    setLit(false);
  }

  bool isLit() {
    return m_lit;
  }

  void setLit(bool lit) {
    if (lit != m_lit) {
      m_mcp.digitalWrite(m_ledPin, lit ? HIGH : LOW);
      m_lit = lit;
    }
  }

  bool update() {
    uint8_t buttonValue = m_mcp.digitalRead(m_buttonPin);
    bool wasPressed = false;

    if (buttonValue == LOW && m_lastButtonValue == HIGH) {
      wasPressed = true;
    }
    m_lastButtonValue = buttonValue;
    return wasPressed;
  }

private:
  Adafruit_MCP23X17 &m_mcp;
  uint8_t m_ledPin;
  uint8_t m_lit;
  uint8_t m_buttonPin;
  uint8_t m_lastButtonValue;
};

#endif /* LIGHTEDBUTTON_H */