package com.kimascend.light.mesh;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.kimascend.light.R;
import com.kimascend.light.databinding.ItemHomeBinding;
import com.kimascend.light.home.entity.MyData;

import java.util.ArrayList;
import java.util.List;

/**
 * 首页我的数据
 */
public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {

    private List<MyData> data;


    public HomeAdapter() {
        data =new ArrayList<>();
        data.add(new MyData("室内温度", "23℃", R.drawable.icon_temperature));
        data.add(new MyData("二氧化碳", "正常", R.drawable.icon_co2));
        data.add(new MyData("室内湿度", "68%", R.drawable.icon_humidity));
        data.add(new MyData("空气质量", "优", R.drawable.icon_pm));

    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemHomeBinding binding= DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_home, parent, false);

        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mBinding.setMyData(data.get(position));
        holder.mBinding.executePendingBindings();

    }

    @Override
    public int getItemCount() {
        return data ==null?0: data.size();
    }

     static class ViewHolder extends RecyclerView.ViewHolder {

         private final ItemHomeBinding mBinding;

         public ViewHolder(ItemHomeBinding binding) {
            super(binding.getRoot());
             mBinding = binding;
         }
    }
}

