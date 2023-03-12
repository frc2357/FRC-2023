package com.team2357.frc2023.commands.armextension;

import org.littletonrobotics.junction.Logger;

import com.team2357.frc2023.Constants;
import com.team2357.frc2023.subsystems.ArmExtensionSubsystem;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class ArmExtendAmpZeroCommand extends CommandBase{
    public ArmExtendAmpZeroCommand() {
        addRequirements(ArmExtensionSubsystem.getInstance());
    }
    
    @Override
    public void initialize() {
        ArmExtensionSubsystem.getInstance().manualExtend(Constants.ARM_EXTENSION.ARM_EXTENSION_AMP_ZERO_PERCENT_OUTPUT);
    }
    
    @Override
    public boolean isFinished() {
        return Math.abs(ArmExtensionSubsystem.getInstance().getAmps()) >= Constants.ARM_EXTENSION.ARM_EXTENSION_AMP_ZERO_MAX_AMPS;
    }
    
    @Override
    public void end(boolean interrupted) {
        ArmExtensionSubsystem.getInstance().stopMotor();
        if(interrupted){
            DriverStation.reportError("Amp Zeroing did not finish in time! Arm Extension not zeroed.",false);
            Logger.getInstance().recordOutput("Amp Zero fail", true);
        }
        else{
            Logger.getInstance().recordOutput("Amp Zero fail", false);
        ArmExtensionSubsystem.getInstance().resetEncoder();
        }
    }
}
