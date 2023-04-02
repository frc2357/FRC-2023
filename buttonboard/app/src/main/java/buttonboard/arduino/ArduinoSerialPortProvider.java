package buttonboard.arduino;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class ArduinoSerialPortProvider implements ArduinoSerialPort.Listener, Runnable {
  private static final String HEADER = "||v1||";
  public static final int SLEEP_MS = 500;
  public static final int TIMEOUT_MS = 60 * 1000; // Set timeout to 1 minute
  public static final String MSG_HEADER = "||v1||";
  public static final String MSG_QUERY_DEVICE = MSG_HEADER + "{}";
  public static final String NAME_UNNAMED = "(unnamed)";
  public static final String NAME = "name";

  private static ArduinoSerialPortProvider s_instance;

  private static ArduinoSerialPortProvider getInstance() {
    if (s_instance == null) {
      s_instance = new ArduinoSerialPortProvider();
    }
    return s_instance;
  }

  public static void subscribe(String deviceName, ArduinoJSONDevice device) {
    if (getInstance().m_devicesByName.get(deviceName) != null) {
      System.err.println("Another device instance is already subscribed to '" + deviceName + "'");
      return;
    }
    getInstance().m_devicesByName.put(deviceName, device);
  }

  public static boolean isDeviceConnected(String deviceName) {
    return getInstance().m_deviceNamesByPort.containsValue(deviceName);
  }

  public static void write(String deviceName, JsonNode state) {
    ArduinoSerialPort port = getInstance().m_deviceNamesByPort.inverse().get(deviceName);
    String message = HEADER + state.toString();
    getInstance().write(port, message);
  }

  public static void sendQuery(String deviceName) {
    ArduinoSerialPort port = getInstance().m_deviceNamesByPort.inverse().get(deviceName);
    getInstance().write(port, MSG_QUERY_DEVICE);
  }

  private BiMap<ArduinoSerialPort, String> m_deviceNamesByPort;
  private BiMap<String, ArduinoJSONDevice> m_devicesByName;
  private ObjectMapper m_objectMapper;
  private Thread m_thread;

  public ArduinoSerialPortProvider() {
    m_deviceNamesByPort = HashBiMap.create();
    m_devicesByName = HashBiMap.create();
    m_objectMapper = new ObjectMapper();
    m_thread = new Thread(this);
    m_thread.start();
  }

  public void run() {
    try {
      while (true) {
        ArduinoSerialPort.scan(this);
        Thread.sleep(SLEEP_MS);
      }
    } catch (Exception e) {
      System.err.println("SerialPorthandler exception: " + e.getMessage());
      e.printStackTrace(System.err);
    }
  }

  @Override
  public void onMessage(ArduinoSerialPort port, String message) {
    try {
      // Remove any '\0' bytes
      // Add extra spaces after comments because this JSON library is picky
      // TODO: Change Arduino code to add spaces
      StringBuffer filteredMessage = new StringBuffer(message.length());
      for (int i = 0; i < message.length(); i++) {
        if (message.charAt(i) < ' ') {
          continue;
        } else if (message.charAt(i) == ',') {
          filteredMessage.append(", ");
        } else {
          filteredMessage.append(message.charAt(i));
        }
      }
      message = filteredMessage.toString();

      // Strip off header
      if (!message.startsWith(MSG_HEADER)) {
        System.err.println("Invalid message: '" + message + "'");
        return;
      }
      message = message.substring(MSG_HEADER.length()).strip();

      JsonNode state = m_objectMapper.readTree(message);
      String deviceName = m_deviceNamesByPort.get(port);

      if (deviceName == null) {
        JsonNode nameNode = state.get(NAME);
        deviceName = nameNode != null ? nameNode.asText() : NAME_UNNAMED;
      }

      ArduinoJSONDevice device = m_devicesByName.get(deviceName);

      if (!m_deviceNamesByPort.containsValue(deviceName)) {
        m_deviceNamesByPort.put(port, deviceName);

        if (device != null) {
          device.onConnect();
        }
      }
        
      if (device != null) {
        device.onStateReceived(state);
      }
    } catch (Exception e) {
      System.err.println("JSON Parse Exception: " + e.getMessage());
      e.printStackTrace(System.err);
    }
  }

  @Override
  public void onConnect(ArduinoSerialPort port) {
    writeTimeout(port);
  }

  @Override
  public void onDisconnect(ArduinoSerialPort port) {
    String deviceName = m_deviceNamesByPort.get(port);
    ArduinoJSONDevice device = m_devicesByName.get(deviceName);
    if (device != null) {
      device.onDisconnect();
    }
    m_deviceNamesByPort.remove(port);
  }

  private void writeTimeout(ArduinoSerialPort port) {
    write(port, MSG_HEADER + "{\"timeoutMs\":" + TIMEOUT_MS + "}");
  }

  private void write(ArduinoSerialPort port, String message) {
    port.write(message);
  }
}
