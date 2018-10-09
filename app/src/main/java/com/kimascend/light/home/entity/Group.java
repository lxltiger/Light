package com.kimascend.light.home.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 场景
 * 如果增加字段 注意Parcelable要添加相应的读写 否则序列化会丢失地段
 */
@Entity(tableName = "group", primaryKeys = "id")
public class Group implements Parcelable {
    /**
     * groupSceneId : 32790
     * name : Ghhjjhjj
     * icon : /groupIcon/groupIcon_20180717175722.png
     * id : 064d2fe04d5e4fa0a7b28eefe8a52d09
     */

    @ColumnInfo(name = "groupId")
    private int groupId;
    @NonNull
    private String id;
    private String name = "";
    private String icon = "";
    private String deviceIds = "";
    //    private String meshId;
    @Ignore
    public boolean selected = false;

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getName() {
        if (TextUtils.isEmpty(name)) {
            return "请输入";
        }
        return name;
    }

    public String getDeviceNum() {
        if (TextUtils.isEmpty(deviceIds)) {
            return "请输入";
        }

        String[] ids = deviceIds.split(",");
        return String.valueOf(ids.length);
    }

    public List<Integer> getDeviceIdList() {
        List<Integer> ids = new ArrayList<>();
        if (!TextUtils.isEmpty(deviceIds)) {
            String[] strings = deviceIds.split(",");
            for (String string : strings) {
                ids.add(Integer.valueOf(string));
            }
        }

        return ids;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDeviceIds() {
        return deviceIds;
    }

    public void setDeviceIds(String deviceIds) {
        this.deviceIds = deviceIds;
    }

    public Group() {
        this.groupId = -1;
        id = UUID.randomUUID().toString();
    }

    @Override
    public String toString() {
        return "Group{" +
                "groupId=" + groupId +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", icon='" + icon + '\'' +
                ", deviceIds='" + deviceIds + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.groupId);
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.icon);
        dest.writeString(this.deviceIds);
    }

    protected Group(Parcel in) {
        this.groupId = in.readInt();
        this.id = in.readString();
        this.name = in.readString();
        this.icon = in.readString();
        this.deviceIds = in.readString();
    }

    public static final Creator<Group> CREATOR = new Creator<Group>() {
        @Override
        public Group createFromParcel(Parcel source) {
            return new Group(source);
        }

        @Override
        public Group[] newArray(int size) {
            return new Group[size];
        }
    };
}