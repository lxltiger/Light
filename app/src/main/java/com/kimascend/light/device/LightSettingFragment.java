package com.kimascend.light.device;


import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kimascend.light.R;
import com.kimascend.light.app.SmartLightApp;
import com.kimascend.light.databinding.FragmentLightSettingBinding;
import com.kimascend.light.device.entity.Lamp;
import com.kimascend.light.utils.BundleConstant;
import com.kimascend.light.utils.MeshEventManager;

/**
 * 单个灯具的亮度设置
 *
 */
@Deprecated
public class LightSettingFragment extends Fragment  {
    public static final String TAG = LightSettingFragment.class.getSimpleName();
    private FragmentLightSettingBinding binding;


    public LightSettingFragment() {
    }

    public static LightSettingFragment newInstance(Lamp lamp) {
        Bundle args = new Bundle();
        args.putParcelable("lamp", lamp);
        LightSettingFragment fragment = new LightSettingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static LightSettingFragment newInstance(Bundle bundle) {
        LightSettingFragment fragment = new LightSettingFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_light_setting, container, false);
        binding.titleBar.toolbar.setNavigationOnClickListener((view)->getActivity().finish());
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Lamp lamp = getArguments().getParcelable(BundleConstant.LAMP);
        //使用工厂注入mesh address
        DeviceControlViewModel.Factory factory = new DeviceControlViewModel.Factory(lamp.getDevice_id(),lamp.getBrightness());
        DeviceControlViewModel viewModel = ViewModelProviders.of(this, factory).get(DeviceControlViewModel.class);
//        使用Life observer监听页面的生命周期 并交给view model处理
        MeshEventManager.bindListenerForLightSetting(this, viewModel.eventListener, SmartLightApp.INSTANCE());
//        逻辑都在view model
        binding.setViewModel(viewModel);
        binding.ivRgb.setOnColorChangedListenner(viewModel::onColorChanged);

    }


}
