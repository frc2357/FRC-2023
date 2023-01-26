package com.team2357.log.topics;

import com.team2357.log.outputs.LogOutput;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

public class StringTopicTest {

  @Test
  public void testLog() {
    final LogOutput output = Mockito.mock(LogOutput.class);
    final StringTopic topic = new StringTopic("string-topic");
    final long nanos = System.nanoTime();
    topic.addSubscriber(output);

    topic.log("zero", nanos + 0);
    topic.log("one", nanos + 100);
    topic.log("two", nanos + 200);
    topic.log("three", nanos + 300);
    topic.log("four", nanos + 400);
    topic.log("five", nanos + 500);
    topic.log("six", nanos + 600);
    topic.log("seven", nanos + 700);

    InOrder inOrder = Mockito.inOrder(output);
    inOrder.verify(output).writeEntry("string-topic", "zero", nanos + 0);
    inOrder.verify(output).writeEntry("string-topic", "one", nanos + 100);
    inOrder.verify(output).writeEntry("string-topic", "two", nanos + 200);
    inOrder.verify(output).writeEntry("string-topic", "three", nanos + 300);
    inOrder.verify(output).writeEntry("string-topic", "four", nanos + 400);
    inOrder.verify(output).writeEntry("string-topic", "five", nanos + 500);
    inOrder.verify(output).writeEntry("string-topic", "six", nanos + 600);
    inOrder.verify(output).writeEntry("string-topic", "seven", nanos + 700);
  }
}
