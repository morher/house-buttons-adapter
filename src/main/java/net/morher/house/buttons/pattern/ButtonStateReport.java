package net.morher.house.buttons.pattern;

import java.time.Instant;

import lombok.Data;

@Data
public class ButtonStateReport {
    private final Instant timeOccured;
    private final boolean highState;
}