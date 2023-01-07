package com.team2357.log.outputs;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipOutputStream;

public class ZipFileLogWriter implements LogWriter {

  public static final int COMPRESSION_LEVEL = 5;
  public static final String INTERNAL_FILE_NAME = "robotlog-session.json";

  private double m_timeSecondsRoundingFactor;
  private ZipOutputStream m_zipOut;
  private OutputStreamWriter m_zipWriter;
  private Map<String, Object> m_header;
  private Map<String, Class<?>> m_subscribedTopics;
  private boolean m_needsComma = false;

  public ZipFileLogWriter(
    String dirPath,
    String fileName,
    Map<String, Object> header,
    double timeSecondsRoundingFactor
  ) {
    try {
      m_zipOut =
        ZipFileUtils.initZipFile(
          dirPath,
          fileName,
          COMPRESSION_LEVEL,
          INTERNAL_FILE_NAME
        );
      m_zipWriter = new OutputStreamWriter(m_zipOut);
      m_header = header;
      m_subscribedTopics = new HashMap<String, Class<?>>();
      m_timeSecondsRoundingFactor = timeSecondsRoundingFactor;
    } catch (Exception e) {
      System.err.println("ZipFileOutput: Failed to initialize zip file");
      e.printStackTrace();
      m_zipOut = null;
      m_zipWriter = null;
      return;
    }
  }

  @Override
  public void onStart(long relativeNanos) {
    println("{");
    println("  " + ZipFileUtils.printHeader(m_header) + ",");
    println("  \"entries\": [");
  }

  @Override
  public void onStop(long relativeNanos) {
    println("");
    println("  ],");
    printSubscribedTopics();
    println("}");
    completeFile();
  }

  @Override
  public void onSubscribe(
    String topicName,
    Class<?> valueType,
    long relativeNanos
  ) {
    m_subscribedTopics.put(topicName, valueType);
  }

  @Override
  public void onUnsubscribe(String topicName, long relativeNanos) {
    // Don't remove topics. We need to keep a list of all topics for this session.
  }

  @Override
  public void onEntry(String topicName, Object value, long relativeNanos) {
    Class<?> valueType = m_subscribedTopics.get(topicName);
    String entryStr = ZipFileUtils.printEntry(
      topicName,
      value,
      valueType,
      relativeNanos,
      m_timeSecondsRoundingFactor
    );

    if (m_needsComma) {
      println(",");
    }
    print("    " + entryStr);
    m_needsComma = true;
  }

  private void printSubscribedTopics() {
    println("  \"topics\": [");
    m_needsComma = false;

    for (Map.Entry<String, Class<?>> entry : m_subscribedTopics.entrySet()) {
      printTopic(entry.getKey(), entry.getValue());
    }

    println("");
    println("  ]");
  }

  private void printTopic(String name, Class<?> valueType) {
    if (m_needsComma) {
      println(",");
    }
    print("    " + ZipFileUtils.printTopic(name, valueType));
    m_needsComma = true;
  }

  private void println(String text) {
    print(text + '\n');
  }

  private void print(String text) {
    if (m_zipWriter == null) {
      // Don't log to stderr, it was already logged in the constructor
      return;
    }

    try {
      m_zipWriter.write(text);
      m_zipWriter.flush();
    } catch (IOException ioe) {
      System.err.println("ZipFileLogWriter.print: IO Exception");
      ioe.printStackTrace();
    }
  }

  private void completeFile() {
    try {
      ZipFileUtils.completeZipFile(m_zipOut);
    } catch (IOException e) {
      System.err.println("ZipFileLogWriter.complete: IO Exception");
      e.printStackTrace();
    }
  }
}
