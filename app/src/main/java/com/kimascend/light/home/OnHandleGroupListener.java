package com.kimascend.light.home;


import com.kimascend.light.home.entity.Group;

/**
 * 首页场景列表点击事件处理
 */
public interface OnHandleGroupListener {


    void onItemClick(Group group);

    void onEditClick(Group group);

    void onDeleteClick(Group group);
}
