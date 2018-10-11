package com.kimascend.light.clock;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.kimascend.light.R;
import com.kimascend.light.common.DataBoundAdapter;
import com.kimascend.light.databinding.ItemClockBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 闹钟列表适配器
 */
public class ClockAdapter extends DataBoundAdapter<Clock, ItemClockBinding> {


    private final OnHandleClockListener handleClockListener;


    public ClockAdapter(OnHandleClockListener handleLampListener) {
        handleClockListener = handleLampListener;
    }


    @Override
    protected boolean areContentsTheSame(Clock oldItem, Clock newItem) {
        return oldItem.equals(newItem);
    }

    @Override
    protected boolean areItemsTheSame(Clock oldItem, Clock newItem) {
        return Objects.equals(oldItem.getId(),newItem.getId());
    }

    @Override
    protected void bind(ItemClockBinding binding, Clock item) {
        binding.setClock(item);
    }

    @Override
    protected ItemClockBinding createBinding(ViewGroup parent) {
        ItemClockBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_clock, parent, false);
        binding.setListener(handleClockListener);
        return binding;
    }


}

