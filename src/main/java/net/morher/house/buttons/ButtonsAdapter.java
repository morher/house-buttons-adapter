package net.morher.house.buttons;

import net.morher.house.api.context.HouseAdapter;
import net.morher.house.api.context.HouseMqttContext;
import net.morher.house.api.entity.DeviceManager;
import net.morher.house.api.mqtt.client.HouseMqttClient;
import net.morher.house.buttons.config.ButtonsAdapterConfiguration;
import net.morher.house.buttons.controller.ButtonsController;
import net.morher.house.buttons.pattern.ButtonManager;

public class ButtonsAdapter implements HouseAdapter {
    public static void main(String[] args) throws Exception {
        new ButtonsAdapter().run(new HouseMqttContext("buttons-adapter"));
    }

    public void run(HouseMqttContext ctx) {
        HouseMqttClient client = ctx.client();
        DeviceManager deviceManager = ctx.deviceManager();

        ButtonManager buttonManager = new ButtonManager();

        ButtonsController buttons = new ButtonsController(client, buttonManager, deviceManager);
        buttons.configure(ctx.loadAdapterConfig(ButtonsAdapterConfiguration.class).getButtons());

    }
}
