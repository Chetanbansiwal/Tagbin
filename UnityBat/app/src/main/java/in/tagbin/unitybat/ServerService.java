package in.tagbin.unitybat;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;


public class ServerService  extends Service {

    private final int SERVER_PORT = 2626; //Define the server port
    private SensorManager mSensorManager;
    private Sensor mSensor;

    public String TAG = "SERVER";
    public int  i = 0;

    SensorEventListener listen;
    public static String SENSOR_DATA;

    public static boolean isServiceRunning = false;

    public  ServerSocket socServer;
    //Client Socket
    Socket socClient = null;



    private PendingIntent pendingIntent;
    private AlarmManager manager;

    // Services related stuffs
    IBinder mBinder;
    boolean mAllowRebind;


    @SuppressWarnings("unused")
    private final IBinder myBinder = new MyLocalBinder();



    @Override
    public void onCreate() {
        super.onCreate();

        /* Sensor related stuff */
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        listen = new SensorListen();
        mSensorManager.registerListener(listen, mSensor, SensorManager.SENSOR_DELAY_FASTEST);



        /* Wake the Device to receive intent sent from ALARM MANAGER for sending keepAlive Packet */
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
        manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        int interval = 70000;

        if (manager != null) {
            manager.cancel(pendingIntent);
        }

        manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //Start the server stuff
        getDeviceIpAddress();
        //New thread to listen to incoming connections
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    //Create a server socket object and bind it to a port
                    socServer = new ServerSocket(SERVER_PORT);

                    //Infinite loop will listen for client requests to connect
                    while (true) {
                        //Accept the client connection and hand over communication to server side client socket
                        Log.d("IN_SERVER","Waiting for client");
                        socClient = socServer.accept();


                        Thread t = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    //Get the data input stream comming from the client
                                    InputStream is = socClient.getInputStream();
                                    // byte[] data = "Null".getBytes();
                                    //is.read(data);
                                    Log.d("IN_SERVER","New Thread Started"+i++);

                                    //Get the output stream to the client
                                    PrintWriter out = new PrintWriter(
                                            socClient.getOutputStream(), true);
                                    while(true) {
                                        //Write data to the data output stream
                                        out.println(SENSOR_DATA);
                                        Log.d(TAG,SENSOR_DATA);

                                        try {
                                            Thread.sleep(200);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        if(!socClient.isConnected()){
                                            Log.d(TAG,"Socket disconnected");
                                            break;
                                        }


                                    }
                                    //Close the client connection
                                    socClient.close();
                                } catch (IOException e) {
                                    Log.d("IN_SERVER",e.toString());

                                }
                            }
                        });
                        t.start();
                        //For each client new instance of AsyncTask will be created
                        //ServerAsyncTask serverAsyncTask = new ServerAsyncTask();
                        //Start the AsyncTask execution
                        //Accepted client socket object will pass as the parameter

                        //serverAsyncTask.execute(new Socket[] {socClient});
                    }
                } catch (IOException e) {
                    Log.d("IN_SERVER",e.toString());
                    e.printStackTrace();
                }
            }
        }).start();

        isServiceRunning = true;
        return START_STICKY;

    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }



    /**
     * Called when all clients have unbound with unbindService()
     */
    @Override
    public boolean onUnbind(Intent intent) {
            try {
                socServer.close();
                Log.d(TAG,"SEVER CLOSED");
            }catch(IOException e){
                Log.d(TAG,e.toString());
            }
        Log.d(TAG, "Service Unbinded");
        isServiceRunning = false;
        return mAllowRebind;
    }

    /**
     * Called when a client is binding to the service with bindService()
     */
    @Override
    public void onRebind(Intent intent) {

    }

    @Override
    public void onDestroy() {

        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();

    }

    public class MyLocalBinder extends Binder {
        ServerService getService() {
            return ServerService.this;
        }
    }


    public static class AlarmReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent arg1) {

            //TODO send keepAlive packet


        }
    }

    /**
     * Get ip address of the device
     */
    public void getDeviceIpAddress() {
        try {
            //Loop through all the network interface devices
            for (Enumeration<NetworkInterface> enumeration = NetworkInterface
                    .getNetworkInterfaces(); enumeration.hasMoreElements();) {
                NetworkInterface networkInterface = enumeration.nextElement();
                //Loop through all the ip addresses of the network interface devices
                for (Enumeration<InetAddress> enumerationIpAddr = networkInterface.getInetAddresses(); enumerationIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumerationIpAddr.nextElement();
                    //Filter out loopback address and other irrelevant ip addresses
                    if (!inetAddress.isLoopbackAddress() && inetAddress.getAddress().length == 4) {
                        //Print the device ip address in to the text view
                        //tvServerIP.setText(inetAddress.getHostAddress());
                        Log.d("SERVER",inetAddress.getHostAddress());
                    }
                }
            }
        } catch (SocketException e) {
            Log.e("ERROR:", e.toString());
        }
    }


    /**
     * AsyncTask which handles the commiunication with clients
     */
    class ServerAsyncTask extends AsyncTask<Socket, Void, String> {
        //Background task which serve for the client
        Socket mySocket;

        @Override
        protected String doInBackground(Socket... params) {

            String result = null;
            //Get the accepted socket object

            mySocket = params[0];
            try {
                //Get the data input stream comming from the client
                InputStream is = mySocket.getInputStream();
               // byte[] data = "Null".getBytes();
                //is.read(data);
                Log.d("IN_SERVER","New Thread Started"+i++);

                //Get the output stream to the client
                PrintWriter out = new PrintWriter(
                        mySocket.getOutputStream(), true);
                while(true) {
                    //Write data to the data output stream
                    out.println(SENSOR_DATA);
                    Log.d(TAG,SENSOR_DATA);

                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(!mySocket.isConnected()){
                        Log.d(TAG,"Socket disconnected");
                        return result;

                    }


                }
                //Close the client connection
                //mySocket.close();
            } catch (IOException e) {
                Log.d("IN_SERVER",e.toString());

            }
            return result;
        }



        @Override
        protected void onPostExecute(String s) {
            //After finishing the execution of background task data will be write the text view
            Log.d(TAG,s);
        }
    }


    public class SensorListen implements SensorEventListener{

        @Override
        public void onSensorChanged(SensorEvent event) {
            // TODO Auto-generated method stub
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                //getAccelerometer(event);
                ServerService.SENSOR_DATA =  event.values[0]+"";
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // TODO Auto-generated method stub

        }

    }
}

