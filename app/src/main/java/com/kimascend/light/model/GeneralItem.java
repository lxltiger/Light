package com.kimascend.light.model;

import android.databinding.ObservableField;

import com.kimascend.light.R;
import com.kimascend.light.home.entity.Group;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 通用的条目
 */
public class GeneralItem {

    private String name;
    //值类型 1.图片 2.文本，默认文本
    private boolean isText;
    //默认图片资源id
    private int defaultResId;
    //是否可以点击，能点击显示右箭头 默认可点击
    private boolean clickable;
    //如果是图片 ，值就是url，是文本就是文字内容
    private ObservableField<String> observableValue;
    //条目在列表的位置 用来点击时确定条目
    private final int pos;


    private GeneralItem(Builder builder) {
        this.name = builder.name;
        this.clickable = builder.clickable;
        this.isText = builder.isText;
        this.pos = builder.pos;
        this.observableValue = builder.observableValue;
        this.defaultResId = builder.defaultResId;
    }


    public static List<GeneralItem> fromGroup(Group group) {
        List<GeneralItem> items = new ArrayList<>();
        items.add(new Builder("图片", 0).setValue(group.getIcon()).isText(false).build());
        items.add(new Builder("名称", 1).setValue(group.getName()).build());
        items.add(new Builder("设备", 2).setValue(group.getDeviceNum()).build());

        return items;
    }

    public static List<GeneralItem> from(String icon,String name,String deviceNum) {
        List<GeneralItem> items = new ArrayList<>();
        items.add(new Builder("图片", 0).setValue(icon).isText(false).build());
        items.add(new Builder("名称", 1).setValue(name).build());
        items.add(new Builder("设备", 2).setValue(deviceNum).build());

        return items;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GeneralItem that = (GeneralItem) o;
        return isText == that.isText &&
                defaultResId == that.defaultResId &&
                clickable == that.clickable &&
                pos == that.pos &&
                Objects.equals(name, that.name) &&
                Objects.equals(observableValue, that.observableValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, isText, defaultResId, clickable, observableValue, pos);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isText() {
        return isText;
    }

    public void setText(boolean text) {
        isText = text;
    }

    public int getDefaultResId() {
        return defaultResId;
    }

    public void setDefaultResId(int defaultResId) {
        this.defaultResId = defaultResId;
    }

    public boolean isClickable() {
        return clickable;
    }

    public void setClickable(boolean clickable) {
        this.clickable = clickable;
    }

    public ObservableField<String> getObservableValue() {
        return observableValue;
    }

    public void setValue(String value) {
        this.observableValue.set(value);
    }

    public int getPos() {
        return pos;
    }

    private static class Builder {
        private ObservableField<String> observableValue;
        private final int pos;
        private final String name;
        private boolean isText = true;
        private boolean clickable = true;
        private int defaultResId = R.drawable.btn_addpic;

        public Builder(String name, int pos) {
            this.name = name;
            this.pos = pos;

        }

        public Builder setValue(String value) {
            observableValue = new ObservableField<>(value);
            return this;
        }

        public Builder setClickable(boolean clickable) {
            this.clickable = clickable;
            return this;
        }

        public Builder isText(boolean isText) {
            this.isText = isText;
            return this;
        }

        public Builder setDefaultResId(int defaultResId) {
            this.defaultResId = defaultResId;
            return this;
        }

        public GeneralItem build() {
            return new GeneralItem(this);
        }

    }

    @Override
    public String toString() {
        return "CommonItem{" +
                "name='" + name + '\'' +
                ", isText=" + isText +
                ", clickable=" + clickable +
                '}';
    }
}
