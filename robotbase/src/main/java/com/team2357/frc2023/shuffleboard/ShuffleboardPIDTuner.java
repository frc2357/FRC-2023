package com.team2357.frc2023.shuffleboard;

import java.util.Map;

import com.team2357.frc2023.Constants;
import com.team2357.frc2023.subsystems.ArmExtensionSubsystem.Configuration;

import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.shuffleboard.SimpleWidget;

public class ShuffleboardPIDTuner {
    private static ShuffleboardPIDTuner instance = null;
    
    public ShuffleboardPIDTuner getInstance(){
        return instance;
    }
    private Configuration armExtensionConfig;

    public ShuffleboardPIDTuner() {
        armExtensionConfig = Constants.ARM_EXTENSION.GET_EXTENSION_CONFIG();
        instance = this;
    }

    private double originalArmExtensionP = armExtensionConfig.m_extendMotorP;
    private double ExtensionPMin = 0;
    private double ExtensionPMax = 1;

    private double originalArmExtensionI = armExtensionConfig.m_extendMotorI;
    private double ExtensionIMin = 0;
    private double ExtensionIMax = 1;

    private double originalArmExtensionD = armExtensionConfig.m_extendMotorD;
    private double ExtensionDMin = 0;
    private double ExtensionDMax = 1;

    private ShuffleboardTab tab = Shuffleboard.getTab("PID's");
    // used to update all of the PID's with the newly inputed values
    private SimpleWidget ExtensionPWidget = tab.add("Extension P", 0).withWidget(BuiltInWidgets.kNumberSlider)
            .withProperties(Map.of("min", ExtensionPMin, "max", ExtensionPMax));
    public double m_currentArmExtensionPValue = getDouble("Extension P");

    private SimpleWidget ExtensionIWidget = tab.add("Extension I", 0).withWidget(BuiltInWidgets.kNumberSlider)
            .withProperties(Map.of("min", ExtensionIMin, "max", ExtensionIMax));
    public double m_currentArmExtensionIValue = getDouble("Extension I");

    private SimpleWidget ExtensionDWidget = tab.add("Extension D", 0).withWidget(BuiltInWidgets.kNumberSlider)
            .withProperties(Map.of("min", ExtensionDMin, "max", ExtensionDMax));
    public double m_currentArmExtensionDValue = getDouble("Extension D");

    public double getDouble(String entry) {
        return NetworkTableInstance.getDefault().getTable("Shuffleboard").getEntry(entry).getDouble(0);
    }

    public boolean isExtensionUpdated() {
        if ((m_currentArmExtensionPValue != originalArmExtensionP) || (m_currentArmExtensionIValue != originalArmExtensionI)
            || (m_currentArmExtensionDValue != originalArmExtensionD)) {
                originalArmExtensionP = m_currentArmExtensionPValue;
                originalArmExtensionI = m_currentArmExtensionIValue;
                originalArmExtensionD = m_currentArmExtensionDValue;
                return true;
        }
        return false;
    }
}