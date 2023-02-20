package com.team2357.frc2023.shuffleboard;

import com.team2357.frc2023.commands.auto.blue.grid3.BlueGridThreeTwoConeAutoCommand;
import com.team2357.frc2023.commands.drive.ZeroDriveCommand;
import com.team2357.frc2023.util.AvailableTrajectoryCommands;
import com.team2357.frc2023.util.TrajectoryUtil;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class AutoCommandChooser {

    private AutoActionChooser[] choosers;

    private enum automodes {
        NONE,
        BLUE_GRID_THREE_TWO_CONE_AUTO;
    }

    private class AutoActionChooser {
        protected SendableChooser<automodes> m_chooser;
        protected String m_waitCommandKey;

        protected AutoActionChooser(int index) {
            m_waitCommandKey = "wait " + index;
            m_chooser = new SendableChooser<>();

            m_chooser.setDefaultOption("None", automodes.NONE);
            for (automodes s : automodes.values()) {
                if (s != automodes.NONE)
                    m_chooser.addOption(s.toString().toLowerCase(), s);
            }

            SmartDashboard.putNumber((m_waitCommandKey), 0.0);
            SmartDashboard.putData("Auto chooser " + index, m_chooser);
        }

        public Command getWaitCommand() {
            double waitTime = SmartDashboard.getNumber(m_waitCommandKey, 0.0);
            return new WaitCommand(waitTime);
        }

        public Command getActionCommand() {
            switch (m_chooser.getSelected()) {
                case BLUE_GRID_THREE_TWO_CONE_AUTO:
                    return AvailableTrajectoryCommands.blueGridThreeTwoConeAuto;
                default:
                    System.out.println("ACTION: NONE");
                    return new WaitCommand(0);
            }
        }
    }

    public AutoCommandChooser() {
        choosers = new AutoActionChooser[3];
        choosers[0] = new AutoActionChooser(0);
    }

    public Command generateCommand() {
        CommandScheduler.getInstance().removeComposedCommand(choosers[0].getActionCommand());
        return new SequentialCommandGroup(
                new ParallelCommandGroup(choosers[0].getWaitCommand(), new ZeroDriveCommand()),
                choosers[0].getActionCommand());
    }
}