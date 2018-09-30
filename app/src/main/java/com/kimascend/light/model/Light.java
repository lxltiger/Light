package com.kimascend.light.model;

import android.databinding.ObservableInt;

import com.kimascend.light.common.BindingAdapters;
import com.telink.bluetooth.light.DeviceInfo;

public final class Light {


    private DeviceInfo deviceInfo;

    public Light() {
    }

    public Light(DeviceInfo raw) {
        this.deviceInfo = raw;
    }

    /**
     * 灯具描述
     * 描述根据场景不同所使用的自断不同
     */
    private String description = "";
    /**
     * 新灯条码中添加按钮的状态
     * 0-添加
     * 1-正在添加
     * 2-添加成功
     * 当这个可观察的值发生变化时 会在绑定的方法中重新设置icon的图标
     */
    public ObservableInt status = new ObservableInt(BindingAdapters.ADD);

    public DeviceInfo getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(DeviceInfo deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    @Override
    public String toString() {
        return "Light{" +
                ", macAddress=" + deviceInfo.macAddress +
                ", meshAddress=" + deviceInfo.meshAddress +
                ", status=" + status.get() +
                '}';
    }
}
