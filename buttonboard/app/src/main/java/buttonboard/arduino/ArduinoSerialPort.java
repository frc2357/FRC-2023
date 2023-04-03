package buttonboard.arduino;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.fazecast.jSerialComm.SerialPort;

public class ArduinoSerialPort implements Runnable {
  public static interface Listener {
    public void onMessage(ArduinoSerialPort port, String message);
    public void onConnect(ArduinoSerialPort port);
    public void onDisconnect(ArduinoSerialPort port);
  }

  private static int SLEEP_MS = 10;
  private static int RECONNECT_SLEEP_MS = 1000;
  private static int BAUD_RATE = 115200;
  private static int READ_SLEEP = 20;
  private static int BUFFER_SIZE = 1024;


  private static List<String> SUPPORTED_PORT_NAMES = Arrays.asList(
    "PicoArduino (Dial-In)",
    "XIAO RP2040 (Dial-In)",
    "Feather RP2040 SCORPIO (Dial-In)",
    "USB Serial Device"
  );

  private static Map<String, ArduinoSerialPort> m_serialPorts = new HashMap<String, ArduinoSerialPort>();

  public static boolean isValidDevice(String descriptivePortName) {
    Iterator<String> itr = SUPPORTED_PORT_NAMES.iterator();
    while (itr.hasNext()) {
      String supportedName = itr.next();
      // If the serial port comes in with extra text, like (COM3) at the end, it's ok.
      if (descriptivePortName.startsWith(supportedName)) {
        return true;
      }
    }
    return false;
  }

  public static void scan(Listener listener) {
    SerialPort[] serialPorts = SerialPort.getCommPorts();
    for (SerialPort port : serialPorts) {
      if (!m_serialPorts.containsKey(port.getSystemPortPath())) {
        if (isValidDevice(port.getDescriptivePortName())) {
          System.out.println(
            "Found compatible device '" +
            port.getDescriptivePortName() +
            "' on serial port " +
            port.getSystemPortPath()
          );
          ArduinoSerialPort p = new ArduinoSerialPort(port);
          m_serialPorts.put(port.getSystemPortPath(), p);
          p.setListener((listener));
          p.start();
        } else {
          System.out.println(
            "Found non-compatible device '" +
            port.getDescriptivePortName() +
            "' on serial port " +
            port.getSystemPortPath()
          );
          m_serialPorts.put(port.getSystemPortPath(), null);
        }
      }
    }
  }

  private Listener m_listener;
  private Thread m_thread;
  private SerialPort m_port;
  private byte[] m_readBuffer;
  private StringBuffer m_stringBuffer;

  private ArduinoSerialPort(SerialPort port) {
    m_thread = null;
    m_readBuffer = new byte[BUFFER_SIZE];
    m_stringBuffer = new StringBuffer();
    m_port = port;
  }

  public void setListener(Listener listener) {
    m_listener = listener;
  }

  public String getPortPath() {
    return m_port.getSystemPortPath();
  }

  public boolean isRunning() {
    return m_thread != null;
  }

  private void start() {
    if (isRunning()) {
      System.err.println("already running");
      stop();
    }

    m_stringBuffer.delete(0, m_stringBuffer.length());

    boolean success = m_port.openPort();
    if (!success) {
      System.err.println("Failed to open port: " + m_port.getSystemPortPath());
      stop();
      return;
    }
    m_port.setComPortParameters(BAUD_RATE, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);

    m_thread = new Thread(this);
    m_thread.setPriority(Thread.currentThread().getPriority() - 1);
    m_thread.start();

    if (m_listener != null) {
      m_listener.onConnect(this);
    }
  }

  private void stop() {
    m_stringBuffer.delete(0, m_stringBuffer.length());
    m_thread = null;
    m_port.closePort();
    m_serialPorts.remove(getPortPath());
  }

  @Override
  public void run() {
    while (m_thread != null) {
      try {
        while (m_port.bytesAvailable() == 0) {
          Thread.sleep(READ_SLEEP);
        }

        if (m_port.bytesAvailable() == -1) {
          stop();
          if (m_listener != null) {
            m_listener.onDisconnect(this);
          }
          return;
        }

        int bytesRead = m_port.readBytes(m_readBuffer, m_port.bytesAvailable());

        addBytesToBuffer(m_readBuffer, m_stringBuffer, bytesRead);
        int newlineIndex = m_stringBuffer.indexOf("\n");

        if (newlineIndex >= 0) {
          String message = m_stringBuffer.substring(0, newlineIndex);
          System.out.println("read: " + message);
          m_stringBuffer.delete(0, newlineIndex + 1);
          if (m_listener != null) {
            m_listener.onMessage(this, message);
          }
        }

      } catch (Exception e) {
        System.err.println("Exception while reading from " + m_port.getSystemPortPath());
        e.printStackTrace(System.err);
      }
    }
  }

  private void addBytesToBuffer(byte[] bytes, StringBuffer buffer, int bytesRead) {
    String s = new String(bytes, 0, bytesRead);

    for (int i = 0; i < s.length(); i++) {
      char ch = s.charAt(i);
      if (ch != 0) {
        buffer.append(ch);
      }
    }
  }

  public boolean write(String message) {
    if (!isRunning()) {
      System.err.println("Cannot write message while port is not running!");
      return false;
    }

    byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
    int bytesRemaining = bytes.length;

    while (bytesRemaining > 0) {
      int offset = bytes.length - bytesRemaining;
      int bytesWritten = m_port.writeBytes(bytes, bytesRemaining, offset);
      System.out.println("write: " + message);

      if (bytesWritten == -1) {
        System.err.println("Write to serial port failed.");
        stop();
        return false;
      }

      bytesRemaining -= bytesWritten;
    }
    return true;
  }
}
