package net.morher.house.buttons.action;

import lombok.AllArgsConstructor;
import net.morher.house.api.entity.common.CommandableEntity;

@AllArgsConstructor
public class CommandEntityAction<C> implements Action {
    private final CommandableEntity<?, ?, C> entity;
    private final C modifiedState;

    @Override
    public void perform() {
        entity.sendCommand(modifiedState);
    }

    @Override
    public void storePreEventState() {
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Set state of \"")
                .append(entity.getId().getDevice().getRoomName())
                .append(" - ")
                .append(entity.getId().getDevice().getDeviceName())
                .append(" - ")
                .append(entity.getId().getEntity())
                .append("\" to ")
                .append(modifiedState);
        return sb.toString();
    }

}
