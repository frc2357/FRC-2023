package com.team2357.log.outputs;

public interface LogWriter {
  public abstract void onStart(long relativeNanos);

  public abstract void onStop(long relativeNanos);

  public abstract void onSubscribe(
    String topicName,
    Class<?> valueType,
    long relativeNanos
  );

  public abstract void onUnsubscribe(String topicName, long relativeNanos);

  public abstract void onEntry(
    String topicName,
    Object value,
    long relativeNanos
  );
}
