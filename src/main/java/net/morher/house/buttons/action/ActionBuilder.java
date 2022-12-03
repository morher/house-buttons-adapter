package net.morher.house.buttons.action;

import java.util.List;
import java.util.Map;

import net.morher.house.api.config.DeviceName;
import net.morher.house.api.devicetypes.GeneralDevice;
import net.morher.house.api.devicetypes.LampDevice;
import net.morher.house.api.entity.Device;
import net.morher.house.api.entity.DeviceManager;
import net.morher.house.api.entity.EntityDefinition;
import net.morher.house.api.entity.light.LightEntity;
import net.morher.house.api.entity.light.LightState;
import net.morher.house.api.entity.light.LightState.PowerState;
import net.morher.house.api.entity.switches.SwitchEntity;
import net.morher.house.buttons.action.ActionBlock.Builder;
import net.morher.house.buttons.config.ActionConfig;
import net.morher.house.buttons.config.ActionConfig.ConditionConfig;
import net.morher.house.buttons.config.ActionConfig.ConditionalActionConfig;
import net.morher.house.buttons.config.ActionConfig.LightConfig;
import net.morher.house.buttons.config.ActionConfig.SwitchConfig;
import net.morher.house.buttons.config.ActionConfig.TriggerConfig;
import net.morher.house.buttons.config.ButtonsConfig.InputConfig;

public class ActionBuilder {
    private final DeviceManager deviceManager;
    private final DeviceDefaults lamps;
    private final DeviceDefaults switches;
    private final Map<String, Trigger> triggers;

    public ActionBuilder(
            DeviceManager deviceManager,
            InputConfig inputConfig,
            Map<String, Trigger> triggers) {

        this.deviceManager = deviceManager;

        lamps = new DeviceDefaults(inputConfig.getLamps());
        switches = new DeviceDefaults(inputConfig.getSwitches());
        this.triggers = triggers;

    }

    public Action buildAction(List<ActionConfig> actionConfigs) {
        Builder block = ActionBlock.builder();

        for (ActionConfig actionConfig : actionConfigs) {
            addActions(block, actionConfig);
        }

        return block.build();
    }

    private void addActions(Builder block, ActionConfig actionConfig) {
        addFirstMatch(block, actionConfig.getFirstMatch());
        addLightAction(block, actionConfig.getLight());
        addSwitchAction(block, actionConfig.getPower(), GeneralDevice.POWER);
        addSwitchAction(block, actionConfig.getEnable(), GeneralDevice.ENABLE);
        addTriggerAction(block, actionConfig.getTrigger());
    }

    private void addFirstMatch(Builder block, List<ConditionalActionConfig> config) {
        if (config != null) {
            RunFirstMatchAction runFirst = new RunFirstMatchAction();
            for (ConditionalActionConfig conditionalAction : config) {
                runFirst.addAlternative(
                        buildAction(conditionalAction.getAction()),
                        buildCondition(conditionalAction.getCondition()));

            }
            block.add(runFirst);
        }
    }

    private void addLightAction(Builder block, LightConfig config) {
        if (config != null) {
            LightState lightState = buildLightState(config);

            List<DeviceName> devices = lamps.getDeviceNames(config.getLamps(), config.getRefs());
            for (DeviceName deviceName : devices) {
                Device lamp = deviceManager.device(deviceName.toDeviceId());
                block.add(new CommandEntityAction<>(lamp.entity(LampDevice.LIGHT), lightState));
            }
        }
    }

    private void addSwitchAction(Builder block, SwitchConfig config, EntityDefinition<SwitchEntity> defaultEntityDefinition) {
        if (config != null) {
            List<DeviceName> devices = switches.getDeviceNames(config.getSwitches(), config.getRefs());
            for (DeviceName deviceName : devices) {
                EntityDefinition<SwitchEntity> entityDefinition = entityDef(deviceName, defaultEntityDefinition);
                SwitchEntity switchEntity = deviceManager.device(deviceName.toDeviceId()).entity(entityDefinition);
                block.add(new CommandEntityAction<>(switchEntity, config.isState()));
            }
        }
    }

    private void addTriggerAction(Builder block, TriggerConfig config) {
        if (config != null) {
            Trigger trigger = triggers.get(config.getEvent());
            if (trigger != null) {
                block.add(new TriggerEventAction(trigger));
            } else {
                throw new IllegalArgumentException("Trigger " + config.getEvent() + " not found");
            }
        }
    }

    private EntityDefinition<SwitchEntity> entityDef(DeviceName deviceName, EntityDefinition<SwitchEntity> defaultEntityDefinition) {
        if (deviceName.getEntity() != null) {
            return new EntityDefinition<>(deviceName.getEntity(), (em, id) -> em.switchEntity(id));
        }
        return defaultEntityDefinition;
    }

    private LightState buildLightState(LightConfig config) {
        LightState lightState = new LightState();
        if (config.getPower() != null) {
            lightState = lightState.withState(config.getPower()
                    ? PowerState.ON
                    : PowerState.OFF);
        }
        lightState = lightState.withBrightness(config.getBrightness());
        lightState = lightState.withEffect(config.getEffect());
        return lightState;
    }

    private Condition buildCondition(ConditionConfig config) {
        AllConditions conditions = new AllConditions();
        if (config != null) {
            addAllOfConditions(conditions, config.getAllOf());
            addAnyOfConditions(conditions, config.getAnyOf());
            addLightConditions(conditions, config.getLight());
            addNotCondition(conditions, config.getNot());
            addSwitchCondition(conditions, config.getPower(), GeneralDevice.POWER);
            addSwitchCondition(conditions, config.getEnable(), GeneralDevice.ENABLE);
        }
        return conditions.optimize();
    }

    private void addAllOfConditions(AllConditions conditions, List<ConditionConfig> allOf) {
        if (allOf != null) {
            for (ConditionConfig conditionConfig : allOf) {
                conditions.add(buildCondition(conditionConfig));
            }
        }
    }

    private void addAnyOfConditions(AllConditions conditions, List<ConditionConfig> anyOf) {
        if (anyOf != null) {
            AnyCondition any = new AnyCondition();
            for (ConditionConfig conditionConfig : anyOf) {
                any.add(buildCondition(conditionConfig));
            }
            conditions.add(any.optimize());
        }
    }

    private void addLightConditions(AllConditions conditions, LightConfig config) {
        if (config != null) {
            LightState lightState = buildLightState(config);
            List<DeviceName> devices = lamps.getDeviceNames(config.getLamps(), config.getRefs());
            for (DeviceName deviceId : devices) {
                LightEntity lightEntity = deviceManager.device(deviceId.toDeviceId()).entity(LampDevice.LIGHT);
                conditions.add(new LightCondition(lightEntity, lightState));
            }
        }
    }

    private void addNotCondition(AllConditions conditions, ConditionConfig not) {
        if (not != null) {
            conditions.add(new NotCondition(buildCondition(not)));
        }
    }

    private void addSwitchCondition(AllConditions conditions, SwitchConfig config, EntityDefinition<SwitchEntity> defaultEntityDefinition) {
        if (config != null) {
            List<DeviceName> devices = switches.getDeviceNames(config.getSwitches(), config.getRefs());
            for (DeviceName deviceName : devices) {
                EntityDefinition<SwitchEntity> entityDefinition = entityDef(deviceName, defaultEntityDefinition);
                SwitchEntity switchEntity = deviceManager.device(deviceName.toDeviceId()).entity(entityDefinition);
                conditions.add(new EntityStateCondition<>(switchEntity, config.isState()));
            }
        }
    }
}
