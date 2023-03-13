package buttonboard.arduino;

public class ArduinoButtonboard {
  ArduinoJSONDevice m_device;

  public ArduinoButtonboard() {
    m_device = new ArduinoJSONDevice("buttonboard");
  }

}
