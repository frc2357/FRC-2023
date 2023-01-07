package com.team2357.log.topics;

import com.team2357.log.lib.Utils;

/**
 * Logs integers as a data topic.
 */
public class IntegerTopic extends DataTopic {

  private final int m_roundingFactor;

  public IntegerTopic(final String name, final int roundingFactor) {
    super(name, Integer.class);
    m_roundingFactor = roundingFactor;
  }

  public void log(final int value) {
    log(value, System.nanoTime());
  }

  public void log(final int value, final long nanos) {
    final int roundedValue = Utils.roundByFactor(value, m_roundingFactor);
    super.log(roundedValue, nanos);
  }
}
