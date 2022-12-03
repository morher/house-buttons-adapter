package net.morher.house.buttons.pattern;

import static net.morher.house.buttons.pattern.ButtonEvent.EventType.END;
import static net.morher.house.buttons.pattern.ButtonEvent.EventType.HOLD;
import static net.morher.house.buttons.pattern.ButtonEvent.EventType.PRESS;
import static net.morher.house.buttons.pattern.ButtonEvent.EventType.RELEASE;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

import net.morher.house.buttons.pattern.ButtonEvent.EventType;

public class ButtonEventTest {

    @Test
    public void testPress() {
        ButtonEvent event = ButtonEvent.press();

        assertEvents(event, PRESS);
        assertThat(event.toString(), is(equalTo("\\")));
    }

    @Test
    public void testClick() {
        ButtonEvent event = ButtonEvent.press().thenClick();

        assertEvents(event, PRESS, RELEASE);
        assertThat(event.toString(), is(equalTo(".")));
    }

    @Test
    public void testDoubleClick() {
        ButtonEvent event = ButtonEvent.press().thenClick().thenClick();

        assertEvents(event, PRESS, RELEASE, PRESS, RELEASE);
        assertThat(event.toString(), is(equalTo("..")));
    }

    @Test
    public void testHoldWithoutRelease() {
        ButtonEvent event = ButtonEvent
                .press()
                .thenHold();

        assertEvents(event, PRESS, HOLD);
        assertThat(event.toString(), is(equalTo("_")));
    }

    @Test
    public void testClickAndHoldWithoutRelease() {
        ButtonEvent event = ButtonEvent
                .press()
                .thenClick()
                .thenHold();

        assertEvents(event, PRESS, RELEASE, PRESS, HOLD);
        assertThat(event.toString(), is(equalTo("._")));
    }

    @Test
    public void testClickAndHoldWithRelease() {
        ButtonEvent event = ButtonEvent
                .press()
                .thenClick()
                .thenHold()
                .thenRelease();

        assertEvents(event, PRESS, RELEASE, PRESS, HOLD, RELEASE);
        assertThat(event.toString(), is(equalTo("._/")));
    }

    @Test
    public void testHoldThenHoldWithRelease() {
        ButtonEvent event = ButtonEvent
                .press()
                .thenHold()
                .thenRelease()
                .thenPress()
                .thenHold()
                .thenRelease();

        assertEvents(event, PRESS, HOLD, RELEASE, PRESS, HOLD, RELEASE);
        assertThat(event.toString(), is(equalTo("_/_/")));
    }

    @Test
    public void testDoubleHoldWithRelease() {
        ButtonEvent event = ButtonEvent
                .press()
                .thenHold()
                .thenHold()
                .thenRelease();

        assertEvents(event, PRESS, HOLD, HOLD, RELEASE);
        assertThat(event.toString(), is(equalTo("__/")));
    }

    @Test
    public void testClickAndEnd() {
        ButtonEvent event = ButtonEvent
                .press()
                .thenRelease()
                .thenEnd();

        assertEvents(event, PRESS, RELEASE, END);
        assertThat(".!", event.toString(), is(equalTo(".!")));
    }

    @Test
    public void testHoldAndEnd() {
        ButtonEvent event = ButtonEvent
                .press()
                .thenHold()
                .thenRelease()
                .thenEnd();

        assertEvents(event, PRESS, HOLD, RELEASE, END);
        assertThat(event.toString(), is(equalTo("_!")));
    }

    @Test
    public void testIgnoreConsequtivePress() {
        ButtonEvent event = ButtonEvent
                .press()
                .thenPress()
                .thenPress();

        assertEvents(event, PRESS);
        assertThat(event.toString(), is(equalTo("\\")));
    }

    @Test
    public void testIgnoreConsequtiveRelease() {
        ButtonEvent event = ButtonEvent
                .press()
                .thenRelease()
                .thenRelease()
                .thenRelease();

        assertEvents(event, PRESS, RELEASE);
        assertThat(event.toString(), is(equalTo(".")));
    }

    @Test
    public void testReleaseHoldBeforeClick() {
        ButtonEvent event = ButtonEvent
                .press()
                .thenHold()
                .thenClick();

        assertEvents(event, PRESS, HOLD, RELEASE, PRESS, RELEASE);
        assertThat(event.toString(), is(equalTo("_.")));
    }

    private static void assertEvents(ButtonEvent event, EventType... eventTypes) {
        assertThat(event, is(not(nullValue())));
        ButtonEvent[] events = buildEventArray(event, 0);
        assertThat("Number of events", events.length, is(equalTo(eventTypes.length)));
        for (int i = 0; i < eventTypes.length; i++) {
            assertThat("Event #" + i, events[i].getType(), is(equalTo(eventTypes[i])));

        }
    }

    private static ButtonEvent[] buildEventArray(ButtonEvent event, int pos) {
        if (event == null) {
            return new ButtonEvent[pos];

        } else {
            ButtonEvent[] eventArray = buildEventArray(event.getPrecedingEvent(), pos + 1);
            eventArray[eventArray.length - pos - 1] = event;
            return eventArray;

        }
    }
}
