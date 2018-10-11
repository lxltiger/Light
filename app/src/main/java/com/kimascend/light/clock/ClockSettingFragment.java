package com.kimascend.light.clock;

import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kimascend.light.R;
import com.kimascend.light.common.BindingAdapters;
import com.kimascend.light.databinding.FragmentLampListDialogBinding;
import com.kimascend.light.device.entity.Lamp;
import com.kimascend.light.group.GroupActivity;
import com.kimascend.light.group.GroupViewModel;
import com.kimascend.light.home.LampAdapter;
import com.kimascend.light.home.OnHandleLampListener;
import com.kimascend.light.utils.ToastUtil;

/**
 * 灯具列表，可以作为对话框 从页面底部弹出
 */
public class ClockSettingFragment extends BottomSheetDialogFragment  {
    public static final String TAG = ClockSettingFragment.class.getSimpleName();
    private LampAdapter lampAdapter;
    private ClockViewModel viewModel;
    private BottomSheetBehavior<View> mBottomSheetBehavior;

    public static ClockSettingFragment newInstance() {
        final ClockSettingFragment fragment = new ClockSettingFragment();
        final Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        FragmentLampListDialogBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_lamp_list_dialog, null, false);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        lampAdapter = new LampAdapter(mOnHandleLampListener);
        //显示是否选中图片
        lampAdapter.setShowSelectIcon(true);
        binding.recyclerView.setAdapter(lampAdapter);
        viewModel = ClockActivity.obtainViewModel(getActivity());
//        binding.setListener(viewModel::OnClick);
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();

        if (dialog != null) {
            View bottomSheet = dialog.findViewById(R.id.design_bottom_sheet);
            bottomSheet.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
        }
        final View view = getView();
        view.post(() -> {
            View parent = (View) view.getParent();
            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) (parent).getLayoutParams();
            CoordinatorLayout.Behavior behavior = params.getBehavior();
            mBottomSheetBehavior = (BottomSheetBehavior) behavior;
            int heightPixels = getResources().getDisplayMetrics().heightPixels;
            //设置高度
            int height = heightPixels / 2;
            mBottomSheetBehavior.setPeekHeight(height);
//                parent.setBackgroundColor(Color.TRANSPARENT);
        });


    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel.lampsObserver.observe(this, lamps -> lampAdapter.addLamps(lamps));

    }


    private OnHandleLampListener mOnHandleLampListener = new OnHandleLampListener() {
        @Override
        public void onItemClick(Lamp lamp) {
            lampAdapter.markLamp(lamp);
            viewModel.markLamp(lamp);
            dismiss();
            /*int status = lamp.lampStatus.get();
            if (BindingAdapters.LIGHT_HIDE == status) {
                lamp.lampStatus.set(BindingAdapters.LIGHT_SELECTED);
            } else {
                lamp.lampStatus.set(BindingAdapters.LIGHT_HIDE);
            }*/
        }


        @Override
        public void onDeleteClick(Lamp lamp) {
        }
    };



}
