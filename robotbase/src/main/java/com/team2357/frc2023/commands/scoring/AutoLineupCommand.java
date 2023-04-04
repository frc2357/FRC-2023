package com.team2357.frc2023.commands.scoring;

import com.team2357.frc2023.Constants;
import com.team2357.frc2023.commands.auto.DriveToPoseCommand;
import com.team2357.frc2023.controls.GunnerControls;
import com.team2357.frc2023.networktables.Buttonboard;
import com.team2357.frc2023.subsystems.DualLimelightManagerSubsystem;
import com.team2357.frc2023.util.Utility;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class AutoLineupCommand extends CommandBase {

    DriveToPoseCommand m_driveToPose;
    Command m_preposeCommand;
    Pose2d m_targetPose;
    Pose2d m_initialPose;

    private boolean hasDriveToPoseCompleted;

    public AutoLineupCommand() {
        m_driveToPose = new DriveToPoseCommand(() -> {
            return m_initialPose;
        }, () -> {
            return m_targetPose;
        });
    }

    @Override
    public void initialize() {
        int targetCol = Buttonboard.getInstance().getColValue();
        int targetRow = Buttonboard.getInstance().getRowValue();
        Buttonboard.Gamepiece targetGamepiece = Buttonboard.getInstance().getGamepieceValue();

        m_targetPose = Utility.gridColumnToTargetPose(targetCol);

        m_preposeCommand = Utility.getPreposeCommand(targetRow, targetCol, targetGamepiece);

        System.out.println("Auto line col: " + targetCol + " row: " + targetRow + " game: " + targetGamepiece);

        if (m_targetPose == null) {
            this.cancel();
            return;
        }

        hasDriveToPoseCompleted = false;

        System.out.println("Target pose: " + m_targetPose.toString());
    }

    @Override
    public void execute() {
        if (!hasDriveToPoseCompleted && !m_driveToPose.isRunning()) {
            Pose2d visionPose = DualLimelightManagerSubsystem.getInstance().getAveragePose();
            if (visionPose != null && visionPose.getTranslation()
                    .getDistance(m_targetPose.getTranslation()) <= Constants.DRIVE.AUTO_LINEUP_RANGE_METERS) {
                GunnerControls.getInstance().setRumble(RumbleType.kBothRumble, 0.0);
                m_initialPose = visionPose;
                m_driveToPose.schedule();
                // m_preposeCommand.schedule();
            } else {
                GunnerControls.getInstance().setRumble(RumbleType.kBothRumble, Constants.CONTROLLER.RUMBLE_INTENSITY);
            }
        }

        if (m_driveToPose.atGoal()) {
            hasDriveToPoseCompleted = true;
            m_driveToPose.cancel();
        }
    }

    @Override
    public boolean isFinished() {
        return hasDriveToPoseCompleted && m_preposeCommand.isFinished();
    }

    @Override
    public void end(boolean interrupted) {

        if (m_driveToPose.isScheduled()) {
            m_driveToPose.cancel();
        }

        if (m_preposeCommand != null && m_preposeCommand.isScheduled()) {
            m_preposeCommand.cancel();
        }

        GunnerControls.getInstance().setRumble(RumbleType.kBothRumble, 0.0);
    }
}
