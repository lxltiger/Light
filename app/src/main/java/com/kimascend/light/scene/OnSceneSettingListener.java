package com.kimascend.light.scene;


import com.kimascend.light.device.entity.Lamp;

/**
 * 情景列表点击事件处理
 */
public interface OnSceneSettingListener {


    void onItemClick(Lamp lamp);

    void onSwitchClick(Lamp lamp);

}
