package com.kimascend.light.group;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.kimascend.light.CommonItemClickListener;
import com.kimascend.light.R;
import com.kimascend.light.adapter.CommonItemAdapter;
import com.kimascend.light.databinding.GroupFragmentBinding;
import com.kimascend.light.fragment.EditNameFragment;
import com.kimascend.light.fragment.ProduceAvatarFragment;
import com.kimascend.light.home.entity.Group;
import com.kimascend.light.model.CommonItem;
import com.kimascend.light.utils.DialogManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class GroupFragment extends Fragment implements ProduceAvatarFragment.Listener,
        EditNameFragment.Listener ,LampListDialogFragment.Listener{
    public static final String ARGUMENT_EDIT_GROUP = "EDIT_GROUP";

    private GroupViewModel viewModel;
    private GroupFragmentBinding binding;
    private CommonItemAdapter itemAdapter;
    private DialogManager dialogManager;
    private Group group = null;
    private boolean edit = false;

    public static GroupFragment newInstance() {
        return new GroupFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.group_fragment, container, false);
        viewModel = GroupActivity.obtainViewModel(getActivity());
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.setViewModel(viewModel);
        setHasOptionsMenu(true);
        dialogManager = new DialogManager(this);
        return binding.getRoot();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupSnackbar();
        loadData();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.ic_comfirm, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    private void setupSnackbar() {

    }


    private void loadData() {
        if (getArguments() != null) {
            group = getArguments().getParcelable(ARGUMENT_EDIT_GROUP);
            if (group != null) {
                edit = group.getGroupId() > 0;
            }
        }
        viewModel.setGroup(group);
        binding.setEdit(edit);
        List<CommonItem> commonItems = generateItems(group);
        itemAdapter = new CommonItemAdapter(itemClickListener, commonItems);
        binding.recyclerView.setAdapter(itemAdapter);

    }

    public List<CommonItem> generateItems(Group group) {
        List<CommonItem> items = new ArrayList<>();
        CommonItem pic = new CommonItem(0, "图片", false, R.drawable.btn_addpic, true, "");
        CommonItem name = new CommonItem(1, "名称", true, -1, true, edit?  group.getName():"请输入");
        CommonItem device = new CommonItem(2, "设备", true, -1, true, "请添加");
        items.add(pic);
        items.add(name);
        items.add(device);
        return items;
    }

    private CommonItemClickListener itemClickListener = commonItem -> {
        switch (commonItem.pos) {
            case 0:
                ProduceAvatarFragment.newInstance().show(getChildFragmentManager(), ProduceAvatarFragment.TAG);
                break;
            case 1:
                dialogManager.showDialog(EditNameFragment.TAG, EditNameFragment.newInstance(group.getName()));
                break;
            case 2:
                dialogManager.showDialog(LampListDialogFragment.TAG, LampListDialogFragment.newInstance());
                break;

        }
    };

    @Override
    public void onItemClicked(File file) {
        CommonItem item = itemAdapter.getItem(0);
        item.observableValue.set(file.getAbsolutePath());
        group.setIcon(file.getAbsolutePath());

    }

    @Override
    public void onConfirmClick(String content) {
        CommonItem item = itemAdapter.getItem(1);
        item.observableValue.set(content);
        group.setName(content);
    }

    @Override
    public void onClick(String deviceIds) {
        CommonItem item = itemAdapter.getItem(2);
        String[] ids = deviceIds.split(",");
        item.observableValue.set(String.valueOf(ids.length));
        group.setDeviceIds(deviceIds);
    }
}
