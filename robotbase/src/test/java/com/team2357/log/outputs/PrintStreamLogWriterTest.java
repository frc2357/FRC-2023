package com.team2357.log.outputs;

import static org.mockito.Mockito.verify;

import java.io.PrintStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class PrintStreamLogWriterTest {

  public static final long MS = 1000000L;
  public static final long SECOND = MS * 1000L;
  public static final long MINUTE = SECOND * 60;

  @Test
  public void testNanosToTime() {
    var writer = new PrintStreamLogWriter("prefix", null);

    Assertions.assertEquals("00:00.042", writer.nanosToTime(42 * MS));
    Assertions.assertEquals("00:22.000", writer.nanosToTime(22 * SECOND));
    Assertions.assertEquals("01:18.000", writer.nanosToTime(78 * SECOND));
    Assertions.assertEquals("13:00.000", writer.nanosToTime(13 * MINUTE));
    Assertions.assertEquals("84:00.000", writer.nanosToTime(84 * MINUTE));
    Assertions.assertEquals("122:00.000", writer.nanosToTime(122 * MINUTE));
    Assertions.assertEquals(
      "02:30.500",
      writer.nanosToTime((2 * MINUTE) + (30 * SECOND) + (500 * MS))
    );
    Assertions.assertEquals(
      "23:12.988",
      writer.nanosToTime((23 * MINUTE) + (12 * SECOND) + (988 * MS))
    );
    Assertions.assertEquals("00:00.000", writer.nanosToTime(-2 * SECOND));
  }

  @Test
  public void testOnStart() {
    var stream = Mockito.mock(PrintStream.class);
    var writer = new PrintStreamLogWriter("prefix", stream);

    writer.onStart(120);

    verify(stream).println("prefix: 00:00.000 ( Session Start )");
  }

  @Test
  public void testOnStop() {
    var stream = Mockito.mock(PrintStream.class);
    var writer = new PrintStreamLogWriter("prefix", stream);

    writer.onStart(120);
    writer.onStop(120 + (2 * MINUTE) + (30 * SECOND));

    verify(stream).println("prefix: 02:30.000 ( Session Stop )");
  }

  @Test
  public void testOnSubscribe() {
    var stream = Mockito.mock(PrintStream.class);
    var writer = new PrintStreamLogWriter("prefix", stream);

    writer.onStart(120);
    writer.onSubscribe(
      "test-topic",
      String.class,
      120 + (1 * MINUTE) + (5 * SECOND)
    );

    verify(stream).println("prefix: 01:05.000 [test-topic]( Subscribed )");
  }

  @Test
  public void testOnUnsubscribe() {
    var stream = Mockito.mock(PrintStream.class);
    var writer = new PrintStreamLogWriter("prefix", stream);

    writer.onStart(120);
    writer.onUnsubscribe(
      "test-topic",
      120 + (1 * MINUTE) + (2 * SECOND) + (152 * MS)
    );

    verify(stream).println("prefix: 01:02.152 [test-topic]( Unsubscribed )");
  }

  @Test
  public void testOnEntry() {
    var stream = Mockito.mock(PrintStream.class);
    var writer = new PrintStreamLogWriter("prefix", stream);

    writer.onStart(120);
    writer.onEntry(
      "test-topic",
      "Test value",
      120 + (1 * MINUTE) + (15 * SECOND) + (4 * MS)
    );

    verify(stream).println("prefix: 01:15.004 [test-topic]: Test value");
  }

  public void testOnEntryDoubleValue() {
    var stream = Mockito.mock(PrintStream.class);
    var writer = new PrintStreamLogWriter("prefix", stream, 3);

    writer.onStart(120);
    writer.onEntry("test-topic", 20.230000001, 120 + (30 * SECOND));

    verify(stream).println("prefix: 00:30.000 [test-topic]: 20.230");
  }
}
