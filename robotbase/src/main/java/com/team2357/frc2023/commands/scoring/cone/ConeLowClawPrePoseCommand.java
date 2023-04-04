package com.team2357.frc2023.commands.scoring.cone;

import com.team2357.frc2023.Constants;
import com.team2357.frc2023.commands.armrotation.ArmRotateToPositionCommand;
import com.team2357.frc2023.commands.everybot.ClawHoldConeCommand;
import com.team2357.frc2023.commands.everybot.ClawIntakeConeCommand;
import com.team2357.frc2023.commands.intake.IntakeRollerReverseCommand;
import com.team2357.frc2023.commands.state.SetRobotStateCommand;
import com.team2357.frc2023.state.RobotState;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class ConeLowClawPrePoseCommand extends SequentialCommandGroup {
    public ConeLowClawPrePoseCommand() {
        super(
            new SetRobotStateCommand(RobotState.State.ROBOT_PRE_SCORE_CONE_LOW),

            // Claw Rollers
            new SequentialCommandGroup(
                new ClawIntakeConeCommand(),
                new ClawHoldConeCommand()
            ),

            // Intake Rollers
            new IntakeRollerReverseCommand().withTimeout(1),

            // Arm
            new ArmRotateToPositionCommand(Constants.ARM_ROTATION.SCORE_LOW_ROTATIONS)
        );
    }
    
}
