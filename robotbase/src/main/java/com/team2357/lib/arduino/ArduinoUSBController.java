// package com.team2357.lib.arduino;

// import java.io.IOException;
// import java.util.Map;

// import com.fasterxml.jackson.core.JsonParseException;
// import com.fasterxml.jackson.databind.JsonNode;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.fasterxml.jackson.databind.node.ObjectNode;

// import edu.wpi.first.wpilibj.SerialPort;

// /**
//  * Controls an Arduino over USB via state.
//  *
//  * The Arduino code operates on a JSON state object. The Arduino is the owner of
//  * its own state, but will receive updates to state in the form of partial JSON
//  * objects.
//  *
//  * e.g. { name: 'Intake Sensors', devices: { { name: 'laserToF', distance_mm:
//  * 50.5 }, { name: 'limitForward', active: false }, { name: 'limitReferse',
//  * active: false }, } }
//  */
// public class ArduinoUSBController implements Runnable {

//   private static String NAME = "name";
//   private static String ERROR = "error";
//   private static String NAME_DISCONNECTED = "(disconnected)";
//   private static String NAME_UNNAMED = "(unnamed)";
//   private static int READ_TIMEOUT = 250;

//   private Thread m_thread;
//   private JsonNode m_state;
//   private SerialPort m_serialPort;
//   private byte[] m_byteBuffer = new byte[1024];
//   private StringBuffer m_stringBuffer = new StringBuffer();
//   private ObjectMapper m_objectMapper = new ObjectMapper();

//   /**
//    * Creates a new ArduinoUSBController for a serial port
//    *
//    * @param ttyDevice The serial device to be used (e.g. "/dev/ttyACM0")
//    */
//   public ArduinoUSBController(String ttyDevice) {
//     try {
//       m_serialPort = SerialPort.getCommPort(ttyDevice);
//       m_serialPort.setComPortParameters(
//         115200,
//         8,
//         SerialPort.ONE_STOP_BIT,
//         SerialPort.NO_PARITY
//       );
//       m_serialPort.setComPortTimeouts(
//         SerialPort.TIMEOUT_READ_SEMI_BLOCKING,
//         READ_TIMEOUT,
//         0
//       );
//     } catch (Exception e) {
//       m_serialPort = null;
//       System.err.println(e.getMessage());
//     }
//   }

//   /**
//    * Checks if the Arduino is connected
//    *
//    * @return True if we've received state from the Arduino, false if not.
//    */
//   public boolean isConnected() {
//     return m_state != null;
//   }

//   /**
//    * Gets the "name" field from the Arduino state.
//    *
//    * @return The name given, "(disconnected)" if not yet connected, or "(unnamed)"
//    *         if the arduino state contains no name.
//    */
//   public String getName() {
//     if (!isConnected()) {
//       return NAME_DISCONNECTED;
//     }
//     JsonNode nameNode = m_state.get(NAME);
//     return nameNode != null ? nameNode.asText() : NAME_UNNAMED;
//   }

//   /**
//    * Gets the current error returned from the Arduino state.
//    *
//    * @return A string containing the last error message from the Arduino, or null
//    *         if none.
//    */
//   public String getError() {
//     JsonNode errorNode = m_state.get(ERROR);
//     return errorNode != null ? errorNode.asText() : null;
//   }

//   /**
//    * Checks if the Arduino state contains a given device.
//    *
//    * This checks if the state's top-level devices object contains an object with a
//    * name that matches the name given to this method.
//    *
//    * e.g. { devices: [ { name: 'myDevice' }, ] }
//    *
//    * @param deviceName The name of the device to check.
//    * @return true if the Arduino state contains the given device by name.
//    */
//   public boolean hasDevice(String deviceName) {
//     return isConnected() && getDevice(deviceName) != null;
//   }

//   /**
//    * Checks if a device is available and has a given field.
//    *
//    * @param deviceName The device upon which to read the field.
//    * @param fieldName  The name of the field to get.
//    * @return true if the device exists and has the given field, false otherwise.
//    */
//   public boolean hasDeviceField(String deviceName, String fieldName) {
//     if (!hasDevice(deviceName)) {
//       return false;
//     }
//     JsonNode field = getDeviceField(deviceName, fieldName);
//     return field != null;
//   }

//   /**
//    * Gets a string value from a given device.
//    *
//    * @param deviceName The device upon which to read the field.
//    * @param fieldName  The name of the field to get.
//    * @return The current value of the field, or null if not available.
//    */
//   public String getDeviceFieldString(String deviceName, String fieldName) {
//     JsonNode field = getDeviceField(deviceName, fieldName);
//     return field != null ? field.asText() : null;
//   }

