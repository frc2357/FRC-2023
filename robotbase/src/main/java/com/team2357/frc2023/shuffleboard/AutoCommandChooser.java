package com.team2357.frc2023.shuffleboard;

import java.util.function.Consumer;

import com.team2357.frc2023.commands.intake.IntakeSolenoidExtendCommand;
import com.team2357.frc2023.trajectoryutil.AvailableTrajectoryCommands;

import edu.wpi.first.networktables.NetworkTableEvent;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class AutoCommandChooser {

    private static AutoCommandChooser m_instance = null;

    public static AutoCommandChooser getInstance() {
        if (m_instance == null) {
            m_instance = new AutoCommandChooser();
        }
        return m_instance;
    }

    // private AutoActionChooser[] choosers;
    private AutoActionChooser actionChooser;
    private AllianceChooser allianceChooser;

    private enum alliances {
        NONE,
        BLUE,
        RED
    }

    private class AllianceChooser {
        protected SendableChooser<alliances> m_chooser;
        
        protected AllianceChooser() {
            m_chooser = new SendableChooser<>();

            m_chooser.setDefaultOption("None", alliances.NONE);
            for (alliances s : alliances.values()) {
                if (s != alliances.NONE) {
                    m_chooser.addOption(s.toString().toLowerCase(), s);
                }
            }

            SmartDashboard.putData("Alliance", m_chooser);
        }

        public alliances getAlliance() {
            return m_chooser.getSelected();
        }
    }

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
        actionChooser = new AutoActionChooser(0);
        allianceChooser = new AllianceChooser();
        // choosers = new AutoActionChooser[3];
        // choosers[0] = new AutoActionChooser(0);
        m_instance = this;
    }

    public Command generateCommand() {
        CommandScheduler.getInstance().removeComposedCommand(actionChooser.getActionCommand());
        return new ParallelCommandGroup(
            new SequentialCommandGroup(
                // choosers[0].getWaitCommand(),
                actionChooser.getActionCommand()),
            new IntakeSolenoidExtendCommand());
    }

    public DriverStation.Alliance getAlliance() {
        switch (allianceChooser.getAlliance()) {
            case BLUE:
                return DriverStation.Alliance.Blue;
            case RED:
                return DriverStation.Alliance.Red;
            default:
                return DriverStation.Alliance.Invalid;

        }
    }
}