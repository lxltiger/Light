

package com.kimascend.light.home;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.kimascend.light.Config;
import com.kimascend.light.R;
import com.kimascend.light.activity.LightSettingActivity;
import com.kimascend.light.adapter.CommonPagerAdapter;
import com.kimascend.light.api.Resource;
import com.kimascend.light.api.Status;
import com.kimascend.light.app.SmartLightApp;
import com.kimascend.light.common.AutoClearValue;
import com.kimascend.light.databinding.FragmentDeviceBinding;
import com.kimascend.light.databinding.ViewRecycleBinding;
import com.kimascend.light.device.DeviceActivity;
import com.kimascend.light.device.entity.Lamp;
import com.kimascend.light.mesh.DefaultMesh;
import com.kimascend.light.model.LightSetting;
import com.kimascend.light.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * 设备页面 包含灯具
 */

public class DeviceFragment extends Fragment {
    public static final String TAG = DeviceFragment.class.getSimpleName();

    private AutoClearValue<FragmentDeviceBinding> binding;
    private HomeViewModel viewModel;
    //灯具
    private LampAdapter2 lampAdapter;

    public DeviceFragment() {
        // Required empty public constructor
    }

    public static DeviceFragment newInstance() {
        Bundle args = new Bundle();
        DeviceFragment fragment = new DeviceFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentDeviceBinding fragmentDeviceBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_device, container, false);
        fragmentDeviceBinding.toolbar.toolbar.inflateMenu(R.menu.fragment_device);
        fragmentDeviceBinding.toolbar.toolbar.setOnMenuItemClickListener(this::onMenuItemClick);

        ViewRecycleBinding viewLampBinding = DataBindingUtil.inflate(inflater, R.layout.view_recycle, container, false);
        List<View> viewList = new ArrayList<>();
        viewList.add(viewLampBinding.getRoot());
        CommonPagerAdapter pagerAdapter = new CommonPagerAdapter(viewList);
        fragmentDeviceBinding.viewPager.setAdapter(pagerAdapter);

//        显示灯具列表
        lampAdapter = new LampAdapter2(mHandleLampListener);
        viewLampBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        viewLampBinding.recyclerView.setAdapter(lampAdapter);

        binding = new AutoClearValue<>(this, fragmentDeviceBinding);

        return fragmentDeviceBinding.getRoot();

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        subscribeUI(viewModel);
        viewModel.deviceListRequest.setValue(1);

    }


    private void subscribeUI(HomeViewModel viewModel) {
        viewModel.deviceListObserver.observe(this, new Observer<Resource<List<Lamp>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<Lamp>> resource) {
                binding.get().setResource(resource);
                List<Lamp> data = resource.data;
                if (data != null) {
                    Log.d(TAG, data.toString());
                }
                lampAdapter.replaceLamps(data);
            }
        });


        viewModel.deleteLampObserver.observe(this, new Observer<Resource<Lamp>>() {
            @Override
            public void onChanged(@Nullable Resource<Lamp> resource) {
                if (Status.ERROR == resource.status) {
                    ToastUtil.showToast(resource.message);
                }
            }
        });


    }


    private OnHandleLampListener mHandleLampListener = new OnHandleLampListener() {
        @Override
        public void onItemClick(Lamp lamp) {
            if (lamp.getBrightness() < 0) {
                ToastUtil.showToast("设备已离线");
            } else {
                LightSetting lightSetting = new LightSetting(lamp);
                LightSettingActivity.start(getActivity(), lightSetting);
            }
        }

        @Override
        public void onEditClick(Lamp lamp) {

        }

        @Override
        public void onDeleteClick(Lamp lamp) {
            DefaultMesh defaultMesh = SmartLightApp.INSTANCE().getDefaultMesh();
            if (defaultMesh.isMine) {
                viewModel.deleteLampRequest.setValue(lamp);
            } else {
                ToastUtil.showToast("不是自己的蓝牙网络");
            }
        }
    };


    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                Intent intent = new Intent(getActivity(), DeviceActivity.class);
                intent.putExtra("action", DeviceActivity.ACTION_ADD_DEVICE);
                //添加类型
                intent.putExtra("type", binding.get().viewPager.getCurrentItem());
                //如果添加成功会设置成功信号
                startActivityForResult(intent, 1);
                return true;
            case R.id.action_setting:
                ToastUtil.showToast("暂未实现");
                return true;
        }
        return false;
    }

    //    接到成功信息 更新灯具列表
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult() called with: requestCode = [" + requestCode + "], resultCode = [" + resultCode + "], data = [" + data + "]");
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            viewModel.deviceListRequest.setValue(1);
        }

    }
}

