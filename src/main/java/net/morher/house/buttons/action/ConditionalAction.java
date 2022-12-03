package net.morher.house.buttons.action;

import lombok.Data;

@Data
public class ConditionalAction {
    private final Condition condition;
    private final Action action;

    public void storePreEventState() {
        if (condition != null) {
            condition.storePreEventState();
        }
        action.storePreEventState();
    }
}