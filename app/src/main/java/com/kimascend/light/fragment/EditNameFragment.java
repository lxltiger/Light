package com.kimascend.light.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableField;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.kimascend.light.R;
import com.kimascend.light.databinding.FragmentEditNameBinding;
import com.kimascend.light.utils.KeyBoardUtils;


/**
 * https://guides.codepath.com/android/using-dialogfragment
 */
public class EditNameFragment extends DialogFragment {
    public static final String TAG = EditNameFragment.class.getSimpleName();
    private static final String ARGUMENT_CONTENT = "content";

    public ObservableField<String> content = new ObservableField<>();
    private FragmentEditNameBinding binding;

    private Listener listener;

    public static EditNameFragment newInstance(String content) {
        Bundle args = new Bundle();
        args.putString(ARGUMENT_CONTENT, content);
        EditNameFragment fragment = new EditNameFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        final Fragment parent = getParentFragment();
        if (parent != null) {
            listener = (Listener) parent;
        } else {
            listener = (Listener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_name, container, false);
        binding.setContent(content);
        if (getArguments() != null) {
            content.set(getArguments().getString(ARGUMENT_CONTENT, ""));
        }
        binding.setListener(this::onClick);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        Window window = getDialog().getWindow();
        int width = getResources().getDisplayMetrics().widthPixels;
        window.setLayout((int) (width * 0.75), WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        super.onResume();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancle:
                dismiss();
                break;
            case R.id.confirm:
                String name = content.get();
                if (TextUtils.isEmpty(name) || name.length() > 10) {
                    binding.content.setError("名称在1到10个字符之间");
                    binding.content.requestFocus();
                    return;
                }
                KeyBoardUtils.closeKeyboard(binding.content, getActivity());
                listener.onConfirmClick(name);
                dismiss();
                break;
        }
    }

    public interface Listener {
        void onConfirmClick(String content);
    }
}
