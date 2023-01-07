package com.team2357.log.topics;

import com.team2357.log.lib.Utils;

public class DoubleTopic extends DataTopic {

  private double m_roundingFactor;

  public DoubleTopic(String name, double roundingFactor) {
    super(name, Double.class);
    m_roundingFactor = roundingFactor;
  }

  public void log(double value) {
    log(value, System.nanoTime());
  }

  public void log(double value, long nanos) {
    final double roundedValue = Utils.roundByFactor(value, m_roundingFactor);
    super.log(roundedValue, nanos);
  }
}
