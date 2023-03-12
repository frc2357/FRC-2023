package com.team2357.frc2023.shuffleboard;

import com.team2357.frc2023.commands.auto.gridzero.GridZeroTwoConeAutoCommand;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class AutoCommandChooser {

    private InitialLocationChooser initialLocationChooser;
    private SecondaryLocationChooser secondaryLocationChooser;

    private enum initialLocations {
        NONE,
        GRID_0,
        GRID_1,
        GRID_2
    }

    private class InitialLocationChooser {
        protected SendableChooser<initialLocations> m_chooser;

        protected InitialLocationChooser(int index) {
            m_chooser = new SendableChooser<>();

            m_chooser.setDefaultOption("None", initialLocations.NONE);
            for (initialLocations s : initialLocations.values()) {
                if (s != initialLocations.NONE)
                    m_chooser.addOption(s.toString().toLowerCase(), s);
            }

            SmartDashboard.putData("Initial Location", m_chooser);
        }
    }

    private enum secondaryLocations {
        NONE,
        CHARGE_STATION,
        GAME_PIECE
    }

    private class SecondaryLocationChooser {
        protected SendableChooser<secondaryLocations> m_chooser;

        protected SecondaryLocationChooser(int index) {
            m_chooser = new SendableChooser<>();

            m_chooser.setDefaultOption("None", secondaryLocations.NONE);
            for (secondaryLocations s : secondaryLocations.values()) {
                if (s != secondaryLocations.NONE) {
                    m_chooser.addOption(s.toString().toLowerCase(), s);
                }
            }

            SmartDashboard.putData("Secondary Location", m_chooser);
        }
    }

    public AutoCommandChooser() {
        initialLocationChooser = new InitialLocationChooser(0);
        secondaryLocationChooser = new SecondaryLocationChooser(1);
    }

    public Command generateCommand() {
        initialLocations initialLocation = initialLocationChooser.m_chooser.getSelected();
        secondaryLocations secondaryLocation = secondaryLocationChooser.m_chooser.getSelected();

        switch (initialLocation) {
            case GRID_0:
                switch (secondaryLocation) {
                    case CHARGE_STATION:
                        break;
                    case GAME_PIECE:
                        return new GridZeroTwoConeAutoCommand();
                    default:
                        break;
                }
            case GRID_1:
                switch (secondaryLocation) {
                    case CHARGE_STATION:
                        break;
                    case GAME_PIECE:
                        break;
                    default:
                        break;
                }
            case GRID_2:
                switch (secondaryLocation) {
                    case CHARGE_STATION:
                        break;
                    case GAME_PIECE:
                        break;
                    default:
                        break;
                }
            default:
                break;
        }

        return new WaitCommand(0);
    }
}