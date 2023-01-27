package com.team2357.log.topics;

import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import com.team2357.log.outputs.LogOutput;

public class BooleanTopicTest {

  @Test
  public void testLog() {
    final LogOutput output = Mockito.mock(LogOutput.class);
    final BooleanTopic topic = new BooleanTopic("boolean-topic");
    final long nanos = System.nanoTime();
    topic.addSubscriber(output);

    topic.log(false, nanos + 0);
    topic.log(true, nanos + 100);
    topic.log(true, nanos + 200);
    topic.log(true, nanos + 300);
    topic.log(false, nanos + 400);
    topic.log(false, nanos + 500);
    topic.log(false, nanos + 600);
    topic.log(true, nanos + 700);

    InOrder inOrder = Mockito.inOrder(output);
    inOrder.verify(output).writeEntry("boolean-topic", false, nanos + 0);
    inOrder.verify(output).writeEntry("boolean-topic", true, nanos + 100);
    inOrder.verify(output).writeEntry("boolean-topic", true, nanos + 300);
    inOrder.verify(output).writeEntry("boolean-topic", false, nanos + 400);
    inOrder.verify(output).writeEntry("boolean-topic", false, nanos + 600);
    inOrder.verify(output).writeEntry("boolean-topic", true, nanos + 700);
  }
}
