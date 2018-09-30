package com.kimascend.light.home;

import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.kimascend.light.R;
import com.kimascend.light.common.DataBoundAdapter;
import com.kimascend.light.databinding.ItemLamp2Binding;
import com.kimascend.light.device.entity.Lamp;

import java.util.Objects;


public class LampAdapter2 extends DataBoundAdapter<Lamp,ItemLamp2Binding> {

    private final OnHandleLampListener handleLampListener;

    public LampAdapter2(OnHandleLampListener handleLampListener) {
        this.handleLampListener = handleLampListener;
    }

    @Override
    protected boolean areContentsTheSame(Lamp oldItem, Lamp newItem) {
        return Objects.equals(oldItem.getId(), newItem.getId()) &&
                Objects.equals(oldItem.getBrightness(), newItem.getBrightness()) &&
                Objects.equals(oldItem.getColor(), newItem.getColor());
    }

    @Override
    protected boolean areItemsTheSame(Lamp oldItem, Lamp newItem) {
        return Objects.equals(oldItem, newItem);
    }

    @Override
    protected void bind(ItemLamp2Binding binding, Lamp lamp) {
        lamp.setDescription(String.format("%s\n%s", lamp.getName(), lamp.getMac()));
        binding.setLamp(lamp);
    }

    @Override
    protected ItemLamp2Binding createBinding(ViewGroup parent) {
        ItemLamp2Binding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_lamp2, parent, false);
        binding.setHandler(handleLampListener);
        return binding;
    }

}

