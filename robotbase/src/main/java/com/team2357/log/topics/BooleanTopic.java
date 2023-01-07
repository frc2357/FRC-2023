package com.team2357.log.topics;

public class BooleanTopic extends DataTopic {

  public BooleanTopic(String name) {
    super(name, Boolean.class);
  }

  public void log(boolean value) {
    log(value, System.nanoTime());
  }

  public void log(boolean value, long nanos) {
    super.log(value, nanos);
  }
}
