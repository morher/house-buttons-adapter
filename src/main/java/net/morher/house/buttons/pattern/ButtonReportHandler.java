package net.morher.house.buttons.pattern;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.TemporalAmount;
import java.util.List;
import java.util.Optional;

public class ButtonReportHandler {
    private final TemporalAmount holdDuration = Duration.ofMillis(800);
    private final TemporalAmount endEventDuration = Duration.ofMillis(200);

    public Optional<Instant> handleReports(ButtonState state, List<ButtonStateReport> reportsToHandle, Instant now) {
        for (ButtonStateReport report : reportsToHandle) {
            handleReport(state, report);
        }
        if (state.isEventStateHigh()) {
            handleHoldBefore(state, now);
            return Optional.of(state.getEventTime().plus(holdDuration));

        } else {
            handleEnd(state, now);
            return state.isEventOngoing()
                    ? Optional.of(state.getEventTime().plus(endEventDuration))
                    : Optional.empty();
        }
    }

    private void handleReport(ButtonState state, ButtonStateReport report) {
        if (report.isHighState()) {
            if (!state.isEventStateHigh()) {
                handleEnd(state, report.getTimeOccured());
                state.press(report.getTimeOccured());
            }

        } else {
            if (state.isEventStateHigh()) {
                handleHoldBefore(state, report.getTimeOccured());
                state.release(report.getTimeOccured());
            }
        }
    }

    private void handleHoldBefore(ButtonState state, Instant time) {
        if (state.isEventOngoing() && state.isEventStateHigh()) {
            Instant nextHoldTime = state.getEventTime().plus(holdDuration);
            if (nextHoldTime.isBefore(time)) {
                state.hold(nextHoldTime);
                handleHoldBefore(state, time);
            }
        }
    }

    private void handleEnd(ButtonState state, Instant time) {
        if (state.isEventOngoing()
                && !state.isEventStateHigh()
                && state.getEventTime().plus(endEventDuration).isBefore(time)) {

            state.end(time);
        }
    }

}
