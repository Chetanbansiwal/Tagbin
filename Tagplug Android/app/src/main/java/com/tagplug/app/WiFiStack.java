package com.tagplug.app;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.List;

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

    private byte AuthModeOpen = 0x00;
    private byte AuthModeShared = 0x01;
    private byte AuthModeAutoSwitch = 0x02;
    private byte AuthModeWPA = 0x03;
    private byte AuthModeWPAPSK = 0x04;
    private byte AuthModeWPANone = 0x05;
    private byte AuthModeWPA2 = 0x06;
    private byte AuthModeWPA2PSK = 0x07;
    private byte AuthModeWPA1WPA2 = 0x08;
    private byte AuthModeWPA1PSKWPA2PSK = 0x09;
    private byte AUTH_NULL = 0x10;

	public WiFiStack(Context c) {
        context = c;
		wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

	}

	/* Enable Wifi */
	public void enableWifi() {

		wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		if (!wifiManager.isWifiEnabled()) {
			wifiManager.setWifiEnabled(true);
		} else{

		}


	}

    /* Get  MAC */
    public String getMAC(){
        String MAC = null;
        initialSSID = wifiManager.getConnectionInfo().getBSSID();
        return initialSSID;
    }

    /* Get  SSID Connected to */
	public String getInitialSSID(){
		initialSSID = wifiManager.getConnectionInfo().getSSID();
        int iLen = initialSSID.length();
        if (initialSSID.startsWith("\"") && initialSSID.endsWith("\""))
        {
            initialSSID = initialSSID.substring(1, iLen - 1);
        }
        return initialSSID;
	}

    /* Get Auth Type and SSID */
    public String getAuthTypeAndSSID(){
        if(wifiManager.isWifiEnabled()) {
            String mAuthString = null;
            byte mAuthMode = AUTH_NULL;
            String initialSsid = getInitialSSID();
            List<ScanResult> ScanResultlist = wifiManager.getScanResults();
            for (int i = 0, len = ScanResultlist.size(); i < len; i++) {
                ScanResult AccessPoint = ScanResultlist.get(i);

                if (AccessPoint.SSID.equals(initialSsid)) {
                    boolean WpaPsk = AccessPoint.capabilities.contains("WPA-PSK");
                    boolean Wpa2Psk = AccessPoint.capabilities.contains("WPA2-PSK");
                    boolean Wpa = AccessPoint.capabilities.contains("WPA-EAP");
                    boolean Wpa2 = AccessPoint.capabilities.contains("WPA2-EAP");

                    if (AccessPoint.capabilities.contains("WEP")) {
                        mAuthString = "OPEN-WEP";
                        mAuthMode = AuthModeOpen;
                        break;
                    }

                    if (WpaPsk && Wpa2Psk) {
                        mAuthString = "WPA-PSK WPA2-PSK";
                        mAuthMode = AuthModeWPA1PSKWPA2PSK;
                        break;
                    } else if (Wpa2Psk) {
                        mAuthString = "WPA2-PSK";
                        mAuthMode = AuthModeWPA2PSK;
                        break;
                    } else if (WpaPsk) {
                        mAuthString = "WPA-PSK";
                        mAuthMode = AuthModeWPAPSK;
                        break;
                    }

                    if (Wpa && Wpa2) {
                        mAuthString = "WPA-EAP WPA2-EAP";
                        mAuthMode = AuthModeWPA1WPA2;
                        break;
                    } else if (Wpa2) {
                        mAuthString = "WPA2-EAP";
                        mAuthMode = AuthModeWPA2;
                        break;
                    } else if (Wpa) {
                        mAuthString = "WPA-EAP";
                        mAuthMode = AuthModeWPA;
                        break;
                    }

                    mAuthString = "OPEN";
                    mAuthMode = AuthModeOpen;

                }
            }
            return mAuthString+"_"+mAuthMode+"_"+initialSsid;
        }else return "WIFI_OFF";

    }


}