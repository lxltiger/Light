package com.kimascend.light.clock;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;

import com.kimascend.light.R;
import com.kimascend.light.common.NavigatorController;
import com.kimascend.light.databinding.ActivityClockBinding;
import com.kimascend.light.scene.Scene;
import com.kimascend.light.scene.SceneFragment;
import com.kimascend.light.scene.SceneViewModel;

public class ClockActivity extends AppCompatActivity {
    public static final String ARGUMENT_ADD_EDIT_CLOCK = "add_edit_clock ";
    private ActivityClockBinding binding;

    public static void start(Context context, Clock clock) {
        Intent intent = new Intent(context, ClockActivity.class);
        intent.putExtra(ARGUMENT_ADD_EDIT_CLOCK, clock);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_clock);

        setupToolBar();

        setupViewModel();

        setupViewFragment();

    }

    private void setupToolBar() {
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        binding.toolbar.setNavigationOnClickListener((v -> finish()));
    }

    private void setupViewModel() {
        ClockViewModel viewModel = obtainViewModel(this);
        binding.setViewModel(viewModel);
        Clock clock = getIntent().getParcelableExtra(ARGUMENT_ADD_EDIT_CLOCK);

        viewModel.loadClock(clock);

        viewModel.completeEvent.observe(this, aVoid -> finish());
    }

    private void setupViewFragment() {

        ClockFragment fragment = (ClockFragment) getSupportFragmentManager().findFragmentById(R.id.fl_container);
        if (fragment == null) {
            fragment = ClockFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fl_container, fragment)
                    .commitNow();
        }


    }

    public static ClockViewModel obtainViewModel(FragmentActivity activity) {
        return ViewModelProviders.of(activity).get(ClockViewModel.class);

    }


}
