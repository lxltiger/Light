

package com.kimascend.light.home;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.kimascend.light.R;
import com.kimascend.light.adapter.CommonPagerAdapter;
import com.kimascend.light.common.AutoClearValue;
import com.kimascend.light.databinding.FragmentDeviceBinding;
import com.kimascend.light.databinding.ViewRecycleBinding;
import com.kimascend.light.device.DeviceActivity;
import com.kimascend.light.device.entity.Lamp;
import com.kimascend.light.utils.SnackbarUtils;
import com.kimascend.light.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * 设备页面
 */

public class DeviceListFragment extends Fragment {
    public static final String TAG = DeviceListFragment.class.getSimpleName();

    private DeviceListViewModel viewModel;
    //灯具
    private LampAdapter2 lampAdapter;

    public DeviceListFragment() {
    }

    public static DeviceListFragment newInstance() {
        Bundle args = new Bundle();
        DeviceListFragment fragment = new DeviceListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentDeviceBinding fragmentDeviceBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_device, container, false);

        ViewRecycleBinding viewLampBinding = DataBindingUtil.inflate(inflater, R.layout.view_recycle, container, false);
        List<View> viewList = new ArrayList<>();
        viewList.add(viewLampBinding.getRoot());
        CommonPagerAdapter pagerAdapter = new CommonPagerAdapter(viewList);
        fragmentDeviceBinding.viewPager.setAdapter(pagerAdapter);

//        显示灯具列表
        lampAdapter = new LampAdapter2(mHandleLampListener);
        viewLampBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        viewLampBinding.recyclerView.setAdapter(lampAdapter);

        return fragmentDeviceBinding.getRoot();

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(DeviceListViewModel.class);
        viewModel.deviceListObserver.observe(this, resource -> {
            lampAdapter.replace(resource.data);
        });

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_device, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                Intent intent = new Intent(getActivity(), DeviceActivity.class);
                intent.putExtra("action", DeviceActivity.ACTION_ADD_DEVICE);
                startActivity(intent);
                return true;
            case R.id.action_setting:
                ToastUtil.showToast("暂未实现");
                return true;
        }

        return false;
    }


    OnHandleLampListener mHandleLampListener = new OnHandleLampListener() {
        @Override
        public void onItemClick(Lamp lamp) {
            if (lamp.getBrightness() < 0) {
                SnackbarUtils.showSnackbar(getView(), "设备已离线");
            } else {
                boolean on = lamp.getBrightness() > 0;
                viewModel.toggle(!on, lamp.getDevice_id());
            }
        }

        @Override
        public void onDeleteClick(Lamp lamp) {
            viewModel.deleteLamp(lamp);
        }
    };


}

