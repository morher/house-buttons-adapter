package net.morher.house.buttons.pattern;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import net.morher.house.buttons.input.Button;

public class ButtonManager {
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public Button createButton(ButtonListener listener) {
        return new ButtonImpl(scheduler, listener);
    }

    private static class ButtonImpl implements Button {
        private final List<ButtonStateReport> reports = new ArrayList<>();
        private final ScheduledExecutorService scheduler;
        private final ButtonReportHandler reportHandler;
        private final ButtonState state;

        public ButtonImpl(ScheduledExecutorService scheduler, ButtonListener listener) {
            this.scheduler = scheduler;
            this.reportHandler = new ButtonReportHandler();
            this.state = new ButtonState(listener);
        }

        @Override
        public synchronized void reportState(boolean state, Instant time) {
            reports.add(new ButtonStateReport(time, state));
            scheduler.execute(this::handleReports);
        }

        public synchronized List<ButtonStateReport> takeReports() {
            if (reports.isEmpty()) {
                return Collections.emptyList();
            }
            ArrayList<ButtonStateReport> reports = new ArrayList<>(this.reports);
            this.reports.clear();
            return reports;
        }

        private void handleReports() {
            List<ButtonStateReport> newReports = takeReports();
            Optional<Instant> nextCheck = reportHandler.handleReports(state, newReports, Instant.now());
            nextCheck.ifPresent(this::scheduleCheck);
        }

        private void scheduleCheck(Instant time) {
            long millis = Math.max(0, ChronoUnit.MILLIS.between(Instant.now(), time));
            scheduler.schedule(this::handleReports, millis, TimeUnit.MILLISECONDS);
        }
    }

}
