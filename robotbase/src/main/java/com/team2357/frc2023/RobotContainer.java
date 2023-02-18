// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package com.team2357.frc2023;

import com.team2357.frc2023.commands.drive.DefaultDifferentialDriveCommand;
import com.team2357.frc2023.commands.drive.DefaultSwerveDriveCommand;
import com.team2357.frc2023.controls.SwerveDriveControls;
import com.team2357.frc2023.shuffleboard.AutoCommandChooser;
import com.team2357.frc2023.subsystems.IntakeRollerSubsystem;
import com.team2357.frc2023.subsystems.SubsystemFactory;
import com.team2357.frc2023.subsystems.SwerveDriveSubsystem;
import com.team2357.frc2023.util.AvailableTrajectories;
import com.team2357.frc2023.util.AvailableTrajectoryCommands;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.button.Trigger;

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

  private final XboxController m_controller = new XboxController(0);

  /**
   * The container for the robot. Contains subsystems, OI devices, and commands.
   */
  public RobotContainer() {

    // Create subsystems
    SubsystemFactory subsystemFactory = new SubsystemFactory();
    subsystemFactory.CreateIntakeRollerSubsystem();
    subsystemFactory.CreateIntakeArmSubsystem();
    // subsystemFactory.CreateClawSubsystem();
    // subsystemFactory.CreateWristSubsystem();
    // subsystemFactory.CreateArmRotationSubsystem();
    // subsystemFactory.CreateArmExtensionSubsystem();

    m_drivetrainSubsystem = subsystemFactory.CreateSwerveDriveSubsystem();

    m_drivetrainSubsystem.setDefaultCommand(new DefaultSwerveDriveCommand(
        m_drivetrainSubsystem,
        new SwerveDriveControls(m_controller,
            Constants.CONTROLLER.DRIVE_CONTROLLER_DEADBAND)));
    // m_drivetrainSubsystem.setDefaultCommand(new
    // DefaultDifferentialDriveCommand(m_drivetrainSubsystem,
    // new SwerveDriveControls(m_controller,
    // Constants.CONTROLLER.DRIVE_CONTROLLER_DEADBAND)));

    // Setup compressor
    m_compressor = new Compressor(Constants.CAN_ID.PNEUMATICS_HUB_ID, PneumaticsModuleType.REVPH);
    m_compressor.enableAnalog(Constants.COMPRESSOR.MIN_PRESSURE_PSI, Constants.COMPRESSOR.MAX_PRESSURE_PSI);

    // Build trajectory paths
    AvailableTrajectories.generateTrajectories();
    AvailableTrajectoryCommands.generateTrajectories();

    // Configure the button bindings
    configureButtonBindings();

    // Configure Shuffleboard
    configureShuffleboard();
  }

  /**
   * This method should set up the shuffleboard
   */
  public void configureShuffleboard() {
    m_autoCommandChooser = new AutoCommandChooser();
  }

  /**
   * Use this method to define your button->command mappings. Buttons can be
   * created by
   * instantiating a {@link GenericHID} or one of its subclasses ({@link
   * edu.wpi.first.wpilibj.Joystick} or {@link XboxController}), and then passing
   * it to a {@link
   * edu.wpi.first.wpilibj2.command.button.JoystickButton}.
   */
  private void configureButtonBindings() {
    // Back button zeros the gyroscope
    new Trigger(m_controller::getBackButton).onTrue(new InstantCommand(() -> {
      m_drivetrainSubsystem.zeroGyroscope();
    }));
    // No requirements because we don't need to interrupt anything
  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    int auto = 1;

    switch (auto) {
      case -1:
        return AvailableTrajectories.lineTrajectory;
      default:
        return m_autoCommandChooser.generateCommand();
    }
  }

}
