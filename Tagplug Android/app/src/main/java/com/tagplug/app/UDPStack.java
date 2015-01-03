/*
 * This Class has two function
 * a)connectToSocket()
 * b)writeToSocket()
 * 
 * Always call writeToSocket since it checks if socket is connected , and connect if not present
 * 
 * 
 * Following params is mandatory for constructor
 * Connection type..connect to SERVER or DEVICE
 * Message to be Sent
 * 
 * Absence of any of them will lead to failure
 */
package com.tagplug.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Enumeration;

public class UDPStack {

    /* Connection type */
    public String connType = null;
    NetworkInterface network = null;


    /* IP Address for server and device(TagPlug) */
    public static String SERVER_IP = "23.23.209.78";
    public static String DEVICE_IP = "192.168.2.255";//Not Used
    public String IP = null;
    public String BROADCAST_IP = null;
    public String RECEIVED_FOR = null;

    /* PORT for server and device(TagPlug) */
    public static int  SERVER_PORT = 2626;
    public static int DEVICE_PORT = 988;
    public static int LOCAL_PORT = 1234;

    /* Socket */
    public DatagramPacket packet;
    public MulticastSocket socket = null;
    DatabaseStack db;

    /* TAG for debugging */
    public String TAG = "WIFI";

    /* Boolean for Status Update */
    boolean isStatusUpdated = false;

    /* Shared preference for Data Path */
    static Context context;
    SharedPreferences sPref;
    String prefDataPath;
    AsyncTask<Void, Void, String> asyncTask;
    public static WifiManager.MulticastLock multicastLock = null;


    public UDPStack(Context con, boolean multicast, String IP, final String RECEIVED_FOR) throws SocketException {


        this.RECEIVED_FOR = RECEIVED_FOR;
        UDPStack.context = con;
        this.socket = null;
        this.IP = IP;
        db = new DatabaseStack(context);

        //TODO Update Broadcast IP when connected to Home WIFI
        try {
            BROADCAST_IP = getBroadcastAddress().toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Remove the first forward slash from IP
        BROADCAST_IP = BROADCAST_IP.substring(1);
        DEVICE_IP = (BROADCAST_IP == null)?DEVICE_IP:BROADCAST_IP;
        Log.d("UDP SERVER","Broadcast address is "+DEVICE_IP);


        if(multicast ) {
            WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            multicastLock = wifi.createMulticastLock("multicastLock");
            multicastLock.setReferenceCounted(true);
            if(!multicastLock.isHeld())
                multicastLock.acquire();
            Log.d("UDP_SERVER","Multicast acquired");
        }
        try {
            socket = new MulticastSocket();
        } catch (SocketException e) {
            e.printStackTrace();
            Log.d("SERVER",e.getMessage().toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.setBroadcast(true);
        } catch (SocketException e) {
            e.printStackTrace();
            Log.d("SERVER",e.getMessage().toString());
        }


        /* START THE UDP SERVER */
        Thread thread = new Thread(new Runnable(){

            @Override
            public void run() {
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

                String message;
                byte[] lmessage = new byte[1024];
                DatagramPacket packet = new DatagramPacket(lmessage, lmessage.length);



                try {

                    Log.d("SERVER","UDP SERVER STARTED"+socket.getLocalPort());
                    while(true) {
                        // TODO check if socket exist if not recreate the socket
                        socket.receive(packet);
                        String remoteIp = packet.getAddress().toString();
                        remoteIp = remoteIp.replace("/","");

                        message = new String(lmessage, 0, packet.getLength());
                        Log.d("SERVER","Received  <<-"+message+"_"+remoteIp);

                        Message msg = new Message();
                        msg.obj = message+"_"+remoteIp;

                        if(TextUtils.equals(RECEIVED_FOR,"SEARCH")){
                            PlaceholderFragment.handler.sendMessage(msg);
                        }else if(TextUtils.equals(RECEIVED_FOR,"ADD")){
                            AddDevice.handler.sendMessage(msg);
                        }else if(TextUtils.equals(RECEIVED_FOR,"ON_OFF")){
                           ExpandableListViewAdapter.handler.sendMessage(msg);
                        }else if(TextUtils.equals(RECEIVED_FOR,"ALARM")){
                            AlarmSetter.handler.sendMessage(msg);
                        }


                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }

                if (socket != null) {
                    socket.close();
                }
            }

        });
        thread.start();
    }


    public void disableMultiCastLock(){
        if (multicastLock != null) {
            multicastLock.release();
            multicastLock = null;
        }
    }

    public void closeSocket(){
        if (socket != null) {
            socket.close();
        }
    }

    public void writeToSocket(final String MESSAGE){

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {

                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
                try {

                    Log.d("SERVER"," Connecting to...:"+socket.getLocalPort());
                    InetAddress address = InetAddress.getByName(DEVICE_IP);
                    packet = new DatagramPacket(MESSAGE.getBytes(), MESSAGE.length(),
                            address, DEVICE_PORT);

                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("SERVER",e.getMessage().toString()+ "Exception 1");
                }
                try {
                    //TODO multicast fails after some time and start working on reboot of device
                    socket.send(packet);
                    String _a = new String(packet.getData(), 0, packet.getLength());
                    Log.d("SERVER","Sending  ->>"+_a);
                    if(multicastLock.isHeld()){
                        Log.d("SERVER","multicast active");
                    }else  Log.d("SERVER","multicast not-active");


                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("SERVER",e.getMessage().toString()+ "Exception 2");
                }

            }
        });
        thread.start();

    }


    public String covertToTime(long unixTimestamp){
        long unixSeconds = 1372339860;
        Date date = new Date(unixSeconds*1000L); // *1000 is to convert seconds to milliseconds
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z"); // the format of your date
        //sdf.setTimeZone(TimeZone.getTimeZone("GMT-4"));
        String formattedDate = sdf.format(date);
        return formattedDate;
    }

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

    public static String getFormattedDate(){

        long unixTime = System.currentTimeMillis() / 1000L;
        Date date = new Date(unixTime); // *1000 is to convert seconds to milliseconds
        SimpleDateFormat sdf = new SimpleDateFormat("E hh:mm a"); // the format of your date
        String formattedDate = sdf.format(date);
        return formattedDate;

    }

    public void ShowToast(final String sText) {
        final Context MyContext = context;

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {

                //PlaceholderFragment.deviceClock.setText(sText);
                Toast toast1 = Toast.makeText(MyContext, sText,
                        Toast.LENGTH_SHORT);
                toast1.show();
            }
        });
    };

    public static String getBroadcast() throws SocketException {
        System.setProperty("java.net.preferIPv4Stack", "true");
        for (Enumeration<NetworkInterface> niEnum = NetworkInterface.getNetworkInterfaces(); niEnum.hasMoreElements();) {
            NetworkInterface ni = niEnum.nextElement();
            if (!ni.isLoopback()) {
                for (InterfaceAddress interfaceAddress : ni.getInterfaceAddresses()) {
                    return interfaceAddress.getBroadcast().toString().substring(1);
                }
            }
        }
        return null;
    }

    InetAddress getBroadcastAddress() throws IOException {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcp = wifi.getDhcpInfo();
        // handle null somehow

        int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++)
            quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
        return InetAddress.getByAddress(quads);
    }

}