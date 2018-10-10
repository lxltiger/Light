package com.kimascend.light.home;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
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
import com.kimascend.light.api.Resource;
import com.kimascend.light.command.SceneCommand;
import com.kimascend.light.command.TelinkSceneCommand;
import com.kimascend.light.databinding.FragmentSceneListBinding;
import com.kimascend.light.scene.SceneActivity;
import com.kimascend.light.scene.OnHandleSceneListener;
import com.kimascend.light.scene.Scene;
import com.kimascend.light.scene.SceneAdapter;
import com.kimascend.light.scene.SceneViewModel;

import java.util.List;

/**
 * 情景列表页面，点击条目直接加载设置
 *
 */
public class SceneListFragment extends Fragment /*implements CallBack*/ {
    public static final String TAG = SceneListFragment.class.getSimpleName();
    private SceneAdapter sceneAdapter;
    private SceneCommand sceneCommand;


    public SceneListFragment() {
    }

    public static SceneListFragment newInstance() {
        Bundle args = new Bundle();
        SceneListFragment fragment = new SceneListFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentSceneListBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_scene_list, container, false);
        binding.scenes.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        sceneAdapter = new SceneAdapter(mHandleSceneListener);
        binding.scenes.setAdapter(sceneAdapter);
        sceneCommand = new TelinkSceneCommand();
        setHasOptionsMenu(true);

        return binding.getRoot();
    }

    private OnHandleSceneListener mHandleSceneListener = new OnHandleSceneListener() {
        @Override
        public void onItemClick(Scene scene) {
            byte redundant=0;
            sceneCommand.setSceneAddress(scene.getSceneId());
            sceneCommand.setDstAddress(0xffff);
            sceneCommand.handleSceneOperation(SceneCommand.SceneOperation.LOAD,redundant,redundant,redundant,redundant);
        }

        @Override
        public void onEditClick(Scene scene) {
            SceneActivity.start(getContext(), scene);
        }
    };


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        HomeViewModel viewModel = ViewModelProviders.of(getActivity()).get(HomeViewModel.class);
        viewModel.sceneListObserver.observe(this, new Observer<List<Scene>>() {
            @Override
            public void onChanged(@Nullable List<Scene> scenes) {
                if (scenes != null) {
                    sceneAdapter.addScenes(scenes);
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
                SceneActivity.start(getContext(), new Scene());
                return true;
        }

        return false;
    }


}
