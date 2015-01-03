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
package com.tagplug.app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class PushService extends Service {


    public static boolean isServiceRunning = false;

    /** Keeps track of all current registered clients. */
    //ArrayList<Messenger> mClients = new ArrayList<Messenger>();
    //mClients.add(msg.replyTo);
    //mClients.remove(i);

    private PendingIntent pendingIntent;
    private AlarmManager manager;

    // Services related stuffs
    IBinder mBinder;
    boolean mAllowRebind;

    // Module class Instantiation
    WiFiStack wiFiStack;
    DatabaseStack db;
    public static TCPClient tcpClient;
    public static Thread tcpClientThread;

    @SuppressWarnings("unused")
    private final IBinder myBinder = new MyLocalBinder();
    //IntentFilter filter;
    //BroadcastReceiver r;

    // Global variables
    public String TAG = "WIFI";
    boolean _HAS_MASTER = false; // A MASTER DEVICE IS CONFIGURED OR NOT

    // Variables for Proximity
    static boolean PROXIMITY_CASE_ON = false;
    static boolean PROXIMITY_CASE_OFF = false;
    public static boolean PROXIMITY_ENABLED = false;




    @Override
    public void onCreate() {
        super.onCreate();


        /* Wake the Device to receive intent sent from ALARM MANAGER for sending keepAlive Packet */
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
        manager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        int interval = 70000;

        if (manager != null) {
            manager.cancel(pendingIntent);
        }

        manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);


        // Object Initialization
        //wiFiStack = new WiFiStack(this);
        db = new DatabaseStack(this);


        //filter = new IntentFilter();
        //r = new WifiChangeReceiver();

        // db.addDevice("TESTING", "BED", "BATH", "AN:KI:TS:IN:HA");
        if (db.getDeviceCount() >= 1) {
            _HAS_MASTER = true;
            Log.d(TAG, "Master Device is present");
        }
        //wiFiStack.enableWifi();

        // filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        //filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);// connectivity
        // changed
        //filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);// enable
        // disable
        // enabling
        // disabling
        //filter.addAction(WifiManager.RSSI_CHANGED_ACTION);// Change in Signal
        // Strength for
        // Proximity

        //registerReceiver(r, filter);
        Log.d("TcpServer", "Service Started");
        // wiFiStack.createSocket("SERVER", "SOME AWESOME STUFFS");

        //tcpStack.logger();


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


       tcpClientThread = new Thread(new Runnable() {
            @Override
            public void run() {

                tcpClient = new TCPClient(new TCPClient.OnMessageReceived() {
                    @Override
                    //here the messageReceived method is implemented
                    public void messageReceived(String message) {
                        //this method calls the onProgressUpdate
                        //publishProgress(message);
                        Log.i("Debug","Input message: " + message);
                        // TODO handle closed connection scenario

                    }
                },getApplicationContext());
                tcpClient.run();

            }
        });
        SharedPreferences sPref = getSharedPreferences(MainActivity.SHARED_PREF, Context.MODE_PRIVATE);
        String state = sPref.getString(MainActivity.SERVER_CONN_STATE,"");

        if(TextUtils.equals(state,  "DOWN")){
            tcpClientThread.start();
            Log.d("TcpClient","Service DOWN => starting");
        }else if(TextUtils.equals(state,  "UP")){
            tcpClientThread.start();
        }else {
            Log.d("TcpClient","No state received from Shared preferences = > Statring");
            tcpClientThread.start();
        }






        isServiceRunning = true;
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /** Called when all clients have unbound with unbindService() */
    @Override
    public boolean onUnbind(Intent intent) {
        Log.d("TcpServer", "Service Unbinded");
        isServiceRunning = false;
        return mAllowRebind;
    }

    /** Called when a client is binding to the service with bindService() */
    @Override
    public void onRebind(Intent intent) {

    }

    @Override
    public void onDestroy() {
        SharedPreferences sPref = getSharedPreferences(MainActivity.SHARED_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sPref.edit();
        editor.putString("SERVER_STATE","DOWN");
        editor.commit();

        Log.d("TcpServer", "Service Destroyed");
        //unregisterReceiver(r);
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
        // Date d = new Date();
        // CharSequence s = DateFormat.format("EEEE-hh:mm:ss", d.getTime());
        // generateNoteOnSD("Notes.txt","Service closed at "+ s+"\n");
    }

    public class MyLocalBinder extends Binder {
        PushService getService() {
            return PushService.this;
        }
    }



    public static class AlarmReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent arg1) {

            //TODO send keepAlive packet

            if(tcpClient.mRun) {
                /* client is connected, send Keep Alive Packet */
                tcpClient.sendMessage("_KEEP_ALIVE");
                Log.d("SERVICE","SEND keep Alive Packet");
            }else{
                /* Client ain't connected */
                //tcpClientThread.start();
                Log.d("SERVICE","Client not connected while sending keep alive packet => re connecting");

            }

        }

    }

    public class WifiChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            SharedPreferences sPref = getSharedPreferences("dataPathSetting", Context.MODE_PRIVATE);

            //		getApplicationContext().getSharedPreferences("dataPathSetting", Context.MODE_PRIVATE).edit();//getSharedPreferences("dataPathSetting", Context.MODE_PRIVATE).edit();
            if(TextUtils.equals(sPref.getString("PROXIMITY", ""),"ENABLED")){
                PROXIMITY_ENABLED = true;
            }else{
                PROXIMITY_ENABLED = false;
            }


            /** If a Master Device is added */
            if (_HAS_MASTER) {

                Log.d(TAG, "Broadcast Received "
                        + intent.getAction().toString());
                {
                    wiFiStack.enableWifi();

					/*
					 * PROXIMIY: There are 4 levels of signal 1 - 4 1 being
					 * weakest, 4th being strongest if signal is less than or
					 * equal to 1 => TURN OFF if signal is geater than or equal
					 * to 3 => TURN ON
					 *
					 * CSS = Current Signal Strength
					 */
                    if (PushService.PROXIMITY_ENABLED) {
                        Log.d("WIFI","PROXIMITY ENABLED");
                        int CSS = getSignalStrength();
                        if (CSS == 3 || CSS == 4) {
                            PROXIMITY_CASE_OFF = false;
                            PROXIMITY_CASE_ON = true;
                            //tcpStack.writeToSocket(MainActivity._OFF);

							/*
							 * //Now lets recheck after 2 seconds try {
							 * wait(2000); int CSS_TEMP = getSignalStrength();
							 * if(CSS_TEMP == 3 || CSS_TEMP == 4){
							 * tcpStack.writeToSocket("_T_1@"); } } catch
							 * (InterruptedException e) { e.printStackTrace(); }
							 */

                        } else if ( CSS <= 1) {
                            PROXIMITY_CASE_OFF = true;
                            PROXIMITY_CASE_ON = false;
                            //tcpStack.writeToSocket(MainActivity._ON);
                            // Now lets recheck after 2 seconds
							/*
							 * try { wait(2000); int CSS_TEMP =
							 * getSignalStrength(); if(CSS_TEMP == 1){
							 * tcpStack.writeToSocket("_T0@"); } } catch
							 * (InterruptedException e) { e.printStackTrace(); }
							 */
                        }
                    }else{
                        //Toast("Proximity is not enabled");
                    }

                    /**
                     * No Need to call next function from within since once
                     * enabled it will again pass a broadcast that will fire up
                     * our next required function ->checkIfConnectedToMaster()
                     */
                    // wiFiStack.checkIfConnectedToMaster(context, intent);
                }
            } else
                Log.d(TAG, "No Master Device Present");
            // Toast("Lets start with a device");

        };

        public int getSignalStrength() {
            int signalStrength = 0;
            List<ScanResult> results = wiFiStack.wifiManager.getScanResults();
            for (ScanResult result : results) {
                if (result.BSSID.equals(wiFiStack.wifiManager
                        .getConnectionInfo().getBSSID())) {
                    int level = WifiManager
                            .calculateSignalLevel(wiFiStack.wifiManager
                                            .getConnectionInfo().getRssi(),
                                    result.level);
                    int difference = level * 100 / result.level;

                    if (difference >= 100)
                        signalStrength = 4;
                    else if (difference >= 75)
                        signalStrength = 3;
                    else if (difference >= 50)
                        signalStrength = 2;
                    else if (difference >= 25)
                        signalStrength = 1;
                    Log.d(TAG, "Signal Strength " + signalStrength);
                    Toast(signalStrength + "");

                }

            }

            return signalStrength;

        }

        public void Toast(final String sText) {
            final Context MyContext = getApplicationContext();

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast toast1 = Toast.makeText(MyContext, sText,
                            Toast.LENGTH_SHORT);
                    toast1.show();
                }
            });
        };

        public  void generateNoteOnSD(String sFileName, String sBody) {
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