//   /**
//    * Gets an int value from a given device.
//    *
//    * @param deviceName The device upon which to read the field.
//    * @param fieldName  The name of the field to get.
//    * @return The current value of the field, or Integer.MIN_VALUE if not
//    *         available.
//    */
//   public int getDeviceFieldInt(String deviceName, String fieldName) {
//     JsonNode field = getDeviceField(deviceName, fieldName);
//     return field != null ? field.asInt() : Integer.MIN_VALUE;
//   }

//   /**
//    * Gets an double value from a given device.
//    *
//    * @param deviceName The device upon which to read the field.
//    * @param fieldName  The name of the field to get.
//    * @return The current value of the field, or Double.NaN if not available.
//    */
//   public double getDeviceFieldDouble(String deviceName, String fieldName) {
//     JsonNode field = getDeviceField(deviceName, fieldName);
//     return field != null ? field.asDouble() : Double.NaN;
//   }

//   /**
//    * Gets an boolean value from a given device.
//    *
//    * @param deviceName The device upon which to read the field.
//    * @param fieldName  The name of the field to get.
//    * @return The current value of the field, or false if not available.
//    */
//   public boolean getDeviceFieldBoolean(String deviceName, String fieldName) {
//     JsonNode field = getDeviceField(deviceName, fieldName);
//     return field != null ? field.asBoolean() : false;
//   }

//   /**
//    * Sends a request to the Arduino to set a given field.
//    *
//    * @param deviceName The device upon which to set the field.
//    * @param fieldName  The name of the field to set.
//    * @param fieldValue The desired value of the field.
//    */
//   public void setDeviceField(
//     String deviceName,
//     String fieldName,
//     String fieldValue
//   ) {
//     ObjectNode root = m_objectMapper.createObjectNode();
//     root.with("devices").with(deviceName).put(fieldName, fieldValue);
//     write(root.toString());
//   }

//   /**
//    * Sends a request to the Arduino to set a given field.
//    *
//    * @param deviceName The device upon which to set the field.
//    * @param fieldName  The name of the field to set.
//    * @param fieldValue The desired value of the field.
//    */
//   public void setDeviceField(
//     String deviceName,
//     String fieldName,
//     int fieldValue
//   ) {
//     ObjectNode root = m_objectMapper.createObjectNode();
//     root.with("devices").with(deviceName).put(fieldName, fieldValue);
//     write(root.toString());
//   }

//   /**
//    * Sends a request to the Arduino to set a given field.
//    *
//    * @param deviceName The device upon which to set the fields.
//    * @param fieldName  The name of the field to set, should always be a string.
//    * @param fieldValue The desired value of the field.
//    */
//   public void setDeviceField(String deviceName, Map<String, Object> fields) {
//     ObjectNode root = m_objectMapper.createObjectNode();

//     for (String fieldName : fields.keySet()) {
//       if (Integer.class.isInstance(fields.get(fieldName))) {
//         int value = (int) fields.get(fieldName);
//         root.with("devices").with(deviceName).put(fieldName, value);
//       } else if (Double.class.isInstance(fields.get(fieldName))) {
//         double value = (double) fields.get(fieldName);
//         root.with("devices").with(deviceName).put(fieldName, value);
//       } else if (Boolean.class.isInstance(fields.get(fieldName))) {
//         boolean value = (boolean) fields.get(fieldName);
//         root.with("devices").with(deviceName).put(fieldName, value);
//       } else if (String.class.isInstance(fields.get(fieldName))) {
//         String value = (String) fields.get(fieldName);
//         root.with("devices").with(deviceName).put(fieldName, value);
//       }
//     }
//     write(root.toString());
//   }

//   /**
//    * Sends a request to the Arduino to set a given field.
//    *
//    * @param deviceName The device upon which to set the field.
//    * @param fieldName  The name of the field to set.
//    * @param fieldValue The desired value of the field.
//    */
//   public void setDeviceField(
//     String deviceName,
//     String fieldName,
//     double fieldValue
//   ) {
//     ObjectNode root = m_objectMapper.createObjectNode();
//     root.with("devices").with(deviceName).put(fieldName, fieldValue);
//     write(root.toString());
//   }

//   /**
//    * Sends a request to the Arduino to set a given field.
//    *
//    * @param deviceName The device upon which to set the field.
//    * @param fieldName  The name of the field to set.
//    * @param fieldValue The desired value of the field.
//    */
//   public void setDeviceField(
//     String deviceName,
//     String fieldName,
//     boolean fieldValue
//   ) {
//     ObjectNode root = m_objectMapper.createObjectNode();
//     root.with("devices").with(deviceName).put(fieldName, fieldValue);
//     write(root.toString());
//   }

