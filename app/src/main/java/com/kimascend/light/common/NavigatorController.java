package com.kimascend.light.common;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.kimascend.light.clock.Clock;
import com.kimascend.light.clock.ClockFragment;
import com.kimascend.light.home.ClockListFragment;
import com.kimascend.light.device.AddDeviceFragment;
import com.kimascend.light.device.AddHubFragment;
import com.kimascend.light.device.AddLampFragment;
import com.kimascend.light.home.DeviceListFragment;
import com.kimascend.light.home.GroupListFragment;
import com.kimascend.light.home.HomeFragment;
import com.kimascend.light.home.MoreFragment;
import com.kimascend.light.mesh.AddMeshFragment;
import com.kimascend.light.mesh.DefaultMesh;
import com.kimascend.light.mesh.MeshDetailFragment;
import com.kimascend.light.mesh.MeshListFragment;
import com.kimascend.light.scene.EditFragment;
import com.kimascend.light.home.SceneListFragment;
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
                .replace(container, ClockFragment.newInstance(), ClockFragment.TAG)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }



    public void navigateToEditName() {
        fm.beginTransaction()
                .replace(container, EditFragment.newInstance(), EditFragment.TAG)
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
