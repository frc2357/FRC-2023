package com.team2357.log.topics;

/**
 * Simply logs messages at the given timestamp.
 */
public class StringTopic extends LogTopic {

  public StringTopic(String name) {
    super(name, String.class);
  }

  public void log(String message) {
    log(message, System.nanoTime());
  }

  public void log(String message, long nanos) {
    writeEntry(message, nanos);
  }
}
