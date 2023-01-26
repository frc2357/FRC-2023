package com.team2357.log.topics;

import com.team2357.log.outputs.LogOutput;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

public class LogTopicRegistryTest {

  /**
   * This is for other tests in other packages to use.
   */
  @Ignore
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

    Assert.assertEquals(topic1, registry.getTopic("test-topic-1"));
    Assert.assertEquals(topic2, registry.getTopic("test-topic-2"));
    Assert.assertNull(registry.getTopic("test-topic-3"));
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

    Assert.assertTrue(topic1.hasSubscribers());
    Assert.assertTrue(topic2.hasSubscribers());

    registry.removeAllSubscribers();

    Assert.assertFalse(topic1.hasSubscribers());
    Assert.assertFalse(topic2.hasSubscribers());
  }

  @Ignore
  private class TestTopic extends LogTopic {

    public TestTopic(final String name) {
      super(name, String.class, Mockito.mock(LogTopicRegistry.class));
    }
  }
}
