package com.metanetglobal.knowledge.worker.schedule;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import android.widget.TimePicker;

import java.util.Calendar;

import static android.text.format.DateFormat.is24HourFormat;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {


    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle saveInstanceState){

        Calendar mcal = Calendar.getInstance();
        int hour = mcal.get(Calendar.HOUR_OF_DAY);
        int min = mcal.get(Calendar.MINUTE);
        TimePickerDialog mTime = new TimePickerDialog(getContext(),android.R.style.Theme_Holo_Light_Dialog,this,hour,min, is24HourFormat(getContext()));

        return  mTime;

    }


}
