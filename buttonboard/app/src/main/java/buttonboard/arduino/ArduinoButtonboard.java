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
  }

  @Override
  public void onDisconnect() {
    System.out.println("--- Buttonboard Disconnected ---");
  }

  @Override
  public void onStateReceived() {
    updateAlliance(m_device.getSensor(SENSOR_ALLIANCE).getStringValue());
    updateTarget(m_device.getSensor(SENSOR_TARGET).getIntArray());
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

    if (m_targetRow != targetRow) {
      m_targetRow = targetRow;
      System.out.println("targetRow set: '" + m_targetRow + "'");
    }

    if (m_targetCol != targetCol) {
      m_targetCol = targetCol;
      System.out.println("targetCol set: '" + m_targetCol + "'");
    }

    if (m_targetType != targetType) {
      m_targetType = targetType;
      System.out.println("targetType set: '" + m_targetType + "'");
    }
  }

  @Override
  public void gridUpdated(String high, String mid, String low) {
    String gridString = high + mid + low;
    if (gridString != m_gridString) {
      if (m_device.isConnected()) {
        m_device.setSensorField(SENSOR_GRID, "value", gridString);
        m_gridString = gridString;
      }
    }
  }
}
