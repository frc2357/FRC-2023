package com.team2357.log.lib;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UtilsTest {

  @Test
  public void testRoundByFactorDouble() {
    Assertions.assertEquals(0.8, Utils.roundByFactor(0.828128, 0.1), 0.0);
    Assertions.assertEquals(10.8, Utils.roundByFactor(10.7828, 0.1), 0.0);

    Assertions.assertEquals(4.25, Utils.roundByFactor(4.2102325, 0.25), 0.0);
    Assertions.assertEquals(0.0, Utils.roundByFactor(0.11251, 0.25), 0.0);

    Assertions.assertEquals(2.0, Utils.roundByFactor(1.8, 1.0), 0.0);
    Assertions.assertEquals(15.0, Utils.roundByFactor(15.42359, 1.0), 0.0);

    Assertions.assertEquals(120.0, Utils.roundByFactor(115.01512, 10.0), 0.0);
    Assertions.assertEquals(0.0, Utils.roundByFactor(4.9824, 10.0), 0.0);

    Assertions.assertEquals(Double.NaN, Utils.roundByFactor(50.0, 0.0), 0.0);
  }

  @Test
  public void testRoundByFactorInt() {
    Assertions.assertEquals(5, Utils.roundByFactor(5, 1));
    Assertions.assertEquals(28183, Utils.roundByFactor(28183, 1));

    Assertions.assertEquals(10, Utils.roundByFactor(5, 10));
    Assertions.assertEquals(28180, Utils.roundByFactor(28183, 10));

    Assertions.assertEquals(500, Utils.roundByFactor(258, 500));
    Assertions.assertEquals(15813000, Utils.roundByFactor(15812838, 500));

    Assertions.assertEquals(0, Utils.roundByFactor(50, 0));
  }
}
