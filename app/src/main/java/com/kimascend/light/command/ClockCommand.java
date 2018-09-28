package com.kimascend.light.command;

public abstract class ClockCommand extends MeshStatus{

    private int addr;
    public ClockCommand(int address) {
        this.addr = address;
    }

    public void setAddr(int addr) {
        this.addr = addr;
    }

    public  final void addAlarm(int brightness) {
        if (isBlueTooth()) {
            if (isStatusValid()) {
                addAlarmByBLE();
            }
        } else {
            addAlarmByWIFI();

        }
    }

    //获取设备时间
    protected abstract void getDeviceTime();
//    同步设备时间
    protected abstract void synDeviceTime();
//    添加闹钟
    protected abstract void addAlarmByBLE();
    protected abstract void addAlarmByWIFI();


}
