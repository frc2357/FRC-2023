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
  private static final int armRotationID = 26;
  private CANSparkMax m_rotationMotor;
  private SparkMaxPIDController m_rotationPidController;
  private ArmFeedforward m_rotationFeedforward;
  private RelativeEncoder m_rotationEncoder;
  public double rotationKP, rotationKI, rotationKD, rotationKIz, rotationKFF, rotationKMaxOutput, rotationKMinOutput, rotationMaxRPM, rotationMaxVel, rotationMinVel, rotationMaxAcc, rotationAllowedErr, rotationKG;

  private static final int extensionID = 26;
  private CANSparkMax m_extensionMotor;
  private SparkMaxPIDController m_extensionPidController;
  private ArmFeedforward m_extensionFeedforward;
  private RelativeEncoder m_extensionEncoder;
  public double extensionKP, extensionKI, extensionKD, extensionKIz, extensionKFF, extensionKMaxOutput, extensionKMinOutput, extensionMaxRPM, extensionMaxVel, extensionMinVel, extensionMaxAcc, extensionAllowedErr, extensionKG;

  public double m_rotationsPerRadian = 190.91 / (2 * Math.PI);
  public double m_horizontalRotations = 47.7275; // Position when arm is parallel with floor

  private double intakeFloorSetpoint = 146;
  private double armHighRotationSetpoint = 51;

  // Controller
  XboxController controller = new XboxController(1);

  @Override
  public void robotInit() {
    // initialize motor
    m_rotationMotor = new CANSparkMax(armRotationID, MotorType.kBrushless);
    m_extensionMotor = new CANSparkMax(extensionID, MotorType.kBrushless);

    /**
     * The RestoreFactoryDefaults method can be used to reset the configuration
     * parameters
     * in the SPARK MAX to their factory default state. If no argument is passed,
     * these
     * parameters will not persist between power cycles
     */

    // Arm rotation motr config
    m_rotationMotor.restoreFactoryDefaults();

    m_rotationMotor.setInverted(true);
    m_rotationMotor.setSmartCurrentLimit(30, 30);
    m_rotationMotor.setIdleMode(IdleMode.kBrake);
    m_rotationMotor.enableVoltageCompensation(12);

    // Arm extension motor config
    m_extensionMotor.restoreFactoryDefaults();

    m_extensionMotor.setInverted(true);
    m_extensionMotor.setSmartCurrentLimit(30, 30);
    m_extensionMotor.setIdleMode(IdleMode.kBrake);
    m_extensionMotor.enableVoltageCompensation(12);


    // initialze PID controller and encoder objects

    // Rotation motor
    m_rotationPidController = m_rotationMotor.getPIDController();
    m_rotationEncoder = m_rotationMotor.getEncoder();

    // Extension motor
    m_extensionPidController = m_rotationMotor.getPIDController();
    m_extensionEncoder = m_rotationMotor.getEncoder();

    // Reset encoder
    m_rotationEncoder.setPosition(0);
    m_extensionEncoder.setPosition(0);

    // PID coefficients

    // Arm rotation
    rotationKP = 0.00075;
    rotationKI = 0;
    rotationKD = 0;
    rotationKIz = 0;
    rotationKFF = 0.001; // 0.00007
    rotationKMaxOutput = 1;
    rotationKMinOutput = -1;
    rotationMaxRPM = 5676; // 5676 for ne0, 11000 for neo 550
    rotationKG = 0.64;

    // arm extension
    extensionKP = 0.00075;
    extensionKI = 0;
    extensionKD = 0;
    extensionKIz = 0;
    extensionKFF = 0.001; // 0.00007
    extensionKMaxOutput = 1;
    extensionKMinOutput = -1;
    extensionMaxRPM = 5676; // 5676 for ne0, 11000 for neo 550
    extensionKG = 0.64;


    // Smart Motion Coefficients

    // ARm rotation
    rotationMaxVel = 4600; // rpm
    rotationMaxAcc = 4600;

    // arm extension
    extensionMaxVel = 4600; // rpm
    extensionMaxAcc = 4600;

    // set PID coefficients

    // Arm rotation
    m_rotationPidController.setP(rotationKP);
    m_rotationPidController.setI(rotationKI);
    m_rotationPidController.setD(rotationKD);
    m_rotationPidController.setIZone(rotationKIz);
    m_rotationPidController.setFF(rotationKFF);
    m_rotationPidController.setOutputRange(rotationKMinOutput, rotationKMaxOutput);

    // Arm extension
    m_extensionPidController.setP(rotationKP);
    m_extensionPidController.setI(rotationKI);
    m_extensionPidController.setD(rotationKD);
    m_extensionPidController.setIZone(rotationKIz);
    m_extensionPidController.setFF(rotationKFF);
    m_extensionPidController.setOutputRange(rotationKMinOutput, rotationKMaxOutput);


    // feed forward
    m_rotationFeedforward = new ArmFeedforward(0, rotationKG, 0);

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

     // ARm rotation
    int smartMotionSlot = 0;
    m_rotationPidController.setSmartMotionMaxVelocity(rotationMaxVel, smartMotionSlot);
    m_rotationPidController.setSmartMotionMinOutputVelocity(rotationMinVel, smartMotionSlot);
    m_rotationPidController.setSmartMotionMaxAccel(rotationMaxAcc, smartMotionSlot);
    rotationAllowedErr = 0.5;
    m_rotationPidController.setSmartMotionAllowedClosedLoopError(rotationAllowedErr, smartMotionSlot);


    // ARm extension
    smartMotionSlot = 0;
    m_rotationPidController.setSmartMotionMaxVelocity(extensionMaxVel, smartMotionSlot);
    m_rotationPidController.setSmartMotionMinOutputVelocity(extensionMinVel, smartMotionSlot);
    m_rotationPidController.setSmartMotionMaxAccel(extensionMaxAcc, smartMotionSlot);
    extensionAllowedErr = 0.5;
    m_rotationPidController.setSmartMotionAllowedClosedLoopError(extensionAllowedErr, smartMotionSlot);


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

    SmartDashboard.putNumber("Velocity", m_rotationEncoder.getVelocity());
    SmartDashboard.putNumber("Current rotations", m_rotationEncoder.getPosition());
    SmartDashboard.putNumber("Output", m_rotationMotor.getAppliedOutput());
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
      feedForward = m_rotationFeedforward.calculate(ffCalc, 0);

      // feedForward = m_armFeedforward.calculate(0, 0);
      //System.out.println(feedForward);
      m_rotationPidController.setReference(setPoint, CANSparkMax.ControlType.kSmartMotion, 0, feedForward, ArbFFUnits.kVoltage);
      processVariable = m_rotationEncoder.getPosition();
    }

    System.out.println(m_rotationEncoder.getVelocity());
    SmartDashboard.putNumber("Velocity", m_rotationEncoder.getVelocity());
    SmartDashboard.putNumber("Current rotations", m_rotationEncoder.getPosition());
    SmartDashboard.putNumber("Output", m_rotationMotor.getAppliedOutput());
  }

  @Override
  public void testPeriodic() {
    m_rotationMotor.set(deadband(controller.getRightY(), 0.1) * -0.4);

    if (controller.getAButtonPressed()) {
      m_rotationEncoder.setPosition(0);
    }

    SmartDashboard.putNumber("Velocity", m_rotationEncoder.getVelocity());
    SmartDashboard.putNumber("Current rotations", m_rotationEncoder.getPosition());
    SmartDashboard.putNumber("Output", m_rotationMotor.getAppliedOutput());
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
