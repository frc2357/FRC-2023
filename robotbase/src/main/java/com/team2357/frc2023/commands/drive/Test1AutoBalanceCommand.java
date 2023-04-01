package com.team2357.frc2023.commands.drive;

import com.team2357.frc2023.Constants;
import com.team2357.frc2023.subsystems.SwerveDriveSubsystem;
import com.team2357.lib.commands.CommandLoggerBase;

public class Test1AutoBalanceCommand extends CommandLoggerBase {

    // Drive less proportional to difference in previous angle and current angle

    private SwerveDriveSubsystem m_swerve;
     
    private double balanceStart = -1;
    private double prevAngle = Double.NaN;
    private double yaw, direction, angle, error, power;
    
    public Test1AutoBalanceCommand() {
        m_swerve = SwerveDriveSubsystem.getInstance();
        addRequirements(m_swerve);
    }

    @Override
    public void initialize() {
        m_swerve.zero();
        prevAngle = m_swerve.getYaw0To360(); 
    }

    @Override
    public void execute() {
        yaw = m_swerve.getYaw0To360();
        
        angle = m_swerve.getTilt(yaw);
        direction = m_swerve.getDirection(yaw);

        if (angle <= Constants.DRIVE.BALANCE_FULL_TILT_DEGREES) {
            // original:
			// error = Math.copySign(Constants.DRIVE.BALANCE_LEVEL_DEGREES + Math.abs(angle), angle);
            error = Math.copySign(Math.abs(angle), angle);
            power = Math.min(Math.abs(Constants.DRIVE.BALANCE_KP * error), Constants.DRIVE.BALANCE_MAX_POWER);
            power = Math.copySign(power, error) * direction;

            power /= (1 + (Math.abs(prevAngle - angle) * Constants.DRIVE.BALANCE_DENOMINATOR_MULTIPLIER));

            m_swerve.drive(power, 0, 0);

        }

        prevAngle = angle;
    }

    @Override
    public boolean isFinished() {
        double elapsedBalanceTime = System.currentTimeMillis() - balanceStart;
        boolean isBalanced = Math.abs(m_swerve.getTilt(m_swerve.getYaw0To360())) < Constants.DRIVE.BALANCE_LEVEL_DEGREES;
        if (!isBalanced) {
            balanceStart = -1;
        } else if (isBalanced && balanceStart == -1) {
            balanceStart = System.currentTimeMillis();
        }

        return isBalanced && elapsedBalanceTime >= Constants.DRIVE.BALANCE_WAIT_MILLIS;
    }
    
    @Override
    public void end(boolean interrupted) {
        SwerveDriveSubsystem.getInstance().drive(0, 0, 0);
    }

}
