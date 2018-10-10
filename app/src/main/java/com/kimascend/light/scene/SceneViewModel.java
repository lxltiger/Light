package com.kimascend.light.scene;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableList;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import android.support.v4.util.Pair;
import android.text.TextUtils;
import android.util.SparseArray;
import android.util.SparseIntArray;

import com.google.gson.Gson;
import com.kimascend.light.R;
import com.kimascend.light.command.OnOffCommand;
import com.kimascend.light.command.SceneCommand;
import com.kimascend.light.command.TelinkOnOffCommand;
import com.kimascend.light.command.TelinkSceneCommand;
import com.kimascend.light.common.BindingAdapters;
import com.kimascend.light.common.SingleLiveEvent;
import com.kimascend.light.common.SnackbarMessage;
import com.kimascend.light.device.entity.Lamp;
import com.kimascend.light.model.GeneralItem;
import com.kimascend.light.repository.HomeRepository;
import com.kimascend.light.utils.ToastUtil;
import com.telink.bluetooth.event.NotificationEvent;
import com.telink.bluetooth.light.OnlineStatusNotificationParser;
import com.telink.util.EventListener;

import java.io.File;
import java.util.List;

/**
 * 情景ViewModel
 */
public class SceneViewModel extends AndroidViewModel {
    private static final String TAG = SceneViewModel.class.getSimpleName();
    private final HomeRepository repository;
    private Scene scene;
    public ObservableField<String> title = new ObservableField<>();
    public ObservableBoolean edit = new ObservableBoolean();
    public ObservableList<GeneralItem> generalItems = new ObservableArrayList<>();
    SingleLiveEvent<Void> completeEvent = new SingleLiveEvent<>();
    SingleLiveEvent<Void> dismissEvent = new SingleLiveEvent<>();
    LiveData<List<Lamp>> lampsObserver;
    SnackbarMessage snackbarMessage;
    private OnOffCommand onOffCommand;
    private SceneCommand sceneCommand;

    public SceneViewModel(@NonNull Application application) {
        super(application);
        repository = HomeRepository.INSTANCE(application);
        snackbarMessage = new SnackbarMessage();
        onOffCommand = new TelinkOnOffCommand();
        sceneCommand = new TelinkSceneCommand();
    }

    public Scene getScene() {
        return scene;
    }


    void loadScene(Scene scene) {
        this.scene = scene;
        edit.set(scene.getSceneId() > 0);
        title.set(edit.get() ? "修改情景" : "新建情景");
        if (!edit.get()) {
            int maxSceneId = repository.getMaxSceneId();
            scene.setSceneId(maxSceneId == 0 ? 0x8001 : maxSceneId + 1);
        }
        sceneCommand.setSceneAddress(scene.getSceneId());
        SparseIntArray sceneSetting = scene.getSceneSetting();
        String deviceNum = sceneSetting.size() == 0 ? "请输入" : String.valueOf(sceneSetting.size());
        List<GeneralItem> items = GeneralItem.from(scene.getIcon(), scene.getName(),deviceNum);
        generalItems.clear();
        generalItems.addAll(items);
        lampsObserver = repository.loadDeviceListWithStatus(sceneSetting);
    }


    void handleIcon(File file) {
        GeneralItem generalItem = generalItems.get(0);
        generalItem.setValue(file.getAbsolutePath());
        scene.setIcon(file.getAbsolutePath());
    }

    void handleName(String content) {
        GeneralItem generalItem = generalItems.get(1);
        generalItem.setValue(content);
        scene.setName(content);
    }


