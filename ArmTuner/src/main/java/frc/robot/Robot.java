/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/
package frc.robot;

import edu.wpi.first.math.controller.ArmFeedforward;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.SparkMaxPIDController.ArbFFUnits;

/**
 * Before Running:
 * Open shuffleBoard, select File->Load Layout and select the
 * shuffleboard.json that is in the root directory of this example
 */

/**
 * REV Smart Motion Guide
 *
 * The SPARK MAX includes a new control mode, REV Smart Motion which is used to
 * control the position of the motor, and includes a max velocity and max
 * acceleration parameter to ensure the motor moves in a smooth and predictable
 * way. This is done by generating a motion profile on the fly in SPARK MAX and
 * controlling the velocity of the motor to follow this profile.
 *
 * Since REV Smart Motion uses the velocity to track a profile, there are only
 * two steps required to configure this mode:
 * 1) Tune a velocity PID loop for the mechanism
 * 2) Configure the smart motion parameters
 *
 * Tuning the Velocity PID Loop
 *
 * The most important part of tuning any closed loop control such as the
 * velocity
 * PID, is to graph the inputs and outputs to understand exactly what is
 * happening.
 * For tuning the Velocity PID loop, at a minimum we recommend graphing:
 *
 * 1) The velocity of the mechanism (‘Process variable’)
 * 2) The commanded velocity value (‘Setpoint’)
 * 3) The applied output
 *
 * This example will use ShuffleBoard to graph the above parameters. Make sure
 * to
 * load the shuffleboard.json file in the root of this directory to get the full
 * effect of the GUI layout.
 */
public class Robot extends TimedRobot {
  private static final int deviceID = 26;
  private CANSparkMax m_motor;
  private SparkMaxPIDController m_pidController;
  private ArmFeedforward m_armFeedforward;
  private RelativeEncoder m_encoder;
  public double kP, kI, kD, kIz, kFF, kMaxOutput, kMinOutput, maxRPM, maxVel, minVel, maxAcc, allowedErr, kG;

  public double m_rotationsPerRadian = 190.91 / (2 * Math.PI);
  // public double m_horizontalRotations = 0; // Position when the arm is parallel
  // with the floor
  public double m_horizontalRotations = 47.7275;

  private double intakeFloorSetpoint = 146;
  private double armHighRotationSetpoint = 114.55 / 4;

  // Controller
  XboxController controller = new XboxController(1);

  @Override
  public void robotInit() {
    // initialize motor
    m_motor = new CANSparkMax(deviceID, MotorType.kBrushless);

    /**
     * The RestoreFactoryDefaults method can be used to reset the configuration
     * parameters
     * in the SPARK MAX to their factory default state. If no argument is passed,
     * these
     * parameters will not persist between power cycles
     */
    m_motor.restoreFactoryDefaults();

    m_motor.setInverted(true);
    m_motor.setSmartCurrentLimit(30, 30);
    m_motor.setIdleMode(IdleMode.kBrake);
    m_motor.enableVoltageCompensation(12);

    // initialze PID controller and encoder objects
    m_pidController = m_motor.getPIDController();
    m_encoder = m_motor.getEncoder();

    // Reset encoder
    m_encoder.setPosition(0);

    // PID coefficients
    kP = 0.00075;
    kI = 0;
    kD = 0;
    kIz = 0;
    kFF = 0.001; // 0.00007
    kMaxOutput = 1;
    kMinOutput = -1;
    maxRPM = 11000; // 5676 for ne0, 11000 for neo 550
    kG = 0.64;

    // Smart Motion Coefficients
    maxVel = 4600; // rpm
    maxAcc = 4600;

    // set PID coefficients
    m_pidController.setP(kP);
    m_pidController.setI(kI);
    m_pidController.setD(kD);
    m_pidController.setIZone(kIz);
    m_pidController.setFF(kFF);
    m_pidController.setOutputRange(kMinOutput, kMaxOutput);

    // feed forward
    m_armFeedforward = new ArmFeedforward(0, kG, 0);

    /**
     * Smart Motion coefficients are set on a SparkMaxPIDController object
     *
     * - setSmartMotionMaxVelocity() will limit the velocity in RPM of
     * the pid controller in Smart Motion mode
     * - setSmartMotionMinOutputVelocity() will put a lower bound in
     * RPM of the pid controller in Smart Motion mode
     * - setSmartMotionMaxAccel() will limit the acceleration in RPM^2
     * of the pid controller in Smart Motion mode
     * - setSmartMotionAllowedClosedLoopError() will set the max allowed
     * error for the pid controller in Smart Motion mode
     */
    int smartMotionSlot = 0;
    m_pidController.setSmartMotionMaxVelocity(maxVel, smartMotionSlot);
    m_pidController.setSmartMotionMinOutputVelocity(minVel, smartMotionSlot);
    m_pidController.setSmartMotionMaxAccel(maxAcc, smartMotionSlot);
    allowedErr = 0.5;
    m_pidController.setSmartMotionAllowedClosedLoopError(allowedErr, smartMotionSlot);

    // display PID coefficients on SmartDashboard
    // SmartDashboard.putNumber("P Gain", kP);
    // SmartDashboard.putNumber("I Gain", kI);
    // SmartDashboard.putNumber("D Gain", kD);
    // SmartDashboard.putNumber("I Zone", kIz);
    // SmartDashboard.putNumber("Feed Forward", kFF);
    // SmartDashboard.putNumber("Max Output", kMaxOutput);
    // SmartDashboard.putNumber("Min Output", kMinOutput);

    // display Smart Motion coefficients
    // SmartDashboard.putNumber("Max Velocity", maxVel);
    // SmartDashboard.putNumber("Min Velocity", minVel);
    // SmartDashboard.putNumber("Max Acceleration", maxAcc);
    // SmartDashboard.putNumber("Allowed Closed Loop Error", allowedErr);
    SmartDashboard.putNumber("Set Position", 0);
    SmartDashboard.putNumber("Set Velocity", 0);

    SmartDashboard.putNumber("Velocity", m_encoder.getVelocity());
    SmartDashboard.putNumber("Current rotations", m_encoder.getPosition());
    SmartDashboard.putNumber("Output", m_motor.getAppliedOutput());
  }

