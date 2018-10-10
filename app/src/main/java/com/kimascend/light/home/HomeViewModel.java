package com.kimascend.light.home;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import android.util.Log;

import com.kimascend.light.R;
import com.kimascend.light.api.ApiResponse;
import com.kimascend.light.api.Resource;
import com.kimascend.light.app.SmartLightApp;
import com.kimascend.light.common.SnackbarMessage;
import com.kimascend.light.device.entity.Lamp;
import com.kimascend.light.home.entity.Group;
import com.kimascend.light.home.entity.GroupList;
import com.kimascend.light.home.entity.Hub;
import com.kimascend.light.home.entity.HubList;
import com.kimascend.light.mesh.DefaultMesh;
import com.kimascend.light.model.User;
import com.kimascend.light.repository.HomeRepository;
import com.kimascend.light.scene.Scene;
import com.kimascend.light.sevice.TelinkLightService;
import com.kimascend.light.utils.LightCommandUtils;
import com.kimascend.light.utils.ToastUtil;
import com.telink.bluetooth.event.DeviceEvent;
import com.telink.bluetooth.event.MeshEvent;
import com.telink.bluetooth.event.NotificationEvent;
import com.telink.bluetooth.event.ServiceEvent;
import com.telink.bluetooth.light.DeviceInfo;
import com.telink.bluetooth.light.LeAutoConnectParameters;
import com.telink.bluetooth.light.LeRefreshNotifyParameters;
import com.telink.bluetooth.light.LightAdapter;
import com.telink.bluetooth.light.OnlineStatusNotificationParser;
import com.telink.bluetooth.light.Parameters;
import com.telink.util.Event;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * 负责首页各页面的网络请求 并提供数据返回接口供UI监听
 */
public class HomeViewModel extends AndroidViewModel {
    private static final String TAG = HomeViewModel.class.getSimpleName();
    private HomeRepository repository;


    //    分享mesh
    public MutableLiveData<String> shareMeshRequest = new MutableLiveData<>();
    // 分享mesh监听
    public final LiveData<Resource<Boolean>> shareMeshObserver;


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
//    public final LiveData<ApiResponse<GroupList>> groupListObserver;

    //情景列表
    public final MutableLiveData<Integer> sceneListRequest = new MutableLiveData<>();
//    public final LiveData<Resource<List<Scene>>> sceneListObserver;


    public final MutableLiveData<Integer> userInfoRequest = new MutableLiveData<>();
    public final LiveData<User> userInfoObserver;

     final LiveData<List<Group>> groupListObserver;
     final LiveData<List<Scene>> sceneListObserver;

    SnackbarMessage snackbarMessage = new SnackbarMessage();
    private Handler handler;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        repository = HomeRepository.INSTANCE(application);
        handler = new Handler();
        shareMeshObserver = Transformations.switchMap(shareMeshRequest, repository::shareMesh);

        hubListObserver = Transformations.switchMap(hubListRequest, input -> repository.getHubList(input));

//        groupListObserver = Transformations.switchMap(groupListRequest, input -> repository.getGroupList(input));

        deleteLampObserver = Transformations.switchMap(deleteLampRequest, input -> repository.deleteDevice(input));

//        sceneListObserver = Transformations.switchMap(sceneListRequest, repository::getSceneList);

        deleteHubObserver = Transformations.switchMap(deleteHubRequest, repository::deleteHub);

        userInfoObserver = Transformations.switchMap(userInfoRequest, input -> repository.getUserInfo());

