package com.team2357.lib.commands;

import com.team2357.lib.controllers.DriverControls;
import com.team2357.lib.subsystems.drive.FalconDriveSubsystem;

public class DriveVelocityCommand extends CommandLoggerBase {

  private DriverControls m_driverController;

  public DriveVelocityCommand(DriverControls driverController) {
    m_driverController = driverController;
    addRequirements(FalconDriveSubsystem.getInstance());
  }

  @Override
  public void execute() {
    FalconDriveSubsystem
      .getInstance()
      .driveVelocity(
        m_driverController.getSpeed(),
        m_driverController.getTurn()
      );
  }

  @Override
  public boolean isFinished() {
    return false;
  }
}
