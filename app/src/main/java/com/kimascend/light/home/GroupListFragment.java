package com.kimascend.light.home;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.kimascend.light.R;
import com.kimascend.light.activity.LightSettingActivity;
import com.kimascend.light.api.ApiResponse;
import com.kimascend.light.databinding.FragmentGroupListBinding;
import com.kimascend.light.device.DeviceActivity;
import com.kimascend.light.group.GroupActivity;
import com.kimascend.light.group.GroupFragment;
import com.kimascend.light.home.entity.Group;
import com.kimascend.light.home.entity.GroupList;
import com.kimascend.light.model.LightSetting;
import com.kimascend.light.scene.GroupSceneActivity;
import com.kimascend.light.utils.ToastUtil;

import java.util.List;

/**
 * 场景列表页面
 * 从本地数据库获取场景列表
 */
public class GroupListFragment extends Fragment {
    public static final String TAG = GroupListFragment.class.getSimpleName();
    private GroupAdapter groupAdapter;
    private HomeViewModel viewModel;


    public GroupListFragment() {
        // Required empty public constructor
    }

    public static GroupListFragment newInstance() {
        Bundle args = new Bundle();
        GroupListFragment fragment = new GroupListFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentGroupListBinding mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_group_list, container, false);
        mBinding.scenes.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        groupAdapter = new GroupAdapter(mHandleSceneListener);
        mBinding.scenes.setAdapter(groupAdapter);
        return mBinding.getRoot();
    }

    private OnHandleGroupListener mHandleSceneListener = new OnHandleGroupListener() {
        @Override
        public void onItemClick(Group group) {
            LightSetting lightSetting = new LightSetting(group.getGroupId());
            LightSettingActivity.start(getActivity(),lightSetting);
        }

        @Override
        public void onEditClick(Group group) {
            Intent intent = new Intent(getActivity(), GroupActivity.class);
            intent.putExtra(GroupFragment.ARGUMENT_EDIT_GROUP, group);
            startActivity(intent);

        }

        @Override
        public void onDeleteClick(Group scene) {
        }
    };


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(HomeViewModel.class);
        viewModel.groupListObserver.observe(this, new Observer<List<Group>>() {
            @Override
            public void onChanged(@Nullable List<Group> list) {
                if (list != null) {
                    groupAdapter.addScenes(list);
                }
            }
        });
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
                Intent intent = new Intent(getActivity(), GroupActivity.class);
                Group group=new Group();
                intent.putExtra(GroupFragment.ARGUMENT_EDIT_GROUP, group);
                startActivity(intent);
                return true;
        }

        return false;
    }


}
