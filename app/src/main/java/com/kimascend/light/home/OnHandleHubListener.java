package com.kimascend.light.home;


import com.kimascend.light.home.entity.Hub;

/**
 * 首页Hub列表点击事件处理
 */
public interface OnHandleHubListener {


    void onItemClick(Hub hub);

    void onEditClick(Hub hub);

    void onDeleteClick(Hub hub);
}
