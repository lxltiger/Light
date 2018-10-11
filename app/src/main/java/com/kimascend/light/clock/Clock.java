package com.kimascend.light.clock;

import android.arch.persistence.room.Entity;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.Objects;
import java.util.UUID;

@Entity(primaryKeys = "id")
public class Clock implements Parcelable {


    @NonNull
    private String id;
    private int isOpen;
    private int deviceId;
    private String name;
    private boolean on;
    private String cycle;
    private int hour;
    private int min;
    //用来显示
    private String time = "";
    //    用来设置闹钟的格式
    public String cronTime = "";
    private String repeat = "";

    public Clock() {
        deviceId = -1;
        isOpen=1;
        id = UUID.randomUUID().toString();
    }


    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getIsOpen() {
        return isOpen;
    }

    public void setIsOpen(int isOpen) {
        this.isOpen = isOpen;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getDeviceId() {
        return deviceId;

    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isOn() {
        return on;
    }

    public void setOn(boolean on) {
        this.on = on;
    }

    public String getCycle() {
        return cycle;
    }

    public void setCycle(String cycle) {
        this.cycle = cycle;
    }

    public String getRepeat() {
        return repeat;
    }

    public void setRepeat(String repeat) {
        this.repeat = repeat;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Clock clock = (Clock) o;
        return deviceId == clock.deviceId &&
                Objects.equals(id, clock.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(deviceId, id);
    }

    @Override
    public String toString() {
        return "Clock{" +
                "isOpen=" + isOpen +
                ", name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", cycle='" + cycle + '\'' +
                ", time='" + time + '\'' +
                ", cronTime='" + cronTime + '\'' +
                ", repeat='" + repeat + '\'' +
                '}';
    }

    public void parseCycle() {
        if (!TextUtils.isEmpty(cycle)) {
            String[] strings = cycle.split(" ");
            if (strings.length > 5) {
                time = String.format("%s:%s", strings[2], strings[1]);
                cronTime = String.format("%s %s", strings[1], strings[2]);
                repeat = strings[5];
            }
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeInt(this.isOpen);
        dest.writeInt(this.deviceId);
        dest.writeString(this.name);
        dest.writeByte(this.on ? (byte) 1 : (byte) 0);
        dest.writeString(this.cycle);
        dest.writeInt(this.hour);
        dest.writeInt(this.min);
        dest.writeString(this.time);
        dest.writeString(this.cronTime);
        dest.writeString(this.repeat);
    }

    protected Clock(Parcel in) {
        this.id = in.readString();
        this.isOpen = in.readInt();
        this.deviceId = in.readInt();
        this.name = in.readString();
        this.on = in.readByte() != 0;
        this.cycle = in.readString();
        this.hour = in.readInt();
        this.min = in.readInt();
        this.time = in.readString();
        this.cronTime = in.readString();
        this.repeat = in.readString();
    }

    public static final Creator<Clock> CREATOR = new Creator<Clock>() {
        @Override
        public Clock createFromParcel(Parcel source) {
            return new Clock(source);
        }

        @Override
        public Clock[] newArray(int size) {
            return new Clock[size];
        }
    };
}
