// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package com.team2357.frc2023;


import org.littletonrobotics.junction.Logger;

import com.team2357.frc2023.commands.drive.DefaultDriveCommand;
import com.team2357.frc2023.controls.GunnerControls;
import com.team2357.frc2023.controls.SwerveDriveControls;
import com.team2357.frc2023.shuffleboard.AutoCommandChooser;
import com.team2357.frc2023.state.LEDState;
import com.team2357.frc2023.state.RobotState;
import com.team2357.frc2023.subsystems.DualLimelightManagerSubsystem;
import com.team2357.frc2023.subsystems.SubsystemFactory;
import com.team2357.frc2023.subsystems.SwerveDriveSubsystem;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.Command;

/**
 * This class is where the bulk of the robot should be declared. Since
 * Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in
 * the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of
 * the robot (including
 * subsystems, commands, and button mappings) should be declared here.
 */
public class RobotContainer {
  // The robot's subsystems and commands are defined here...
  private final SwerveDriveSubsystem m_drivetrainSubsystem;
  private final Compressor m_compressor;

  private AutoCommandChooser m_autoCommandChooser;

  /**
   * The container for the robot. Contains subsystems, OI devices, and commands.
   */
  public RobotContainer() {
    // Init LEDs
    new LEDState(Constants.GAMEPIECE_LED.PWM_PORT);

    RobotState.robotInit();

    // Create subsystems
    SubsystemFactory subsystemFactory = new SubsystemFactory();
    subsystemFactory.CreateIntakeRollerSubsystem();
    subsystemFactory.CreateIntakeArmSubsystem();
    subsystemFactory.CreateEverybotClawSubsystem();
    subsystemFactory.CreateEverybotWristSubsystem();
    subsystemFactory.CreateArmRotationSubsystem();
    subsystemFactory.CreateArmExtensionSubsystem();
    subsystemFactory.CreateDualLimelightManagerSubsystem();

    m_drivetrainSubsystem = subsystemFactory.CreateSwerveDriveSubsystem();

    // Create gunner controls and drive controls
    SwerveDriveControls driveControls = new SwerveDriveControls(
        new XboxController(Constants.CONTROLLER.DRIVE_CONTROLLER_PORT), Constants.CONTROLLER.DRIVE_CONTROLLER_DEADBAND);
    GunnerControls gunnerControls = new GunnerControls(new XboxController(Constants.CONTROLLER.GUNNER_CONTROLLER_PORT));

    // Set default commands
    m_drivetrainSubsystem.setDefaultCommand(new DefaultDriveCommand(
        m_drivetrainSubsystem,
        driveControls));

    // Set default pipeline
    DualLimelightManagerSubsystem.getInstance().setAprilTagPipelineActive();

    // Setup compressor
    m_compressor = new Compressor(Constants.CAN_ID.PNEUMATICS_HUB_ID, PneumaticsModuleType.REVPH);
    m_compressor.enableAnalog(Constants.COMPRESSOR.MIN_PRESSURE_PSI,
        Constants.COMPRESSOR.MAX_PRESSURE_PSI);
    // m_compressor.disable();

    m_autoCommandChooser = new AutoCommandChooser();

    // PathPlannerServer.startServer(5811); // 5811 = port number. adjust this
    // according to your needs
  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    return m_autoCommandChooser.getSelectedAutoCommand();
  }

  public void logPressure() {
    Logger.getInstance().recordOutput("Pressure", m_compressor.getPressure());
  }
}
