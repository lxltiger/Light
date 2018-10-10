package com.kimascend.light.scene;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.kimascend.light.ItemClickListener;
import com.kimascend.light.R;
import com.kimascend.light.adapter.GeneralItemAdapter;
import com.kimascend.light.common.SnackbarMessage;
import com.kimascend.light.databinding.FragmentSceneBinding;
import com.kimascend.light.databinding.GroupFragmentBinding;
import com.kimascend.light.fragment.EditNameFragment;
import com.kimascend.light.fragment.ProduceAvatarFragment;
import com.kimascend.light.group.LampListDialogFragment;
import com.kimascend.light.model.GeneralItem;
import com.kimascend.light.utils.DialogManager;
import com.kimascend.light.utils.SnackbarUtils;

import java.io.File;


public class SceneFragment extends Fragment implements ProduceAvatarFragment.Listener,
        EditNameFragment.Listener {

    private SceneViewModel viewModel;
    private DialogManager dialogManager;

    public static SceneFragment newInstance() {
        return new SceneFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        FragmentSceneBinding  binding = DataBindingUtil.inflate(inflater, R.layout.fragment_scene, container, false);
        viewModel = SceneActivity.obtainViewModel(getActivity());
        binding.setViewModel(viewModel);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        GeneralItemAdapter itemAdapter = new GeneralItemAdapter(itemClickListener);
        binding.recyclerView.setAdapter(itemAdapter);

        dialogManager = new DialogManager(this);
        setHasOptionsMenu(true);
        return binding.getRoot();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel.snackbarMessage.observe(this,
                (SnackbarMessage.SnackbarObserver) snackbarMessageResourceId -> SnackbarUtils.showSnackbar(getView(), getString(snackbarMessageResourceId)));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.ic_comfirm, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        viewModel.addEditScene();
        return true;
    }


    private ItemClickListener<GeneralItem> itemClickListener = commonItem -> {
        switch (commonItem.getPos()) {
            case 0:
                ProduceAvatarFragment.newInstance().show(getChildFragmentManager(), ProduceAvatarFragment.TAG);
                break;
            case 1:
                String name = viewModel.getScene().getName();
                if ("请输入".equals(name)) {
                    name = "";
                }
                dialogManager.showDialog(EditNameFragment.TAG, EditNameFragment.newInstance(name));
                break;
            case 2:
                dialogManager.showDialog(SceneSettingFragment.TAG, SceneSettingFragment.newInstance());
                break;

        }
    };

    @Override
    public void onItemClicked(File file) {
        viewModel.handleIcon(file);
    }

    @Override
    public void onConfirmClick(String content) {
        viewModel.handleName(content);
    }

}
