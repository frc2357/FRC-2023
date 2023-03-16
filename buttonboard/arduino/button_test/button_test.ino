// Blinks an LED attached to a MCP23XXX pin.

// ok to include only the one needed
// both included here to make things simple for example
#include <Adafruit_MCP23X17.h>

// --- Pin Definitions ---
#define NEOPIXEL_PIN             16
#define ALLIANCE_RED_LED         0
#define ALLIANCE_RED_BUTTON      1
#define ALLIANCE_BLUE_LED        2
#define ALLIANCE_BLUE_BUTTON     3

// uncomment appropriate line
Adafruit_MCP23X17 mcp;

void setup() {
  Serial.begin(115200);
  while (!Serial);
  Serial.println("MCP23xxx Blink Test!");

  // uncomment appropriate mcp.begin
  if (!mcp.begin_I2C(0x20, &Wire1)) {
    Serial.println("Error.");
    while (1);
  }

  // configure pin for output
  mcp.pinMode(ALLIANCE_RED_LED, OUTPUT);
  mcp.digitalWrite(ALLIANCE_RED_LED, LOW);
  mcp.pinMode(ALLIANCE_RED_BUTTON, INPUT_PULLUP);
  mcp.pinMode(ALLIANCE_BLUE_LED, OUTPUT);
  mcp.digitalWrite(ALLIANCE_BLUE_LED, LOW);
  mcp.pinMode(ALLIANCE_BLUE_BUTTON, INPUT_PULLUP);

  Serial.println("Looping...");
}

void loop() {
  mcp.digitalWrite(ALLIANCE_RED_LED, HIGH);
  mcp.digitalWrite(ALLIANCE_BLUE_LED, HIGH);
  delay(500);
  mcp.digitalWrite(ALLIANCE_RED_LED, LOW);
  mcp.digitalWrite(ALLIANCE_BLUE_LED, LOW);
  delay(500);
}
