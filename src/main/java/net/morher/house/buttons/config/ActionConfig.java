package net.morher.house.buttons.config;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Singular;
import net.morher.house.api.config.DeviceName;

@Data
public class ActionConfig {
    private List<ConditionalActionConfig> firstMatch;
    private LightConfig light;
    private SwitchConfig power;
    private SwitchConfig enable;
    private TriggerConfig trigger;

    @Data
    public static class ConditionalActionConfig {
        private ConditionConfig condition;
        private final List<ActionConfig> action = new ArrayList<>();
    }

    @Data
    public static class ConditionConfig {
        private List<ConditionConfig> allOf;
        private List<ConditionConfig> anyOf;
        private ConditionConfig not;
        private LightConfig light;
        private SwitchConfig power;
        private SwitchConfig enable;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class LightConfig extends LightStateConfig {
        @Singular
        private final List<String> refs = new ArrayList<>();
        @Singular
        private final List<DeviceName> lamps = new ArrayList<>();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LightStateConfig {
        private Boolean power;
        private Integer brightness;
        private String effect;
    }

    @Data
    public static class SwitchConfig {
        private final List<String> refs = new ArrayList<>();
        private final List<DeviceName> switches = new ArrayList<>();
        private boolean state;
    }

    @Data
    public static class TriggerConfig {
        @Singular
        private final List<String> refs = new ArrayList<>();
        @Singular
        private final List<DeviceName> triggers = new ArrayList<>();
        private String event;
    }
}