package com.kimascend.light.common;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.kimascend.light.clock.Clock;
import com.kimascend.light.clock.ClockFragment;
import com.kimascend.light.clock.ClockListFragment;
import com.kimascend.light.device.AddDeviceFragment;
import com.kimascend.light.device.AddHubFragment;
import com.kimascend.light.device.AddLampFragment;
import com.kimascend.light.device.GroupControlFragment;
import com.kimascend.light.device.LightSettingFragment;
import com.kimascend.light.home.DeviceListFragment;
import com.kimascend.light.home.GroupListFragment;
import com.kimascend.light.home.HomeFragment;
import com.kimascend.light.home.MoreFragment;
import com.kimascend.light.home.entity.Group;
import com.kimascend.light.mesh.AddMeshFragment;
import com.kimascend.light.mesh.DefaultMesh;
import com.kimascend.light.mesh.MeshDetailFragment;
import com.kimascend.light.mesh.MeshListFragment;
import com.kimascend.light.scene.EditFragment;
import com.kimascend.light.scene.GroupFragment2;
import com.kimascend.light.scene.LampListDialogFragment;
import com.kimascend.light.scene.Scene;
import com.kimascend.light.scene.SceneFragment;
import com.kimascend.light.scene.SceneListFragment;
import com.kimascend.light.scene.SelectedLampListFragment;
import com.kimascend.light.user.AboutUSFragment;
import com.kimascend.light.user.FeedBackFragment;
import com.kimascend.light.user.SettingFragment;
import com.kimascend.light.user.UserFragment;

/**
 * 页面跳转控制
 */
public class NavigatorController {

    private FragmentManager fm;
    private final int container;

    public NavigatorController(AppCompatActivity activity, int container) {
        this.fm = activity.getSupportFragmentManager();
        this.container = container;
    }


    public void navigateToLogin() {
        fm.beginTransaction()
                .replace(container, UserFragment.newInstance(), UserFragment.TAG)
//                .addToBackStack(null)
                .commitAllowingStateLoss();
    }

    public void navigateToSetting() {
        fm.beginTransaction()
                .replace(container, SettingFragment.newInstance(), SettingFragment.TAG)
                .commitAllowingStateLoss();
    }

    public void navigateToAboutUs() {
        fm.beginTransaction()
                .replace(container, new AboutUSFragment(), "AboutUSFragment")
                .commitAllowingStateLoss();
    }

    public void navigateToFeedBack() {
        fm.beginTransaction()
                .replace(container, new FeedBackFragment(), "FeedBackFragment")
                .commitAllowingStateLoss();
    }

    public void navigateToHome() {
        fm.beginTransaction()
                .replace(container, HomeFragment.newInstance(), HomeFragment.TAG)
                .commitAllowingStateLoss();
    }

    public void navigateToDevice() {
        fm.beginTransaction()
                .replace(container, DeviceListFragment.newInstance(), DeviceListFragment.TAG)
                .commitAllowingStateLoss();
    }

    public void navigateToAddDevice(int type) {
        fm.beginTransaction()
                .replace(container, AddDeviceFragment.newInstance(type), AddDeviceFragment.TAG)
                .commitAllowingStateLoss();
    }

    public void navigateToAddLamp() {
        fm.beginTransaction()
                .replace(container, AddLampFragment.newInstance(), AddLampFragment.TAG)
                .commitAllowingStateLoss();
    }

    public void navigateToAddHub() {
        fm.beginTransaction()
                .replace(container, AddHubFragment.newInstance(), AddHubFragment.TAG)
                .addToBackStack(null)

                .commitAllowingStateLoss();
    }

    public void navigateToLampSetting(Bundle bundle) {
        fm.beginTransaction()
                .replace(container, LightSettingFragment.newInstance(bundle), LightSettingFragment.TAG)
                .commitAllowingStateLoss();
    }


    public void navigateToGroupControl(Bundle args) {
        fm.beginTransaction()
                .replace(container, GroupControlFragment.newInstance(args), GroupControlFragment.TAG)
                .commitAllowingStateLoss();
    }

    public void navigateToGroup() {
        fm.beginTransaction()
                .replace(container, GroupListFragment.newInstance(), GroupListFragment.TAG)
                .commitAllowingStateLoss();
    }

    public void navigateToSceneList() {
        fm.beginTransaction()
                .replace(container, SceneListFragment.newInstance(), SceneListFragment.TAG)
                .commitAllowingStateLoss();
    }

    public void navigateToClockList() {
        fm.beginTransaction()
                .replace(container, ClockListFragment.newInstance(), ClockListFragment.TAG)
                .commitAllowingStateLoss();
    }

    /*闹钟的添加和编辑*/
    public void navigateToClock(Clock clock) {
        fm.beginTransaction()
                .replace(container, ClockFragment.newInstance(clock), ClockFragment.TAG)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }

    public void navigateToGroup(Group group) {
        fm.beginTransaction()
                .replace(container, GroupFragment2.newInstance(group), GroupFragment2.TAG)
                .commitAllowingStateLoss();
    }

    public void navigateToEditName() {
        fm.beginTransaction()
                .replace(container, EditFragment.newInstance(), EditFragment.TAG)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }

//    场景 、情景灯具列表
    public void navigateToLampList() {
        fm.beginTransaction()
                .replace(container, LampListDialogFragment.newInstance(), LampListDialogFragment.TAG)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }

    public void navigateToClockLampList() {
        fm.beginTransaction()
                .replace(container, com.kimascend.light.clock.LampListDialogFragment.newInstance(), LampListDialogFragment.TAG)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }

    public void navigateToSelectedLamps() {
        fm.beginTransaction()
                .replace(container, SelectedLampListFragment.newInstance(), SelectedLampListFragment.TAG)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }

//    闹钟已选择灯具
    public void navigateToClockSelectedLamps() {
        fm.beginTransaction()
                .replace(container, com.kimascend.light.clock.SelectedLampListFragment.newInstance(), SelectedLampListFragment.TAG)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }

    public void navigateToScene(Scene scene) {
        fm.beginTransaction()
                .replace(container, SceneFragment.newInstance(scene), SceneFragment.TAG)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }

    public void navigateToMore() {
        fm.beginTransaction()
                .replace(container, MoreFragment.newInstance(), MoreFragment.TAG)
                .commitAllowingStateLoss();
    }

    //添加mesh
  /*  public void navigateToAddMesh() {
        fm.beginTransaction()
                .replace(container, AddMeshFragment.newInstance(), AddMeshFragment.TAG)
                .commitAllowingStateLoss();
    }*/

    //显示mesh列表
    public void navigateToMeshList() {
        fm.beginTransaction()
                .replace(container, MeshListFragment.newInstance(), MeshListFragment.TAG)
                .commitAllowingStateLoss();
    }

    public void navigateToMeshDetail(DefaultMesh mesh) {
        fm.beginTransaction()
                .replace(container, MeshDetailFragment.newInstance(mesh), MeshDetailFragment.TAG)
                .commitAllowingStateLoss();
    }

    public void navigateToAddMesh() {
        fm.beginTransaction()
                .replace(container, AddMeshFragment.newInstance(), AddMeshFragment.TAG)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }



    //    处理返回键
    public boolean navigateToLast() {
        UserFragment fragment = (UserFragment) fm.findFragmentByTag(UserFragment.TAG);
        if (fragment != null) {
            return fragment.handleBackPressed();
        }
        return false;
    }

}
