package com.kimascend.light.model;

import android.databinding.ObservableInt;

import com.kimascend.light.common.BindingAdapters;
import com.telink.bluetooth.light.DeviceInfo;

public final class Light {

//    public String name;
//    public String macAddress;
//    public int meshAddress;
    public int brightness;
    public int color;
    public int temperature;
    public DeviceInfo raw;
    /**
     * 灯的类型 4-灯 6 插座 9 面板
     */
    public int type;
    public Light() { }

    public Light(DeviceInfo raw) {
        this.raw = raw;
    }

    /**
     * 灯具描述
     * 描述根据场景不同所使用的自断不同
     */
    public String mDescription="";
    /**
     * 新灯条码中添加按钮的状态
     * 0-添加
     * 1-正在添加
     * 2-添加成功
     * 当这个可观察的值发生变化时 会在绑定的方法中重新设置icon的图标
     */
    public ObservableInt status = new ObservableInt(BindingAdapters.ADD);


    @Override
    public String toString() {
        return "Light{" +
                ", brightness=" + brightness +
                ", status=" + status.get() +
                '}';
    }
}
