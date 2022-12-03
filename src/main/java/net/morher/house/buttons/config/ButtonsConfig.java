package net.morher.house.buttons.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;
import net.morher.house.api.config.DeviceName;

@Data
public class ButtonsConfig {
    private Map<String, TemplateConfig> templates = new HashMap<>();
    private List<InputConfig> inputs = new ArrayList<>();
    private List<TriggerConfig> triggers = new ArrayList<>();

    @Data
    public static class TriggerConfig {
        private DeviceName device;
        private Map<String, String> actionMapping = new HashMap<>();
    }

    @Data
    public static class TemplateConfig {
        private Map<String, List<ActionConfig>> events = new HashMap<>();
    }

    @Data
    public static class InputConfig {
        private String topic;
        private String property;
        private boolean inverted;
        private List<String> templates = new ArrayList<>();
        private List<DeviceName> lamps = new ArrayList<>();
        private List<DeviceName> switches = new ArrayList<>();
        private Map<String, List<ActionConfig>> events = new HashMap<>();
    }
}