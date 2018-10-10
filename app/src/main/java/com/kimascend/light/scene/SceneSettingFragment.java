package com.kimascend.light.scene;

import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kimascend.light.R;
import com.kimascend.light.app.SmartLightApp;
import com.kimascend.light.databinding.FragmentSceneSettingBinding;
import com.kimascend.light.utils.MeshEventManager;

/**
 * 场景下灯具设置
 */
public class SceneSettingFragment extends BottomSheetDialogFragment  {
    public static final String TAG = SceneSettingFragment.class.getSimpleName();
    private LampForSceneAdapter lampAdapter;
    private SceneViewModel viewModel;
    private BottomSheetBehavior<View> mBottomSheetBehavior;

    public static SceneSettingFragment newInstance() {
        final SceneSettingFragment fragment = new SceneSettingFragment();
        final Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        FragmentSceneSettingBinding  binding = DataBindingUtil.inflate(inflater, R.layout.fragment_scene_setting, null, false);
        viewModel = SceneActivity.obtainViewModel(getActivity());
        binding.setViewModel(viewModel);

        MeshEventManager.bindListenerForLightSetting(this, viewModel.eventListener, SmartLightApp.INSTANCE());

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        lampAdapter = new LampForSceneAdapter(viewModel.sceneSettingListener);
        binding.recyclerView.setAdapter(lampAdapter);
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
//            int height = heightPixels / 2;
            mBottomSheetBehavior.setPeekHeight(heightPixels*3/4);
//                parent.setBackgroundColor(Color.TRANSPARENT);
        });


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel.lampsObserver.observe(this, lamps -> lampAdapter.addLamps(lamps));
        viewModel.dismissEvent.observe(this,aVoid -> dismiss());
    }


}
