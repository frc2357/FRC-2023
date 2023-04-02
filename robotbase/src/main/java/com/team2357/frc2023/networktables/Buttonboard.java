package com.team2357.frc2023.networktables;

import java.util.EnumSet;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import com.team2357.frc2023.Constants;
import com.team2357.frc2023.commands.util.ConfigureAllianceCommand;

import edu.wpi.first.networktables.MultiSubscriber;
import edu.wpi.first.networktables.NetworkTableEvent;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.PubSubOption;
import edu.wpi.first.wpilibj.DriverStation;

public class Buttonboard {

    private static Buttonboard m_instance;

    private final AtomicReference<Integer> m_rowValue = new AtomicReference<Integer>();
    private final AtomicReference<Integer> m_colValue = new AtomicReference<Integer>();

    private final AtomicReference<DriverStation.Alliance> m_allianceValue = new AtomicReference<DriverStation.Alliance>();

    private final MultiSubscriber m_buttonboardSub;
    private final int m_buttonboardListenerHandle;

    public final Consumer<NetworkTableEvent> m_updateConsumer = (NetworkTableEvent event) -> {
        String[] split = event.valueData.getTopic().getName().split("/");
        String name = split[split.length - 1];
        switch (name) {
            case Constants.BUTTONBOARD.COLUMN_TOPIC_NAME:
                m_colValue.set((int) event.valueData.value.getInteger());
                break;
            case Constants.BUTTONBOARD.ROW_TOPIC_NAME:
                m_rowValue.set((int) event.valueData.value.getInteger());
                break;
            case Constants.BUTTONBOARD.ALLIANCE_TOPIC_NAME:
                switch (event.valueData.value.getString()) {
                    case "red":
                        //m_allianceValue.set(DriverStation.Alliance.Red);
                        break;
                    case "blue":
                        //m_allianceValue.set(DriverStation.Alliance.Blue);
                        break;
                    default:
                        //m_allianceValue.set(DriverStation.Alliance.Invalid);
                        break;
                }
                new ConfigureAllianceCommand(getAlliance()).schedule();
                break;
            case Constants.BUTTONBOARD.INTAKE_WINCH_TOPIC_NAME:
                break;
            case Constants.BUTTONBOARD.INTAKE_SPEED_TOPIC_NAME:
                break;
            case Constants.BUTTONBOARD.ARM_ROTATION_TOPIC_NAME:
                break;
            case Constants.BUTTONBOARD.ARM_EXTENSION_TOPIC_NAME:
                break;
            case Constants.BUTTONBOARD.INTAKE_EXTEND_TOPIC_NAME:
                break;
            case Constants.BUTTONBOARD.WRIST_TOPIC_NAME:
                break;
            case Constants.BUTTONBOARD.CLAW_TOGGLE_TOPIC_NAME:
                break;
        }
        ;
    };

    public static Buttonboard getInstance() {
        if (m_instance == null) {
            m_instance = new Buttonboard();
        }
        return m_instance;
    }

    private Buttonboard() {
        NetworkTableInstance inst = NetworkTableInstance.getDefault();

        m_buttonboardSub = new MultiSubscriber(inst,
                new String[] { "/" + Constants.BUTTONBOARD.BUTTONBOARD_TABLE_NAME + "/" },
                PubSubOption.keepDuplicates(false), PubSubOption.pollStorage(1));

        m_buttonboardListenerHandle = inst.addListener(m_buttonboardSub,
                EnumSet.of(NetworkTableEvent.Kind.kValueAll),
                m_updateConsumer);
    }

    public int getRowValue() {
        if (m_rowValue.get() == null) {
            return -1;
        }
        return m_rowValue.get();
    }

    public int getColValue() {
        if (m_colValue.get() == null) {
            return -1;
        }
        return m_colValue.get();
    }

    public DriverStation.Alliance getAlliance() {
        return m_allianceValue.get();
    }

    public void setAlliance() {
        m_allianceValue.set(DriverStation.getAlliance());
    }

    public void close() {
        NetworkTableInstance inst = NetworkTableInstance.getDefault();
        inst.removeListener(m_buttonboardListenerHandle);
        m_buttonboardSub.close();
    }
}
