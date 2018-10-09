package com.kimascend.light.group;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;

import com.kimascend.light.R;
import com.kimascend.light.app.SmartLightApp;
import com.kimascend.light.databinding.GroupActivityBinding;
import com.kimascend.light.home.entity.Group;

public class GroupActivity extends AppCompatActivity {

    private GroupActivityBinding binding;
    private GroupViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.group_activity);

        setupToolBar();

        setupViewModel();

        setupViewFragment();

    }


    private void setupViewModel() {
        viewModel = obtainViewModel(this);
        binding.setViewModel(viewModel);
        Group group = getIntent().getParcelableExtra(GroupFragment.ARGUMENT_EDIT_GROUP);
        viewModel.loadGroup(group);

        viewModel.completeEvent.observe(this, aVoid -> finish());
    }

    private void setupToolBar() {
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        binding.toolbar.setNavigationOnClickListener((v -> finish()));
    }

    private void setupViewFragment() {

        GroupFragment fragment = (GroupFragment) getSupportFragmentManager().findFragmentById(R.id.fl_container);
        if (fragment == null) {
            fragment = GroupFragment.newInstance();
//            Bundle bundle = new Bundle();
//            binding.setTitle(getIntent().getParcelableExtra(GroupFragment.ARGUMENT_EDIT_GROUP) == null ? "新建场景" : "修改场景");
//            bundle.putParcelable(GroupFragment.ARGUMENT_EDIT_GROUP, getIntent().getParcelableExtra(GroupFragment.ARGUMENT_EDIT_GROUP));
//            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fl_container, fragment)
                    .commitNow();
        }


    }

    public static GroupViewModel obtainViewModel(FragmentActivity activity) {
        GroupViewModel.Factory factory = new GroupViewModel.Factory(SmartLightApp.INSTANCE());
        return ViewModelProviders.of(activity, factory).get(GroupViewModel.class);

    }
}
