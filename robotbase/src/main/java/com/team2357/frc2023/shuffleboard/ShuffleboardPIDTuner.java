package com.team2357.frc2023.shuffleboard;

import java.util.Map;

import com.team2357.frc2023.Constants;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.shuffleboard.SimpleWidget;

public class ShuffleboardPIDTuner {
    private static ShuffleboardPIDTuner instance = null;

    public ShuffleboardPIDTuner getInstance() {
        return instance;
    }

    /**
     * The name of the subsystem PID that this instance will control.
     */
    private String m_subsystemName;
    /**
     * These are the updated values for the PID the instance of this controls.
     */
    private double m_updatedPValue;
    private double m_updatedIValue;
    private double m_updatedDValue;
    /**
     * These are how far the sliders can be set in each direction away from the
     * default value.
     */
    private double m_pRange;
    private double m_iRange;
    private double m_dRange;
    /**
     * The defaults for the PID this instance will control
     * should be set from constants or the controler.
     */
    private double m_pDefault;
    private double m_iDefault;
    private double m_dDefault;
    private NetworkTable m_table;
    private ShuffleboardTab m_tab;

    public ShuffleboardPIDTuner(String subsystemName, double pRange, double iRange, double dRange, double pDefault,
            double iDefault, double dDefault) {
        m_subsystemName = subsystemName;
        m_tab = Constants.SHUFFLEBOARD.PID_TUNER_TAB;
        m_pRange = pRange;
        m_iRange = iRange;
        m_dRange = dRange;
        m_pDefault = pDefault;
        m_iDefault = iDefault;
        m_dDefault = dDefault;
        m_table = NetworkTableInstance.getDefault().getTable("Shuffleboard");
        instance = this;
        makePIDWidgets();
    }

    public double getDouble(String entry) {
        return m_table.getEntry(entry).getDouble(0);
    }

    public void makePIDWidgets() {
        ShuffleboardLayout layout = m_tab.getLayout(m_subsystemName, BuiltInLayouts.kList);
        SimpleWidget pWidget = layout.add(m_subsystemName + " P", m_pDefault).withWidget(BuiltInWidgets.kNumberSlider)
                .withProperties(Map.of("min", m_pRange * -1, "max", m_pRange));
        SimpleWidget iWidget = layout.add(m_subsystemName + " I", m_iDefault).withWidget(BuiltInWidgets.kNumberSlider)
                .withProperties(Map.of("min", m_iRange * -1, "max", m_iRange));
        SimpleWidget dWidget = layout.add(m_subsystemName + " D", m_dDefault).withWidget(BuiltInWidgets.kNumberSlider)
                .withProperties(Map.of("min", m_dRange * -1, "max", m_dRange));
    }

    public boolean arePIDsUpdated(double currentPValue, double currentIValue, double currentDValue) {
        m_updatedPValue = getDouble(m_subsystemName + " P");
        m_updatedIValue = getDouble(m_subsystemName + " I");
        m_updatedDValue = getDouble(m_subsystemName + " D");
        if ((m_updatedPValue != currentPValue) || (m_updatedIValue != currentIValue)
                || (m_updatedDValue != currentDValue)) {
            return true;
        }
        return false;
    }
}