package com.team2357.log.outputs;

import static org.mockito.Mockito.verify;

import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

public class SimpleLogOutputTest {

  long initNanos = System.nanoTime();

  @Test
  public void testStartStop() {
    var writer = Mockito.mock(LogWriter.class);
    var output = new SimpleLogOutput(writer);

    long startNanos = initNanos;
    long stopNanos = initNanos + 3000000000L;

    output.start(this::convertToRelativeNanos, startNanos);
    output.stop(stopNanos);

    verify(writer).onStart(0);
    verify(writer).onStop(3000000000L);
  }

  @Test
  public void testSubscribeUnsubscribe() {
    var writer = Mockito.mock(LogWriter.class);
    var output = new SimpleLogOutput(writer);

    long startNanos = initNanos;
    long subscribeNanos = initNanos + 1000000000L;
    long unsubscribeNanos = initNanos + 2000000000L;
    long stopNanos = initNanos + 3000000000L;

    output.start(this::convertToRelativeNanos, startNanos);
    output.notifySubscribe("test-topic", String.class, subscribeNanos);
    output.notifyUnsubscribe("test-topic", unsubscribeNanos);
    output.stop(stopNanos);

    verify(writer).onSubscribe("test-topic", String.class, 1000000000L);
    verify(writer).onUnsubscribe("test-topic", 2000000000L);
  }

  @Test
  public void testPreSubscribe() {
    var writer = Mockito.mock(LogWriter.class);
    var output = new SimpleLogOutput(writer);

    long subscribeNanos = initNanos - 1000000000L;
    long startNanos = initNanos;
    long stopNanos = initNanos + 3000000000L;

    output.notifySubscribe("test-topic", String.class, subscribeNanos);
    output.start(this::convertToRelativeNanos, startNanos);
    output.stop(stopNanos);

    verify(writer).onSubscribe("test-topic", String.class, -1);
  }

  @Test
  public void testWriteEntry() {
    var writer = Mockito.mock(LogWriter.class);
    var output = new SimpleLogOutput(writer);

    long startNanos = initNanos;
    long valueNanos = initNanos + 1500000000L;

    output.start(this::convertToRelativeNanos, startNanos);
    output.writeEntry("test-topic", "test-value", valueNanos);
    verify(writer).onEntry("test-topic", "test-value", 1500000000L);
  }

  @Ignore
  public long convertToRelativeNanos(long nanos) {
    return nanos - initNanos;
  }
}
