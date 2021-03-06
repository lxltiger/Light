package com.kimascend.light.device;


import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.kimascend.light.R;
import com.kimascend.light.app.SmartLightApp;
import com.kimascend.light.common.SnackbarMessage;
import com.kimascend.light.databinding.FragmentAddLampBinding;
import com.kimascend.light.utils.MeshEventManager;
import com.kimascend.light.utils.SnackbarUtils;

/**
 * 扫面添加灯具页面
 *  note：选择在OnResume开始启动扫描  OnPause停止 是因为防止和HomeActivity的连接操作冲突，
 *  即：这个页面finish时，会先执行HomeActivity的OnStart启动了连接，后执行此页面的OnStop，他们对蓝牙的操作是一个对象
 *
 */
public class AddLampFragment extends Fragment {
    public static final String TAG = AddLampFragment.class.getSimpleName();

    private AddLampAdapter lampAdapter;

    private AddLampViewModel viewModel;

    public static AddLampFragment newInstance() {
        Bundle args = new Bundle();
        AddLampFragment fragment = new AddLampFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        viewModel = ViewModelProviders.of(this).get(AddLampViewModel.class);
//        处理生命周期对蓝牙事件的监听和移除
        MeshEventManager.bindListenerForAddLamp(this,viewModel::callBack,SmartLightApp.INSTANCE());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentAddLampBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_lamp, container, false);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        lampAdapter = new AddLampAdapter(viewModel::addLamp);
        binding.recyclerView.setAdapter(lampAdapter);
        return binding.getRoot();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel.getScanningObservable().observe(this, scanning -> {
            if (scanning != null&&getActivity()!=null) {
                getActivity().invalidateOptionsMenu();
            }
        });

        viewModel.getLightObserver().observe(this, lampAdapter::set);

        viewModel.getSnackbarMessage().observe(this, (SnackbarMessage.SnackbarObserver) snackbarMessageResourceId ->
                SnackbarUtils.showSnackbar(getView(), getString(snackbarMessageResourceId)));
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.scanLeDevice(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Boolean value = viewModel.getScanningObservable().getValue();
        boolean scanning=value==null?true:value;
        inflater.inflate(R.menu.fragment_add_lamp, menu);
        menu.findItem(R.id.menu_stop).setVisible(scanning);
        menu.findItem(R.id.menu_scan).setVisible(!scanning);
        if (scanning) {
            menu.findItem(R.id.menu_refresh).setActionView(R.layout.actionbar_indeterminate_progress);
        }else {
            menu.findItem(R.id.menu_refresh).setActionView(null);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_scan:
                viewModel.scanLeDevice(true);
                break;
            case R.id.menu_stop:
                viewModel.scanLeDevice(false);
                break;
        }
        if (getActivity()!=null) {
//            会调用onCreateOptionsMenu重新渲染Menu
            getActivity().invalidateOptionsMenu();
        }
        return true;
    }

    @Override
    public void onPause() {
        super.onPause();
        viewModel.scanLeDevice(false);
        lampAdapter.set(null);
    }

}