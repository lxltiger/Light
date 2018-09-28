package com.kimascend.light.device;

import android.databinding.DataBindingUtil;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.kimascend.light.R;
import com.kimascend.light.common.Consumer;
import com.kimascend.light.common.DataBoundAdapter;
import com.kimascend.light.databinding.ItemLightAddBinding;
import com.kimascend.light.model.Light;

/**
 * 扫描Mesh下蓝牙灯具的列表适配器
 */
public class AddLampAdapter extends DataBoundAdapter<Light,ItemLightAddBinding> {

    private final Consumer<Light> clickCallBack;


    public AddLampAdapter( Consumer<Light> handleNewLightListener) {
        clickCallBack = handleNewLightListener;
    }


    public void addLight(Light light) {
        add(light);
    }


    @Override
    protected void clear() {
        super.clear();
    }

    /**
     * 因为macAddress是唯一的
     * 在灯的状态发生改变的时候 通过meshAddress 来定位Light修改状态
     *
     * @param mac
     */
    public Light getLightByMAC(String mac) {
        if(TextUtils.isEmpty(mac)) return null;
        return get(light -> mac.endsWith(light.raw.macAddress));

    }

    @Override
    protected boolean areContentsTheSame(Light oldItem, Light newItem) {
        return false;
    }

    @Override
    protected boolean areItemsTheSame(Light oldItem, Light newItem) {
        return false;
    }

    @Override
    protected void bind(ItemLightAddBinding binding, Light item) {
        binding.setLight(item);
    }

    @Override
    protected ItemLightAddBinding createBinding(ViewGroup parent) {
        ItemLightAddBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_light_add, parent, false);
        binding.setHandler(clickCallBack);
        return binding;
    }

}

