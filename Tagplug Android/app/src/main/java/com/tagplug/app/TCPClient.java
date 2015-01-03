package com.tagplug.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;


public class TCPClient {

    Socket socket;
    private String serverMessage;
    public static final String SERVERIP = "23.23.209.78"; //your computer IP address
    public static final int SERVERPORT = 2626;
    private OnMessageReceived mMessageListener = null;
    public static boolean mRun = false;

    PrintWriter out;
    BufferedReader in;

    Context con;

    /**
     *  Constructor of the class. OnMessagedReceived listens for the messages received from server
     */
    public TCPClient(OnMessageReceived listener, Context context) {
        mMessageListener = listener;
        this.con = context;
    }

    /**
     * Sends the message entered by client to the server
     * @param message text entered by client
     */
    public void sendMessage(String message){
        if (out != null && !out.checkError()) {
            out.println(message);
            out.flush();
        }
    }

    public void stopClient(){
        mRun = false;
    }

    public void run() {

        mRun = true;

        try {
            //here you must put your computer's IP address.
            InetAddress serverAddr = InetAddress.getByName(SERVERIP);

            Log.e("TCP Client", "C: Connecting...");


            //create a socket to make the connection with the server
            socket = new Socket(serverAddr, SERVERPORT);

            try {

                //send the message to the server
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

                Log.e("TCP Client", "C: Sent.");

                Log.e("TCP Client", "C: Done.");

                //receive the message which the server sends back
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                //in this while the client listens for the messages sent by the server
                SharedPreferences sPref = con.getSharedPreferences(MainActivity.SHARED_PREF, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sPref.edit();
                editor.putString(MainActivity.SERVER_CONN_STATE,"UP");
                editor.commit();


                while (mRun) {
                    serverMessage = in.readLine();

                    if (serverMessage != null && mMessageListener != null) {
                        //call the method messageReceived from MyActivity class
                        mMessageListener.messageReceived(serverMessage);
                    }
                    serverMessage = null;

                }


                Log.e("RESPONSE FROM SERVER", "S: Received Message: '" + serverMessage + "'");


            } catch (Exception e) {

                Log.e("TCP", "S: Error", e);
                mRun = false;

            } finally {
                //the socket must be closed. It is not possible to reconnect to this socket
                // after it is closed, which means a new socket instance has to be created.
                socket.close();
                mRun = false;
                //in this while the client listens for the messages sent by the server
                SharedPreferences sPref = con.getSharedPreferences(MainActivity.SHARED_PREF, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sPref.edit();
                editor.putString("SERVER_STATE","DOWN");
                editor.commit();
            }

        } catch (Exception e) {

            Log.e("TCP", "C: Error", e);

        }

    }




    //Declare the interface. The method messageReceived(String message) will must be implemented in the MyActivity
    //class at on asynckTask doInBackground
    public interface OnMessageReceived {
        public void messageReceived(String message);
    }
}