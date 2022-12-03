package net.morher.house.buttons.action;

import lombok.Data;
import net.morher.house.api.entity.trigger.TriggerEntity;

@Data
public class Trigger {
    private final TriggerEntity entity;
    private final String event;

    public void sendEvent() {
        entity.publishEvent(event);
    }
}