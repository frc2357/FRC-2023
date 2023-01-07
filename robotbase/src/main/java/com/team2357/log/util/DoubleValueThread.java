package com.team2357.log.util;

import com.team2357.log.topics.DoubleTopic;

public abstract class DoubleValueThread implements Runnable {

  public static final double DEFAULT_SAMPLE_RATE_SECONDS = 0.01D;
  private static final int THREAD_PRIORITY = Thread.NORM_PRIORITY;

  private static ThreadGroup m_threadGroup = initThreadGroup();

  private static ThreadGroup initThreadGroup() {
    final ThreadGroup threadGroup = new ThreadGroup("ThreadLogOutputs");
    threadGroup.setMaxPriority(THREAD_PRIORITY);
    return threadGroup;
  }

  private final double m_sampleRateSeconds;
  private final long m_startNanos;
  private final DoubleTopic valueTopic;
  private Thread m_thread;

  public DoubleValueThread(
    final String valueTopicName,
    final double sampleRateSeconds,
    final double roundingFactor
  ) {
    m_sampleRateSeconds = sampleRateSeconds;
    m_startNanos = System.nanoTime();
    m_thread = new Thread(m_threadGroup, this, getClass().getName());

    valueTopic = new DoubleTopic(valueTopicName, roundingFactor);

    m_thread.start();
  }

  public final void stop() {
    m_thread = null;
  }

  public final double getSecondsPassed() {
    final double nanoDiff = System.nanoTime() - m_startNanos;
    return nanoDiff / 1000000000.0D;
  }

  @Override
  public void run() {
    try {
      while (m_thread != null) {
        valueTopic.log(getValue());
        Thread.sleep((long) (m_sampleRateSeconds * 1000));
      }
    } catch (InterruptedException ie) {
      stop();
    }
  }

  public abstract double getValue();
}