  @Override
  public void teleopPeriodic() {
    // read PID coefficients from SmartDashboard
    // double p = SmartDashboard.getNumber("P Gain", 0);
    // double i = SmartDashboard.getNumber("I Gain", 0);
    // double d = SmartDashboard.getNumber("D Gain", 0);
    // double iz = SmartDashboard.getNumber("I Zone", 0);
    // double ff = SmartDashboard.getNumber("Feed Forward", 0);
    // double max = SmartDashboard.getNumber("Max Output", 0);
    // double min = SmartDashboard.getNumber("Min Output", 0);
    // double maxV = SmartDashboard.getNumber("Max Velocity", 0);
    // double minV = SmartDashboard.getNumber("Min Velocity", 0);
    // double maxA = SmartDashboard.getNumber("Max Acceleration", 0);
    // double allE = SmartDashboard.getNumber("Allowed Closed Loop Error", 0);

    // // if PID coefficients on SmartDashboard have changed, write new values to
    // controller
    // if((p != kP)) { m_pidController.setP(p); kP = p; }
    // if((i != kI)) { m_pidController.setI(i); kI = i; }
    // if((d != kD)) { m_pidController.setD(d); kD = d; }
    // if((iz != kIz)) { m_pidController.setIZone(iz); kIz = iz; }
    // if((ff != kFF)) { m_pidController.setFF(ff); kFF = ff; }
    // if((max != kMaxOutput) || (min != kMinOutput)) {
    // m_pidController.setOutputRange(min, max);
    // kMinOutput = min; kMaxOutput = max;
    // }
    // if((maxV != maxVel)) { m_pidController.setSmartMotionMaxVelocity(maxV,0);
    // maxVel = maxV; }
    // if((minV != minVel)) {
    // m_pidController.setSmartMotionMinOutputVelocity(minV,0); minVel = minV; }
    // if((maxA != maxAcc)) { m_pidController.setSmartMotionMaxAccel(maxA,0); maxAcc
    // = maxA; }
    // if((allE != allowedErr)) {
    // m_pidController.setSmartMotionAllowedClosedLoopError(allE,0); allowedErr =
    // allE; }

    double setPoint, processVariable, feedForward;
    boolean mode = false;
    if (mode) {
      // setPoint = SmartDashboard.getNumber("Set Velocity", 0);
      // m_pidController.setReference(setPoint, CANSparkMax.ControlType.kVelocity);
      // processVariable = m_encoder.getVelocity();
    } else {
      setPoint = SmartDashboard.getNumber("Set Position", 0);
      /**
       * As with other PID modes, Smart Motion is set by calling the
       * setReference method on an existing pid object and setting
       * the control type to kSmartMotion
       */

      double ffCalc = (setPoint - m_horizontalRotations) / m_rotationsPerRadian;
      feedForward = m_armFeedforward.calculate(ffCalc, 0);

      // feedForward = m_armFeedforward.calculate(0, 0);
      //System.out.println(feedForward);
      m_pidController.setReference(setPoint, CANSparkMax.ControlType.kSmartMotion, 0, feedForward, ArbFFUnits.kVoltage);
      processVariable = m_encoder.getPosition();
    }

    System.out.println(m_encoder.getVelocity());
    SmartDashboard.putNumber("Velocity", m_encoder.getVelocity());
    SmartDashboard.putNumber("Current rotations", m_encoder.getPosition());
    SmartDashboard.putNumber("Output", m_motor.getAppliedOutput());
  }

  @Override
  public void testPeriodic() {
    m_motor.set(deadband(controller.getRightY(), 0.1) * -0.4);

    if (controller.getAButtonPressed()) {
      m_encoder.setPosition(0);
    }

    SmartDashboard.putNumber("Velocity", m_encoder.getVelocity());
    SmartDashboard.putNumber("Current rotations", m_encoder.getPosition());
    SmartDashboard.putNumber("Output", m_motor.getAppliedOutput());
  }

  public static double deadband(double value, double deadband) {
    if (Math.abs(value) > deadband) {
      if (value > 0.0) {
        return (value - deadband) / (1.0 - deadband);
      } else {
        return (value + deadband) / (1.0 - deadband);
      }
    } else {
      return 0.0;
    }
  }

}
