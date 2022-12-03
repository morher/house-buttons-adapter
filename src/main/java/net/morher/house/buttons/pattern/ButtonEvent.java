package net.morher.house.buttons.pattern;

import java.util.Objects;

/**
 * 
 * . . --- . . ! Click click hold for three seconds click click
 * 
 */
public class ButtonEvent {
    private final ButtonEvent precedingEvent;
    private final EventType type;

    public ButtonEvent(ButtonEvent precedingEvent, EventType type) {
        this.precedingEvent = precedingEvent;
        this.type = type;
    }

    public static ButtonEvent press() {
        return new ButtonEvent(null, EventType.PRESS);
    }

    public ButtonEvent thenPress() {
        checkNotEnded();
        if (EventType.PRESS.equals(type)) {
            return this;
        }
        return wrapPressed(wrapReleased(this));
    }

    public ButtonEvent thenClick() {
        checkNotEnded();
        if (EventType.HOLD.equals(type)) {
            return wrapReleased(this).thenClick();
        }
        return wrapReleased(wrapPressed(this));
    }

    public ButtonEvent thenHold() {
        checkNotEnded();
        return new ButtonEvent(wrapPressed(this), EventType.HOLD);
    }

    public ButtonEvent thenRelease() {
        checkNotEnded();
        if (EventType.RELEASE.equals(type)) {
            return this;
        }
        return wrapReleased(this);
    }

    public ButtonEvent thenEnd() {
        checkNotEnded();
        return new ButtonEvent(wrapReleased(this), EventType.END);
    }

    private static ButtonEvent wrapReleased(ButtonEvent event) {
        if (!EventType.RELEASE.equals(event.type)) {
            return new ButtonEvent(event, EventType.RELEASE);
        }
        return event;
    }

    private static ButtonEvent wrapPressed(ButtonEvent event) {
        if (EventType.RELEASE.equals(event.type)) {
            return new ButtonEvent(event, EventType.PRESS);
        }
        return event;
    }

    private void checkNotEnded() {
        if (EventType.END.equals(type)) {
            throw new IllegalStateException("This EVENT has ended.");
        }
    }

    public ButtonEvent getPrecedingEvent() {
        return precedingEvent;
    }

    public EventType getType() {
        return type;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        buildString(sb, null, null);
        return sb.toString();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof ButtonEvent) {
            ButtonEvent otherEvent = (ButtonEvent) other;
            return type.equals(otherEvent.type)
                    && Objects.equals(precedingEvent, otherEvent.precedingEvent);
        }
        return false;
    }

    private void buildString(StringBuilder sb, EventType nextType, EventType afterNextType) {
        if (this.precedingEvent != null) {
            this.precedingEvent.buildString(sb, type, nextType);
        }
        switch (type) {
        case PRESS:
            if (nextType == null) {
                sb.append('\\');
            }
            break;

        case HOLD:
            sb.append('_');
            break;

        case RELEASE:
            if (EventType.PRESS.equals(getPrecedingEvent().type)) {
                sb.append('.');
            } else if (!EventType.END.equals(nextType)
                    && !EventType.RELEASE.equals(afterNextType)) {
                sb.append('/');
            }
            break;

        case END:
            sb.append('!');
            break;
        }
    }

    public static enum EventType {
        PRESS, HOLD, RELEASE, END;
    }
}
