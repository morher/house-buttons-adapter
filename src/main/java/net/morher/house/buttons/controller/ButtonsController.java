package net.morher.house.buttons.controller;

import static net.morher.house.api.mqtt.payload.BooleanMessage.onOff;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import net.morher.house.api.devicetypes.GeneralDevice;
import net.morher.house.api.entity.DeviceId;
import net.morher.house.api.entity.DeviceInfo;
import net.morher.house.api.entity.DeviceManager;
import net.morher.house.api.entity.trigger.TriggerEntity;
import net.morher.house.api.entity.trigger.TriggerOptions;
import net.morher.house.api.mqtt.client.HouseMqttClient;
import net.morher.house.api.subscription.Subscribable;
import net.morher.house.api.subscription.Subscription;
import net.morher.house.buttons.action.Action;
import net.morher.house.buttons.action.ActionBuilder;
import net.morher.house.buttons.action.Trigger;
import net.morher.house.buttons.config.ActionConfig;
import net.morher.house.buttons.config.ButtonsConfig;
import net.morher.house.buttons.config.ButtonsConfig.InputConfig;
import net.morher.house.buttons.config.ButtonsConfig.TemplateConfig;
import net.morher.house.buttons.config.ButtonsConfig.TriggerConfig;
import net.morher.house.buttons.input.Button;
import net.morher.house.buttons.pattern.ButtonEvent;
import net.morher.house.buttons.pattern.ButtonListener;
import net.morher.house.buttons.pattern.ButtonManager;

@Slf4j
public class ButtonsController {
    private final HouseMqttClient client;
    private final ButtonManager buttonManager;
    private final DeviceManager deviceManager;
    private final List<ButtonInput> inputs = new ArrayList<>();
    private final Map<String, Trigger> triggers = new HashMap<>();

    public ButtonsController(HouseMqttClient client, ButtonManager buttonManager, DeviceManager deviceManager) {
        this.client = client;
        this.buttonManager = buttonManager;
        this.deviceManager = deviceManager;
    }

    public void configure(ButtonsConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("No buttons configuration");
        }
        Map<String, TemplateConfig> templates = config.getTemplates();

        configureTriggers(config.getTriggers());

        configureInputs(config.getInputs(), templates);
    }

    private void configureTriggers(List<TriggerConfig> triggerConfigs) {
        for (TriggerConfig triggerConfig : triggerConfigs) {
            DeviceId deviceId = triggerConfig.getDevice().toDeviceId();
            TriggerEntity entity = deviceManager.device(deviceId).entity(GeneralDevice.CONTROL);

            TriggerOptions options = new TriggerOptions();
            for (Map.Entry<String, String> action : triggerConfig.getActionMapping().entrySet()) {
                options.getAvailableEvents().add(action.getKey());
                triggers.put(action.getValue(), new Trigger(entity, action.getKey()));
            }
            DeviceInfo deviceInfo = new DeviceInfo();
            entity.setDeviceInfo(deviceInfo);
            entity.setOptions(options);
        }
    }

    private void configureInputs(List<InputConfig> inputs, Map<String, TemplateConfig> templates) {
        for (InputConfig input : inputs) {
            configureInput(input, templates);
        }
    }

    private void configureInput(InputConfig config, Map<String, TemplateConfig> templates) {

        Subscribable<Boolean> topic = client.topic(
                config.getTopic(),
                onOff().inverted(config.isInverted()).inJsonField(config.getProperty()));

        ButtonInput input = new ButtonInput(topic);

        Map<String, List<ActionConfig>> events = new HashMap<>();
        addEventsFromTemplates(events, config.getTemplates(), templates);
        events.putAll(config.getEvents());

        ActionBuilder context = new ActionBuilder(deviceManager, config, triggers);

        for (Map.Entry<String, List<ActionConfig>> event : events.entrySet()) {
            String eventType = event.getKey();
            Action action = context.buildAction(event.getValue());
            input.putEvent(eventType, action);
        }

        inputs.add(input);
    }

    private void addEventsFromTemplates(
            Map<String, List<ActionConfig>> events,
            List<String> templateNames,
            Map<String, TemplateConfig> templates) {

        for (String templateName : templateNames) {
            TemplateConfig template = templates.get(templateName);
            if (template != null) {
                events.putAll(template.getEvents());

            } else {
                throw new IllegalArgumentException("Switch template not found: " + templateName);
            }
        }

    }

    private class ButtonInput implements ButtonListener, Closeable {
        private Map<String, Action> eventAction = new HashMap<>();
        private final Button button;
        private final Subscription subscription;

        public ButtonInput(Subscribable<Boolean> topic) {
            button = buttonManager.createButton(this);
            subscription = topic.subscribe(button::reportState);
        }

        public void putEvent(String eventType, Action action) {
            eventAction.put(eventType, action);
        }

        @Override
        public void onButtonEvent(ButtonEvent event) {
            if (event.getPrecedingEvent() == null) {
                log.debug("New event, store pre event state");
                storePreEventContext();
            }
            String eventType = event.toString();
            log.trace("Received event: {}", eventType);
            Action action = eventAction.get(eventType);
            if (action != null) {
                log.debug("Perform action:\n{}", action);
                action.perform();
            }
        }

        private void storePreEventContext() {
            for (Action action : eventAction.values()) {
                action.storePreEventState();
            }
        }

        @Override
        public void close() throws IOException {
            subscription.unsubscribe();
        }
    }
}
