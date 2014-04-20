package in.tagbin.smartdevice;

import in.tagbin.smartdevice.TheHeart.MyLocalBinder;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.IBinder;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

	Intent heart;
	TheHeart theHeart;
	boolean isBound;
	WifiManager mainWifi;
	TextView text;
	Button send;
	WiFiStack wiFiStack;
	
	private static String PAYLOAD_DATA = "PAYLOAD_DATA";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		send = (Button) findViewById(R.id.send);
		send.setOnClickListener(this);
		text = (TextView) findViewById(R.id.text);
		text.append("Starting\n");

		heart = new Intent(this, TheHeart.class);
		heart.putExtra(PAYLOAD_DATA, "_INIT");
		startService(heart);
		bindService(heart, myConnection, 0); // Why 0?

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
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
		super.onPause();
		Toast.makeText(this, "onPause()", Toast.LENGTH_LONG).show();
		// unbindService(myConnection);
	}
	

	@Override
	protected void onStop() {
		super.onStop();
		Toast.makeText(this, "onStop()", Toast.LENGTH_LONG).show();
	}

	@Override
	protected void onResume() {
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

	@Override
	public void onClick(View v) {
		
		Intent intermediateData = new Intent(this, TheHeart.class);
		intermediateData.putExtra(PAYLOAD_DATA, 1);
        startService(intermediateData);
		                  
		
	}

}
