package com.team2357.frc2023.commands.scoring.cone;

import com.team2357.frc2023.commands.controller.RumbleCommand;
import com.team2357.frc2023.commands.intake.IntakeArmStowCommand;
import com.team2357.frc2023.commands.intake.IntakeRollerReverseCommand;
import com.team2357.frc2023.commands.state.SetRobotStateCommand;
import com.team2357.frc2023.controls.ControllerManager;
import com.team2357.frc2023.state.RobotState;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class ConeLowScoreCommand extends SequentialCommandGroup {
    public ConeLowScoreCommand() {
        super(
            new IntakeRollerReverseCommand().withTimeout(0.75),
            new SetRobotStateCommand(RobotState.State.ROBOT_STOWED_EMPTY),
            RumbleCommand.createRumbleCommand(ControllerManager.getInstance().getDriveController(), 0.5),
            new IntakeArmStowCommand()
        );
    }
}
