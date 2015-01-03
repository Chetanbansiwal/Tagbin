package com.tagplug.app;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressLint({ "ValidFragment", "NewApi" })
public class DeviceListTemplate extends Fragment implements OnItemLongClickListener {

	String data = null;
	RelativeLayout deleteDropArea;
	static View statusBar;
	//TcpStack tcpStack;
	DatabaseStack dataBaseStack;
	
	ExpandableListViewAdapter listAdapter;
	ExpandableListView expListView;
	List<String> listDataHeader;
	HashMap<String, List<String>> listDataChild;
	ImageView closeImage;
	
	
	@Override
	public void onResume() {
		super.onResume();

		//listAdapter.notifyDataSetChanged();
		//expListView.setAdapter(listAdapter);
		//getActivity().registerReceiver(Updated, new IntentFilter("stateChanged"));
	}
	
	
	
	@Override
	public void onPause() {
		super.onPause();
		//getActivity().unregisterReceiver(Updated);
	}



	@SuppressLint("ValidFragment")
	public DeviceListTemplate(String payload) {
		data = payload;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.device_list_template,
				container, false);
		
		
		// get the listview
		
		closeImage = (ImageView) rootView.findViewById(R.id.imageView1);
        closeImage.bringToFront();
		rootView.findViewById(R.id.deleteDropArea).setOnDragListener(new MyDragListener());
		expListView = (ExpandableListView) rootView
				.findViewById(R.id.expandableListView);

		 expListView.setGroupIndicator(null);
		// preparing list data
		listDataHeader = new ArrayList<String>();//
		dataBaseStack= new DatabaseStack(getActivity());
		if (data == "_BATHROOM") {
			listDataHeader=dataBaseStack.getDevice("Bathroom");
		}
		if (data == "_BEDROOM") {
			listDataHeader=dataBaseStack.getDevice("Bedroom");
		}
		if (data == "_KITCHEN") {
			listDataHeader=dataBaseStack.getDevice("Kitchen");
		}
		if (data == "_HALL") {
			listDataHeader=dataBaseStack.getDevice("Hall");
		}

		
		listDataChild = new HashMap<String, List<String>>();

		int headerCount = listDataHeader.size();

		// Adding child data
		List<String> a = new ArrayList<String>();
		a.add("Power");
		a.add("Schedule");

		for (int i = 0; i < headerCount; i++) {
			listDataChild.put(listDataHeader.get(i), a);
		}

		listAdapter = new ExpandableListViewAdapter(rootView.getContext(),
				listDataHeader, listDataChild);

		// setting list adapter
		expListView.setAdapter(listAdapter);

         expListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("WIFI","Clicked on "+ position);

            }
        });
		
		//Now the DRAG AND DROP STUFF
		expListView.setOnItemLongClickListener((OnItemLongClickListener) this);

		
		return rootView;
	}

	

	

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressLint("NewApi")
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View v,
			int position, long id) {
		 
		closeImage.setVisibility(View.VISIBLE);
		Animation myFadeInAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.close_image_animation_show);
		closeImage.startAnimation(myFadeInAnimation);
		
		 Log.d("WIFI","Clicked on "+ position);
		 int itemType = ExpandableListView.getPackedPositionType(id);

         if ( itemType == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
             //childPosition = ExpandableListView.getPackedPositionChild(id);
             //groupPosition = ExpandableListView.getPackedPositionGroup(id);

             //do your per-item callback here
            // return retVal; //true if we consumed the click, false if not

         } else if(itemType == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
            
        	 ClipData data = ClipData.newPlainText("", "");
     		DragShadowBuilder shadowBuilder = new DragShadowBuilder(v);
     		v.startDrag(data, shadowBuilder, v, 0);
     		v.setVisibility(View.INVISIBLE);
     		Log.d("WIFI","Long Pressed");
     		
     		
     		//DeviceListTemplate.deleteDropArea.animate().translationX(-53).withLayer();
     		
         } else {
             // null item; we don't consume the click
             return false;
         }
		
		return false;
	}
	
	
	
	public  BroadcastReceiver Updated= new BroadcastReceiver() {

	    @Override
	    public void onReceive(Context context, Intent intent) {
	    	
	        
	    	listAdapter.notifyDataSetChanged();
	    	Log.d("WIFI","Broadcas received for state change");
	    	//expListView.invalidate();
	       // \\Toast.makeText(getActivity(), "State Updated: "+intent.getStringExtra("state"), Toast.LENGTH_LONG).show();
	    }
	};
	
	
	class MyDragListener implements View.OnDragListener {
	    Drawable enterShape = getResources().getDrawable(R.anim.shape_droptarget);
	    Drawable normalShape = getResources().getDrawable(R.anim.shape);
	    

	    @Override
	    public boolean onDrag(View v, DragEvent event) {
	    	
	    	 View view = (View) event.getLocalState();
		     ViewGroup owner = (ViewGroup) view.getParent();
		     RelativeLayout container = (RelativeLayout) v;
		        
	      //int action = event.getAction();
	      switch (event.getAction()) {
	      case DragEvent.ACTION_DRAG_STARTED:
	    	  v.setVisibility(View.VISIBLE);
	        Log.d("WIFI","DRAGGING>>>>>>");
	        break;
	      case DragEvent.ACTION_DRAG_ENTERED:
	    	  //v.setBackground(enterShape);
	          v.setBackgroundDrawable(enterShape);
	        break;
	      case DragEvent.ACTION_DRAG_EXITED:
	    	//  v.setBackground(normalShape);
	        v.setBackgroundDrawable(normalShape);
	    	  Log.d("WIFI","ACTION_DRAG_EXITED>>>>>>");
	        break;
	      case DragEvent.ACTION_DROP:
	    	Log.d("WIFI","ACTION_DROP>>>>>>");
	        owner.removeViewInLayout(view);
	        //v.setBackground(normalShape);
              v.setBackgroundDrawable(normalShape);
	        DatabaseStack db = new DatabaseStack(getActivity());
	        db.deleteAll();
              db.close();
	        owner.invalidate();
	        MediaPlayer mediaPlayer = MediaPlayer.create(getActivity(), R.raw.delete);
	        mediaPlayer.start(); 
	        break;
	      case DragEvent.ACTION_DRAG_ENDED:
		    view.setVisibility(View.VISIBLE);    
	        Log.d("WIFI","ACTION_DRAG_ENDED>>>>>>");
	        Animation myFadeInAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.close_image_animation_hide);
	  		closeImage.startAnimation(myFadeInAnimation);
	    	closeImage.setVisibility(View.INVISIBLE);
	        break;
	      default:
	        break;
	      }
	      return true;
	    }
	  }

}

