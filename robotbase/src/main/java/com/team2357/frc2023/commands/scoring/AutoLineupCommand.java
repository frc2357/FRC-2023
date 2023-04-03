package com.team2357.frc2023.commands.scoring;

import com.team2357.frc2023.Constants;
import com.team2357.frc2023.commands.auto.DriveToPoseCommand;
import com.team2357.frc2023.controls.GunnerControls;
import com.team2357.frc2023.networktables.Buttonboard;
import com.team2357.frc2023.subsystems.DualLimelightManagerSubsystem;
import com.team2357.frc2023.util.Utility;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class AutoLineupCommand extends CommandBase {

    DriveToPoseCommand m_driveToPose;
    Command m_preposeCommand;
    Pose2d m_targetPose;

    @Override
    public void initialize() {
        int targetCol = Buttonboard.getInstance().getColValue();
        int targetRow = Buttonboard.getInstance().getRowValue();
        Buttonboard.Gamepiece targetGamepiece = Buttonboard.getInstance().getGamepieceValue();
        m_targetPose = Utility.gridColumnToTargetPose(targetCol);
        m_driveToPose = null;
        m_preposeCommand = Utility.getPreposeCommand(targetRow, targetCol, targetGamepiece);

    }

    @Override
    public void execute() {
        if (m_driveToPose == null) {
            Pose2d visionPose = DualLimelightManagerSubsystem.getInstance().getAveragePose();
            if (visionPose != null) {
                if (visionPose.getTranslation().getDistance(m_targetPose.getTranslation()) <= Constants.DRIVE.AUTO_LINEUP_RANGE_METERS) {

                    m_driveToPose = new DriveToPoseCommand(visionPose, m_targetPose);
                    m_driveToPose.schedule();
                    m_preposeCommand.schedule();
                }
            } else {
               GunnerControls.getInstance().setRumble(RumbleType.kBothRumble, Constants.CONTROLLER.RUMBLE_INTENSITY);
            }
        }

    }

    @Override
    public boolean isFinished() {
        return m_driveToPose != null && m_driveToPose.atGoal() && m_preposeCommand.isFinished();
    }

    @Override
    public void end(boolean interrupted) {

        if (m_driveToPose != null) {
            m_driveToPose.cancel();
        }
        if (m_preposeCommand != null) {
            m_preposeCommand.cancel();
        }

        GunnerControls.getInstance().setRumble(RumbleType.kBothRumble, 0.0);
    }
}
