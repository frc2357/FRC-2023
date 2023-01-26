package com.team2357.log.outputs;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class ThreadLogOutputTest {

  private long m_initNanos = System.nanoTime();

  @Test
  public void testStartStop() throws InterruptedException {
    LogWriter writer = Mockito.mock(LogWriter.class);
    ThreadLogOutput output = new TestLogOutput(writer);

    long startNanos = m_initNanos;
    long stopNanos = m_initNanos + 3000000000L;

    output.start(this::convertToRelativeNanos, startNanos);
    output.stop(stopNanos);
    output.m_thread.join(500);

    verify(writer).onStart(0);
    verify(writer).onStop(stopNanos - startNanos);
  }

  @Test
  public void testSubscribeUnsubscribe() throws InterruptedException {
    LogWriter writer = Mockito.mock(LogWriter.class);
    ThreadLogOutput output = new TestLogOutput(writer);

    long startNanos = m_initNanos;
    long subscribeNanos = m_initNanos + 1000000000L;
    long unsubscribeNanos = m_initNanos + 2000000000L;
    long stopNanos = m_initNanos + 3000000000L;

    output.start(this::convertToRelativeNanos, startNanos);
    output.notifySubscribe("test-topic", String.class, subscribeNanos);
    output.notifyUnsubscribe("test-topic", unsubscribeNanos);
    output.stop(stopNanos);
    output.m_thread.join(500);

    verify(writer)
      .onSubscribe("test-topic", String.class, subscribeNanos - startNanos);
    verify(writer).onUnsubscribe("test-topic", unsubscribeNanos - startNanos);
  }

  @Test
  public void testPreSubscribe() throws InterruptedException {
    LogWriter writer = Mockito.mock(LogWriter.class);
    ThreadLogOutput output = new TestLogOutput(writer);

    long subscribeNanos = m_initNanos - 1000000000L;
    long startNanos = m_initNanos;
    long stopNanos = m_initNanos + 3000000000L;

    output.notifySubscribe("test-topic", String.class, subscribeNanos);
    output.start(this::convertToRelativeNanos, startNanos);
    output.stop(stopNanos);
    output.m_thread.join(500);

    verify(writer).onSubscribe("test-topic", String.class, -1);
  }

  @Test
  public void testTopicValue() throws InterruptedException {
    LogWriter writer = Mockito.mock(LogWriter.class);
    ThreadLogOutput output = new TestLogOutput(writer);

    long startNanos = m_initNanos;
    long valueNanos = m_initNanos + 1500000000L;
    long stopNanos = m_initNanos + 3000000000L;

    output.start(this::convertToRelativeNanos, startNanos);
    output.writeEntry("test-topic", "test-value", valueNanos);
    output.stop(stopNanos);
    output.m_thread.join(500);

    verify(writer).onEntry("test-topic", "test-value", valueNanos - startNanos);
  }

  // @Ignore
  public long convertToRelativeNanos(long nanos) {
    return nanos - m_initNanos;
  }

  // @Ignore
  private class TestLogOutput extends ThreadLogOutput {

    private TestLogOutput(LogWriter writer) {
      super(writer);
    }
  }
}
