package com.team2357.log.outputs;

import java.io.PrintStream;

class PrintStreamLogWriter implements LogWriter {

  public static int DEFAULT_DECIMAL_PLACES = 2;

  private final String m_prefix;
  private final PrintStream m_stream;
  private final int m_decimalPlaces;

  public PrintStreamLogWriter(final String prefix, final PrintStream stream) {
    this(prefix, stream, DEFAULT_DECIMAL_PLACES);
  }

  public PrintStreamLogWriter(
    final String prefix,
    final PrintStream stream,
    final int decimalPlaces
  ) {
    m_prefix = prefix;
    m_stream = stream;
    m_decimalPlaces = decimalPlaces;
  }

  protected String nanosToTime(long relativeNanos) {
    if (relativeNanos < 0) {
      relativeNanos = 0;
    }

    long relativeMillis = relativeNanos / 1000000L;
    long minutes = (relativeMillis / 1000) / 60;
    long seconds = (relativeMillis / 1000) % 60;
    long millis = (relativeMillis % 1000);

    return String.format("%02d:%02d.%03d", minutes, seconds, millis);
  }

  @Override
  public void onStart(long relativeNanos) {
    m_stream.println(
      m_prefix + ": " + nanosToTime(relativeNanos) + " ( Session Start )"
    );
  }

  @Override
  public void onStop(long relativeNanos) {
    m_stream.println(
      m_prefix + ": " + nanosToTime(relativeNanos) + " ( Session Stop )"
    );
  }

  @Override
  public void onSubscribe(
    String topicName,
    Class<?> valueType,
    long relativeNanos
  ) {
    m_stream.println(
      m_prefix +
      ": " +
      nanosToTime(relativeNanos) +
      " [" +
      topicName +
      "]( Subscribed )"
    );
  }

  @Override
  public void onUnsubscribe(String topicName, long relativeNanos) {
    m_stream.println(
      m_prefix +
      ": " +
      nanosToTime(relativeNanos) +
      " [" +
      topicName +
      "]( Unsubscribed )"
    );
  }

  @Override
  public void onEntry(String topicName, Object value, long relativeNanos) {
    String stringValue;
    if (value instanceof Double) {
      stringValue = String.format("%." + m_decimalPlaces + "f", value);
    } else {
      stringValue = value.toString();
    }

    m_stream.println(
      m_prefix +
      ": " +
      nanosToTime(relativeNanos) +
      " [" +
      topicName +
      "]: " +
      stringValue
    );
  }
}
