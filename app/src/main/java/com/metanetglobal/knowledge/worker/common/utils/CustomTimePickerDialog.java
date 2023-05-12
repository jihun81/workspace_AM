package com.metanetglobal.knowledge.worker.common.utils;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Custom TimePicker Dialog
 *
 * Interval Time 을 주면서 기본 Theme 를 사용할 수 있는 TimePickerDialog
 * Theme 에 따라서 폰마다 보여지는 형식이 다를 수 있다.
 *
 * 일단 사용하지 않는다.
 *
 * @author      namki.an
 * @version     1.0.0
 * @see         TimePickerDialog
 */
public class CustomTimePickerDialog extends TimePickerDialog {
    private final static int TIME_PICKER_INTERVAL = 30;
    private TimePicker mTimePicker;
    private final OnTimeSetListener mTimeSetListener;
    private boolean autoTitle = false;

    public CustomTimePickerDialog(Context context, int theme_Holo_Dialog, OnTimeSetListener listener,
                                  int hourOfDay, int minute, boolean is24HourView) {
        super(context, TimePickerDialog.THEME_HOLO_LIGHT, null, hourOfDay,
                minute / TIME_PICKER_INTERVAL, is24HourView);
        mTimeSetListener = listener;
    }

    @Override
    public void updateTime(int hourOfDay, int minuteOfHour) {
        mTimePicker.setCurrentHour(hourOfDay);
        mTimePicker.setCurrentMinute(minuteOfHour / TIME_PICKER_INTERVAL);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case BUTTON_POSITIVE:
                if (mTimeSetListener != null) {
                    if(mTimePicker.getCurrentMinute() >1) {
                        mTimeSetListener.onTimeSet(mTimePicker, mTimePicker.getCurrentHour(),
                                mTimePicker.getCurrentMinute() * 0);
                    }else{
                        mTimeSetListener.onTimeSet(mTimePicker, mTimePicker.getCurrentHour(),
                                mTimePicker.getCurrentMinute() * TIME_PICKER_INTERVAL);
                    }
                }
                break;
            case BUTTON_NEGATIVE:
                cancel();
                break;
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        try {
            Class<?> classForid = Class.forName("com.android.internal.R$id");
            Field timePickerField = classForid.getField("timePicker");
            mTimePicker = (TimePicker) findViewById(timePickerField.getInt(null));
            Field field = classForid.getField("minute");

            NumberPicker minuteSpinner = (NumberPicker) mTimePicker
                    .findViewById(field.getInt(null));
            minuteSpinner.setMinValue(0);
            minuteSpinner.setMaxValue((60 / TIME_PICKER_INTERVAL));
            List<String> displayedValues = new ArrayList<>();


            for (int i = 0; i < 60; i += TIME_PICKER_INTERVAL) {
                displayedValues.add(String.format("%02d", i));
            }
            displayedValues.add(String.format("%02d", 0));

            minuteSpinner.setDisplayedValues(displayedValues
                    .toArray(new String[displayedValues.size()]));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        if(autoTitle) {
            super.onTimeChanged(view, hourOfDay, minute * TIME_PICKER_INTERVAL);
        }
    }

    @Override
    public void setTitle(int titleId) {
        super.setTitle(titleId);
        autoTitle = (titleId == 0);
    }
}
