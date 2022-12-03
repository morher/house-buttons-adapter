package net.morher.house.buttons.action;

import static java.util.stream.Collectors.joining;

import java.util.ArrayList;
import java.util.List;

public class ActionBlock implements Action {
    private final List<Action> actions = new ArrayList<>();

    private ActionBlock(List<Action> actions) {
        this.actions.addAll(actions);
    }

    @Override
    public void storePreEventState() {
        for (Action action : actions) {
            action.storePreEventState();
        }
    }

    @Override
    public void perform() {
        for (Action action : actions) {
            action.perform();
        }
    }

    @Override
    public String toString() {
        return actions.stream()
                .map(Object::toString)
                .collect(joining("\n"));
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final List<Action> actions = new ArrayList<>();

        public Builder add(Action action) {
            if (action instanceof ActionBlock) {
                this.actions.addAll(((ActionBlock) action).actions);
            } else {
                this.actions.add(action);
            }
            return this;
        }

        public Builder addAll(List<Action> actions) {
            this.actions.addAll(actions);
            return this;
        }

        public Action build() {
            if (actions.size() == 1) {
                return actions.get(0);
            }
            return new ActionBlock(actions);
        }
    }
}
