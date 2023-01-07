package com.team2357.log;

import com.team2357.log.outputs.LogOutput;
import com.team2357.log.topics.LogTopic;
import com.team2357.log.topics.LogTopicRegistry;
import java.util.Map;

/**
 * Represents a time-based session of logging. Each session is pre-configured
 * and logs to its outputs after it's started until it is stoppped.
 */
public class LogSession {

  protected final LogTopicRegistry m_topicRegistry;
  protected final Map<String, LogOutput> m_logOutputs;
  private long m_startTimeNanos = Long.MIN_VALUE;
  private long m_stopTimeNanos = Long.MIN_VALUE;

  public LogSession(final Map<String, LogOutput> logOutputs) {
    this(logOutputs, LogTopicRegistry.getInstance(), System.nanoTime());
  }

  public LogSession(
    final Map<String, LogOutput> logOutputs,
    final LogTopicRegistry topicRegistry,
    final long nanoTime
  ) {
    m_topicRegistry = topicRegistry;
    m_logOutputs = logOutputs;
    start(nanoTime);
  }

  public final long timeSinceStartNanos(long nanos) {
    if (m_startTimeNanos == Long.MIN_VALUE) {
      System.err.println(
        "LogOutput.timeSinceStartNanos: Should not be called before session started"
      );
      return -1;
    }
    if (nanos < m_startTimeNanos) {
      System.err.println(
        "LogOutput.timeSinceStartNanos: nanos should not be before start."
      );
      return -1;
    }
    return nanos - m_startTimeNanos;
  }

  public final boolean subscribeTopic(
    final String topicName,
    final String outputName
  ) {
    return subscribeTopic(topicName, outputName, System.nanoTime());
  }

  protected final boolean subscribeTopic(
    final String topicName,
    final String outputName,
    final long nanos
  ) {
    final LogTopic topic = m_topicRegistry.getTopic(topicName);
    final LogOutput output = m_logOutputs.get(outputName);

    if (topic == null) {
      System.err.println(
        "LogSession.subscribeTopic: topic by name '" +
        topicName +
        "' does not exist."
      );
      return false;
    }
    if (output == null) {
      System.err.println(
        "LogSession.subscribeTopic: output by name '" +
        outputName +
        "' does not exist."
      );
      return false;
    }

    topic.addSubscriber(output, nanos);
    return true;
  }

  public final boolean unsubscribeTopic(
    final String topicName,
    final String outputName
  ) {
    return unsubscribeTopic(topicName, outputName, System.nanoTime());
  }

  protected final boolean unsubscribeTopic(
    final String topicName,
    final String outputName,
    final long nanos
  ) {
    final LogTopic topic = m_topicRegistry.getTopic(topicName);
    final LogOutput output = m_logOutputs.get(outputName);

    if (topic == null) {
      System.err.println(
        "LogSession.unsubscribeTopic: topic by name '" +
        topicName +
        "' does not exist."
      );
      return false;
    }
    if (output == null) {
      System.err.println(
        "LogSession.unsubscribeTopic: output by name '" +
        outputName +
        "' does not exist."
      );
      return false;
    }

    final boolean wasRemoved = topic.removeSubscriber(output, nanos);

    if (!wasRemoved) {
      System.err.println(
        "LogSession.unsubscribeTopic: output wasn't subscribed to topic '" +
        topicName +
        "'"
      );
      return false;
    }
    return true;
  }

  protected final boolean start(final long nanos) {
    m_startTimeNanos = nanos;

    for (LogOutput output : m_logOutputs.values()) {
      output.start(this::timeSinceStartNanos, nanos);
    }
    return true;
  }

  public final boolean stop() {
    return stop(System.nanoTime());
  }

  protected final boolean stop(final long nanos) {
    if (m_startTimeNanos == Long.MIN_VALUE) {
      System.err.println(
        "LogSession.stop: Cannot stop. Session not yet started"
      );
      return false;
    }
    if (m_stopTimeNanos != Long.MIN_VALUE) {
      System.err.println("LogSession.stop: Cannot stop more than once.");
      return false;
    }

    m_stopTimeNanos = nanos;

    for (LogOutput output : m_logOutputs.values()) {
      output.stop(nanos);
    }

    m_topicRegistry.removeAllSubscribers();
    return true;
  }
}
