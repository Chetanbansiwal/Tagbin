/*
THIS IS DEPRECATED FOR NOW SINCE  AM USING A TIME PICKER DIALOG MAY BE I WILL USE IT LATER
 */
package com.tagplug.app;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import java.net.SocketException;
import java.util.Calendar;

public class AlarmSetter extends Activity {
	
	
	TimePicker tp;
	Button alarmButton;
	public static Handler handler = null;

	DatabaseStack db;
    UDPStack udpStack = null;

    long alarmTimestamp = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.activity_transition, R.anim.activity_transition_hold);
		setContentView(R.layout.alarm_setter);

        try {
            udpStack = new UDPStack(getApplicationContext(),true,null,"ALARM");
        } catch (SocketException e) {
            e.printStackTrace();
        }
		
		tp = (TimePicker)findViewById(R.id.tp);
		tp.setIs24HourView(true);
		tp.setCurrentHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
		
		alarmButton = (Button) findViewById(R.id.setAlarm);
		//tcpStack = new TcpStack(getApplicationContext());
		db = new DatabaseStack(getApplicationContext());

		alarmButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				//Current Timestamp
				long unixTime = System.currentTimeMillis() / 1000L;


				java.util.Date curTime= new java.util.Date();
				java.util.Date alarm= new java.util.Date();
				
				alarm.setDate(curTime.getDate());
				alarm.setMonth(curTime.getMonth());
				alarm.setYear(curTime.getYear());
				alarm.setHours(tp.getCurrentHour());
				alarm.setMinutes(tp.getCurrentMinute());
				
				
				Log.d("WIFI",Long.toString(alarm.getTime())); 
				alarmTimestamp = alarm.getTime()/1000L ;
				alarmTimestamp -= curTime.getSeconds();
			
				if(alarmTimestamp < unixTime){
					 Toast.makeText(getApplicationContext(), "Cannot set alarm for past time!",Toast.LENGTH_LONG).show();					
				}else{
					udpStack.writeToSocket(MainActivity.HLK_COMMAND+"_A_"+alarmTimestamp+MainActivity.DELIMITER);
				    finish();
				}
				
				
			}
		});

         /* Handler for setting toggle button status */
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {

                if(TextUtils.equals(msg.obj.toString(), "ALARM_OK")){
                    db.addAlarm(alarmTimestamp);
                    db.close();
                    Toast.makeText(getApplicationContext(), "Alarm set for "+ tp.getCurrentHour() +":" + tp.getCurrentMinute() ,Toast.LENGTH_LONG).show();
                }

            }

        };
	}
	
	

}
