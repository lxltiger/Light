package com.kimascend.light.group;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableList;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.kimascend.light.R;
import com.kimascend.light.common.BindingAdapters;
import com.kimascend.light.common.SingleLiveEvent;
import com.kimascend.light.common.SnackbarMessage;
import com.kimascend.light.device.entity.Lamp;
import com.kimascend.light.home.entity.Group;
import com.kimascend.light.model.GeneralItem;
import com.kimascend.light.repository.HomeRepository;
import com.kimascend.light.utils.LightCommandUtils;

import java.io.File;
import java.util.List;

public class GroupViewModel extends AndroidViewModel {
    private HomeRepository repository;
    private Group group;
    public ObservableField<String> title = new ObservableField<>();
    public ObservableBoolean edit = new ObservableBoolean();
    public ObservableList<GeneralItem> groupItems = new ObservableArrayList<>();
    SingleLiveEvent<Void> completeEvent = new SingleLiveEvent<>();
    public LiveData<List<Lamp>> lampsObserver;
    SnackbarMessage snackbarMessage;

    public GroupViewModel(@NonNull HomeRepository repository, @NonNull Application application) {
        super(application);
        this.repository = repository;
        snackbarMessage = new SnackbarMessage();
    }

    void loadGroup(Group group) {
        this.group = group;
        edit.set(group.getGroupId() > 0);
        title.set(edit.get() ? "修改场景" : "新建场景");
        List<GeneralItem> generalItems = GeneralItem.fromGroup(group);
        groupItems.clear();
        groupItems.addAll(generalItems);
        lampsObserver = repository.loadDeviceListWithMark(group.getDeviceIdList());

    }

    public Group getGroup() {
        return group;
    }

    public void delete() {
        // TODO: 2018/10/9 0009 解除所有关系
        repository.deleteGroup(group);
        completeEvent.call();
    }

    void handleIcon(File file) {
        GeneralItem generalItem = groupItems.get(0);
        generalItem.setValue(file.getAbsolutePath());
        group.setIcon(file.getAbsolutePath());
    }

    void handleName(String content) {
        GeneralItem generalItem = groupItems.get(1);
        generalItem.setValue(content);
        group.setName(content);
    }

    void handleDevices(String deviceIds) {
        GeneralItem generalItem = groupItems.get(2);
        group.setDeviceIds(deviceIds);
        generalItem.setValue(group.getDeviceNum());

    }

    void addEditGroup() {
        if (TextUtils.isEmpty(group.getName()) || "请输入".equals(group.getName())) {
            snackbarMessage.setValue(R.string.lack_name);
            return;
        }
        if (TextUtils.isEmpty(group.getIcon())) {
            snackbarMessage.setValue(R.string.lack_icon);
            return;
        }

        if (TextUtils.isEmpty(group.getDeviceIds())) {
            snackbarMessage.setValue(R.string.lack_lamps);
            return;
        }
        List<Lamp> lampList = lampsObserver.getValue();

        if (null == lampList || lampList.isEmpty()) {
            snackbarMessage.setValue(R.string.empty_lamps);
            return;
        }

        if (!edit.get()) {
            int groupNum = repository.getGroupNum();
//            场景的编号从0x8000开始
            group.setGroupId(groupNum == 0 ? 0x8001 : groupNum + 1);
        }

        for (Lamp lamp : lampList) {
            LightCommandUtils.allocDeviceGroup(group.getGroupId(), lamp.getDevice_id(), lamp.isSelected());

        }
        repository.addUpdateGroup(group);
        completeEvent.call();
    }


    static class Factory extends ViewModelProvider.NewInstanceFactory {
        private final HomeRepository repository;
        private final Application application;

        public Factory(Application application) {
            this.repository = HomeRepository.INSTANCE(application);
            this.application = application;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {

            return (T) new GroupViewModel(repository, application);
        }
    }

}
