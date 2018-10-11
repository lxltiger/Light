package com.kimascend.light.home;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
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
import android.widget.CompoundButton;

import com.kimascend.light.R;
import com.kimascend.light.clock.Clock;
import com.kimascend.light.clock.ClockActivity;
import com.kimascend.light.clock.ClockAdapter;
import com.kimascend.light.clock.ClockViewModel;
import com.kimascend.light.clock.OnHandleClockListener;
import com.kimascend.light.databinding.FragmentClockListBinding;
import com.kimascend.light.utils.ToastUtil;

import java.util.List;

/**
 * 闹钟列表
 */
public class ClockListFragment extends Fragment {
    public static final String TAG = ClockListFragment.class.getSimpleName();
    private ClockAdapter clockAdapter;
    private HomeViewModel viewModel;


    public ClockListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public static ClockListFragment newInstance() {
        Bundle args = new Bundle();
        ClockListFragment fragment = new ClockListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentClockListBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_clock_list, container, false);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        clockAdapter = new ClockAdapter(handleClockListener);
        binding.recyclerView.setAdapter(clockAdapter);
        return binding.getRoot();
    }

    private OnHandleClockListener handleClockListener = new OnHandleClockListener() {
        @Override
        public void onItemClick(Clock clock) {
            ClockActivity.start(getContext(), clock);
        }

        @Override
        public void onItemDelete(Clock clock) {
            viewModel.deleteClick(clock);
        }


        @Override
        public void onSwitchClick(Clock clock) {
            viewModel.switchClock(clock);
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked, Clock clock) {
            Log.d(TAG, "onCheckedChanged() calle, isChecked = [" + isChecked + "], clock = [" + clock + "]");
        }
    };


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        viewModel.clockListObserver.observe(this, list -> clockAdapter.replace(list));


       /* viewModel.switchClockObserver.observe(this, new Observer<Clock>() {
            @Override
            public void onChanged(@Nullable Clock clock) {
                if (clock != null) {
                    clockAdapter.notifyDataSetChanged();
                }else{
                    ToastUtil.showToast("切换失败");
                }
            }
        });*/
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.icon_add, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                ClockActivity.start(getContext(), new Clock());
                return true;
        }
        return false;
    }


}
