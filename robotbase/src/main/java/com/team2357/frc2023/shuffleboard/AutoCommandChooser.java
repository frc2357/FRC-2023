package com.team2357.frc2023.shuffleboard;

import com.team2357.frc2023.commands.auto.ScoreHighCone;
import com.team2357.frc2023.commands.auto.Col4StowBalance;
import com.team2357.frc2023.commands.auto.Col6StowBalance;
import com.team2357.frc2023.commands.auto.Col9Col7;
import com.team2357.frc2023.commands.auto.Col9Col7Balance;
import com.team2357.frc2023.commands.auto.Col9Col7Col8;
import com.team2357.frc2023.commands.auto.Col9Col8Col8;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class AutoCommandChooser {
    private Command[] m_autoCommands;
    private SendableChooser<Command> m_chooser;

    public AutoCommandChooser() {
        m_autoCommands = new Command[] {
            new ScoreHighCone(),
            new Col4StowBalance(),
            new Col6StowBalance(),
            new Col9Col7(),
            new Col9Col7Balance(),
            new Col9Col8Col8(),
            new Col9Col7Col8()
        };

        m_chooser = new SendableChooser<>();

        m_chooser.setDefaultOption("None", new WaitCommand(0));
        for (Command autoCommand : m_autoCommands) {
            m_chooser.addOption(autoCommand.toString(), autoCommand);
        }

        SmartDashboard.putData("Auto chooser", m_chooser);
    }

    public Command getSelectedAutoCommand() {
        return m_chooser.getSelected();
    }
}
