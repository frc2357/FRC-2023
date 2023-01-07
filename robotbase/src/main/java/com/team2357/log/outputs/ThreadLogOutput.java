package com.team2357.log.outputs;

import com.team2357.log.lib.RelativeTimeSource;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Used as base class for non-async logging outputs.
 */
public abstract class ThreadLogOutput implements LogOutput, Runnable {

  private enum EventValue {
    START,
    STOP,
    SUBSCRIBE,
    UNSUBSCRIBE,
  }

  private static final int THREAD_PRIORITY = Thread.NORM_PRIORITY;
  private static final int EVENT_QUEUE_CAPACITY = 20;
  private static final long QUEUE_POLL_TIMEOUT_MILLISECONDS = 100;
  private static final long SHUTDOWN_TIMEOUT_MILLISECONDS = 100;

  private static ThreadGroup m_logThreadGroup = initThreadGroup();

  private static ThreadGroup initThreadGroup() {
    final ThreadGroup threadGroup = new ThreadGroup("ThreadLogOutputs");
    threadGroup.setMaxPriority(THREAD_PRIORITY);
    return threadGroup;
  }

  private class Entry {

    private final String m_topicName;
    private final Object m_value;
    private final Class<?> m_valueType;
    private final long m_relativeNanos;

    private Entry(
      final String topicName,
      final Object value,
      final Class<?> valueType,
      final long relativeNanos
    ) {
      m_topicName = topicName;
      m_value = value;
      m_valueType = valueType;
      m_relativeNanos = relativeNanos;
    }
  }

  final Thread m_thread;
  private LogWriter m_logWriter;
  private final BlockingQueue<Entry> m_entryQueue = new ArrayBlockingQueue<Entry>(
    EVENT_QUEUE_CAPACITY
  );
  private RelativeTimeSource m_session;

  protected ThreadLogOutput() {
    this(null);
  }

  protected ThreadLogOutput(LogWriter logWriter) {
    setLogWriter(logWriter);
    m_thread = new Thread(m_logThreadGroup, this, getClass().getName());
  }

  protected void setLogWriter(LogWriter logWriter) {
    m_logWriter = logWriter;
  }

  private long convertToRelativeNanos(long nanos) {
    if (m_session == null) {
      return -1;
    }
    return m_session.convertToRelativeNanos(nanos);
  }

  @Override
  public void run() {
    if (m_logWriter == null) {
      System.err.println(
        getClass().getName() +
        ": setLogWriter() must be called before thread start"
      );
      return;
    }

    try {
      while (m_session != null || !m_entryQueue.isEmpty()) {
        writeNextEntry();
      }
    } catch (final InterruptedException ie) {
      System.err.println(
        getClass().getName() +
        ": event queue poll interrupted. Terminating thread."
      );
    }
  }

  private void queueEntry(
    final String topicName,
    final Object value,
    final Class<?> valueType,
    final long relativeNanos
  ) {
    if (m_entryQueue.remainingCapacity() == 0) {
      System.err.println(
        getClass().getName() + ": queue capacity is full! Discarding entry."
      );
      return;
    }
    m_entryQueue.add(new Entry(topicName, value, valueType, relativeNanos));
  }

  private boolean writeNextEntry() throws InterruptedException {
    final Entry entry = m_entryQueue.poll(
      QUEUE_POLL_TIMEOUT_MILLISECONDS,
      TimeUnit.MILLISECONDS
    );
    if (entry == null) {
      return false;
    }
    writeEntry(entry);
    return true;
  }

  private void writeEntry(final Entry entry) {
    if (entry.m_value instanceof EventValue) {
      switch ((EventValue) entry.m_value) {
        case START:
          m_logWriter.onStart(entry.m_relativeNanos);
          break;
        case STOP:
          m_logWriter.onStop(entry.m_relativeNanos);
          break;
        case SUBSCRIBE:
          m_logWriter.onSubscribe(
            entry.m_topicName,
            entry.m_valueType,
            entry.m_relativeNanos
          );
          break;
        case UNSUBSCRIBE:
          m_logWriter.onUnsubscribe(entry.m_topicName, entry.m_relativeNanos);
          break;
        default:
          System.err.println(
            "ThreadLogOutput.writeEntry: Unrecognized EventValue: " +
            entry.m_value
          );
          break;
      }
    } else {
      m_logWriter.onEntry(
        entry.m_topicName,
        entry.m_value,
        entry.m_relativeNanos
      );
    }
  }

  @Override
  public final boolean start(
    final RelativeTimeSource session,
    final long nanos
  ) {
    if (m_session != null) {
      System.err.println("ThreadLogOutput.start: Already started.");
      return false;
    }
    m_session = session;
    m_thread.start();
    queueEntry(null, EventValue.START, null, convertToRelativeNanos(nanos));
    return true;
  }

  @Override
  public final boolean stop(final long nanos) {
    if (m_session == null) {
      System.err.println("ThreadLogOutput.stop: Cannot stop. Not yet started");
      return false;
    }
    queueEntry(null, EventValue.STOP, null, convertToRelativeNanos(nanos));

    try {
      m_thread.join(SHUTDOWN_TIMEOUT_MILLISECONDS);
    } catch (InterruptedException ie) {
      System.err.println("ThreadLogOutput.stop: Shutdown timeout");
    }

    m_session = null;
    return true;
  }

  @Override
  public final void notifySubscribe(
    final String topicName,
    final Class<?> valueType,
    final long nanos
  ) {
    queueEntry(
      topicName,
      EventValue.SUBSCRIBE,
      valueType,
      convertToRelativeNanos(nanos)
    );
  }

  @Override
  public final void notifyUnsubscribe(
    final String topicName,
    final long nanos
  ) {
    queueEntry(
      topicName,
      EventValue.UNSUBSCRIBE,
      null,
      convertToRelativeNanos(nanos)
    );
  }

  @Override
  public final void writeEntry(
    final String topicName,
    final Object value,
    final long nanos
  ) {
    queueEntry(topicName, value, null, convertToRelativeNanos(nanos));
  }
}
