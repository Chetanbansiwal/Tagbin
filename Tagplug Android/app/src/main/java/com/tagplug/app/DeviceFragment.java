package com.tagplug.app;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

@SuppressLint("NewApi")
public class DeviceFragment extends Fragment {
	

	/**
	 * The fragment argument representing the section number for this
	 * fragment.
	 */
	private static final String ARG_SECTION_NUMBER = "section_number";
	
	
	


	public static final String TAG = DeviceFragment.class
			.getSimpleName();
	
	
	public static DeviceFragment newInstance(int sectionNumber) {
		DeviceFragment fragment = new DeviceFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return new DeviceFragment();
	}
	
	public DeviceFragment() {
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((MainActivity) activity).onSectionAttached(1);
	}
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 	
	}
	
	

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressLint("NewApi")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.detail, container,
				false);
		//ActionBar actionBar = getActivity().getActionBar();
		//actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		//actionBar.setDisplayShowCustomEnabled(true);
		//actionBar.setDisplayShowHomeEnabled(false);
		//actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		
		//TextView headerText = (TextView) getActivity().findViewById(R.id.headerText);
		//ImageView actionBarLogo = (ImageView) getActivity().findViewById(R.id.actionBarLogo);
		
		//actionBarLogo.setVisibility(View.VISIBLE);
		//headerText.setVisibility(View.GONE);
		
		
		return rootView;
	}



	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressLint("NewApi")
	@Override
	public void onViewCreated(final View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		//ActionBar actionBar = getActivity().getActionBar();
		//final ImageView	iv = (ImageView) actionBar.getCustomView().findViewById(R.id.actionBarLogo);
		
		
		 // Initialize the ViewPager and set an adapter
		 ViewPager pager = (ViewPager) view.findViewById(R.id.pager);
		 pager.setAdapter(new MyPagerAdapter(getChildFragmentManager()));


        // Bind the tabs to the ViewPager
        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);
        tabs.setViewPager(pager);
        tabs.setSmoothScrollingEnabled(true);
		 
	}
	

	public class MyPagerAdapter extends FragmentStatePagerAdapter {

		public MyPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		private final String[] TITLES = { "Bathroom", "Bedroom", "Kitchen",
				"Hall","Other" };

		@Override
		public CharSequence getPageTitle(int position) {
			return TITLES[position];
		}

		@Override
		public int getCount() {
			return TITLES.length;
		}

		@Override
		public Fragment getItem(int arg0) {

			switch (arg0) {
			  
	        case 0:
	            return new DeviceListTemplate("_BATHROOM");
	        
	        case 1:
	            return new DeviceListTemplate("_BEDROOM");
	        
	        case 2:
	            return new DeviceListTemplate("_KITCHEN");
	         
	        case 3:
	            return new DeviceListTemplate("_HALL");
			
		    }
			 return new DeviceListTemplate("_HALL");
		}

		

	}


	

}
