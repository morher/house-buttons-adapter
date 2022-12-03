package net.morher.house.buttons.pattern;

import java.time.Instant;

public class ButtonState {
    private final ButtonListener listener;
    private ButtonEvent event;
    private boolean eventStateHigh;
    private Instant eventTime;

    public ButtonState(ButtonListener listener) {
        this.listener = listener;
    }

    public ButtonEvent getEvent() {
        return event;
    }

    public boolean isEventOngoing() {
        return event != null;
    }

    public boolean isEventStateHigh() {
        return eventStateHigh;
    }

    public Instant getEventTime() {
        return eventTime;
    }

    public void press(Instant time) {
        if (event == null) {
            event = ButtonEvent.press();
        } else {
            event = event.thenPress();
        }
        eventStateHigh = true;
        eventTime = time;
        listener.onButtonEvent(event);
    }

    public void hold(Instant time) {
        event = event.thenHold();
        eventTime = time;
        eventStateHigh = true;
        listener.onButtonEvent(event);
    }

    public void release(Instant time) {
        event = event.thenRelease();
        eventTime = time;
        eventStateHigh = false;
        listener.onButtonEvent(event);
    }

    public void end(Instant time) {
        if (event != null) {
            event = event.thenEnd();
            listener.onButtonEvent(event);
            event = null;
            eventStateHigh = false;
            eventTime = null;
        }
    }

}
