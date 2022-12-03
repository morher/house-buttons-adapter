package net.morher.house.buttons.action;

import java.util.ArrayList;
import java.util.List;

public class AllConditions implements Condition {
    private final List<Condition> conditions = new ArrayList<>();

    @Override
    public void storePreEventState() {
        for (Condition condition : conditions) {
            condition.storePreEventState();
        }
    }

    public AllConditions add(Condition condition) {
        if (condition instanceof AllConditions) {
            conditions.addAll(((AllConditions) condition).conditions);
        } else {
            conditions.add(condition);
        }
        return this;
    }

    @Override
    public boolean isMatch() {
        for (Condition condition : conditions) {
            if (!condition.isMatch()) {
                return false;
            }
        }
        return true;
    }

    public Condition optimize() {
        if (conditions.size() == 1) {
            return conditions.get(0);
        }
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("all of\n");
        for (Condition condition : conditions) {
            sb.append(condition.toString().indent(4));
        }
        return sb.toString();
    }
}
