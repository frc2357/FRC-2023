package com.team2357.log.lib;

public class Utils {

  public static long NANO = 1000000000;

  public static double roundByFactor(double value, double factor) {
    if (factor == 0.0) {
      System.err.println("Utils.roundByFactor: factor cannot be zero!");
      return Double.NaN;
    }
    return Math.round(value / factor) * factor;
  }

  public static int roundByFactor(int value, int factor) {
    if (factor == 0) {
      System.err.println("Utils.roundByFactor: factor cannot be zero!");
      return 0;
    }
    return (int) Math.round(((double) value) / ((double) factor)) * factor;
  }
}
