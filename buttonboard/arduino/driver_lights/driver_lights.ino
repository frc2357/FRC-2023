/*
 * Driver Grid Lights
 */
#include <Sensor2357.h>
#include <Adafruit_NeoPixel.h>

#define LOOP_DELAY_MS            10
#define SECTION_COUNT            3
#define LEDS_PER_SECTION         5
#define LED_COUNT                (SECTION_COUNT * LEDS_PER_SECTION)
#define CONE                     'A'
#define CUBE                     'O'
#define LED_PIN                  D10

char target[SECTION_COUNT + 1];

const char *updateTarget(const SensorSettings& settings) {
  const char *nextTarget = settings["value"].asString();
  if (strncmp(target, nextTarget, SECTION_COUNT) != 0) {
    strncpy(target, nextTarget, SECTION_COUNT);
  }
  return nextTarget;
}

Adafruit_NeoPixel strip(LED_COUNT, LED_PIN, NEO_GRB + NEO_KHZ800);
SensorDevice_Seeed_XIAO_RP2040<1> device("driverlights");

void setup() {
  memset(target, 0, SECTION_COUNT + 1);

  strip.begin();
  strip.show();
  strip.setBrightness(50);
  strip.clear();

  device.initSensor("target", updateTarget, (size_t) SECTION_COUNT + 1);
  device.begin();
}

void loop() {
  device.update();
  updateLEDs();
  delay(LOOP_DELAY_MS);
}

void updateLEDs() {
  for (int sectionIndex = 0; sectionIndex < SECTION_COUNT; sectionIndex++) {
    int startLED = LED_COUNT - ((sectionIndex + 1) * LEDS_PER_SECTION);
    for (int ledIndex = startLED; ledIndex < startLED + LEDS_PER_SECTION; ledIndex++) {
      strip.setPixelColor(ledIndex, getLEDColor(sectionIndex));
    }
  }
  strip.show();
}

uint32_t getLEDColor(int sectionIndex) {
  uint32_t color;

  if (target[sectionIndex] == 'O') {
    return getColorCube();
  }
  if (target[sectionIndex] == 'A') {
    return getColorCone();
  }
  return getColorEmpty();
}

uint32_t getColorEmpty() {
  return strip.Color(0, 0, 0);
}

uint32_t getColorCone() {
  return strip.Color(175, 125, 0);
}

uint32_t getColorCube() {
  return strip.Color(100, 0, 200);
}