        groupListObserver = repository.getGroupList();
        sceneListObserver = repository.getSceneList();

    }

    void callBack(Event<String> event) {
        switch (event.getType()) {
            case NotificationEvent.ONLINE_STATUS:
                onOnlineStatusNotify((NotificationEvent) event);
                break;
            case DeviceEvent.STATUS_CHANGED:
                onDeviceStatusChanged((DeviceEvent) event);
                break;
            case NotificationEvent.GET_TIME: {
                NotificationEvent notificationEvent = (NotificationEvent) event;
                Calendar calendar = (Calendar) notificationEvent.parse();
                if (Math.abs(Calendar.getInstance().getTimeInMillis() - calendar.getTimeInMillis()) > 60 * 1000) {
                    LightCommandUtils.synLampTime();
                }
                String format = DateFormat.getDateTimeInstance().format(calendar.getTimeInMillis());
                Log.d(TAG, format);
                break;
            }
            case MeshEvent.OFFLINE:
                SmartLightApp.INSTANCE().setMeshStatus(-1);
                repository.updateMeshStatus(-1);
                snackbarMessage.setValue(R.string.mesh_offline);
                break;
            case MeshEvent.ERROR:
                SmartLightApp.INSTANCE().setMeshStatus(-2);
                snackbarMessage.setValue(R.string.start_bollue2);
                break;
            case ServiceEvent.SERVICE_CONNECTED:
                autoConnect();
                break;
            case ServiceEvent.SERVICE_DISCONNECTED:
                Log.d(TAG, "performed: disconnected");
                break;
        }
    }

    @WorkerThread
    protected void onOnlineStatusNotify(NotificationEvent event) {

        List<OnlineStatusNotificationParser.DeviceNotificationInfo> notificationInfoList
                = (List<OnlineStatusNotificationParser.DeviceNotificationInfo>) event.parse();

        if (notificationInfoList == null || notificationInfoList.size() <= 0)
            return;
        for (OnlineStatusNotificationParser.DeviceNotificationInfo notificationInfo : notificationInfoList) {
            int meshAddress = notificationInfo.meshAddress;
            int brightness = notificationInfo.brightness;
            Log.d(TAG, meshAddress + "meshAddress:" + brightness);
        }
        repository.updateDevicesStatus(notificationInfoList);
    }

    private void onDeviceStatusChanged(DeviceEvent event) {
        DeviceInfo deviceInfo = event.getArgs();
        switch (deviceInfo.status) {
            case LightAdapter.STATUS_LOGIN:
                Log.d(TAG, "connecting success");
                snackbarMessage.setValue(R.string.succeed_to_connect);
                //获取灯具时间
                SmartLightApp.INSTANCE().setMeshStatus(LightAdapter.STATUS_LOGIN);
                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(LightCommandUtils::getLampTime, 3 * 1000);
                break;
            case LightAdapter.STATUS_CONNECTING:
                SmartLightApp.INSTANCE().setMeshStatus(LightAdapter.STATUS_CONNECTING);
                Log.d(TAG, "connecting");
                break;
            case LightAdapter.STATUS_LOGOUT:
                SmartLightApp.INSTANCE().setMeshStatus(LightAdapter.STATUS_LOGOUT);
                Log.d(TAG, "disconnect");
                break;
            default:
                break;
        }
    }

     void autoConnect() {
        Log.d(TAG, "autoConnect() called");
        if (TelinkLightService.Instance() != null) {
            if (TelinkLightService.Instance().getMode() != LightAdapter.MODE_AUTO_CONNECT_MESH) {
                //自动重连参数
                Log.d(TAG, "connect");
                DefaultMesh mesh = SmartLightApp.INSTANCE().getDefaultMesh();
                if (null == mesh) {
                    return;
                }
                SmartLightApp.INSTANCE().setMeshStatus(LightAdapter.STATUS_CONNECTING);
                String meshName = mesh.name;
                String psw = mesh.password;
                Log.d(TAG, meshName + "--" + psw);
                LeAutoConnectParameters connectParams = Parameters.createAutoConnectParameters();
                connectParams.setMeshName(meshName);
                connectParams.setPassword(psw);
                connectParams.autoEnableNotification(true);
                //自动重连
                TelinkLightService.Instance().autoConnect(connectParams);
            }

            //刷新Notify参数
            LeRefreshNotifyParameters refreshNotifyParams = Parameters.createRefreshNotifyParameters();
            refreshNotifyParams.setRefreshRepeatCount(2);
            refreshNotifyParams.setRefreshInterval(5000);
            //开启自动刷新Notify
            TelinkLightService.Instance().autoRefreshNotify(refreshNotifyParams);
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        handler.removeCallbacksAndMessages(null);
    }
}
