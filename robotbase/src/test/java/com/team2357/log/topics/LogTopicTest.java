package com.team2357.log.topics;

import static org.mockito.Mockito.*;

import com.team2357.log.outputs.LogOutput;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

public class LogTopicTest {

  @Ignore
  public static class TestTopic extends LogTopic {

    public TestTopic(final String name, final LogTopicRegistry registry) {
      super(name, String.class, registry);
    }
  }

  @Test
  public void testConstructor() {
    final LogTopicRegistry registry = Mockito.mock(LogTopicRegistry.class);
    final TestTopic topic = new TestTopic("test-topic", registry);

    Assert.assertEquals("test-topic", topic.getName());
    verify(registry).addTopic(topic);
  }

  @Test
  public void testSubscribers() {
    final LogTopicRegistry registry = Mockito.mock(LogTopicRegistry.class);
    final TestTopic topic = new TestTopic("test-topic", registry);
    final LogOutput subscriber1 = Mockito.mock(LogOutput.class);
    final LogOutput subscriber2 = Mockito.mock(LogOutput.class);

    Assert.assertFalse(topic.hasSubscribers());

    topic.addSubscriber(subscriber1);
    Assert.assertTrue(topic.hasSubscribers());

    topic.writeEntry("test-value", 1000000000L);
    verify(subscriber1).writeEntry("test-topic", "test-value", 1000000000L);

    topic.addSubscriber(subscriber2);
    Assert.assertTrue(topic.hasSubscribers());

    topic.writeEntry("test-value2", 2000000000L);
    verify(subscriber1).writeEntry("test-topic", "test-value2", 2000000000L);
    verify(subscriber2).writeEntry("test-topic", "test-value2", 2000000000L);

    topic.removeSubscriber(subscriber1);
    Assert.assertTrue(topic.hasSubscribers());

    topic.writeEntry("test-value3", 3000000000L);
    verify(subscriber2).writeEntry("test-topic", "test-value3", 3000000000L);

    topic.removeSubscriber(subscriber2);
    Assert.assertFalse(topic.hasSubscribers());
  }

  @Test
  public void testRemoveAllSubscribers() {
    final LogTopicRegistry registry = Mockito.mock(LogTopicRegistry.class);
    final TestTopic topic = new TestTopic("test-topic", registry);
    final LogOutput subscriber1 = Mockito.mock(LogOutput.class);
    final LogOutput subscriber2 = Mockito.mock(LogOutput.class);

    topic.addSubscriber(subscriber1);
    topic.addSubscriber(subscriber2);
    Assert.assertTrue(topic.hasSubscribers());

    topic.removeAllSubscribers();
    Assert.assertFalse(topic.hasSubscribers());
  }
}
