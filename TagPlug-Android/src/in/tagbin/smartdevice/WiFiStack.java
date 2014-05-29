package in.tagbin.smartdevice;

import java.io.BufferedReader; 
import java.io.PrintWriter;
import java.util.List;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

public class WiFiStack {

	// WIFI related variables
	WifiManager wifiManager;
	PrintWriter out;
	BufferedReader in;
	
	boolean isConnected = false;
	List<WifiConfiguration> list;
	Context context;
	
	private String TAG = "WIFI";
	public String initialSSID = null;
	public int netId;
	public boolean foundMaster = false;

	public WiFiStack(Context c) {
		context = c;
		wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		getInitialSSID();

	}

	// Enable Wifi
	public void enableWifi() {

		wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		if (!wifiManager.isWifiEnabled()) {
			//Log.d(TAG, "Wifi  disbaled");
			wifiManager.setWifiEnabled(true);
		} else{
			
		}
			//Log.d(TAG, "Wifi enabled");

	}

	// All functions for WIFI related work goes here

	public boolean checkIfConnectedToMaster(Context context, Intent intent) {
		boolean isConnected = false;
		Log.d(TAG, "checkIfConnectedToMaster()-> called");
		NetworkInfo info = intent
				.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
		if (info != null) {

			//if (info.isConnected()) {
				// Do your work.
				Log.d(TAG, "isConnected()-> True");
				

				// To check the Network Name or other info:
				wifiManager = (WifiManager) context
						.getSystemService(Context.WIFI_SERVICE);
				WifiInfo wifiInfo = wifiManager.getConnectionInfo();

				String ssid = wifiInfo.getSSID();
				Log.d(TAG, "SSID: " + ssid);
				if (TextUtils.equals("\"" + context.getString(R.string._SSID)
						+ "\"", ssid)) {
					Log.d(TAG, "Connected to SMART DVEICE");
					isConnected = true;
					//createSocket();
				} else {
					Log.d(TAG, "Calling  connectToMaster()");
					this.connectToMaster();
					isConnected = false;
				}
			//}else{
			//	Log.d(TAG,"info.isConnected() returns false");
			//}
		}else{
			Log.d(TAG,"Info Null inside checkIfConnectedToMaster()");
		}
		return isConnected;

	}

	// Connect to Wifi
	public void connectToMaster() {

		Log.d(TAG, "connectToMaster() Called");

		list = wifiManager.getConfiguredNetworks();

		for (final WifiConfiguration i : list) {
			if (i.SSID != null
					&& i.SSID.equals("\"" + context.getString(R.string._SSID)
							+ "\"")) {
				foundMaster = true;
				wifiManager.disconnect();
				wifiManager.enableNetwork(i.networkId, true);

				// Start a Async task
				new AsyncTask<Void, Void, String>() {

					protected void onPostExecute(String msg) {
						
						ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
						NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

						if (mWifi.isConnected()) {
							Log.d(TAG,
									"Good to Go");
						}else{
							Log.d(TAG,
									"Wait buddy");
						}
						
						
						if (wifiManager
								.getConnectionInfo()
								.getSSID()
								.equals("\""
										+ context.getString(R.string._SSID)
										+ "\"")) {
							isConnected = true;
							Log.d(TAG, "Connected to SMART WIFI "
									+ wifiManager.getConnectionInfo().getSSID());
							createSocket("SERVER", "Random data");
						} else {
							isConnected = false;
							Log.d(TAG,
									"SMART device not available Connect to INTERNET");
							createSocket("SERVER", "Random data");
							wifiManager.enableNetwork(netId, true);
						}

					}

					@Override
					protected String doInBackground(Void... params) {
						isConnected = wifiManager.reconnect();
						return "NOT_SURE";
					}

				}.execute(null, null, null);

				break;
			}else foundMaster = false;
		}
		
		
		
	}

	// Create socket and write data
	public void createSocket(String connType,String message) {
		Log.d(TAG,"Creating TCP Socket to "+connType+" with message "+message);
		TcpStack tcpStack = new TcpStack(connType, message);
		tcpStack.writeToSocket();


	}
	
	private void getInitialSSID(){
		initialSSID = wifiManager.getConnectionInfo().getSSID();
		netId = wifiManager.getConnectionInfo().getNetworkId();
		Log.d(TAG,"Initial SSID = "+initialSSID);
	}


}
