package com.team2357.log.topics;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.team2357.log.outputs.LogOutput;

public class LogTopicRegistryTest {

  /**
   * This is for other tests in other packages to use.
   */
  // @Ignore
  public static LogTopicRegistry createTestRegistry() {
    return new LogTopicRegistry();
  }

  @Test
  public void testAddTopic() {
    final LogTopicRegistry registry = new LogTopicRegistry();

    final LogTopic topic1 = new TestTopic("test-topic-1");
    final LogTopic topic2 = new TestTopic("test-topic-2");

    registry.addTopic(topic1);
    registry.addTopic(topic2);

    Assertions.assertEquals(topic1, registry.getTopic("test-topic-1"));
    Assertions.assertEquals(topic2, registry.getTopic("test-topic-2"));
    Assertions.assertNull(registry.getTopic("test-topic-3"));
  }

  @Test
  public void testRemoveAllSubscribers() {
    final LogTopicRegistry registry = new LogTopicRegistry();

    final LogTopic topic1 = new TestTopic("test-topic-1");
    final LogTopic topic2 = new TestTopic("test-topic-2");

    registry.addTopic(topic1);
    registry.addTopic(topic2);

    topic1.addSubscriber(Mockito.mock(LogOutput.class));
    topic2.addSubscriber(Mockito.mock(LogOutput.class));

    Assertions.assertTrue(topic1.hasSubscribers());
    Assertions.assertTrue(topic2.hasSubscribers());

    registry.removeAllSubscribers();

    Assertions.assertFalse(topic1.hasSubscribers());
    Assertions.assertFalse(topic2.hasSubscribers());
  }

  // @Ignore
  private class TestTopic extends LogTopic {

    public TestTopic(final String name) {
      super(name, String.class, Mockito.mock(LogTopicRegistry.class));
    }
  }
}