//   /**
//    * Starts to read data from the Arduino.
//    *
//    * This starts an internal thread that blocks on data from the Arduino, which
//    * then processes complete JSON state objects as they are received.
//    */
//   public void start() {
//     if (m_thread != null) {
//       stop();
//     }

//     // TODO: Use logging for this.
//     System.out.println(
//       "Opening serial port '" + m_serialPort.getSystemPortName() + "'"
//     );
//     boolean success = m_serialPort.openPort();

//     if (!success) {
//       // TODO: Use logging for this.
//       System.err.println(
//         "Error opening serial port '" + m_serialPort.getSystemPortName() + "'"
//       );
//       return;
//     }

//     // It all checks out, start the thread for blocking reads.
//     String threadName = "ArduinoUSB[" + m_serialPort.getSystemPortName() + "]";
//     m_thread = new Thread(this, threadName);
//     m_thread.start();
//   }

//   /**
//    * Stops reading from the Arduino.
//    *
//    * This stops the internal thread from reading data from the Arduino.
//    */
//   public void stop() {
//     m_thread = null;
//     m_state = null;
//   }

//   /**
//    * The run loop for the internal thread of this controller.
//    *
//    * This is not meant to be called directly. It's an implementation of the
//    * Runnable interface.
//    */
//   @Override
//   public void run() {
//     while (m_thread != null) {
//       try {
//         read();
//       } catch (IOException ioe) {
//         // TODO: Use logging for this.
//         ioe.printStackTrace();
//       }
//     }
//   }

//   protected void read() throws IOException {
//     int byteCount = m_serialPort.readBytes(m_byteBuffer, 1024);
//     if (byteCount == 0) {
//       return;
//     }

//     String newChars = new String(m_byteBuffer, 0, byteCount);
//     if (newChars != null) {
//       m_stringBuffer.append(newChars);
//       int lineBreakIndex = m_stringBuffer.indexOf("\n");
//       if (lineBreakIndex >= 0) {
//         String line = m_stringBuffer.substring(0, lineBreakIndex);
//         // TODO: Use logging for this.
//         System.out.println("line: '" + line + "'");
//         m_stringBuffer.delete(0, lineBreakIndex + 1);
//         try {
//           m_state = m_objectMapper.readTree(line);
//         } catch (JsonParseException jpe) {
//           // TODO: Use logging for this.
//           System.out.println("JSON Parse Exception: " + jpe.getMessage());
//           m_state = null;
//         }
//       }
//     }
//   }

//   public void write(String message) {
//     // TODO: Use logging for this.
//     System.out.println("write: '" + message + "'");
//     byte[] bytes = message.getBytes();
//     int bytesWritten = m_serialPort.writeBytes(bytes, bytes.length);
//     if (bytesWritten == -1) {
//       // TODO: Use logging for this.
//       System.err.println("Failed to write bytes");
//     } else if (bytesWritten != bytes.length) {
//       // TODO: Use logging for this.
//       System.err.println(
//         "Incomplete write (" +
//         bytesWritten +
//         " of " +
//         bytes.length +
//         " total bytes)"
//       );
//     }
//   }

//   protected JsonNode getDeviceField(String deviceName, String fieldName) {
//     JsonNode device = getDevice(deviceName);
//     if (device == null) {
//       // TODO: Use logging for this.
//       System.err.println(
//         "device '" +
//         deviceName +
//         "' not found for Arduino " +
//         getName() +
//         " on " +
//         m_serialPort.getSystemPortName()
//       );
//       return null;
//     }
//     JsonNode field = device.get(fieldName);
//     if (field == null) {
//       // TODO: Use logging for this.
//       System.err.println(
//         "devices '" +
//         deviceName +
//         "' field '" +
//         fieldName +
//         "' not found for Arduino " +
//         getName() +
//         " on " +
//         m_serialPort.getSystemPortName()
//       );
//       return null;
//     }
//     return field;
//   }

//   protected JsonNode getDevice(String deviceName) {
//     if (m_state == null) {
//       // TODO: Use logging for this.
//       System.err.println(
//         "state not yet available for Arduino on " +
//         m_serialPort.getSystemPortName()
//       );
//       return null;
//     }
//     JsonNode devices = m_state.get("devices");
//     if (devices == null) {
//       // TODO: Use logging for this.
//       System.err.println(
//         "devices not found for Arduino " +
//         getName() +
//         " on " +
//         m_serialPort.getSystemPortName()
//       );
//       return null;
//     }
//     return devices.get(deviceName);
//   }
// }
