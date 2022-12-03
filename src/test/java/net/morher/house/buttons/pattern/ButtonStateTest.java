package net.morher.house.buttons.pattern;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

public class ButtonStateTest {

    private ButtonListener listener = Mockito.mock(ButtonListener.class);

    @Test
    public void testPush() {
        Instant now = Instant.now();

        List<ButtonStateReport> reports = new ArrayList<>();
        reports.add(new ButtonStateReport(now, true));

        ButtonReportHandler handler = new ButtonReportHandler();
        ButtonState state = new ButtonState(listener);
        handler.handleReports(state, reports, now);

        ArgumentCaptor<ButtonEvent> captor = ArgumentCaptor.forClass(ButtonEvent.class);

        verify(listener, times(1)).onButtonEvent(captor.capture());
        assertThat(captor.getValue(), is(equalTo(ButtonEvent.press())));
    }

    @Test
    public void testClick() {
        Instant now = Instant.now();

        List<ButtonStateReport> reports = new ArrayList<>();
        reports.add(new ButtonStateReport(now.minusMillis(100), true));
        reports.add(new ButtonStateReport(now.minusMillis(0), false));

        ButtonReportHandler handler = new ButtonReportHandler();
        ButtonState state = new ButtonState(listener);
        handler.handleReports(state, reports, now);

        ArgumentCaptor<ButtonEvent> captor = ArgumentCaptor.forClass(ButtonEvent.class);

        verify(listener, times(2)).onButtonEvent(captor.capture());
        assertThat(captor.getAllValues().get(0), is(equalTo(ButtonEvent.press())));
        assertThat(captor.getAllValues().get(1), is(equalTo(ButtonEvent.press().thenRelease())));
    }

    @Test
    public void testHold() {
        Instant now = Instant.now();

        List<ButtonStateReport> reports = new ArrayList<>();
        reports.add(new ButtonStateReport(now.minusMillis(1100), true));
        reports.add(new ButtonStateReport(now.minusMillis(0), false));

        ButtonReportHandler handler = new ButtonReportHandler();
        ButtonState state = new ButtonState(listener);
        handler.handleReports(state, reports, now);

        ArgumentCaptor<ButtonEvent> captor = ArgumentCaptor.forClass(ButtonEvent.class);

        verify(listener, times(3)).onButtonEvent(captor.capture());
        assertThat(captor.getAllValues().get(0), is(equalTo(ButtonEvent.press())));
        assertThat(captor.getAllValues().get(1), is(equalTo(ButtonEvent.press().thenHold())));
        assertThat(captor.getAllValues().get(2), is(equalTo(ButtonEvent.press().thenHold().thenRelease())));
    }

    @Test
    public void testDoubleHold() {
        Instant now = Instant.now();

        List<ButtonStateReport> reports = new ArrayList<>();
        reports.add(new ButtonStateReport(now.minusMillis(2100), true));
        reports.add(new ButtonStateReport(now.minusMillis(0), false));

        ButtonReportHandler handler = new ButtonReportHandler();
        ButtonState state = new ButtonState(listener);
        handler.handleReports(state, reports, now);

        ArgumentCaptor<ButtonEvent> captor = ArgumentCaptor.forClass(ButtonEvent.class);

        verify(listener, times(4)).onButtonEvent(captor.capture());
        assertThat(captor.getAllValues().get(0), is(equalTo(ButtonEvent.press())));
        assertThat(captor.getAllValues().get(1), is(equalTo(ButtonEvent.press().thenHold())));
        assertThat(captor.getAllValues().get(2), is(equalTo(ButtonEvent.press().thenHold().thenHold())));
        assertThat(captor.getAllValues().get(3), is(equalTo(ButtonEvent.press().thenHold().thenHold().thenRelease())));
    }

    @Test
    public void testClickAndEnd() {
        Instant now = Instant.now();

        List<ButtonStateReport> reports = new ArrayList<>();
        reports.add(new ButtonStateReport(now.minusMillis(250), true));
        reports.add(new ButtonStateReport(now.minusMillis(201), false));

        ButtonReportHandler handler = new ButtonReportHandler();
        ButtonState state = new ButtonState(listener);
        handler.handleReports(state, reports, now);

        ArgumentCaptor<ButtonEvent> captor = ArgumentCaptor.forClass(ButtonEvent.class);

        verify(listener, times(3)).onButtonEvent(captor.capture());
        assertThat(captor.getAllValues().get(0), is(equalTo(ButtonEvent.press())));
        assertThat(captor.getAllValues().get(1), is(equalTo(ButtonEvent.press().thenRelease())));
        assertThat(captor.getAllValues().get(2), is(equalTo(ButtonEvent.press().thenRelease().thenEnd())));
    }

    @Test
    public void testTwoEvents() {
        Instant now = Instant.now();

        List<ButtonStateReport> reports = new ArrayList<>();
        reports.add(new ButtonStateReport(now.minusMillis(3200), true));
        reports.add(new ButtonStateReport(now.minusMillis(2110), false));
        reports.add(new ButtonStateReport(now.minusMillis(100), true));
        reports.add(new ButtonStateReport(now.minusMillis(0), false));

        ButtonReportHandler handler = new ButtonReportHandler();
        ButtonState state = new ButtonState(listener);
        handler.handleReports(state, reports, now);

        ArgumentCaptor<ButtonEvent> captor = ArgumentCaptor.forClass(ButtonEvent.class);

        verify(listener, times(6)).onButtonEvent(captor.capture());
        List<ButtonEvent> events = captor.getAllValues();
        assertThat(events.get(0), is(equalTo(ButtonEvent.press())));
        assertThat(events.get(1), is(equalTo(ButtonEvent.press().thenHold())));
        assertThat(events.get(2), is(equalTo(ButtonEvent.press().thenHold().thenRelease())));
        assertThat(events.get(3), is(equalTo(ButtonEvent.press().thenHold().thenRelease().thenEnd())));
        assertThat(events.get(4), is(equalTo(ButtonEvent.press())));
        assertThat(events.get(5), is(equalTo(ButtonEvent.press().thenRelease())));
    }

}
