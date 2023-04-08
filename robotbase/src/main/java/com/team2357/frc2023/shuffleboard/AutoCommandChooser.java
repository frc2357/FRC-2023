package com.team2357.frc2023.shuffleboard;

import com.team2357.frc2023.commands.auto.ScoreHighCone;
import com.team2357.frc2023.commands.auto.TestBalance;
import com.team2357.frc2023.commands.auto.blue.BlueCol4StowBalance;
import com.team2357.frc2023.commands.auto.blue.BlueCol6StowBalance;
import com.team2357.frc2023.commands.auto.blue.BlueCol9Col7;
import com.team2357.frc2023.commands.auto.blue.BlueCol9Col7Balance;
import com.team2357.frc2023.commands.auto.blue.BlueCol9Col7Col8;
import com.team2357.frc2023.commands.auto.blue.BlueCol9Col8Col8;
import com.team2357.frc2023.commands.auto.red.RedCol4StowBalance;
import com.team2357.frc2023.commands.auto.red.RedCol6StowBalance;
import com.team2357.frc2023.commands.auto.red.RedCol9Col7;
import com.team2357.frc2023.commands.auto.red.RedCol9Col7Balance;
import com.team2357.frc2023.commands.auto.red.RedCol9Col7Col8;
import com.team2357.frc2023.commands.auto.red.RedCol9Col8Col8;

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
            new RedCol4StowBalance(),
            new RedCol6StowBalance(),
            new RedCol9Col7(),
            new RedCol9Col7Balance(),
            new RedCol9Col8Col8(),
            new RedCol9Col7Col8(),
            new BlueCol4StowBalance(),
            new BlueCol6StowBalance(),
            new BlueCol9Col7(),
            new BlueCol9Col7Balance(),
            new BlueCol9Col8Col8(),
            new BlueCol9Col7Col8(),
            new TestBalance()
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
