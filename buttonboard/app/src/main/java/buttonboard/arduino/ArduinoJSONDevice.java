package buttonboard.arduino;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ArduinoJSONDevice {
  private static final String FIELD_VALUE = "value";
  private static final String FIELD_ARRAY = "array";

  public static interface DeviceListener {
    public void onConnect();
    public void onDisconnect();
    public void onStateReceived();
  }

  public class Sensor {

    private String m_name;
    private JsonNode m_valueNode;

    private Sensor(String name, JsonNode valueNode) {
      m_name = name;
      m_valueNode = valueNode;
    }

    public void updateValueNode(JsonNode valueNode) {
      m_valueNode = valueNode;
    }

    public String getName() {
      return m_name;
    }

    public int getIntValue() {
      return m_valueNode.asInt();
    }

    public double getFloatValue() {
      return m_valueNode.asDouble();
    }

    public String getStringValue() {
      return m_valueNode.asText();
    }

    public int[] getIntArray() {
      return m_objectMapper.convertValue(m_valueNode, int[].class);
    }

    public double[] getFloatArray() {
      return m_objectMapper.convertValue(m_valueNode, double[].class);
    }
  }

  public static final String ERROR = "error";
  public static final String TIMEOUT_MS = "timeoutMs";
  public static final String MAX_UPDATE_HZ = "maxUpdateHz";
  public static final String SENSORS = "sensors";

  private ObjectMapper m_objectMapper;
  private String m_deviceName;
  private String m_error;
  private int m_timeoutMs;
  private Map<String, Sensor> m_sensors;
  private DeviceListener m_deviceListener;

  public ArduinoJSONDevice(String deviceName, DeviceListener listener) {
    m_error = "";
    m_timeoutMs = -1;
    m_sensors = new HashMap<String, Sensor>();
    m_objectMapper = new ObjectMapper();
    m_deviceName = deviceName;
    m_deviceListener = listener;
    ArduinoSerialPortProvider.subscribe(deviceName, this);
  }

  public boolean isConnected() {
    return ArduinoSerialPortProvider.isDeviceConnected(m_deviceName);
  }

  public void onConnect() {
    ArduinoSerialPortProvider.sendQuery(m_deviceName);
    if (m_deviceListener != null) {
      m_deviceListener.onConnect();
    }
  }

  public void onDisconnect() {
    if (m_deviceListener != null) {
      m_deviceListener.onDisconnect();
    }
  }

  public String getError() {
    return m_error;
  }

  public int getTimeoutMs() {
    return m_timeoutMs;
  }

  public boolean hasSensor(String sensorName) {
    return m_sensors.containsKey(sensorName);
  }

  public Sensor getSensor(String sensorName) {
    return m_sensors.get(sensorName);
  }

  public void onStateReceived(JsonNode state) {
    Iterator<String> fieldNameIter = state.fieldNames();
    while (fieldNameIter.hasNext()) {
      String fieldName = fieldNameIter.next();
      switch (fieldName) {
        case ERROR:
          m_error = state.get(ERROR).asText();
          break;
        case TIMEOUT_MS:
          m_timeoutMs = state.get(TIMEOUT_MS).asInt();
          break;
        case SENSORS:
          updateSensors(state.get(SENSORS));
          break;
      }
    }

    if (m_deviceListener != null) {
      m_deviceListener.onStateReceived();
    }
  }

  private void updateSensors(JsonNode sensors) {
    Iterator<String> sensorNameIter = sensors.fieldNames();
    while (sensorNameIter.hasNext()) {
      String sensorName = sensorNameIter.next();
      JsonNode sensorNode = sensors.get(sensorName);
      JsonNode valueNode = sensorNode.has(FIELD_VALUE) ? sensorNode.get(FIELD_VALUE) : sensorNode.get(FIELD_ARRAY);
      if (!m_sensors.containsKey(sensorName)) {
        m_sensors.put(sensorName, new Sensor(sensorName, valueNode));
      } else {
        Sensor sensor = m_sensors.get(sensorName);
        sensor.updateValueNode(valueNode);
      }
    }
  }

  public void clearError() {
    setDeviceField(ERROR, "");
  }

  public void setTimeoutMs(int timeoutMs) {
    setDeviceField(TIMEOUT_MS, timeoutMs);
  }

  public void setDeviceField(String name, String value) {
    ObjectNode state = m_objectMapper.createObjectNode();
    state.put(name, value);
    ArduinoSerialPortProvider.write(m_deviceName, state);
  }

  public void setDeviceField(String name, int value) {
    ObjectNode state = m_objectMapper.createObjectNode();
    state.put(name, value);
    ArduinoSerialPortProvider.write(m_deviceName, state);
  }

  public void setSensorField(String sensorName, String fieldName, String value) {
    ObjectNode state = m_objectMapper.createObjectNode();
    state.with(SENSORS).with(sensorName).put(fieldName, value);
    ArduinoSerialPortProvider.write(m_deviceName, state);
  }

  public void setSensorField(String sensorName, String fieldName, int value) {
    ObjectNode state = m_objectMapper.createObjectNode();
    state.with(SENSORS).with(sensorName).put(fieldName, value);
    ArduinoSerialPortProvider.write(m_deviceName, state);
  }

  public void setSensorField(String sensorName, String fieldName, float value) {
    ObjectNode state = m_objectMapper.createObjectNode();
    state.with(SENSORS).with(sensorName).put(fieldName, value);
    ArduinoSerialPortProvider.write(m_deviceName, state);
  }

  public void setSensorField(String sensorName, String fieldName, int[] value) {
    ObjectNode state = m_objectMapper.createObjectNode();
    ArrayNode array = m_objectMapper.valueToTree(value);
    state.with(SENSORS).with(sensorName).putArray(fieldName).addAll(array);
    ArduinoSerialPortProvider.write(m_deviceName, state);
  }

  public void setSensorField(String sensorName, String fieldName, float[] value) {
    ObjectNode state = m_objectMapper.createObjectNode();
    ArrayNode array = m_objectMapper.valueToTree(value);
    state.with(SENSORS).with(sensorName).putArray(fieldName).addAll(array);
    ArduinoSerialPortProvider.write(m_deviceName, state);
  }
}
