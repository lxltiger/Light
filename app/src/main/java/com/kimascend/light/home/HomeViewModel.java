package com.kimascend.light.home;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;

import com.kimascend.light.api.ApiResponse;
import com.kimascend.light.api.Resource;
import com.kimascend.light.device.entity.Lamp;
import com.kimascend.light.home.entity.GroupList;
import com.kimascend.light.home.entity.Hub;
import com.kimascend.light.home.entity.HubList;
import com.kimascend.light.model.User;
import com.kimascend.light.repository.HomeRepository;
import com.kimascend.light.scene.Scene;
import com.telink.bluetooth.light.OnlineStatusNotificationParser;

import java.util.List;

/**
 * 负责首页各页面的网络请求 并提供数据返回接口供UI监听
 */
public class HomeViewModel extends AndroidViewModel {
    private static final String TAG = HomeViewModel.class.getSimpleName();
    private HomeRepository repository;

    //    用户资料
//    public final LiveData<Profile> profile;
    //    默认的Mesh
//    public final LiveData<DefaultMesh> defaultMeshObserver;

    //    分享mesh
    public MutableLiveData<String> shareMeshRequest = new MutableLiveData<>();
    // 分享mesh监听
    public final LiveData<Resource<Boolean>> shareMeshObserver;


    // lamp列表请求
    public MutableLiveData<Integer> lampListRequest = new MutableLiveData<>();
    // lamp列表监听
//    public final LiveData<Resource<List<Lamp>>> lampListObserver;

    // 插座列表请求
    public MutableLiveData<Integer> socketListRequest = new MutableLiveData<>();
//    public final LiveData<Resource<List<Lamp>>> socketListObserver;

    // 面板列表请求
    public MutableLiveData<Integer> panelListRequest = new MutableLiveData<>();
//    public final LiveData<Resource<List<Lamp>>> panelListObserver;

    public MutableLiveData<Integer> deviceListRequest = new MutableLiveData<>();
    public final LiveData<Resource<List<Lamp>>> deviceListObserver;


    // hub列表请求
    public MutableLiveData<Integer> hubListRequest = new MutableLiveData<>();
    // hub列表监听
    public final LiveData<ApiResponse<HubList>> hubListObserver;

    //    删除lamp请求
    public final MutableLiveData<Lamp> deleteLampRequest = new MutableLiveData<>();
    public final LiveData<Resource<Lamp>> deleteLampObserver;

    //    删除Hub请求
    public final MutableLiveData<Hub> deleteHubRequest = new MutableLiveData<>();
    public final LiveData<Resource<Hub>> deleteHubObserver;

    // 场景列表请求
    public MutableLiveData<Integer> groupListRequest = new MutableLiveData<>();
    //场景列表监听
    public final LiveData<ApiResponse<GroupList>> groupListObserver;

    //情景列表
    public final MutableLiveData<Integer> sceneListRequest = new MutableLiveData<>();
    public final LiveData<Resource<List<Scene>>> sceneListObserver;


    public final MutableLiveData<Integer> userInfoRequest = new MutableLiveData<>();
    public final LiveData<User> userInfoObserver;



    public HomeViewModel(@NonNull Application application) {
        super(application);
        repository = HomeRepository.INSTANCE(application);

        shareMeshObserver = Transformations.switchMap(shareMeshRequest, repository::shareMesh);

//        lampListObserver = Transformations.switchMap(lampListRequest, input -> repository.getDeviceList(input));
//        socketListObserver = Transformations.switchMap(socketListRequest, input -> repository.getDeviceList(input));
//        panelListObserver = Transformations.switchMap(panelListRequest, input -> repository.getDeviceList(input));

        deviceListObserver = Transformations.switchMap(deviceListRequest, input -> repository.getDeviceList(input));

        hubListObserver = Transformations.switchMap(hubListRequest, input -> repository.getHubList(input));

        groupListObserver = Transformations.switchMap(groupListRequest, input -> repository.getGroupList(input));

        deleteLampObserver = Transformations.switchMap(deleteLampRequest, input -> repository.deleteDevice(input));

        sceneListObserver = Transformations.switchMap(sceneListRequest, repository::getSceneList);

        deleteHubObserver = Transformations.switchMap(deleteHubRequest, repository::deleteHub);

        userInfoObserver =Transformations.switchMap(userInfoRequest, input -> repository.getUserInfo());
    }


    public void onMeshOff() {
//        setMeshStatus(-1);
        repository.updateMeshStatus(-1);
    }

    public void updateDeviceStatus(List<OnlineStatusNotificationParser.DeviceNotificationInfo> notificationInfoList) {
        repository.updateDevicesStatus(notificationInfoList);
    }


}
