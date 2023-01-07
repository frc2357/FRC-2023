package com.team2357.lib.util;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

public class Utility {

  public static double clamp(double input, double min, double max) {
    double value = input;
    value = Math.max(value, min);
    value = Math.min(value, max);
    return value;
  }

  public static int clamp(int input, int min, int max) {
    int value = input;
    value = Math.max(value, min);
    value = Math.min(value, max);
    return value;
  }

  /**
   * @param talon The talon SRX to config
   * @param pid   PID values to set
   */
  public static void configTalonPID(WPI_TalonSRX talon, PIDValues pid) {
    configTalonPID(talon, 0, pid);
  }

  /**
   * @param talon   The talon SRX to config
   * @param slotIdx Index of the profile slot to configure
   * @param pid     PID values to set
   */
  public static void configTalonPID(
    WPI_TalonSRX talon,
    int slotIdx,
    PIDValues pid
  ) {
    talon.config_kP(slotIdx, pid.kp);
    talon.config_kI(slotIdx, pid.ki);
    talon.config_kD(slotIdx, pid.kd);
    talon.config_kF(slotIdx, pid.kf);
    talon.config_IntegralZone(slotIdx, pid.izone);
    talon.configClosedLoopPeakOutput(slotIdx, pid.peak);
  }

  public static int getAverage(int[] samples) {
    int sum = 0;
    for (int sample : samples) {
      sum += sample;
    }
    return sum / samples.length;
  }

  public static double deadband(double input, double deadband) {
    if (Math.abs(input) < deadband) {
      return 0.0;
    }
    return input;
  }
}
