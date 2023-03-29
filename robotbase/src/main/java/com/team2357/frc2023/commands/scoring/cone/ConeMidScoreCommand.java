package com.team2357.frc2023.commands.scoring.cone;

import com.team2357.frc2023.Constants;
import com.team2357.frc2023.commands.armrotation.ArmRotateToPositionCommand;
import com.team2357.frc2023.commands.everybot.ClawReleaseConeCommand;
import com.team2357.frc2023.commands.everybot.WristRotateToPositionCommand;
import com.team2357.frc2023.commands.state.SetRobotStateCommand;
import com.team2357.frc2023.state.RobotState;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class ConeMidScoreCommand extends SequentialCommandGroup  {
    public ConeMidScoreCommand() {
        super(
            new ClawReleaseConeCommand().withTimeout(0.5),
            new SetRobotStateCommand(RobotState.State.ROBOT_STOWED_EMPTY),
            new ParallelCommandGroup(
                new WristRotateToPositionCommand(Constants.WRIST.WRIST_RETRACT_ROTATIONS),
                new SequentialCommandGroup(
                    new WaitCommand(0.1),
                    new ArmRotateToPositionCommand(Constants.ARM_ROTATION.RETRACTED_ROTATIONS)
                )
            )
        );
    }
}
