package com.team2357.log.topics;

import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import com.team2357.log.outputs.LogOutput;

public class IntegerTopicTest {

  @Test
  public void testLog() {
    final LogOutput output = Mockito.mock(LogOutput.class);
    final IntegerTopic topic = new IntegerTopic("integer-topic", 50);
    final long nanos = System.nanoTime();
    topic.addSubscriber(output);

    topic.log(30, nanos + 0);
    topic.log(80, nanos + 100);
    topic.log(40, nanos + 200);
    topic.log(45, nanos + 300);
    topic.log(47, nanos + 400);
    topic.log(55, nanos + 500);
    topic.log(65, nanos + 600);
    topic.log(85, nanos + 700);

    InOrder inOrder = Mockito.inOrder(output);
    inOrder.verify(output).writeEntry("integer-topic", 50, nanos + 0);
    inOrder.verify(output).writeEntry("integer-topic", 100, nanos + 100);
    inOrder.verify(output).writeEntry("integer-topic", 50, nanos + 200);
    inOrder.verify(output).writeEntry("integer-topic", 50, nanos + 600);
    inOrder.verify(output).writeEntry("integer-topic", 100, nanos + 700);
  }
}
