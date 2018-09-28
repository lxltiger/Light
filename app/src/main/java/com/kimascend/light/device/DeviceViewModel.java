package com.kimascend.light.device;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.kimascend.light.api.ApiResponse;
import com.kimascend.light.device.entity.AddHubRequest;
import com.kimascend.light.model.Light;
import com.kimascend.light.model.RequestResult;
import com.kimascend.light.repository.HomeRepository;

import java.util.Map;

public class DeviceViewModel extends AndroidViewModel {
    private static final String TAG = "DeviceViewModel";
    private HomeRepository repository;

    /**
     * Fragment 与Activity的通信
     */
    public final MutableLiveData<Integer> navigation = new MutableLiveData<>();

    //    添加lamp请求
    public final MutableLiveData<Map<String, String>> addLampRequest = new MutableLiveData<>();
    public final LiveData<ApiResponse<RequestResult>> addLampObserver;


    //    添加Hub请求
    public final MutableLiveData<AddHubRequest> addHubRequest = new MutableLiveData<>();
    public final LiveData<ApiResponse<RequestResult>> addHubObserver;

    // 获取灯具mesh address
    public final MediatorLiveData<Light> lampMeshAddressObserver=new MediatorLiveData<>();

    //我的mesh列表
//    public LiveData<List<Mesh>> myMeshList;

    public DeviceViewModel(@NonNull Application application) {
        super(application);
        repository = HomeRepository.INSTANCE(application);
        addLampObserver = Transformations.switchMap(addLampRequest, input -> repository.reportDevice(input));
        addHubObserver = Transformations.switchMap(addHubRequest, addHubRequest -> repository.reportHub(addHubRequest));

    }

    public void getDeviceMeshAddress(Light light) {
        LiveData<ApiResponse<RequestResult>> deviceId = repository.getDeviceId();
        lampMeshAddressObserver.addSource(deviceId, new Observer<ApiResponse<RequestResult>>() {
            @Override
            public void onChanged(@Nullable ApiResponse<RequestResult> apiResponse) {
                lampMeshAddressObserver.removeSource(deviceId);
                if (apiResponse.isSuccessful() && apiResponse.body.succeed()) {
                    light.raw.meshAddress = Integer.parseInt(apiResponse.body.resultMsg);
                    lampMeshAddressObserver.setValue(light);
                }else{
                    lampMeshAddressObserver.setValue(null);
                }
            }
        });
    }





}
