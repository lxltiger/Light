package com.kimascend.light.command;

import com.kimascend.light.device.entity.LampCmd;
import com.kimascend.light.mqtt.MQTTClient;
import com.kimascend.light.sevice.TelinkLightService;
import com.google.gson.Gson;
import com.telink.bluetooth.light.Opcode;

/**
 * BLE下使用Telink执行开关灯
 * WIFI下使用MQTT
 */
public class TelinkOnOffCommand extends OnOffCommand {


    public TelinkOnOffCommand(int address) {
        super(address);

    }

    public TelinkOnOffCommand(){}

    @Override
    protected void executeOnOffByBLE(boolean on, int addr,int delay) {
        byte low = (byte) (0xFF & delay);
        byte high = (byte) (0xFF & delay>>8);
//        TelinkLightService.Instance().sendCommandNoResponse(Opcode.BLE_GATT_OP_CTRL_D0.getValue(), addr, new byte[]{(byte) (on ? 0x01 : 0x00), 0x00, 0x00});
        TelinkLightService.Instance().sendCommandNoResponse(Opcode.BLE_GATT_OP_CTRL_D0.getValue(), addr, new byte[]{(byte) (on ? 0x01 : 0x00), low, high});

    }

    @Override
    protected void executeOnOffByWIFI(boolean on, int addr,int delay) {
        LampCmd lampCmd = new LampCmd(5, addr, 1, "0", on ? 100 : 0);
        String message = new Gson().toJson(lampCmd);
        MQTTClient.INSTANCE().publishLampControlMessage("1102F483CD9E6123", message);
    }

}
