package buttonboard.arduino;

import buttonboard.NetworkTablesClient;

public class ArduinoDriverLights implements ArduinoJSONDevice.DeviceListener, NetworkTablesClient.TargetListener {
  private static String SENSOR_TARGET = "target";

  private ArduinoJSONDevice m_device;
  private String m_target;

  public ArduinoDriverLights(NetworkTablesClient nt) {
    m_device = new ArduinoJSONDevice("driverlights", this);
    nt.setTargetListener(this);
  }

  @Override
  public void onConnect() {
    System.out.println("--- Driver Lights Connected ---");
    m_device.setSensorField(SENSOR_TARGET, "value", m_target);
  }

  @Override
  public void onDisconnect() {
    System.out.println("--- Driver Lights Disconnected ---");
  }

  @Override
  public void onStateReceived() {
    if (m_target != null && !m_device.getSensor(SENSOR_TARGET).getStringValue().equals(m_target)) {
      if (m_device.isConnected()) {
        m_device.setSensorField(SENSOR_TARGET, "value", m_target);
      }
    }
  }

  public void targetUpdated(int row, int col, int type) {
    int section = col / 3;
    char typeChar = type == 1 ? 'A' : type == 0 ? 'O' : ' ';
    String target =
      Character.toString(section == 0 ? typeChar : ' ') +
      Character.toString(section == 1 ? typeChar : ' ') +
      Character.toString(section == 2 ? typeChar : ' ');

    if (target != m_target) {
      if (m_device.isConnected()) {
        m_device.setSensorField(SENSOR_TARGET, "value", target);
      }
      m_target = target;
    }
  }
}
