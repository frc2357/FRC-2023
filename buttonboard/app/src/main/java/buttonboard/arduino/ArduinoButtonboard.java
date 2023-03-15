package buttonboard.arduino;

public class ArduinoButtonboard implements ArduinoJSONDevice.DeviceListener {
  private ArduinoJSONDevice m_device;
  private String m_alliance;
  private int m_targetRow;
  private int m_targetCol;
  private int m_targetType;

  public ArduinoButtonboard() {
    m_device = new ArduinoJSONDevice("buttonboard", this);
  }

  @Override
  public void onConnect() {
    System.out.println("--- Buttonboard Connected ---");
  }

  @Override
  public void onDisconnect() {
    System.out.println("--- Buttonboard Disconnected ---");
  }

  @Override
  public void onStateReceived() {
    System.out.println("buttonboard state received");
    
    System.out.println("alliance='" + m_alliance + "'");
    System.out.println("target row='" + m_targetRow + "'");
    System.out.println("target col='" + m_targetCol + "'");
    System.out.println("target type='" + m_targetType + "'");
  }

}
