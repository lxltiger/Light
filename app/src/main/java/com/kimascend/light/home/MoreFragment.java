package com.kimascend.light.home;


import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kimascend.light.CallBack;
import com.kimascend.light.Config;
import com.kimascend.light.R;
import com.kimascend.light.clock.ClockActivity;
import com.kimascend.light.common.AutoClearValue;
import com.kimascend.light.databinding.FragmentMoreBinding;
import com.kimascend.light.model.User;
import com.kimascend.light.scene.SceneActivity;
import com.kimascend.light.user.UserActivity;

/**
 * A simple {@link Fragment} subclass.
 * 主页更多界面
 */
public class MoreFragment extends Fragment implements CallBack {


    public static final String TAG = MoreFragment.class.getSimpleName();
    private AutoClearValue<FragmentMoreBinding> binding;
    private HomeViewModel viewModel;

    public static MoreFragment newInstance() {
        Bundle args = new Bundle();
        MoreFragment fragment = new MoreFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentMoreBinding moreBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_more, container, false);
        moreBinding.setHandler(this);
        binding = new AutoClearValue<>(this, moreBinding);
        return moreBinding.getRoot();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        subscribeUI(viewModel);
    }

    @Override
    public void onStart() {
        super.onStart();
        viewModel.userInfoRequest.setValue(1);
    }

    private void subscribeUI(HomeViewModel homeViewModel) {
        homeViewModel.userInfoObserver.observe(this, new Observer<User>() {
            @Override
            public void onChanged(@Nullable User user) {
                Log.d(TAG, "onChanged: user");
                if (user != null) {
                    binding.get().setHomeIcon(Config.IMG_PREFIX.concat(user.getIcon()));
                    binding.get().setName(user.getAccount());
                } else {
                    Log.d(TAG, "onChanged: null");
                    binding.get().setHomeIcon("");
                    binding.get().setName("未设置");
                }
            }
        });
    }

    public void handleClick(View v) {
        switch (v.getId()) {
            case R.id.portrait://点击头像
                break;
            case R.id.btn_scene: {
               /* Intent intent = new Intent(getActivity(), SceneActivity.class);
                intent.putExtra("action", SceneActivity.ACTION_SCENE_LIST);
                startActivityForResult(intent, 10);*/
            }
            break;
            case R.id.btn_clock:
//                ClockActivity.start(getActivity(), ClockActivity.ACTION_CLOCK_LIST, null);
                break;
            case R.id.about_us:
                UserActivity.start(getActivity(), UserActivity.ACTION_ABOUT_US);
                break;
            case R.id.feed_back:
                UserActivity.start(getActivity(), UserActivity.ACTION_FEED_BACK);
                break;
            case R.id.setting: {
                Intent intent = new Intent(getActivity(), UserActivity.class);
                intent.putExtra("action", UserActivity.ACTION_SETTING);
                //有可能退出
                startActivityForResult(intent, Activity.RESULT_FIRST_USER);
            }
            break;

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult() called with: requestCode = [" + requestCode + "], resultCode = [" + resultCode + "], data = [" + data + "]");
        if (requestCode == Activity.RESULT_FIRST_USER && resultCode == Activity.RESULT_OK) {
//            UserActivity是singleTop 模式
            UserActivity.start(getActivity(), UserActivity.ACTION_LOGIN);
            getActivity().finish();
        }
    }


}
