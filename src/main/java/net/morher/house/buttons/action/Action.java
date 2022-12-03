package net.morher.house.buttons.action;

public interface Action {
    void storePreEventState();

    void perform();
}
