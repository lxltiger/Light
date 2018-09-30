package com.kimascend.light.home;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.kimascend.light.api.Resource;
import com.kimascend.light.command.OnOffCommand;
import com.kimascend.light.command.TelinkOnOffCommand;
import com.kimascend.light.device.entity.Lamp;
import com.kimascend.light.repository.HomeRepository;
import com.kimascend.light.sevice.TelinkLightService;
import com.telink.bluetooth.light.Opcode;

import java.util.List;

/**
 *
 */
public class DeviceListViewModel extends AndroidViewModel {
    private static final String TAG = DeviceListViewModel.class.getSimpleName();
    private HomeRepository repository;
    final LiveData<Resource<List<Lamp>>> deviceListObserver;
    private OnOffCommand onOffCommand;

    public DeviceListViewModel(@NonNull Application application) {
        super(application);
        repository = HomeRepository.INSTANCE(application);
        deviceListObserver = repository.getDeviceList();
        onOffCommand = new TelinkOnOffCommand();
    }

    void deleteLamp(Lamp lamp) {
        repository.deleteDeviceFromLocal(lamp);
        TelinkLightService.Instance().sendCommand(Opcode.BLE_GATT_OP_CTRL_E3.getValue(), lamp.getDevice_id(), null);
    }

    void toggle(boolean onOff,int addr) {
        onOffCommand.onOff(onOff,addr);
    }
}
