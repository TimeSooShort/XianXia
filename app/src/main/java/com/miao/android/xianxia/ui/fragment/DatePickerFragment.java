package com.miao.android.xianxia.ui.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;

import com.miao.android.xianxia.R;

/**
 * Created by Administrator on 2016/11/9.
 */

public class DatePickerFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.date_picker, null);

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle("日期")
                .setPositiveButton(android.R.string.ok, null)
                .create();
    }
}
