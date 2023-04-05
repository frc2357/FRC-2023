package com.team2357.frc2023.commands.scoring.cube;


import com.team2357.frc2023.Constants;
import com.team2357.frc2023.commands.armrotation.ArmRotateToPositionCommand;
import com.team2357.frc2023.commands.everybot.ClawHoldCubeCommand;
import com.team2357.frc2023.commands.everybot.ClawIntakeCubeCommand;
import com.team2357.frc2023.commands.intake.IntakeRollerReverseCommand;
import com.team2357.frc2023.commands.state.SetRobotStateCommand;
import com.team2357.frc2023.state.RobotState;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class CubeLowPrePoseCommand extends ParallelCommandGroup {
    public CubeLowPrePoseCommand() {
        super(
            new SetRobotStateCommand(RobotState.State.ROBOT_PRE_SCORE_CUBE_LOW),

            // Claw Rollers
            new SequentialCommandGroup(
                new ClawIntakeCubeCommand(),
                new ClawHoldCubeCommand()
            ),

            // Intake Rollers
            new IntakeRollerReverseCommand().withTimeout(1.0),

            // Arm
            new ArmRotateToPositionCommand(Constants.ARM_ROTATION.SCORE_LOW_ROTATIONS)
        );
    }
    
}
