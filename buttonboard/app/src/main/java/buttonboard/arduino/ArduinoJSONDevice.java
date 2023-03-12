package buttonboard.arduino;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ArduinoJSONDevice {
  public static final String ERROR = "error";
  public static final String TIMEOUT_MS = "timeoutMs";
  public static final String MAX_UPDATE_HZ = "maxUpdateHz";
  public static final String SENSORS = "sensors";

  private ObjectMapper m_objectMapper;
  private String m_deviceName;
  private JsonNode m_state;

  public ArduinoJSONDevice(String deviceName) {
    m_objectMapper = new ObjectMapper();
    m_deviceName = deviceName;
    ArduinoSerialPortProvider.subscribe(deviceName, this);
  }

  public boolean isConnected() {
    return ArduinoSerialPortProvider.isDeviceConnected(m_deviceName);
  }

  public void onConnect() {
    System.out.println("device onConnect");
    ArduinoSerialPortProvider.sendQuery(m_deviceName);
  }

  public void onDisconnect() {
    System.out.println("device onDisconnect");
  }

  public void onStateReceived(JsonNode state) {
    System.out.println("device state received:" + state);
    m_state = state;
  }

  public String getError() {
    return getStringDeviceField(ERROR);
  }

  public void clearError() {
    setDeviceField(ERROR, "");
  }

  public int getTimeoutMs() {
    return getIntDeviceField(TIMEOUT_MS);
  }

  public void setTimeoutMs(int timeoutMs) {
    setDeviceField(TIMEOUT_MS, timeoutMs);
  }

  public String getStringDeviceField(String name) {
    JsonNode node = m_state.get(name);
    return node != null ? node.asText() : null;
  }

  public int getIntDeviceField(String name) {
    JsonNode node = m_state.get(name);
    return node != null ? node.asInt() : -1;
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

  public String getStringSensorField(String sensorName, String fieldName) {
    JsonNode sensorsNode = m_state.get(SENSORS);
    JsonNode sensorNode = sensorsNode != null ? sensorsNode.get(sensorName) : null;
    JsonNode fieldNode = sensorNode != null ? sensorNode.get(fieldName) : null;
    return fieldNode != null ? fieldNode.asText() : null;
  }

  public int getIntSensorField(String sensorName, String fieldName) {
    JsonNode sensorsNode = m_state.get(SENSORS);
    JsonNode sensorNode = sensorsNode != null ? sensorsNode.get(sensorName) : null;
    JsonNode fieldNode = sensorNode != null ? sensorNode.get(fieldName) : null;
    return fieldNode != null ? fieldNode.asInt() : null;
  }

  public double getFloatSensorField(String sensorName, String fieldName) {
    JsonNode sensorsNode = m_state.get(SENSORS);
    JsonNode sensorNode = sensorsNode != null ? sensorsNode.get(sensorName) : null;
    JsonNode fieldNode = sensorNode != null ? sensorNode.get(fieldName) : null;
    return fieldNode != null ? fieldNode.asDouble() : null;
  }

  public int[] getIntArraySensorField(String sensorName, String fieldName) {
    JsonNode sensorsNode = m_state.get(SENSORS);
    JsonNode sensorNode = sensorsNode != null ? sensorsNode.get(sensorName) : null;
    JsonNode fieldNode = sensorNode != null ? sensorNode.get(fieldName) : null;
    return fieldNode != null ? m_objectMapper.convertValue(fieldNode, int[].class) : null;
  }

  public float[] getFloatArraySensorField(String sensorName, String fieldName) {
    JsonNode sensorsNode = m_state.get(SENSORS);
    JsonNode sensorNode = sensorsNode != null ? sensorsNode.get(sensorName) : null;
    JsonNode fieldNode = sensorNode != null ? sensorNode.get(fieldName) : null;
    return fieldNode != null ? m_objectMapper.convertValue(fieldNode, float[].class) : null;
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
