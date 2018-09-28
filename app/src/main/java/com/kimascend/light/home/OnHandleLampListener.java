package com.kimascend.light.home;


import com.kimascend.light.device.entity.Lamp;

/**
 * 首页灯具列表点击事件处理
 */
public interface OnHandleLampListener {


    void onItemClick(Lamp lamp);

    void onEditClick(Lamp lamp);

    void onDeleteClick(Lamp lamp);
}
