package com.kimascend.light.device;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.kimascend.light.R;
import com.kimascend.light.databinding.ItemLightAddBinding;
import com.kimascend.light.model.Light;

import java.util.List;

/**
 * 扫描Mesh下蓝牙灯具的列表适配器
 */
public class AddLampAdapter extends RecyclerView.Adapter<AddLampAdapter.ViewHolder> {

    private List<Light> mDeviceInfoList;

    private final OnHandleNewLightListener mHandleNewLightListener;

    public AddLampAdapter(List<Light> lights, OnHandleNewLightListener handleNewLightListener) {
        mHandleNewLightListener = handleNewLightListener;
        mDeviceInfoList = lights;
    }

    public void addLight(Light light) {
        mDeviceInfoList.add(light);
        notifyItemChanged(getItemCount() - 1);
    }

    public void clear() {
        mDeviceInfoList.clear();
        notifyDataSetChanged();

    }


    /**
     * 因为macAddress是唯一的
     * 在灯的状态发生改变的时候 通过meshAddress 来定位Light修改状态
     *
     * @param mac
     */
    public Light getLightByMAC(String mac) {
       if(TextUtils.isEmpty(mac)) return null;
        for (Light light : mDeviceInfoList) {
            if (mac.endsWith(light.raw.macAddress)) {
                return light;
            }
        }
        return null;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemLightAddBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_light_add, parent, false);
        binding.setHandler(mHandleNewLightListener);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mBinding.setLight(mDeviceInfoList.get(position));
        holder.mBinding.executePendingBindings();

    }

    @Override
    public int getItemCount() {
        return mDeviceInfoList == null ? 0 : mDeviceInfoList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final ItemLightAddBinding mBinding;

        public ViewHolder(ItemLightAddBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }
    }
}

