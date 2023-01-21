package com.team2357.frc2023.subsystems;

public class IntakeSubsystem {
    public static IntakeSubsystem instance = null;

    public static IntakeSubsystem getInstance() {
        return instance;
    }

    public static class Configuration {

    }

    public Configuration m_config;

    public IntakeSubsystem() {

        instance = this;
    }

    public void configure(Configuration config) {
        m_config = config;
    }
}