    void addEditScene() {
        if (TextUtils.isEmpty(scene.getName()) || "请输入".equals(scene.getName())) {
            snackbarMessage.setValue(R.string.lack_name);
            return;
        }
        if (TextUtils.isEmpty(scene.getIcon())) {
            snackbarMessage.setValue(R.string.lack_icon);
            return;
        }

        if (TextUtils.isEmpty(scene.getDeviceIds())) {
            snackbarMessage.setValue(R.string.lack_lamps);
            return;
        }
        List<Lamp> lampList = lampsObserver.getValue();

        if (null == lampList || lampList.isEmpty()) {
            snackbarMessage.setValue(R.string.empty_lamps);
            return;
        }

        byte redundant = 0;
        int color = Color.RED;
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        for (Lamp lamp : lampList) {
            sceneCommand.setDstAddress(lamp.getDevice_id());
            if (lamp.isSelected()) {
                sceneCommand.handleSceneOperation(SceneCommand.SceneOperation.ADD, (byte) lamp.getBrightness(), (byte) red, (byte)green, (byte)blue);
            } else {
                sceneCommand.handleSceneOperation(SceneCommand.SceneOperation.DELETE,redundant, redundant, redundant, redundant);
            }
        }
        repository.addUpdateScene(scene);
        completeEvent.call();
    }

    public void delete() {
        byte redundant=0;
        sceneCommand.setSceneAddress(scene.getSceneId());
        sceneCommand.setDstAddress(0xffff);
        sceneCommand.handleSceneOperation(SceneCommand.SceneOperation.DELETE,redundant, redundant, redundant, redundant);
        repository.deleteScene(scene);
        completeEvent.call();
    }


    public void onSettingComplete() {
        SparseIntArray lampSetting=getLampSetting();
        if (lampSetting.size()==0) {
            ToastUtil.showToast("至少选择一个灯具");
            return;
        }
        String json = new Gson().toJson(lampSetting);
        scene.setDeviceIds(json);
        GeneralItem generalItem = generalItems.get(2);
        generalItem.setValue(String.valueOf(lampSetting.size()));

        dismissEvent.call();
    }

    private SparseIntArray getLampSetting() {
        SparseIntArray setting = new SparseIntArray();
        List<Lamp> lampList = lampsObserver.getValue();
        if (null == lampList || lampList.isEmpty()) {
            return setting;
        }

        for (Lamp lamp : lampList) {
            if (lamp.isSelected()) {
                setting.put(lamp.getDevice_id(),lamp.getBrightness());
            }
        }

        return setting;

    }

    OnSceneSettingListener sceneSettingListener = new OnSceneSettingListener() {
        @Override
        public void onItemClick(Lamp lamp) {
            int status = lamp.lampStatus.get();
            if (BindingAdapters.LIGHT_HIDE == status) {
                lamp.lampStatus.set(BindingAdapters.LIGHT_SELECTED);
            } else {
                lamp.lampStatus.set(BindingAdapters.LIGHT_HIDE);
            }
        }

        @Override
        public void onSwitchClick(Lamp lamp) {
            onOffCommand.onOff(!lamp.onOff.get(), lamp.getDevice_id());
        }

    };

    EventListener<String> eventListener = event -> {
        switch (event.getType()) {
            case NotificationEvent.ONLINE_STATUS:
                onOnlineStatusNotify((NotificationEvent) event);
                break;
        }
    };

    @WorkerThread
    private void onOnlineStatusNotify(NotificationEvent event) {
        List<OnlineStatusNotificationParser.DeviceNotificationInfo> notificationInfoList
                = (List<OnlineStatusNotificationParser.DeviceNotificationInfo>) event.parse();

        if (notificationInfoList == null || notificationInfoList.size() <= 0)
            return;
        for (OnlineStatusNotificationParser.DeviceNotificationInfo notificationInfo : notificationInfoList) {
            int meshAddress = notificationInfo.meshAddress;
            int brightness = notificationInfo.brightness;

            List<Lamp> lampList = lampsObserver.getValue();
            if (null == lampList || lampList.isEmpty()) {
                return;
            }
            for (Lamp lamp : lampList) {
                if (lamp.getDevice_id() == meshAddress) {
                    lamp.setBrightness(brightness);
                    lamp.onOff.set(brightness > 0);
                }
            }
        }

    }
}
