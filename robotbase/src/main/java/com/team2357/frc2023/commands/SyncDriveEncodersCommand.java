package com.team2357.frc2023.commands;

import com.team2357.frc2023.Constants;
import com.team2357.frc2023.subsystems.SwerveDriveSubsystem;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class SyncDriveEncodersCommand extends CommandBase {

    double m_startMillis;
    SwerveDriveSubsystem m_swerveSub = SwerveDriveSubsystem.getInstance();

    public SyncDriveEncodersCommand() {
        addRequirements(m_swerveSub);
    }

    @Override
    public void initialize() {
        m_startMillis = System.currentTimeMillis();
    }

    @Override
    public void execute() {
        m_swerveSub.syncEncoders();
    }

    @Override
    public boolean isFinished() {
        return m_swerveSub.checkEncodersSynced() ||
                System.currentTimeMillis() - m_startMillis > Constants.DRIVE.SYNC_ENCODER_LIMIT_MS;
    }

    @Override
    public void end(boolean interrupted) {
        if (m_swerveSub.getIsEncodersSynced()) {

            String successMsg = "Swerve synced in " + (System.currentTimeMillis() - m_startMillis) + " millisecods.";
            System.out.println(successMsg);

            return;
        }

        DriverStation.reportError(
                "***************************************************\nSWERVE COULD NOT ZERO\n***************************************************",
                false);
    }

    @Override
    public boolean runsWhenDisabled() {
        return true;
    }
}
