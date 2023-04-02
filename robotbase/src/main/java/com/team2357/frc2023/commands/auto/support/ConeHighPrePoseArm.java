package com.team2357.frc2023.commands.auto.support;

import com.team2357.frc2023.Constants;
import com.team2357.frc2023.commands.armextension.ArmExtendToPositionCommand;
import com.team2357.frc2023.commands.armrotation.ArmRotateToPositionCommand;
import com.team2357.frc2023.commands.armrotation.ArmWaitForGreaterThanPositionCommand;
import com.team2357.frc2023.commands.everybot.WristRotateToPositionCommand;
import com.team2357.frc2023.commands.state.SetRobotStateCommand;
import com.team2357.frc2023.state.RobotState;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class ConeHighPrePoseArm extends ParallelCommandGroup {
    public ConeHighPrePoseArm() {
        super(
            new SetRobotStateCommand(RobotState.State.ROBOT_PRE_SCORE_CONE_HIGH),

            // Arm
            new ArmRotateToPositionCommand(Constants.ARM_ROTATION.SCORE_CONE_HIGH_ROTATIONS),

            // Wrist
            new SequentialCommandGroup(
                new ArmWaitForGreaterThanPositionCommand(Constants.ARM_ROTATION.WRIST_CLEAR_INTAKE_ROTATIONS),
                new WristRotateToPositionCommand(Constants.WRIST.SCORE_CONE_HIGH_ROTATIONS)
            ),

            // Extension
            new SequentialCommandGroup(
                new ArmWaitForGreaterThanPositionCommand(Constants.ARM_ROTATION.EXTENSION_HIGH_START_ROTATIONS),
                new ArmExtendToPositionCommand(Constants.ARM_EXTENSION.AUTO_SCORE_CONE_HIGH_ROTATIONS + 1)
            )
        );
    }
}
