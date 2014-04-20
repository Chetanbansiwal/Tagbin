package in.tagbin.smartdevice;

import in.tagbin.smartdevice.TheHeart.MyLocalBinder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.IBinder;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	Intent heart;
	TheHeart theHeart;
	boolean isBound;
	WifiManager mainWifi;
	TextView text;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		text = (TextView) findViewById(R.id.text);
		text.append("Starting\n");

		heart = new Intent(this, TheHeart.class);
		startService(heart);
		bindService(heart, myConnection, 0); // Why 0?

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Toast.makeText(this, "onDestroy()", Toast.LENGTH_LONG).show();
		unbindService(myConnection);
		stopService(heart);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Toast.makeText(this, "onPause()", Toast.LENGTH_LONG).show();
		// When i clicked back button then it paused then destroyed

		// unbindService(myConnection);
	}
	

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		Toast.makeText(this, "onStop()", Toast.LENGTH_LONG).show();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Toast.makeText(this, "onResume()", Toast.LENGTH_LONG).show();
	}

	private ServiceConnection myConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName className, IBinder service) {
			MyLocalBinder binder = (MyLocalBinder) service;
			theHeart = binder.getService();
			isBound = true;
		}

		public void onServiceDisconnected(ComponentName arg0) {
			isBound = false;
		}

	};

}
