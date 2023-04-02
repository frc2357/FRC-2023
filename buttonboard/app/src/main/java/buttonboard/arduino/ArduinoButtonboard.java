package buttonboard.arduino;

import buttonboard.NetworkTablesClient;
import buttonboard.NetworkTablesClient.GridListener;

public class ArduinoButtonboard implements ArduinoJSONDevice.DeviceListener, NetworkTablesClient.GridListener {
  private static String SENSOR_ALLIANCE = "alliance";
  private static String SENSOR_GRID = "grid";
  private static String SENSOR_TARGET = "target";

  private ArduinoJSONDevice m_device;
  private String m_alliance;
  private int m_targetRow;
  private int m_targetCol;
  private int m_targetType;
  private String m_gridString;
  private NetworkTablesClient m_ntClient;

  public ArduinoButtonboard(NetworkTablesClient nt) {
    m_device = new ArduinoJSONDevice("buttonboard", this);
    m_alliance = "unset";

    m_ntClient = nt;
    m_ntClient.setGridListener(this);
  }

  @Override
  public void onConnect() {
    System.out.println("--- Buttonboard Connected ---");
    m_device.setSensorField(SENSOR_GRID, "value", m_gridString);
  }

  @Override
  public void onDisconnect() {
    System.out.println("--- Buttonboard Disconnected ---");
  }

  @Override
  public void onStateReceived() {
    updateAlliance(m_device.getSensor(SENSOR_ALLIANCE).getStringValue());
    updateTarget(m_device.getSensor(SENSOR_TARGET).getIntArray());

    if (!m_device.getSensor(SENSOR_GRID).getStringValue().equals(m_gridString)) {
      if (m_device.isConnected()) {
        m_device.setSensorField(SENSOR_GRID, "value", m_gridString);
      }
    }
  }

  private void updateAlliance(String alliance) {
    if (m_alliance != alliance) {
      m_alliance = alliance;
      m_ntClient.setAlliance(alliance);
      System.out.println("alliance set: '" + m_alliance + "'");
    }
  }

  private void updateTarget(int[] target) {
    int targetRow = target[0];
    int targetCol = target[1];
    int targetType = target[2];

    if (m_targetRow != targetRow || m_targetCol != targetCol || m_targetType != targetType) {
      m_targetRow = targetRow;
      m_targetCol = targetCol;
      m_targetType = targetType;
      m_ntClient.setGridTarget(m_targetRow, m_targetCol, m_targetType);
      System.out.println("target row/col/type set: " + m_targetRow + ", " + m_targetCol + ", " + m_targetType);
    }
  }

  @Override
  public void gridUpdated(String high, String mid, String low) {
    System.out.println("GridUpdated: " + high + "/" + mid + "/" + low);
    String gridString = high + mid + low;
    if (gridString != m_gridString) {
      if (m_device.isConnected()) {
        m_device.setSensorField(SENSOR_GRID, "value", gridString);
      }
      m_gridString = gridString;
    }
  }
}
