package net.morher.house.buttons.action;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class NotCondition implements Condition {
    private final Condition delegate;

    @Override
    public void storePreEventState() {
        delegate.storePreEventState();
    }

    @Override
    public boolean isMatch() {
        return !delegate.isMatch();
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("not")
                .append(delegate.toString().indent(4))
                .toString();
    }

}
