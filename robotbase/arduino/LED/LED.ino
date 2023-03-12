#include<Wire.h>
#include <Sensor2357.h>
#include <Adafruit_NeoPixel.h>

#define LOOP_DELAY_MS 10

int mode=0;
int pin = D0;
int safeleds = rand() % 100;
bool odd = false;
Adafruit_NeoPixel strip(100, pin, NEO_RGB + NEO_KHZ800);

int ledUpdate(int min, int max) {
  mode = min;
  if(mode==0){
    strip.setBrightness(10);
    strip.fill(strip.Color(0,   128,   104));
    strip.show();
  }else if(mode==1){
    strip.setBrightness(10);
    strip.clear();
    strip.fill(strip.Color(255,   255,   0));
    strip.show();
  }else if(mode==2){
    return 2;
    strip.setBrightness(10);
    if(odd){
      odd=false;
    }else{
      odd=true;
    }
    for(int i=0; i<strip.numPixels(); i++) {
      if(odd){
        strip.setPixelColor(i, strip.Color(0,   0,   100)); 
      }else{
        strip.setPixelColor(i, strip.Color(0,   100,   0)); 
      }
        strip.show();
        delay(30);
      }
     }else if(mode==3){
      strip.clear();
      strip.setPixelColor(safeleds, strip.Color(0,   0,   255));
      strip.setPixelColor(safeleds+1, strip.Color(0,   0,   255));
      strip.setPixelColor(safeleds+2, strip.Color(0,   0,   255));
      strip.setBrightness(10);
      for(int i=0; i<strip.numPixels(); i++) {
        if(i==safeleds||i-1==safeleds||i-2==safeleds){
          safeleds = rand() % 100;
          strip.show();
        }
        strip.setPixelColor(i-1, strip.Color(0,   0,   0));
        strip.setPixelColor(i, strip.Color(0,   100,   0)); 
        strip.show();
        delay(15);
        }
     }else if(mode==4){
      strip.setBrightness(10);
        strip.clear();
        strip.fill(strip.Color(0,   255,   0));
        strip.show();
      }else if(mode==5){
        strip.setBrightness(10);
          strip.clear();
          strip.fill(strip.Color(0,   0,   255));
          strip.show();
      }
      return mode;
}

SensorDevice_Seeed_XIAO_RP2040<1> device("LEDLight");

void setup() {
  device.initSensor("ledState", ledUpdate,0,0);
  device.begin();
  strip.begin();           // INITIALIZE NeoPixel strip object (REQUIRED)
  strip.show();            // Turn OFF all pixels ASAP
  strip.setBrightness(100);
  strip.clear();
}

void loop() {
  device.update(); 
  delay(LOOP_DELAY_MS);
}
