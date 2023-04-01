package com.team2357.frc2023.commands.scoring.cone;

import com.team2357.frc2023.Constants;
import com.team2357.frc2023.state.RobotState;
import com.team2357.frc2023.commands.armrotation.ArmRotateToPositionCommand;
import com.team2357.frc2023.commands.armrotation.ArmWaitForGreaterThanPositionCommand;
import com.team2357.frc2023.commands.everybot.ClawHoldConeCommand;
import com.team2357.frc2023.commands.everybot.ClawIntakeConeCommand;
import com.team2357.frc2023.commands.everybot.WristRotateToPositionCommand;
import com.team2357.frc2023.commands.intake.IntakeArmStowCommand;
import com.team2357.frc2023.commands.intake.IntakeRollerReverseCommand;
import com.team2357.frc2023.commands.intake.WinchRotateToPositionCommand;
import com.team2357.frc2023.commands.state.SetRobotStateCommand;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class ConeMidPrePoseCommand extends ParallelCommandGroup {
    public ConeMidPrePoseCommand() {
        super(
            new SetRobotStateCommand(RobotState.State.ROBOT_PRE_SCORE_CONE_MID),

            // Claw Rollers
            new SequentialCommandGroup(
                new ClawIntakeConeCommand(),
                new ClawHoldConeCommand()
            ),

            // Intake Rollers
            new IntakeRollerReverseCommand().withTimeout(1),

            // Intake Arm
            new SequentialCommandGroup(
                new WaitCommand(0.5),
                new WinchRotateToPositionCommand(Constants.INTAKE_ARM.INTAKE_HANDOFF_WINCH_ROTATIONS),
                new WaitCommand(0.25),
                new IntakeArmStowCommand()
            ),

            // Arm
            new ArmRotateToPositionCommand(Constants.ARM_ROTATION.SCORE_CONE_MID_ROTATIONS),

            // Wrist
            new SequentialCommandGroup(
                new ArmWaitForGreaterThanPositionCommand(Constants.ARM_ROTATION.WRIST_CLEAR_INTAKE_ROTATIONS),
                new WristRotateToPositionCommand(Constants.WRIST.SCORE_CONE_MID_ROTATIONS)
            )
        );
    }
}
