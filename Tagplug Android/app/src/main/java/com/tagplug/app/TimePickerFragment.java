package com.tagplug.app;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TimePicker;
import android.widget.Toast;

import java.net.SocketException;
import java.util.Calendar;



public  class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    long alarmTimestamp = 0;
    DatabaseStack db;

    Handler handler;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        final int hour = c.get(Calendar.HOUR_OF_DAY);
        final int minute = c.get(Calendar.MINUTE);

        db = new DatabaseStack(getActivity().getApplicationContext());

          /* Handler for setting toggle button status */
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {

                if(TextUtils.equals(msg.obj.toString(), "ALARM_OK")) {
                    db.addAlarm(alarmTimestamp);
                    db.close();
                    Toast.makeText(getActivity().getApplicationContext(), "Alarm set for " + hour + ":" + minute, Toast.LENGTH_LONG).show();
                }

            }

        };

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        long unixTime = System.currentTimeMillis() / 1000L;


        java.util.Date curTime= new java.util.Date();
        java.util.Date alarm= new java.util.Date();

        alarm.setDate(curTime.getDate());
        alarm.setMonth(curTime.getMonth());
        alarm.setYear(curTime.getYear());
        alarm.setHours(hourOfDay);
        alarm.setMinutes(minute);


        Log.d("WIFI", Long.toString(alarm.getTime()));
        alarmTimestamp = alarm.getTime()/1000L ;
        alarmTimestamp -= curTime.getSeconds();

        if(alarmTimestamp < unixTime){
            Toast.makeText(getActivity().getApplicationContext(), "Cannot set alarm for past time!", Toast.LENGTH_LONG).show();
        }else{
            db.addAlarm(alarmTimestamp);
            db.close();
            Toast.makeText(getActivity().getApplicationContext(), "Alarm set for "+ hourOfDay +":" + minute ,Toast.LENGTH_LONG).show();
/*
            UDPStack udpStack = null;
            try {
                udpStack = new UDPStack(getActivity().getApplicationContext(),true,null,"ALARM");
            } catch (SocketException e) {
                e.printStackTrace();
            }
            udpStack.writeToSocket("_A_"+alarmTimestamp+MainActivity.DELIMITER);
*/
        }
    }


}
