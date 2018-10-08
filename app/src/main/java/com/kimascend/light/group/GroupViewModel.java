package com.kimascend.light.group;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.kimascend.light.api.Resource;
import com.kimascend.light.common.BindingAdapters;
import com.kimascend.light.device.entity.Lamp;
import com.kimascend.light.home.entity.Group;
import com.kimascend.light.repository.HomeRepository;

import java.util.Arrays;
import java.util.List;

public class GroupViewModel extends AndroidViewModel {
    private HomeRepository repository;
//    LiveData<List<Lamp>> deviceListObserver;
    private Group group;

    public GroupViewModel(@NonNull HomeRepository repository,@NonNull Application application) {
        super(application);
        this.repository = repository;
//        deviceListObserver = repository.loadGroupDeviceList(group);

    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public LiveData<List<Lamp>> load() {
        return repository.loadGroupDeviceList(group);
    }


    static class Factory extends ViewModelProvider.NewInstanceFactory {
        private final HomeRepository repository;
        private final Application application;

        public Factory( Application application) {
            this.repository = HomeRepository.INSTANCE(application);
            this.application = application;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {

            return (T) new GroupViewModel(repository,application);
        }
    }

}
