package com.team2357.log;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.team2357.log.outputs.LogOutput;
import com.team2357.log.topics.LogTopic;
import com.team2357.log.topics.LogTopicRegistry;
import com.team2357.log.topics.LogTopicRegistryTest;

public class LogSessionTest {

  // @Ignore
  public static class TestTopic extends LogTopic {

    public TestTopic(final String name, final LogTopicRegistry registry) {
      super(name, String.class, registry);
    }
  }

  @Test
  public void testStartStop() {
    long startNanos = 1000000000L;
    long stopNanos = 3000000000L;

    final LogTopicRegistry topicRegistry = LogTopicRegistryTest.createTestRegistry();
    final LogOutput testOutput = Mockito.mock(LogOutput.class);
    final LogSession session = new LogSession(
      Map.of("test-output", testOutput),
      topicRegistry,
      startNanos
    );

    Assertions.assertEquals(1L, session.timeSinceStartNanos(1000000001L));
    Assertions.assertEquals(3000000L, session.timeSinceStartNanos(1003000000L));
    Assertions.assertEquals(8000000000L, session.timeSinceStartNanos(9000000000L));
    Assertions.assertEquals(2000000000L, session.timeSinceStartNanos(stopNanos));

    Assertions.assertTrue(session.stop(stopNanos));
    Assertions.assertFalse(session.stop(stopNanos));
  }

  @Test
  public void testSubscribeUnsubscribe() {
    final LogTopicRegistry topicRegistry = Mockito.mock(LogTopicRegistry.class);
    final LogTopic testTopic = new TestTopic("test-topic", topicRegistry);
    when(topicRegistry.getTopic("test-topic")).thenReturn(testTopic);

    final LogOutput testOutput = Mockito.mock(LogOutput.class);
    final LogSession session = new LogSession(
      Map.of("test-output", testOutput),
      topicRegistry,
      1000000000L
    );

    session.subscribeTopic("test-topic", "test-output", 1000000000L);
    verify(testOutput).notifySubscribe("test-topic", String.class, 1000000000L);

    session.unsubscribeTopic("test-topic", "test-output", 2000000000L);
    verify(testOutput).notifyUnsubscribe("test-topic", 2000000000L);

    session.stop(3000000000L);
  }
}
