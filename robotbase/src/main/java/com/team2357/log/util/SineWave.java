package com.team2357.log.util;

public class SineWave extends DoubleValueThread {

  private final double m_frequencyHz;
  private final double m_amplitude;

  public SineWave(
    final String valueTopicName,
    final double sampleRateSeconds,
    final double frequencyHz,
    final double amplitude,
    final double roundingFactor
  ) {
    super(valueTopicName, sampleRateSeconds, roundingFactor);
    m_amplitude = amplitude;
    m_frequencyHz = frequencyHz;
  }

  @Override
  public final double getValue() {
    final double rotation = getSecondsPassed() % m_frequencyHz;
    final double radians = rotation * (2 * Math.PI);
    return Math.sin(radians) * m_amplitude;
  }
}
