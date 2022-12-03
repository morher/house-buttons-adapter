package net.morher.house.buttons.input;

import java.time.Instant;

public class InvertedButton implements Button {
    private final Button delegate;

    public InvertedButton(Button delegate) {
        this.delegate = delegate;
    }

    @Override
    public void reportState(boolean state) {
        delegate.reportState(!state);
    }

    @Override
    public void reportState(boolean state, Instant time) {
        delegate.reportState(!state, time);
    }

    @Override
    public Button inverted() {
        return delegate;
    }

}