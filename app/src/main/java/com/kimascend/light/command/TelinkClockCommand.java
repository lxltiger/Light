package com.kimascend.light.command;

import android.util.Log;

import com.kimascend.light.sevice.TelinkLightService;
import com.telink.bluetooth.light.Opcode;

import java.text.DateFormat;
import java.util.Arrays;
import java.util.Calendar;

public class TelinkClockCommand extends ClockCommand {
    private static final String TAG = TelinkColorCommand.class.getSimpleName();
    public TelinkClockCommand(int address) {
        super(address);
    }

    @Override
    protected void getDeviceTime() {
        TelinkLightService.Instance().sendCommand(Opcode.BLE_GATT_OP_CTRL_E8.getValue(), 0x0000, new byte[]{0x10});
    }

    @Override
    protected void synDeviceTime() {
        byte[] params = new byte[7];
        //所有灯
        int address = 0xffff;
        Calendar instance = Calendar.getInstance();
        int year = instance.get(Calendar.YEAR);
        int offset = 0;
        params[offset++] = (byte) (year >> 8 & 0xff);
        params[offset++] = (byte) (year & 0xff);
        params[offset++] = (byte) instance.get(Calendar.MONTH);
        params[offset++] = (byte) instance.get(Calendar.DAY_OF_MONTH);
        params[offset++] = (byte) instance.get(Calendar.HOUR_OF_DAY);
        params[offset++] = (byte) instance.get(Calendar.MINUTE);
        params[offset++] = (byte) instance.get(Calendar.SECOND);
        TelinkLightService.Instance().sendCommand(Opcode.BLE_GATT_OP_CTRL_E4.getValue(), address, params);
    }

    @Override
    protected void addAlarmByBLE() {
        byte[] params = new byte[10];
        //所有灯
        int address = 0xffff;
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(instance.getTimeInMillis() + 120 * 1000);
        int offset = 0;
        //添加闹钟
        params[offset++] = 0x00;
        //自动分配索引
        params[offset++] = 0x00;
// 0x91 week 开  0x90 week 关
        params[offset++] = (byte) 0x90;
        params[offset++] = (byte) (instance.get(Calendar.MONTH) + 1);
//        params[offset++] = (byte) instance.get(Calendar.DAY_OF_MONTH);
        params[offset++] = 0x7f;
        params[offset++] = (byte) instance.get(Calendar.HOUR_OF_DAY);
        params[offset++] = (byte) instance.get(Calendar.MINUTE);
        params[offset++] = (byte) instance.get(Calendar.SECOND);
        params[offset++] = 0x00;

        String s = Arrays.toString(params);
        Log.d(TAG, "instruction " + s);
        String format = DateFormat.getDateTimeInstance().format(instance.getTimeInMillis());
        Log.d(TAG, format);
        TelinkLightService.Instance().sendCommand(Opcode.BLE_GATT_OP_CTRL_E5.getValue(), address, params);
    }

    @Override
    protected void addAlarmByWIFI() {

    }
}
