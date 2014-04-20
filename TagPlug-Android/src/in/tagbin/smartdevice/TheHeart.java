/*
 * TheHeart.java
 * WorkFlow:
 * This class work in two mode
 * 1)When device start up, it check if WIFI is enabled, if not it enable the WIFI "enableWifi()"
 *   then it check and connect to SMART DEVICE"S WIFI by calling "connectToMaster()"
 *   After connectToMaster() is called it checks for existing PayloadStore for existing Payload data to be
 *   sent if exist by calling sendDataFromPayloadStore().After the payload data is sent to SMART DEVICE
 *   a acknowledge is received which signifies that required payload has been delivered successfully
 *   and corresponding payload data is nullified
 *   It has got a Broadcast receiver running which reacts to changing status of Broadcast Receiver
 *   SUMMARY( In any case it will take care that our device is connected to SMART DEVICE, if not so it will keep it connected to that device)
 *2)When TheHeart.java receives a notification of data availability in PayLoadStore
 *  if data is available in the Payload store it goes through following step
 *  YET TO BE DECIDED
 *  enableWifi();
 *  generate Intent for Broadcast receiver and then the same process as mentioned in STEP 1
 *  
 * 
 */
package in.tagbin.smartdevice;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

public class TheHeart extends Service {
	IBinder mBinder;
	boolean mAllowRebind;

	WiFiStack wiFiStack;

	private final IBinder myBinder = new MyLocalBinder();
	BroadcastReceiver r;

	public String TAG = "WIFI";
	boolean _HAS_MASTER = true;  //A MASTER DEVICE IS CONFIGURED

	@Override
	public void onCreate() {
		super.onCreate();
		wiFiStack = new WiFiStack(this);
		wiFiStack.enableWifi();

		IntentFilter filter = new IntentFilter();
		//filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
		filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);// connectivity
																	// changed
		filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);// enable
																// disable
																// enabling
																// disabling
		r = new WifiChangeReceiver();
		registerReceiver(r, filter);
		Log.d(TAG, "Service Started");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
		Date d = new Date();
		CharSequence s = DateFormat.format("EEEE-hh:mm:ss", d.getTime());
		// generateNoteOnSD("Notes.txt","Service started at "+ s+"\n");

		return START_NOT_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	/** Called when all clients have unbound with unbindService() */
	@Override
	public boolean onUnbind(Intent intent) {
		return mAllowRebind;
	}

	/** Called when a client is binding to the service with bindService() */
	@Override
	public void onRebind(Intent intent) {

	}

	@Override
	public void onDestroy() {
		unregisterReceiver(r);
		Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
		Date d = new Date();
		CharSequence s = DateFormat.format("EEEE-hh:mm:ss", d.getTime());
		// generateNoteOnSD("Notes.txt","Service closed at "+ s+"\n");
		/*
		 * try { socket.close(); Log.d("WIFI", "SOCKET is Closed"); } catch
		 * (IOException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } if (wifiManager.disconnect()) { Log.d("WIFI",
		 * "WIFIMANAGER disconnected"); }
		 */

	}

	public class MyLocalBinder extends Binder {
		TheHeart getService() {
			return TheHeart.this;
		}
	} 

	public class WifiChangeReceiver extends BroadcastReceiver {
		// BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) { 
			
			
			NetworkInfo info = (NetworkInfo)intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
			//Log.d(TAG,"DetailedState "+info.toString());
			
			//If a Master Device is added
			if (_HAS_MASTER) {

				Log.d(TAG, "Broadcast Received "
						+ intent.getAction().toString());

			/*	String action = intent.getAction();
				if (WifiManager.SUPPLICANT_STATE_CHANGED_ACTION.equals(action) && !wiFiStack.isConnected ) {
					SupplicantState state = intent
							.getParcelableExtra(WifiManager.EXTRA_NEW_STATE);
					if (SupplicantState.isValidState(state)
							&& state == SupplicantState.COMPLETED) {
					        wiFiStack.connectToMaster();
					}
				} else
				*/
				{
					wiFiStack.enableWifi();//No Need to call next function from within since once enabled it will again pas a broadcast that will fire up our next required function
					wiFiStack.checkIfConnectedToMaster(context, intent);
				}
			}

		};

		// Send payload data from Payload store if exist
		public void sendDataFromPayloadStore() {
			// Will be implemented later
		}

		public void Toast(final String sText) {
			final Context MyContext = getApplicationContext();

			new Handler(Looper.getMainLooper()).post(new Runnable() {
				@Override
				public void run() {
					Toast toast1 = Toast.makeText(MyContext, sText,
							Toast.LENGTH_LONG);
					toast1.show();
				}
			});
		};

		public void generateNoteOnSD(String sFileName, String sBody) {
			try {
				File root = new File(Environment.getExternalStorageDirectory(),
						"Notes");
				if (!root.exists()) {
					root.mkdirs();
				}
				File gpxfile = new File(root, sFileName);
				BufferedWriter bW;

				bW = new BufferedWriter(new FileWriter(gpxfile, true));

				bW.write(sBody);
				bW.newLine();
				bW.flush();
				bW.close();
			} catch (IOException e) {
				e.printStackTrace();

			}
		}

	}
}