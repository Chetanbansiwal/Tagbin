package com.tagplug.app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class SmartConnectionForm extends DialogFragment {

    WiFiStack wiFiStack;
    EditText ssid;
    EditText password;
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

    private String mAuthString;
    private byte mAuthMode;

    public static SmartConnectionForm newInstance(int num){

        SmartConnectionForm dialogFragment = new SmartConnectionForm();
        Bundle bundle = new Bundle();
        bundle.putInt("num", num);
        dialogFragment.setArguments(bundle);

        return dialogFragment;

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View v = inflater.inflate(R.layout.smart_connection_form, null);

        ssid = (EditText) v.findViewById(R.id.username);
        password = (EditText) v.findViewById(R.id.password);


        wiFiStack = new WiFiStack(v.getContext());
        String wifiData = wiFiStack.getAuthTypeAndSSID();
        if(TextUtils.equals(wifiData,"WIFI_OFF")){
            Toast.makeText(v.getContext(),"WIFI is off, turn it on!",Toast.LENGTH_LONG).show();
        }else{
            String data[] = wifiData.split("_");
            ssid.setText(data[2]);
        }

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(v)

                // Add action buttons
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {


                        SharedPreferences sPref = getActivity().getSharedPreferences(MainActivity.SHARED_PREF, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sPref.edit();
                        editor.putString(MainActivity.SSID, ssid.getText().toString());
                        editor.putString(MainActivity.SSID_PASS, password.getText().toString());
                        editor.commit();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SmartConnectionForm.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }
}
