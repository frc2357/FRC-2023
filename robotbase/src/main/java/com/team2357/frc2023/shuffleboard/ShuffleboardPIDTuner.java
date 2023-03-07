package com.team2357.frc2023.shuffleboard;

import java.util.Map;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.shuffleboard.SimpleWidget;

public class ShuffleboardPIDTuner {

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
     * These are the last measured values of the PID that this instance tunes.
     */
    private double m_measuredPValue;
    private double m_measuredIValue;
    private double m_measuredDValue;
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
    private final ShuffleboardTab m_tab;

    /**
     * @param subsystemName The name of the subsystem, used to give the widgts and
     *                      layout their names.
     * @param pRange        How far the P slider can go to adjust the P value.
     * @param iRange        How far the I slider can go to adjust the I value.
     * @param dRange        How far the D slider can go to adjust the D value.
     * @param pDefault      the default P value, should be a config or from the PID
     *                      controller itself.
     * @param iDefault      the default I value, should be a config or from the PID
     *                      controller itself.
     * @param dDefault      the default D value, should be a config or from the PID
     *                      controller itself.
     */
    public ShuffleboardPIDTuner(String subsystemName, double pRange, double iRange, double dRange, double pDefault,
            double iDefault, double dDefault) {
        m_subsystemName = subsystemName;
        m_pRange = pRange;
        m_iRange = iRange;
        m_dRange = dRange;
        m_pDefault = pDefault;
        m_iDefault = iDefault;
        m_dDefault = dDefault;
        m_tab = Shuffleboard.getTab("PID's");
        m_table = NetworkTableInstance.getDefault().getTable("Shuffleboard");
        makePIDWidgets();
    }

    /**
     * @return The P value of the PID this instance controls.
     */
    public double getPValue() {
        return m_table.getEntry(m_subsystemName + " P").getDouble(m_pDefault);
    }

    /**
     * @return The I value of the PID this instance controls.
     */
    public double getIValue() {
        return m_table.getEntry(m_subsystemName + " I").getDouble(m_iDefault);
    }

    /**
     * @return The D value of the PID this instance controls.
     */
    public double getDValue() {
        return m_table.getEntry(m_subsystemName + " D").getDouble(m_dDefault);
    }

    public void makePIDWidgets() {
        ShuffleboardLayout layout = m_tab.getLayout(m_subsystemName, BuiltInLayouts.kList).withSize(2, 3);
        SimpleWidget pWidget = layout.add(m_subsystemName + " P", m_pDefault).withWidget(BuiltInWidgets.kNumberSlider)
                .withProperties(Map.of("min", m_pRange * -1, "max", m_pRange));
        SimpleWidget iWidget = layout.add(m_subsystemName + " I", m_iDefault).withWidget(BuiltInWidgets.kNumberSlider)
                .withProperties(Map.of("min", m_iRange * -1, "max", m_iRange));
        SimpleWidget dWidget = layout.add(m_subsystemName + " D", m_dDefault).withWidget(BuiltInWidgets.kNumberSlider)
                .withProperties(Map.of("min", m_dRange * -1, "max", m_dRange));
    }

    /**
     * @return Whether or not the PID this instance controls has been updated.
     */
    public boolean arePIDsUpdated() {
        m_updatedPValue = getPValue();
        m_updatedIValue = getIValue();
        m_updatedDValue = getDValue();
        if ((m_updatedPValue != m_measuredPValue) || (m_updatedIValue != m_measuredIValue)
                || (m_updatedDValue != m_measuredDValue)) {
            m_measuredPValue = m_updatedPValue;
            m_measuredIValue = m_updatedIValue;
            m_measuredDValue = m_updatedDValue;
            return true;
        }
        return false;
    }

    /**
     * This is to close the entries that this instance has made, which should clean it up. 
     * This should not need to be called usually.
     */
    public void closePIDTunerEntries() {
        m_table.getEntry(m_subsystemName + " P").close();
        m_table.getEntry(m_subsystemName + " I").close();
        m_table.getEntry(m_subsystemName + " D").close();
    }
}