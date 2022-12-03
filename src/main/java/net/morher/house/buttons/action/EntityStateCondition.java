package net.morher.house.buttons.action;

import java.util.Objects;

import net.morher.house.api.entity.EntityId;
import net.morher.house.api.entity.common.StatefullEntity;

public class EntityStateCondition<T> implements Condition {
    private final EntityId entityId;
    private final T conditionState;
    private T currentState;
    private T preEventState;

    public EntityStateCondition(StatefullEntity<T, ?> entity, T conditionState) {
        this.entityId = entity.getId();
        entity.state().subscribe(this::onStateUpdated);
        this.conditionState = conditionState;
    }

    @Override
    public void storePreEventState() {
        preEventState = currentState;
    }

    void onStateUpdated(T state) {
        this.currentState = state;
        if (this.preEventState == null) {
            this.preEventState = state;
        }
    }

    @Override
    public boolean isMatch() {
        return Objects.equals(preEventState, conditionState);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("State of \"")
                .append(entityId.getDevice().getRoomName())
                .append(" - ")
                .append(entityId.getDevice().getDeviceName())
                .append(" - ")
                .append(entityId.getEntity())
                .append("\" is ")
                .append(conditionState)
                .append(" (")
                .append(isMatch())
                .append(")");
        return sb.toString();
    }

}
