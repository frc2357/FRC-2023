package com.team2357.frc2023.commands.intake;

import org.littletonrobotics.junction.Logger;

import com.team2357.frc2023.Constants;
import com.team2357.frc2023.subsystems.IntakeArmSubsystem;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class WinchAmpZeroCommand extends CommandBase{
    
    public WinchAmpZeroCommand() {
        addRequirements(IntakeArmSubsystem.getInstance());
    }
    
    @Override
    public void initialize() {
        IntakeArmSubsystem.getInstance().manualStow(Constants.INTAKE_ARM.WINCH_AMP_ZERO_PERCENT_OUTPUT);
    }
    
    @Override
    public boolean isFinished() {
        return IntakeArmSubsystem.getInstance().getAmps() >= Constants.INTAKE_ARM.WINCH_AMP_ZERO_MAX_AMPS;
    }
    
    @Override
    public void end(boolean interrupted) {
        IntakeArmSubsystem.getInstance().stopWinchMotor();

        if (!interrupted) {
            Logger.getInstance().recordOutput("Intake Winch Zero", "success");
            IntakeArmSubsystem.getInstance().resetEncoders();
        } else {
            DriverStation.reportError("Intake Winch Zero interrupted!", false);
            Logger.getInstance().recordOutput("Intake Winch Zero", "interrupted");
        }
    }
}
