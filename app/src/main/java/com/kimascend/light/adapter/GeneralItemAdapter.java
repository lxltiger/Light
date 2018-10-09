
package com.kimascend.light.adapter;

import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.kimascend.light.CommonItemClickListener;
import com.kimascend.light.ItemClickListener;
import com.kimascend.light.R;
import com.kimascend.light.common.DataBoundAdapter;
import com.kimascend.light.databinding.ItemGeneralBinding;
import com.kimascend.light.model.GeneralItem;


/**
 * 常用条目适配器
 */

public class GeneralItemAdapter extends DataBoundAdapter<GeneralItem,ItemGeneralBinding> {


    private final ItemClickListener<GeneralItem> itemClickListener;


    public GeneralItemAdapter(ItemClickListener<GeneralItem> mHandleSceneListener) {
        this.itemClickListener = mHandleSceneListener;
    }

    @Override
    protected boolean areContentsTheSame(GeneralItem oldItem, GeneralItem newItem) {
        return oldItem.equals(newItem);
    }

    @Override
    protected boolean areItemsTheSame(GeneralItem oldItem, GeneralItem newItem) {
        return oldItem.getPos()==newItem.getPos();
    }

    @Override
    protected void bind(ItemGeneralBinding binding, GeneralItem item) {
            binding.setItem(item);
    }

    @Override
    protected ItemGeneralBinding createBinding(ViewGroup parent) {
        ItemGeneralBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_general, parent, false);
        binding.setCallBack(itemClickListener);
        return binding;
    }


}


