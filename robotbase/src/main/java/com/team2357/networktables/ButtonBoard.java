package com.team2357.networktables;

import java.util.EnumSet;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import com.team2357.frc2023.Constants;

import edu.wpi.first.networktables.MultiSubscriber;
import edu.wpi.first.networktables.NetworkTableEvent;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.PubSubOption;

public class ButtonBoard {

    private static ButtonBoard m_instance;

    private final AtomicReference<Integer> m_rowValue = new AtomicReference<Integer>();
    private final AtomicReference<Integer> m_colValue = new AtomicReference<Integer>();

    private final MultiSubscriber m_buttonboardSub; 
    private final int m_buttonboardListenerHandle;

    public final Consumer<NetworkTableEvent> m_updateConsumer = (NetworkTableEvent event) -> {  
        String[] split = event.valueData.getTopic().getName().split("/");
        String name = split[split.length - 1];
        switch(name) {
            case Constants.BUTTON_BOARD.COLUMN_TOPIC_NAME:
                m_colValue.set((int) event.valueData.value.getInteger());
                break;
            case Constants.BUTTON_BOARD.ROW_TOPIC_NAME: 
                m_rowValue.set((int) event.valueData.value.getInteger());
                break;
        }
    };

    public static ButtonBoard getInstance() {
        if (m_instance == null) {
            m_instance = new ButtonBoard();
        }
        return m_instance;
    }

    private ButtonBoard() {
        NetworkTableInstance inst = NetworkTableInstance.getDefault();
       
        m_buttonboardSub = new MultiSubscriber(inst, 
        new String[] {"/" + Constants.BUTTON_BOARD.BUTTONBOARD_TABLE_NAME + "/"},
        PubSubOption.keepDuplicates(false), PubSubOption.pollStorage(1));

        m_buttonboardListenerHandle = inst.addListener(m_buttonboardSub, 
        EnumSet.of(NetworkTableEvent.Kind.kValueAll),
        m_updateConsumer);
    }

    public int getRowValue() {
        if(m_rowValue.get() == null) {
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

    public void close() {
        NetworkTableInstance inst = NetworkTableInstance.getDefault();
        inst.removeListener(m_buttonboardListenerHandle);
        m_buttonboardSub.close();
    }
}
