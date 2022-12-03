package net.morher.house.buttons.input;

import java.time.Instant;

public interface Button {

    default void reportState(boolean state) {
        reportState(state, Instant.now());
    }

    void reportState(boolean state, Instant time);

    default Button inverted() {
        return new InvertedButton(this);
    }
}
