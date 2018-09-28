package com.kimascend.light.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.kimascend.light.R;
import com.kimascend.light.app.SmartLightApp;
import com.kimascend.light.home.HomeActivity;
import com.kimascend.light.user.Profile;
import com.kimascend.light.user.UserActivity;

/**
 * 启动页
 */
public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        handleNavigate();
    }


    private void handleNavigate() {
        Profile profile=SmartLightApp.INSTANCE().getProfile();
        Intent intent = null;
        if (profile != null) {
            intent = new Intent(this, HomeActivity.class);
        } else {
            intent = new Intent(this, UserActivity.class);
            intent.putExtra("action", UserActivity.ACTION_LOGIN);
        }
        startActivity(intent);
        finish();
    }


}
