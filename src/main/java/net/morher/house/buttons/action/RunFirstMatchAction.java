package net.morher.house.buttons.action;

import java.util.ArrayList;
import java.util.List;

public class RunFirstMatchAction implements Action {
    private final List<ConditionalAction> alternatives = new ArrayList<>();

    @Override
    public void storePreEventState() {
        for (ConditionalAction alternative : alternatives) {
            alternative.storePreEventState();
        }
    }

    @Override
    public void perform() {
        for (ConditionalAction alternative : alternatives) {
            Condition condition = alternative.getCondition();
            if (condition == null || condition.isMatch()) {
                alternative.getAction().perform();
                break;
            }
        }
    }

    public void addAlternative(Action action, Condition condition) {
        alternatives.add(new ConditionalAction(condition, action));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (ConditionalAction alternative : alternatives) {
            Condition condition = alternative.getCondition();
            if (condition != null) {
                sb.append("if\n");
                sb.append(condition.toString().indent(4));
                sb.append("then\n");
            } else {
                sb.append("else\n");
            }
            sb.append(alternative.getAction().toString().indent(4));
        }
        return sb.toString();
    }
}
