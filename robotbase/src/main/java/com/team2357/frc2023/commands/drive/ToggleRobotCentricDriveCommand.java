package com.team2357.frc2023.commands.drive;

import com.team2357.frc2023.state.RobotState;
import com.team2357.frc2023.state.RobotState.DriveControlState;
import com.team2357.frc2023.subsystems.DualLimelightManagerSubsystem;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class ToggleRobotCentricDriveCommand extends CommandBase {
    @Override
    public void initialize() {

        if (RobotState.isFieldRelative()) {
            DualLimelightManagerSubsystem.getInstance().setHumanPipelineActive();
            RobotState.setDriveControlState(DriveControlState.ROBOT_CENTRIC);
        } else {
            DualLimelightManagerSubsystem.getInstance().setAprilTagPipelineActive();
            RobotState.setDriveControlState(DriveControlState.FIELD_RELATIVE);
        }
    }

    @Override
    public boolean isFinished() {
        return true;
    }
}
