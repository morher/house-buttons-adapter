package net.morher.house.buttons.action;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import net.morher.house.api.devicetypes.GeneralDevice;
import net.morher.house.api.devicetypes.LampDevice;
import net.morher.house.api.entity.DeviceId;
import net.morher.house.api.entity.DeviceManager;
import net.morher.house.api.entity.EntityManager;
import net.morher.house.api.entity.common.CommandableEntity;
import net.morher.house.api.entity.light.LightEntity;
import net.morher.house.api.entity.light.LightState;
import net.morher.house.api.entity.light.LightState.PowerState;
import net.morher.house.api.entity.switches.SwitchEntity;
import net.morher.house.buttons.config.ActionConfig;
import net.morher.house.buttons.config.ButtonsConfig.InputConfig;
import net.morher.house.test.client.TestHouseMqttClient;

public class ActionBuilderTest {
    private DeviceManager deviceManager = new DeviceManager(new EntityManager(TestHouseMqttClient.loopback()));

    LightEntity livingRoomMoodLamp = deviceManager
            .device(new DeviceId("Living room", "Mood lamp"))
            .entity(LampDevice.LIGHT);

    SwitchEntity kitchenMixerSwitch = deviceManager
            .device(new DeviceId("Kitchen", "Mixer"))
            .entity(GeneralDevice.POWER);

    @Test
    public void testLightAction() {
        List<LightState> commands = commandCollector(livingRoomMoodLamp);

        buildAction("""
                light:
                   power: ON
                   brightness: 127
                   effect: Strobe
                """)
                .perform();

        assertThat(commands.size(), is(1));
        assertThat(commands.get(0), is(equalTo(new LightState(PowerState.ON, 127, "Strobe"))));
    }

    @Test
    public void testSwitchAction() {
        List<Boolean> commands = commandCollector(kitchenMixerSwitch);

        buildAction("""
                power:
                   state: ON
                """)
                .perform();

        assertThat(commands.size(), is(1));
        assertThat(commands.get(0), is(true));
    }

    @Test
    public void testFirstMatchAction() {
        livingRoomMoodLamp.state().publish(new LightState(PowerState.ON, 127, "Romantic"));

        List<LightState> commands = commandCollector(livingRoomMoodLamp);

        buildAction("""
                firstMatch:
                 - condition:
                      light:
                         power: Off
                   action:
                    - light:
                         brightness: 10

                 - condition:
                      light:
                         effect: Romantic
                   action:
                    - light:
                         brightness: 20

                 - action:
                    - light:
                         brightness: 30
                """)
                .perform();

        assertThat(commands.size(), is(1));
        assertThat(commands.get(0), is(equalTo(new LightState(null, 20, null))));
    }

    @Test
    public void testFirstMatchActionElse() {
        livingRoomMoodLamp.state().publish(new LightState(PowerState.ON, 127, "Sunrise"));

        List<LightState> commands = commandCollector(livingRoomMoodLamp);

        buildAction("""
                firstMatch:
                 - condition:
                      power:
                         power: Off
                   action:
                    - light:
                         brightness: 10

                 - condition:
                      light:
                         effect: Romantic
                   action:
                    - light:
                         brightness: 20

                 - action:
                    - light:
                         brightness: 30
                """)
                .perform();

        assertThat(commands.size(), is(1));
        assertThat(commands.get(0), is(equalTo(new LightState(null, 30, null))));
    }

    private <C> List<C> commandCollector(CommandableEntity<?, ?, C> entity) {
        List<C> commands = new ArrayList<>();
        entity.command().subscribe(commands::add);
        return commands;
    }

    private Action buildAction(String yaml) {
        Action action = new ActionBuilder(deviceManager, inputConfig(), new HashMap<>())
                .buildAction(Collections.singletonList(parse(yaml, ActionConfig.class)));
        action.storePreEventState();
        return action;

    }

    private static InputConfig inputConfig() {
        return parse("""
                lamps:
                 - room: Living room
                   name: Mood lamp
                switches:
                 - room: Kitchen
                   name: Mixer
                """, InputConfig.class);

    }

    private static <T> T parse(String yaml, Class<T> configClass) {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try {
            return mapper.readValue(yaml, configClass);
        } catch (Exception e) {
            throw new IllegalArgumentException("invalid YAML", e);
        }
    }
}
