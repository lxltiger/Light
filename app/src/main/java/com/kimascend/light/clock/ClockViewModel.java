package com.kimascend.light.clock;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.kimascend.light.R;
import com.kimascend.light.common.SingleLiveEvent;
import com.kimascend.light.common.SnackbarMessage;
import com.kimascend.light.device.entity.Lamp;
import com.kimascend.light.repository.HomeRepository;
import com.kimascend.light.utils.LightCommandUtils;

import java.util.List;

import static com.kimascend.light.utils.ToastUtil.showToast;

public class ClockViewModel extends AndroidViewModel {
    private HomeRepository repository;
    private Clock clock;
    public ObservableField<String> title = new ObservableField<>();
    public ObservableBoolean edit = new ObservableBoolean();
    //灯具开关状态
    public ObservableBoolean on = new ObservableBoolean();
    SingleLiveEvent<Void> completeEvent = new SingleLiveEvent<>();
    public LiveData<List<Lamp>> lampsObserver;
    SnackbarMessage snackbarMessage;

    public ClockViewModel(@NonNull Application application) {
        super(application);
        repository = HomeRepository.INSTANCE(application);
        snackbarMessage = new SnackbarMessage();

    }

    public Clock getClock() {
        return clock;
    }


    void loadClock(Clock clock) {
        this.clock = clock;
        edit.set(clock.getDeviceId() > 0);
        title.set(edit.get() ? "修改闹钟" : "新建闹钟");
        on.set(clock.isOn());
        lampsObserver = repository.loadDeviceList(lamp -> clock.getDeviceId() == lamp.getDevice_id());
    }

    public void setTime(int hour, int min) {
        clock.setHour(hour);
        clock.setMin(min);
        clock.setTime(String.format("%s:%s", hour, min));

    }

    void addEditClock() {
        if (clock.getHour() < 0) {
            snackbarMessage.setValue(R.string.lack_time_setting);
            return;
        }

        if (clock.getDeviceId() < 0) {
            snackbarMessage.setValue(R.string.lack_lamps);
            return;
        }

        if (TextUtils.isEmpty(clock.getRepeat())) {
            snackbarMessage.setValue(R.string.lack_week_day);
            return;
        }
        clock.setOn(on.get());
        LightCommandUtils.addAlarm(on.get(), clock.getDeviceId(), clock.getHour(), clock.getMin());

        repository.addUpdateClock(clock);
        completeEvent.call();

    }

    void setWeek(String weekDays) {
        clock.setRepeat(weekDays);
    }

    void markLamp(Lamp lamp) {
        clock.setDeviceId(lamp.getDevice_id());
    }
}
