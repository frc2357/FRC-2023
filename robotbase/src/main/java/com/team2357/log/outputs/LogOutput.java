package com.team2357.log.outputs;

import com.team2357.log.lib.RelativeTimeSource;

public interface LogOutput {
  /**
   * Starts this log output.
   * @param timeSource The time source to use as a start reference (the LogSession)
   * @param nanos The current System.nanoTime() of start.
   * @return True if successfully started, false if not.
   */
  public boolean start(RelativeTimeSource timeSource, long nanos);

  /**
   * Stops this log output.
   * @param nanos The current System.nanoTime() of the stop.
   * @return True if successfully stopped, false othwersie.
   */
  public boolean stop(long nanos);

  /**
   * Notifies a writer that it has been subscribed to a topic.
   * @param topicName The name of the topic which has been subscribed
   * @param valueType The type of values this topic produces
   * @param nanos The System.nanoTime() value for when the subscription occurred
   */
  public void notifySubscribe(String topicName, Class<?> valueType, long nanos);

  /**
   * Notifies a writer that it has been unsubscribed from a topic.
   * @param topicName The name of the topic which has been unsubscribed
   * @param nanos The System.nanoTime() value for when the unsubscription occurred
   */
  public void notifyUnsubscribe(String topicName, long nanos);

  /**
   * Notifies when a log entry occurs.
   * @param topicName The name of the topic to log
   * @param value The value to be written
   * @param nanos The System.nanoTime() value for this entry
   */
  public void writeEntry(String topicName, Object value, long nanos);
}
