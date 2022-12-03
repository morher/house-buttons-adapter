package net.morher.house.buttons.action;

public interface Condition {
    void storePreEventState();

    boolean isMatch();
}
