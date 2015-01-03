package com.tagplug.app;

import android.animation.ValueAnimator;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ExpandableListViewAdapter extends BaseExpandableListAdapter
		implements OnCheckedChangeListener, OnClickListener {

	private Context _context;
	private List<String> _listDataHeader; // header titles
	// child data in format of header title, child title
	
	private HashMap<String, List<String>> _listDataChild;
	Integer flag = 0;
	DataOutputStream dataOutputStream = null;// output stream to send commands
	DataInputStream dataInputStream = null;
	Socket socket = null;// the socket for the connection
	
	DatabaseStack db;
    UDPStack udpStack = null;

    TextView usageText;
	ImageView off;
	ImageView on;
	static ToggleButton toggle;
	FlipAnimator animator;
	boolean switchState = false;
    public static Handler handler = null;
    Timer longTimer;

	public ExpandableListViewAdapter(Context context,
			List<String> listDataHeader,
			HashMap<String, List<String>> listChildData) {
		this._context = context;
		this._listDataHeader = listDataHeader;
		this._listDataChild = listChildData;

	}

	@Override
	public int getGroupCount() {
		return this._listDataHeader.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
        //Duplicate issue was solved here
		//return this._listDataChild.get(this._listDataHeader.get(groupPosition))
				//.size();
        return 1;
	}

	@Override
	public Object getGroup(int groupPosition) {
		return this._listDataHeader.get(groupPosition);
	}

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosition);
    }

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }


    @Override
	public boolean hasStableIds() {

		return false;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		String data = (String) getGroup(groupPosition);
		String[] headerTitle;
		headerTitle = data.split("_");
		if (convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) this._context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(R.layout.list_group, null);
		}
		
		db = new DatabaseStack(_context);
        try {
            //TODO try to do this only once, it instantiate every time swipe is done or clicked
            udpStack = new UDPStack(_context,true,null,"ON_OFF");
        } catch (SocketException e) {
            e.printStackTrace();
        }
        TextView lblListHeader = (TextView) convertView
				.findViewById(R.id.lblListHeader);
		lblListHeader.setTypeface(null, Typeface.BOLD);
		lblListHeader.setText(headerTitle[0]);

		off = (ImageView) convertView.findViewById(R.id.on_off);
		on = (ImageView) convertView.findViewById(R.id.on);
		toggle = (ToggleButton) convertView.findViewById(R.id.toggle);
		
		//toggle.setChecked(true);
		if(Integer.parseInt(headerTitle[1]) == 1){
			toggle.setChecked(true);
		}else if(Integer.parseInt(headerTitle[1]) == 0){
			toggle.setChecked(false);
		}

		final RelativeLayout layout = (RelativeLayout) convertView
				.findViewById(R.id.layout);



		toggle.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                /* TEMPORARY NOTIFY FOR VIDEO SHOOT */

                //snedNotification("Warning","You left the geyser on!",20000);
                //snedNotification("Warning","Monthly bill limit crossed!",30000);


				if(isChecked){
					 udpStack.writeToSocket(MainActivity._OFF);

				}else{
					 udpStack.writeToSocket(MainActivity._ON);

				}
				
			}
		});

        /* Handler for setting toggle button status */
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                //TODO update using MAC as unique key
                if(TextUtils.equals(msg.obj.toString(),"ON_OK")){
                    db.updateState(MainActivity.ON,false);
                }else if(TextUtils.equals(msg.obj.toString(),"OFF_OK")){
                    db.updateState(MainActivity.OFF,false);
                }

            }

        };
		
		off.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				animator = new FlipAnimator(off, on, layout.getWidth() / 2,
						layout.getHeight() / 2);
				
				//tcpStack.writeToSocket("_T_0@");
				if (off.getVisibility() == View.GONE) {
					animator.reverse();
				}
				layout.startAnimation(animator);
			}
		});
		on.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//tcpStack.writeToSocket("_T_1@");
				animator = new FlipAnimator(on, off, layout.getWidth() / 2,
						layout.getHeight() / 2);
				if (on.getVisibility() == View.GONE) {
					animator.reverse();
				}
				layout.startAnimation(animator);
			}
		});

		return convertView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {

		//final String childText = (String) getChild(groupPosition, childPosition);

		if (convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) this._context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(R.layout.device_detail, null);

            usageText = (TextView) convertView.findViewById(R.id.usageText);
            final ValueAnimator animator = ValueAnimator.ofInt(0,140);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int value = (int) animation.getAnimatedValue();
                    usageText.setText(value+ " KWh");

                }
            });

            animator.setDuration(1000);
            animator.start();

		}

		return convertView;
	}


	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}

	@Override
	public void onClick(View v) {

	}

    @Override
    public void onGroupExpanded(int groupPosition) {
        super.onGroupExpanded(groupPosition);
        Log.d("EXPLISTVIEW", "Clicked");
    }

    @Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

	}

    public void snedNotification(String heading, String text, long time) {
        final NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(_context)
                        .setSmallIcon(R.drawable.logo_small)
                        .setContentTitle(heading)
                        .setDefaults(Notification.DEFAULT_SOUND)
                        .setContentText(text);

        Intent resultIntent = new Intent(_context, MainActivity.class);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        _context,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        mBuilder.setContentIntent(resultPendingIntent);
        final int mNotificationId = 001;
        final NotificationManager mNotifyMgr =
                (NotificationManager) _context.getSystemService(_context.NOTIFICATION_SERVICE);


        Timer longTimer = new Timer();
        longTimer.schedule(new TimerTask() {
            public void run() {
                mNotifyMgr.notify(mNotificationId, mBuilder.build());
            }
        }, time /*delay in milliseconds i.e. 5 min = 300000 ms or use timeout argument*/);

    }



}
