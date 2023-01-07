package com.team2357.log.outputs;

import com.team2357.log.lib.RelativeTimeSource;

/**
 * Simple synchronous logging output
 */
public class SimpleLogOutput implements LogOutput {

  private RelativeTimeSource m_timeSource;
  private LogWriter m_logWriter;

  protected SimpleLogOutput(LogWriter logWriter) {
    m_logWriter = logWriter;
  }

  private long convertToRelativeNanos(long nanos) {
    if (m_timeSource == null) {
      return -1;
    }
    return m_timeSource.convertToRelativeNanos(nanos);
  }

  @Override
  public final boolean start(RelativeTimeSource timeSource, long nanos) {
    if (m_timeSource != null) {
      System.err.println("LogOutput.start: Already started.");
      return false;
    }

    m_timeSource = timeSource;
    m_logWriter.onStart(convertToRelativeNanos(nanos));
    return true;
  }

  @Override
  public final boolean stop(long nanos) {
    if (m_timeSource == null) {
      System.err.println("LogOutput.stop: Cannot stop. Not yet started");
      return false;
    }

    m_logWriter.onStop(convertToRelativeNanos(nanos));
    m_timeSource = null;
    return true;
  }

  @Override
  public final void notifySubscribe(
    String topicName,
    Class<?> valueType,
    long nanos
  ) {
    m_logWriter.onSubscribe(
      topicName,
      valueType,
      convertToRelativeNanos(nanos)
    );
  }

  @Override
  public final void notifyUnsubscribe(String topicName, long nanos) {
    m_logWriter.onUnsubscribe(topicName, convertToRelativeNanos(nanos));
  }

  @Override
  public final void writeEntry(String topicName, Object value, long nanos) {
    m_logWriter.onEntry(topicName, value, convertToRelativeNanos(nanos));
  }
}
