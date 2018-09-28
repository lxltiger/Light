package com.kimascend.light.activity;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;
import android.util.Pair;

import com.kimascend.light.api.ApiResponse;
import com.kimascend.light.api.KimAscendService;
import com.kimascend.light.api.NetWork;
import com.kimascend.light.api.Resource;
import com.kimascend.light.common.RequestCreator;
import com.kimascend.light.device.entity.Lamp;
import com.kimascend.light.model.RequestResult;

import okhttp3.RequestBody;

public class LightSettingRepository {

    private final KimAscendService kimService;


    public LightSettingRepository() {
        this.kimService =  NetWork.kimService();;
    }


    public LiveData<Resource<Lamp>> createDeviceSetting(Pair<String,Lamp> lightSetting) {
        MediatorLiveData<Resource<Lamp>> result=new MediatorLiveData<>();
        result.setValue(Resource.loading(null));
        RequestBody requestBody = RequestCreator.createDeviceSetting(lightSetting);
        LiveData<ApiResponse<RequestResult>> deviceSetting = kimService.createDeviceSetting(requestBody);
        result.addSource(deviceSetting, new Observer<ApiResponse<RequestResult>>() {
            @Override
            public void onChanged(@Nullable ApiResponse<RequestResult> apiResponse) {
                result.removeSource(deviceSetting);
                if (apiResponse!=null&&apiResponse.isSuccessful()&&apiResponse.body.succeed()) {
                    result.setValue(Resource.success(lightSetting.second,""));
                }else{
                    result.setValue(Resource.error(null,"创建失败"));
                }
            }
        });

        return result;
    }
}
