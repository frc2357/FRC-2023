package com.team2357.frc2023.commands.scoring.cone;

import com.team2357.frc2023.state.RobotState;
import com.team2357.frc2023.commands.intake.IntakeArmStowCommand;
import com.team2357.frc2023.commands.intake.IntakeRollerReverseCommand;
import com.team2357.frc2023.commands.state.SetRobotStateCommand;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class ConeLowScoreCommand extends SequentialCommandGroup {
    public ConeLowScoreCommand() {
        super(
            new IntakeRollerReverseCommand().withTimeout(0.75),
            new IntakeArmStowCommand(),
            new SetRobotStateCommand(RobotState.State.ROBOT_STOWED_EMPTY)
        );
    }
}
