package net.morher.house.buttons.action;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TriggerEventAction implements Action {
    private final Trigger trigger;

    @Override
    public void perform() {
        trigger.sendEvent();
    }

    @Override
    public void storePreEventState() {
        // Nothing to store...
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("Trigger ")
                .append(trigger.getEvent())
                .append(" on ")
                .append(trigger.getEntity().getId())
                .toString();
    }
}
