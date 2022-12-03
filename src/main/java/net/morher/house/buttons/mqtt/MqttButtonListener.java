package net.morher.house.buttons.mqtt;

import java.util.ArrayList;
import java.util.List;

import net.morher.house.api.mqtt.client.MqttMessageListener.ParsedMqttMessageListener;
import net.morher.house.buttons.input.Button;

public class MqttButtonListener implements ParsedMqttMessageListener<String> {
    private final List<String> pressedValues = new ArrayList<>();
    private final Button button;

    public MqttButtonListener(Button button) {
        this.button = button;
        this.pressedValues.add("1");
        this.pressedValues.add("true");
        this.pressedValues.add("on");
    }

    @Override
    public void onMessage(String topic, String data, int qos, boolean retained) {
        boolean pressed = pressedValues.contains(data.toLowerCase());
        button.reportState(pressed);
    }

}
