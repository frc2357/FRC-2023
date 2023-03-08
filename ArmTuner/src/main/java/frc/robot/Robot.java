/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

// Full extension rotations = 253

package frc.robot;

import edu.wpi.first.math.controller.ArmFeedforward;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.shuffleboard.SuppliedValueWidget;
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
  private ArmFeedforward m_rotationDefaultFeedforward, m_rotationExtendedFeedforward;
  private RelativeEncoder m_rotationEncoder;
  public double rotationKP, rotationKI, rotationKD, rotationKIz, rotationKFF, rotationKMaxOutput, rotationKMinOutput, rotationMaxRPM, rotationMaxVel, rotationMinVel, rotationMaxAcc, rotationAllowedErr, rotationDefaultKG, rotationExtendedKG, rotationSetPoint;

  private static final int extensionID = 27
  ;
  private CANSparkMax m_extensionMotor;
  private SparkMaxPIDController m_extensionPidController;
  private ArmFeedforward m_extensionFeedforward;
  private RelativeEncoder m_extensionEncoder;
  public double extensionKP, extensionKI, extensionKD, extensionKIz, extensionKFF, extensionKMaxOutput, extensionKMinOutput, extensionMaxRPM, extensionMaxVel, extensionMinVel, extensionMaxAcc, extensionAllowedErr, extensionKG, extensionSetPoint;
  public double m_fullExtensionRotations = 271.65;

  public double m_rotationsPerRadian = 190.91 / (2 * Math.PI);
  public double m_horizontalRotations = 47.7275; // Position when arm is parallel with floor

  private double intakeFloorSetpoint = 146;
  private double armHighRotationSetpoint = 51;

  public ShuffleboardTab m_extensionTab = Shuffleboard.getTab("extension");
  public ShuffleboardTab m_rotationTab = Shuffleboard.getTab("rotation");

  public NetworkTable m_extensionTable = NetworkTableInstance.getDefault().getTable("extension");
  public NetworkTable m_rotationTable = NetworkTableInstance.getDefault().getTable("rotation");

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
    m_extensionPidController = m_extensionMotor.getPIDController();
    m_extensionEncoder = m_extensionMotor.getEncoder();

    // Reset encoder
    m_rotationEncoder.setPosition(0);
    m_extensionEncoder.setPosition(0);

    // PID coefficients

    // Arm rotation
    // rotationKP = 0.00075;
    // rotationKI = 0;
    // rotationKD = 0;
    // rotationKIz = 0;
    // rotationKFF = 0.001; // 0.00007
    // rotationKMaxOutput = 1;
    // rotationKMinOutput = -1;
    // rotationMaxRPM = 5676; // 5676 for ne0, 11000 for neo 550
    // rotationDefaultKG = 0.64;
    // rotationExtendedKG = 0.64;

    rotationKP = 0.00075;
    rotationKI = 0;
    rotationKD = 0;
    rotationKIz = 0;
    rotationKFF = 0.001; // 0.00007
    rotationKMaxOutput = 1;
    rotationKMinOutput = -1;
    rotationMaxRPM = 5676; // 5676 for ne0, 11000 for neo 550
    rotationDefaultKG = 0.64;
    rotationExtendedKG = 0;


    // arm extension
    extensionKP = 0.0000;
    extensionKI = 0;
    extensionKD = 0;
    extensionKIz = 0;
    extensionKFF = 0.0001; // 0.00007
    extensionKMaxOutput = 1;
    extensionKMinOutput = -1;
    extensionMaxRPM = 11000; // 5676 for ne0, 11000 for neo 550

    // Smart Motion Coefficients

    // ARm rotation
    rotationMaxVel = 4600; // rpm
    rotationMaxAcc = 4600;

    // arm extension
    extensionMaxVel = 8700; // rpm
    extensionMaxAcc = 8700;

    // set PID coefficients

    // Arm rotation
    m_rotationPidController.setP(rotationKP);
    m_rotationPidController.setI(rotationKI);
    m_rotationPidController.setD(rotationKD);
    m_rotationPidController.setIZone(rotationKIz);
    m_rotationPidController.setFF(rotationKFF);
    m_rotationPidController.setOutputRange(rotationKMinOutput, rotationKMaxOutput);

    // Arm extension
    m_extensionPidController.setP(extensionKP);
    m_extensionPidController.setI(extensionKI);
    m_extensionPidController.setD(extensionKD);
    m_extensionPidController.setIZone(extensionKIz);
    m_extensionPidController.setFF(extensionKFF);
    m_extensionPidController.setOutputRange(extensionKMinOutput, extensionKMaxOutput);


    // feed forward
    m_rotationDefaultFeedforward = new ArmFeedforward(0, rotationDefaultKG, 0);

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
    m_extensionPidController.setSmartMotionMaxVelocity(extensionMaxVel, smartMotionSlot);
    m_extensionPidController.setSmartMotionMinOutputVelocity(extensionMinVel, smartMotionSlot);
    m_extensionPidController.setSmartMotionMaxAccel(extensionMaxAcc, smartMotionSlot);
    extensionAllowedErr = 1;
    m_extensionPidController.setSmartMotionAllowedClosedLoopError(extensionAllowedErr, smartMotionSlot);


    // display PID coefficients on SmartDashboard
    SmartDashboard.putNumber("P Gain", extensionKP);
    SmartDashboard.putNumber("I Gain", extensionKI);
    SmartDashboard.putNumber("D Gain", extensionKD);
    SmartDashboard.putNumber("I Zone", extensionKIz);
    SmartDashboard.putNumber("Feed Forward", extensionKFF);
    //SmartDashboard.putNumber("G Gain", rotationExtendedKG);
    // SmartDashboard.putNumber("Max Output", kMaxOutput);
    // SmartDashboard.putNumber("Min Output", kMinOutput);

    // display Smart Motion coefficients
    // SmartDashboard.putNumber("Max Velocity", maxVel);
    // SmartDashboard.putNumber("Min Velocity", minVel);
    // SmartDashboard.putNumber("Max Acceleration", maxAcc);
    // SmartDashboard.putNumber("Allowed Closed Loop Error", allowedErr);
    // SmartDashboard.putNumber("Set Position", 0);
    // SmartDashboard.putNumber("Set Velocity", 0);
    SmartDashboard.putNumber("Set Position", 0);

    SmartDashboard.putNumber("Velocity", m_extensionEncoder.getVelocity());
    SmartDashboard.putNumber("Current rotations", m_extensionEncoder.getPosition());
    SmartDashboard.putNumber("Output", m_extensionMotor.getAppliedOutput());
    // m_extensionTab.addDouble("P Gain", () -> {return extensionKP;});
    // m_extensionTab.addDouble("I Gain", () -> {return extensionKI;});
    // m_extensionTab.addDouble("D Gain", () -> {return extensionKD;});
    // m_extensionTab.addDouble("I Zone", () -> {return extensionKIz;});
    // m_extensionTab.addDouble("FF", () -> {return extensionKFF;});
    // m_extensionTab.addDouble("Set Position", () -> {return extensionSetPoint;});
    // m_extensionTab.addDouble("Velocity", () -> {return m_extensionEncoder.getVelocity();});
    // m_extensionTab.addDouble("Current Rotations", () -> {return m_extensionEncoder.getPosition();});
    // m_extensionTab.addDouble("Output", () -> {return m_extensionMotor.getAppliedOutput();});

    // m_rotationTab.addDouble("P", () -> {return rotationKP;});
    // m_rotationTab.addDouble("I", () -> {return rotationKI;});
    // m_rotationTab.addDouble("D", () -> {return rotationKD;});
    // m_rotationTab.addDouble("IZone", () -> {return rotationKIz;});
    // m_rotationTab.addDouble("FF", () -> {return rotationKFF;});
    // m_rotationTab.addDouble("Default KG", () -> {return rotationDefaultKG;});
    // m_rotationTab.addDouble("Extended KG", () -> {return rotationExtendedKG;});
    // m_rotationTab.addDouble("Set Position", () -> {return rotationSetPoint;});
    // m_rotationTab.addDouble("Velocity", () -> {return m_rotationEncoder.getVelocity();});
    // m_rotationTab.addDouble("Current Rotations", () -> {return m_rotationEncoder.getPosition();});
    // m_rotationTab.addDouble("Output", () -> {return m_rotationMotor.getAppliedOutput();});

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
    double P = SmartDashboard.getNumber("P Gain", 0);
    double I = SmartDashboard.getNumber("I Gain",0);
    double D = SmartDashboard.getNumber("D Gain", 0);
    double Iz = SmartDashboard.getNumber("I Zone", 0);
    double FF = SmartDashboard.getNumber("Feed Forward", 0);
    double KG = SmartDashboard.getNumber("G Gain", 0);

    // double rP = m_rotationTable.getEntry("P").getDouble(0);
    // double rI = m_rotationTable.getEntry("I").getDouble(0);
    // double rD = m_rotationTable.getEntry("D").getDouble(0);
    // double rIz = m_rotationTable.getEntry("IZone").getDouble(0);
    // double rFF = m_rotationTable.getEntry("FF").getDouble(0);

    if (rotationKP != P) { m_rotationPidController.setP(P); rotationKP = P;}
    if (rotationKI != I) { m_rotationPidController.setI(I); rotationKI = I;}
    if (rotationKD != D) { m_rotationPidController.setD(D); rotationKD = D;}
    if (rotationKIz != Iz) { m_rotationPidController.setIZone(Iz); rotationKIz = Iz;}
    if (rotationKFF != FF) { m_rotationPidController.setFF(FF); rotationKFF = FF;}
    if (rotationExtendedKG != KG) { rotationExtendedKG = KG;}

    // if (rotationKP != rP) { m_rotationPidController.setP(rP); rotationKP = rP;}
    // if (rotationKI != rI) { m_rotationPidController.setI(rI); rotationKI = rI;}
    // if (rotationKD != rD) { m_rotationPidController.setD(rD); rotationKD = rD;}
    // if (rotationKIz != rIz) { m_rotationPidController.setIZone(rIz); rotationKIz = rIz;}
    // if (rotationKFF != rFF) { m_rotationPidController.setFF(rFF); rotationKFF = rFF;}

    // double extensionetPoint = SmartDashboard.getNumber("Set Position", 0);
    // System.out.println(extensionSetPoint);

    double extensionSetPoint, rotationSetPoint, rotationDefaultFeedForward, rotationExtendedFeedForward;
    // extensionSetPoint = m_extensionTable.getEntry("Set Position").getDouble(0);
    //rotationSetPoint = SmartDashboard.getNumber("Set Position", 0);

    //double ffDefaultCalc = (rotationSetPoint - m_horizontalRotations) / m_rotationsPerRadian;
    //rotationDefaultFeedForward = m_rotationDefaultFeedforward.calculate(0, 0);

    // double ffExtendedCalc = (extensionSetPoint);

    // m_rotationPidController.setReference(0, CANSparkMax.ControlType.kSmartMotion, 0, rotationDefaultKG + rotationExtendedKG, ArbFFUnits.kVoltage);
    //System.out.println(m_rotationMotor.get());
    //System.out.println(rotationDefaultKG + rotationExtendedKG);
    // System.out.println(m_rotationMotor.get);

    extensionSetPoint = SmartDashboard.getNumber("Set Position", 0);
    
    m_extensionPidController.setReference(extensionSetPoint, CANSparkMax.ControlType.kSmartMotion, 0);

    m_rotationPidController.setReference(0, CANSparkMax.ControlType.kSmartMotion, 0, rotationDefaultKG, ArbFFUnits.kVoltage);

    System.out.println("Vel: " + m_extensionEncoder.getVelocity());

    SmartDashboard.putNumber("Velocity", m_extensionEncoder.getVelocity());
    SmartDashboard.putNumber("Current rotations", m_extensionEncoder.getPosition());
    SmartDashboard.putNumber("Output", m_extensionMotor.getAppliedOutput());
  
  }

  @Override
  public void testPeriodic() {
    m_rotationMotor.set(deadband(controller.getRightY(), 0.1) * -0.4);
    m_extensionMotor.set(deadband(controller.getLeftY(), 0.1) * -0.4);

    if (controller.getAButtonPressed()) {
      m_rotationEncoder.setPosition(0);
    }

    if (controller.getBButtonPressed()) {
      m_extensionEncoder.setPosition(0);
    }

    SmartDashboard.putNumber("Velocity", m_extensionEncoder.getVelocity());
    SmartDashboard.putNumber("Current rotations", m_extensionEncoder.getPosition());
    SmartDashboard.putNumber("Output", m_extensionMotor.getAppliedOutput());
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
