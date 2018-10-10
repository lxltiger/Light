package com.kimascend.light.scene;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;

import com.kimascend.light.R;
import com.kimascend.light.databinding.ActivitySceneBinding;

/**
 * single top 模式
 * 情景添加编辑UI
 */

public class SceneActivity extends AppCompatActivity {
    private static final String TAG = SceneActivity.class.getSimpleName();
    public static final String ACTION_SCENE_LIST = "action_scene_list";
    public static final String ARGUMENT_ADD_EDIT_SCENE = "add_edit_scene";
    private ActivitySceneBinding binding;


    public static void start(Context context, Scene scene) {
        Intent intent = new Intent(context, SceneActivity.class);
        intent.putExtra(ARGUMENT_ADD_EDIT_SCENE, scene);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_scene);

        setupToolBar();

        setupViewModel();

        setupViewFragment();


    }




    private void setupViewModel() {
        SceneViewModel viewModel = obtainViewModel(this);
        binding.setViewModel(viewModel);
        Scene scene = getIntent().getParcelableExtra(ARGUMENT_ADD_EDIT_SCENE);
        viewModel.loadScene(scene);

        viewModel.completeEvent.observe(this, aVoid -> finish());
    }

    private void setupToolBar() {
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        binding.toolbar.setNavigationOnClickListener((v -> finish()));
    }

    private void setupViewFragment() {

        SceneFragment fragment = (SceneFragment) getSupportFragmentManager().findFragmentById(R.id.fl_container);
        if (fragment == null) {
            fragment = SceneFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fl_container, fragment)
                    .commitNow();
        }


    }

    public static SceneViewModel obtainViewModel(FragmentActivity activity) {
        return ViewModelProviders.of(activity).get(SceneViewModel.class);

    }



}
