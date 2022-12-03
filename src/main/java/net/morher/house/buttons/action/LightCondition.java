package net.morher.house.buttons.action;

import net.morher.house.api.entity.EntityId;
import net.morher.house.api.entity.common.StatefullEntity;
import net.morher.house.api.entity.light.LightState;

public class LightCondition implements Condition {
    private final EntityId entityId;
    private final LightState conditionState;
    private LightState currentState = new LightState();
    private LightState preEventState = currentState;

    public LightCondition(StatefullEntity<LightState, ?> lamp, LightState conditionState) {
        this.entityId = lamp.getId();
        lamp.state().subscribe(this::onStateUpdated);
        this.conditionState = conditionState;
    }

    @Override
    public void storePreEventState() {
        preEventState = currentState;
    }

    void onStateUpdated(LightState state) {
        this.currentState = state;
    }

    @Override
    public boolean isMatch() {
        return matches(conditionState.getState(), preEventState.getState())
                && matches(conditionState.getBrightness(), preEventState.getBrightness())
                && matches(conditionState.getEffect(), preEventState.getEffect());
    }

    private <T> boolean matches(T conditionValue, T actual) {
        return conditionValue == null
                || conditionValue.equals(actual);
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
