package com.team2357.frc2023.commands.scoring.cone;

import org.littletonrobotics.junction.Logger;

import com.team2357.frc2023.Constants;
import com.team2357.frc2023.state.RobotState;
import com.team2357.frc2023.subsystems.ArmExtensionSubsystem;
import com.team2357.frc2023.subsystems.ArmRotationSubsystem;
import com.team2357.frc2023.subsystems.WristSubsystem;
import com.team2357.frc2023.commands.armrotation.ArmRotateToPositionCommand;
import com.team2357.frc2023.commands.everybot.ClawReleaseConeCommand;
import com.team2357.frc2023.commands.intake.IntakeRollerRunCommand;
import com.team2357.frc2023.commands.intake.WinchRotateToPositionCommand;
import com.team2357.frc2023.commands.state.SetRobotStateCommand;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.ParallelDeadlineGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class ConeLowPrePoseCommand extends SequentialCommandGroup {
    public ConeLowPrePoseCommand() {
        super(
                new ParallelCommandGroup(
                        new SetRobotStateCommand(RobotState.State.ROBOT_PRE_SCORE_CONE_LOW),
                        new WinchRotateToPositionCommand(Constants.INTAKE_ARM.AUTO_SCORE_LOW_ROTATIONS),
                        new ArmRotateToPositionCommand(Constants.ARM_ROTATION.SCORE_CONE_LOW_ROTATIONS),
                        new ParallelDeadlineGroup(
                                new WaitCommand(1),
                                new ClawReleaseConeCommand(),
                                new IntakeRollerRunCommand())),
                new ArmRotateToPositionCommand(Constants.ARM_ROTATION.RETRACTED_ROTATIONS),
                new InstantCommand(() -> Logger.getInstance().recordOutput("Cone Low prePose",
                        new double[] { ArmRotationSubsystem.getInstance().getMotorRotations(),
                                ArmExtensionSubsystem.getInstance().getMotorRotations(),
                                WristSubsystem.getInstance().getRotations() })));
    }
}
