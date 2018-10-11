package com.kimascend.light.clock;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.kimascend.light.CallBack;
import com.kimascend.light.R;
import com.kimascend.light.common.SnackbarMessage;
import com.kimascend.light.databinding.FragmentClockBinding;
import com.kimascend.light.utils.SnackbarUtils;

/**
 * 闹钟的添加和编辑页面
 */
public class ClockFragment extends Fragment implements TimePickerFragment.Listener, WeekDayFragment.Listener, CallBack {
    public static final String TAG = ClockFragment.class.getSimpleName();
    private FragmentClockBinding binding;
    private ClockViewModel viewModel;


    public static ClockFragment newInstance() {
        ClockFragment fragment = new ClockFragment();
        Bundle args = new Bundle();
//        args.putParcelable("clock", clock);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_clock, container, false);
        binding.setHandler(this);
        setHasOptionsMenu(true);
        return binding.getRoot();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ClockActivity.obtainViewModel(getActivity());
        binding.setViewModel(viewModel);
        viewModel.snackbarMessage.observe(this,
                (SnackbarMessage.SnackbarObserver) snackbarMessageResourceId -> SnackbarUtils.showSnackbar(getView(), getString(snackbarMessageResourceId)));
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.ic_comfirm, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        viewModel.addEditClock();
        return true;
    }

    @Override
    public void handleClick(View v) {
        switch (v.getId()) {
            case R.id.setTime:
                TimePickerFragment.newInstance().show(getChildFragmentManager(), TimePickerFragment.TAG);
                break;
            case R.id.epoch:
                WeekDayFragment.newInstance(viewModel.getClock().getRepeat()).show(getChildFragmentManager(), WeekDayFragment.TAG);
                break;
            case R.id.devices:
                ClockSettingFragment.newInstance().show(getChildFragmentManager(), ClockSettingFragment.TAG);
                break;
        }
    }


    @Override
    public void onTimeSet(int hour, int min) {
        viewModel.setTime(hour, min);
    }

    @Override
    public void onWeekDaySet(String weekDays) {
        viewModel.setWeek(weekDays);
    }
}
