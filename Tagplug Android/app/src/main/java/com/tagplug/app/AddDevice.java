package com.tagplug.app;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AddDevice extends Activity {

    Spinner location, type;
    ActionBar actionBar;
    Button save;
    EditText name;
    EditText ssid;
    EditText pass;
    DatabaseStack db = new DatabaseStack(this);
    String Mac;
    Intent i;
    public static Handler handler;
    ProgressBar loading;
    UDPStack udpStack = null;
    String authKey = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.save_device);
        //actionBar.setDisplayShowCustomEnabled(true);
        //actionBar.setCustomView(R.layout.custom_action_bar);

        Random random = new Random();
        authKey = java.util.UUID.randomUUID().toString();


        loading = (ProgressBar) findViewById(R.id.saveDeviceLoading);
        loading.setVisibility(View.INVISIBLE);
        save = (Button) findViewById(R.id.saveButton);
        name = (EditText) findViewById(R.id.plugName);

        location = (Spinner) findViewById(R.id.location);
        type = (Spinner) findViewById(R.id.type);
        i = getIntent();


        List<String> list = new ArrayList<String>();
        list.add("Bedroom");
        list.add("Bathroom");
        list.add("Hall");
        list.add("Kitchen");
        //typeSelect.setAdapter(dataAdapter);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.listview_text, list);
        location.setAdapter(dataAdapter);

        List<String> list1 = new ArrayList<String>();

        list1.add("Fan");
        list1.add("Light");
        list1.add("Coffee Maker");
        list1.add("T.V.");
        list1.add("Water Motor");
        list1.add("Geyser");
        //typeSelect.setAdapter(dataAdapter);

        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(this, R.layout.listview_text, list1);
        type.setAdapter(dataAdapter1);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                udpStack.closeSocket();

                db.addDevice(name.getText().toString(), type.getSelectedItem().toString(), location.getSelectedItem().toString(), i.getStringExtra("MAC"), authKey);

                Toast.makeText(getApplicationContext(), "Added Successfully!", Toast.LENGTH_LONG).show();

                Log.d("Info", db.getData());
                db.close();
                loading.setVisibility(View.INVISIBLE);
                //Navigate to main Activity
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);

            }
        };

        save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                /*db.addDevice("a", "Bathroom", "Geyser", "A1234560","12123");
                db.addDevice("a", "Bedroom", "Light", "B1234561","12124");
                db.addDevice("a", "Bedroom", "AC", "C1234562","12125");
                db.addDevice("a", "Kitchen", "Coffee Maker", "D1234563","12126");
                db.addDevice("a", "Hall", "AC", "1234564","E12127");
                db.addDevice("a", "Hall", "LIGHT", "F1234565","12128");
                */
                String match = "";
                if (!TextUtils.equals(name.getText().toString(), match)) {
                    loading.setVisibility(View.VISIBLE);
                    new AsyncTask<Void, Void, String>() {

                        @Override
                        protected String doInBackground(Void... params) {

                            try {
                                udpStack = new UDPStack(getApplicationContext(), true, null, "ADD");
                            } catch (SocketException e) {
                                e.printStackTrace();
                            }
                            long unixTime = System.currentTimeMillis() / 1000L;
                            WiFiStack wiFiStack = new WiFiStack(getApplicationContext());
                            String MAC = wiFiStack.getMAC();
                            String DATA = authKey+"_" + unixTime + "_" + MAC + "_" + TCPClient.SERVERIP + "_" + TCPClient.SERVERPORT;
                            udpStack.writeToSocket(DATA);
                            return null;
                        }


                        @Override
                        protected void onPostExecute(String result) {


                            super.onPostExecute(result);
                        }


                    }.execute(null, null, null);


                }

            }
        });
    }


}