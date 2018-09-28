package com.kimascend.light.command;

import com.kimascend.light.device.entity.LampCmd;
import com.kimascend.light.mqtt.MQTTClient;
import com.kimascend.light.sevice.TelinkLightService;
import com.google.gson.Gson;
import com.telink.bluetooth.light.Opcode;

/**
 * 亮度调节
 */
public class TelinkBrightnessCommand extends BrightnessCommand {

    public TelinkBrightnessCommand(int address) {
        super(address);

    }

    @Override
    protected void setBrightnessByBLE(int addr, int brightness) {
        TelinkLightService.Instance().sendCommandNoResponse(Opcode.BLE_GATT_OP_CTRL_D2.getValue(), addr, new byte[]{(byte) brightness});
    }

    @Override
    protected void setBrightnessByWIFI(int addr, int brightness) {
        LampCmd lampCmd = new LampCmd(5, addr, 1, "0", brightness);
        String message = new Gson().toJson(lampCmd);
        MQTTClient.INSTANCE().publishLampControlMessage("1102F483CD9E6123", message);
    }
}
