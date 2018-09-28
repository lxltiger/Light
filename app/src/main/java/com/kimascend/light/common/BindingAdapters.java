package com.kimascend.light.common;

import android.content.Context;
import android.content.res.ColorStateList;
import android.databinding.BindingAdapter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.widget.ImageViewCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.kimascend.light.Config;
import com.kimascend.light.R;
import com.kimascend.light.view.ImageTransformationType;

/**
 * xml中不要出现复杂的表达式
 * 不要出现中文字符 负责编译失败
 * 在这里处理负责逻辑
 */
public class BindingAdapters {
    private static final String TAG = "BindingAdapters";
    public static final int LIGHT_HIDE = -1;
    public static final int LIGHT_OFF = 0;
    public static final int LIGHT_ON = 1;
    public static final int LIGHT_CUT = 2;
    public static final int LIGHT_SELECTED = 3;


    public static final int ADD = 0;
    public static final int ADDING = 1;
    public static final int ADDED = 2;


    public static final int INVISIBLE = -1;
    public static final int REFRESH = 0;

    /**
     * 动态设置 添加灯具按钮 的图标
     *
     * @param view
     * @param status
     */
    @android.databinding.BindingAdapter("icon")
    public static void setAddIcon(ImageView view, int status) {
        switch (status) {
            case ADD:
                view.setImageDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.icon_add));
                break;
            case ADDING:
                view.setImageDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.icon_refresh));
                break;
            case ADDED:
                view.setImageDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.icon_add_ok));
                break;
            default:
                view.setVisibility(View.GONE);

        }
    }

    @BindingAdapter(value = {"show", "msg"}, requireAll = false)
    public static void setWarning(EditText editText, boolean show, String msg) {
        if (show) {
            editText.setError(msg);
            editText.requestFocus();
        } else {
            editText.setError(null);

        }
    }

    /**
     * 灯的状态
     * 如果有出现INVISIBLE的可能，就要加上VISIBLE，否则一旦设为不可见，再设为其他状态将依旧不可见
     */
    @android.databinding.BindingAdapter("lightStatus")
    public static void setLightStatus(ImageView view, int status) {
        view.setVisibility(View.VISIBLE);
        switch (status) {
            case LIGHT_OFF:
                view.setImageDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.bg_red_circle));
                break;
            case LIGHT_CUT:
                view.setImageDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.bg_grey_circle));
                break;
            case LIGHT_ON:
                view.setImageDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.bg_green_circle));
                break;
            case LIGHT_SELECTED:
                view.setImageDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.icon_add_ok));
                break;
            case LIGHT_HIDE:
            default:
                view.setVisibility(View.INVISIBLE);
                break;
        }
    }


    @android.databinding.BindingAdapter("deviceStatus")
    public static void setDeviceStatus(ImageView view, int brightness) {
        if (brightness > 0) {
            view.setImageDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.bg_green_circle));
        } else if (brightness == 0) {
            view.setImageDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.bg_red_circle));
        } else {
            view.setImageDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.bg_grey_circle));

        }

    }

    /**
     * @param view
     * @param type
     */
    @BindingAdapter("deviceIcon")
    public static void setDynamicIcon(ImageView view, int type) {
        switch (type) {
            case Config.LAMP_TYPE:
                view.setImageDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.icon_light_rgb));
                break;
            case Config.SOCKET_TYPE:
                view.setImageDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.icon_socket));
                break;
            case Config.PANEL_TYPE:
                view.setImageDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.icon_panel));
                break;
            default:
                view.setImageDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.icon_light));


        }
    }

    @BindingAdapter("clockIcon")
    public static void setClockIcon(ImageView view, int type) {
        switch (type) {
            case Config.CLOCK_OPEN:
                view.setImageDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.select_open));
                break;
            case Config.CLOCK_CLOSE:
                view.setImageDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.select_close));
                break;
            case Config.CLOCK_RGB:
                view.setImageDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.rgb_select_open));
                break;

        }
    }

    @BindingAdapter(value = {"imageUrl", "type"}, requireAll = false)
    public static void loadImageUrl(ImageView view, String url, ImageTransformationType type) {
        if (!TextUtils.isEmpty(url)) {
            Context context = view.getContext();
            Glide.with(context).load(Config.IMG_PREFIX.concat(url)).listener(drawableRequestListener).into(view);
            /*if (type == null) {
                type = ImageTransformationType.NONE;
            }
            switch (type) {
                case ROUND:
                    Glide.with(context).load(Config.IMG_PREFIX.concat(url)).transform(new RoundTransform(context, 2)).crossFade(1000).into(view);
                    break;
                case CIRCLE:
                    Glide.with(context).load(Config.IMG_PREFIX.concat(url)).transform(new CircleTransform(context)).crossFade(1000).listener(drawableRequestListener).into(view);
                    break;
                case NONE:
                default:
                    break;
            }*/
        }
    }

    private static RequestListener<String, GlideDrawable> drawableRequestListener = new RequestListener<String, GlideDrawable>() {
        @Override
        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
            Log.e(TAG, e.toString() + "  model:" + model + " isFirstResource: " + isFirstResource);

            return false;
        }

        @Override
        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
            Log.e(TAG, "isFromMemoryCache:" + isFromMemoryCache + "  model:" + model + " isFirstResource: " + isFirstResource);

            return false;
        }
    };


    /*
     *  注意：参数的顺序要和value中的一致
     * */
    @BindingAdapter(value = {"dynamicImage", "resId"}, requireAll = false)
    public static void loadDynamicImage(ImageView view, String dynamicImage, int resId) {
        if (!TextUtils.isEmpty(dynamicImage)) {
            Glide.with(view.getContext()).load(dynamicImage).listener(drawableRequestListener).into(view);
        } else if (resId > 0) {
            view.setImageResource(resId);
        }
    }

    @BindingAdapter(value = {"avatar", "resId"}, requireAll = false)
    public static void loadAvatar(ImageView view, String avatar, int resId) {
        Context context = view.getContext();
        //第一次收到未空，因为采用Observer模式，视图加载完还没有设置地址
        if (avatar != null) {
            Glide.with(context).load(avatar).error(R.drawable.pic_portrait).listener(drawableRequestListener).into(view);
        }
       /* if (!TextUtils.isEmpty(avatar)) {
        } else  {
            Glide.with(context).load(R.drawable.pic_portrait).into(view);
        }*/
    }

    @BindingAdapter(value = "tintIndicator")
    public static void tintIndicator(ImageView view, int rgb) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            ImageViewCompat.setImageTintList(view, ColorStateList.valueOf(rgb));
        } else {
            Drawable drawable = ContextCompat.getDrawable(view.getContext(), R.drawable.ic_arrow_drop_down_black_24dp);
            DrawableCompat.setTint(drawable,rgb);
            view.setImageDrawable(drawable);
        }

    }


    @BindingAdapter("visibleGone")
    public static void setVisible(View view, boolean show) {
        view.setVisibility(show ? View.VISIBLE : View.GONE);
    }


    //首页蓝牙和WIFI切换使用
    @BindingAdapter("customGravity")
    public static void setCheckBoxGravity(CheckBox view, boolean isChecked) {
        view.setGravity(isChecked ? Gravity.START | Gravity.CENTER_VERTICAL : Gravity.END | Gravity.CENTER_VERTICAL);
    }
}
