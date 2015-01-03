package com.tagplug.app;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlaceholderFragment extends Fragment {

    public interface PlaceholderfragmentCallback {

    }

    // interface for interacting to interfaces
    private PlaceholderfragmentCallback mCallback;

    /**
     * The fragment argument representing the section number for this fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    WiFiStack wiFiStack;
    DatabaseStack dataBaseStack;
    UDPStack udpStack;
    WifiManager mainWifi;
    SmartConnectionForm form;
    List<ScanResult> wifiList;
    BroadcastReceiver br;


    String mac[] = new String[1000];
    ListView lv;
    ListView alarmList;
    Button alarmButton;
    ProgressBar progressBar;
    int menuNo;
    ArrayList<String> list;
    StableArrayAdapter adapter;
    ImageView alarmWatermark;
    Button searchDevice;

    /* handler to manage data received from Network thread */
    public static Handler handler;

    /**
     * Returns a new instance of this fragment for the given section number.
     */
    public static PlaceholderFragment newInstance(int sectionNumber) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);

        return fragment;
    }

    public PlaceholderFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        //final TcpStack tcp = new TcpStack(getActivity());
        long unixTime = System.currentTimeMillis() / 1000L;
        //tcp.writeToSocket("_S_" + Long.toString(unixTime)+MainActivity.DELIMITER);

        //android.app.ActionBar actionBar = getActivity().getActionBar();

        //actionBar.setDisplayShowCustomEnabled(true);
        //actionBar.setDisplayShowHomeEnabled(false);

        View rootView = null;

        TextView headerText = (TextView) getActivity().findViewById(
                R.id.headerText);
        ImageView actionBarLogo = (ImageView) getActivity().findViewById(
                R.id.actionBarLogo);
        alarmWatermark = (ImageView) getActivity().findViewById(R.id.alarm_watermark);

        //Log.d("WIFI", headerText.getVisibility() + " Header Text");

        //actionBarLogo.setVisibility(View.GONE);
        //headerText.setVisibility(View.VISIBLE);

        //Log.d("WIFI", headerText.getVisibility() + " Header Text");

        // Decides which navigation drawer menu is clicked
        menuNo = getArguments().getInt(ARG_SECTION_NUMBER);
        br = new WifiReceiver();
        mainWifi = (WifiManager) getActivity().getSystemService(
                Context.WIFI_SERVICE);
        if (!mainWifi.isWifiEnabled()) {
            mainWifi.setWifiEnabled(true);
        }

        if (menuNo == 2) { // ADD DEVICE SECTION
            rootView = inflater.inflate(R.layout.add_device, container, false);

            try {
                udpStack = new UDPStack(getActivity(),true,null,"SEARCH");
            } catch (SocketException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(),"Error Ocurred while searching for device",Toast.LENGTH_LONG).show();
            }

            searchDevice = (Button) rootView.findViewById(R.id.deviceSearch);

            final SharedPreferences sPref = getActivity().getSharedPreferences(MainActivity.SHARED_PREF, Context.MODE_PRIVATE);
            searchDevice.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {


                    if(TextUtils.equals(sPref.getString(MainActivity.SSID,""),"")){
                        DialogFragment dialogFrag = SmartConnectionForm.newInstance(12);

                        dialogFrag.show(getActivity().getFragmentManager().beginTransaction(), "dialog");

                    }else  Log.d(MainActivity.TAG, "SSID Found");

                    //TODO needs to send perodicaly
                    udpStack.writeToSocket("hlkATat+mac=?");
                }
            });

            lv = (ListView) rootView.findViewById(R.id.deviceList);
            lv.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {

                    DatabaseStack db = new DatabaseStack(getActivity());
                    Intent addDevice = new Intent(getActivity(),
                            AddDevice.class);
                    Data  item = (Data) lv.getItemAtPosition(position);
                    if (db.notPresent(item.MAC)) {
                        //Toast.makeText(getActivity(), mac[position],Toast.LENGTH_LONG).show();

                        addDevice.putExtra("MAC",item.MAC);
                        addDevice.putExtra("IP", item.IP);
                        Log.d("AddDevice",item.MAC+" "+item.IP);
                        startActivity(addDevice);
                    } else {
                        Toast.makeText(getActivity(), "Already Present",
                                Toast.LENGTH_LONG).show();
                    }
                    db.close();

                }
            });

            handler = new Handler(){
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    List<Data> list = new ArrayList<Data>();
                    //ArrayList<String> list = new ArrayList<String>();
                    String mac_ip[] = msg.obj.toString().split("_");

                    Data d = new Data();
                    d.NAME = "Tagplug";
                    d.MAC = mac_ip[0];
                    d.IP = mac_ip[1];

                    list.add(d);
                    ArrayAdapter<Data> adapter = new ArrayAdapter<Data>(getActivity(),
                            android.R.layout.simple_list_item_activated_1, android.R.id.text1, list);
                    lv.setAdapter(adapter);


                }
            };
            //doScan();
            //mainWifi.startScan();



        } else if (menuNo == 3) { // ALARM SECTION

            rootView = inflater.inflate(R.layout.schedule, container, false);
            if (br.isOrderedBroadcast())
                getActivity().unregisterReceiver(br);


            // tcpStack.writeToSocket("_GET_ALARM_LIST@");

            alarmList = (ListView) rootView.findViewById(R.id.alarmList);


            alarmButton = (Button) rootView.findViewById(R.id.setAlarm);
            alarmButton.setOnClickListener(new OnClickListener() {

                @SuppressWarnings("deprecation")
                @Override
                public void onClick(View v) {

                    DialogFragment newFragment = new TimePickerFragment();
                    newFragment.show(getActivity().getFragmentManager(), "timePicker");
                    Intent intent = new Intent(getActivity(), AlarmSetter.class);
                    //startActivity(intent);

                }
            });

            dataBaseStack = new DatabaseStack(getActivity());

            list = new ArrayList<String>();

            list = dataBaseStack.getAlarm();

            adapter = new StableArrayAdapter(getActivity(),
                    R.layout.listview_text, list);
            alarmList.setAdapter(adapter);

            Log.d("WIFI",alarmList.getCount()+"");
            if(alarmList.getCount() != 0 ){

                //alarmWatermark.setVisibility(View.INVISIBLE);
                //alarmList.setVisibility(View.VISIBLE);

            }

        } else if (menuNo == 4) { // SETTINGS section
            rootView = inflater.inflate(R.layout.settings_contents, container,
                    false);
            if (br.isOrderedBroadcast())
                getActivity().unregisterReceiver(br);
			/*
			 * Here the section get the datapath(DEVICE or SERVER) from shared
			 * Preference. If shared prefrence is not Specified it defaults to
			 * DEVICE
			 */

            Button wifiSetting = (Button) rootView.findViewById(R.id.wifi_setting_button);

            wifiSetting.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogFragment dialogFrag = SmartConnectionForm.newInstance(12);
                    dialogFrag.show(getActivity().getFragmentManager().beginTransaction(), "dialog");
                }
            });

            final SharedPreferences.Editor sPref = getActivity()
                    .getSharedPreferences("dataPathSetting",
                            Context.MODE_PRIVATE).edit();

            SharedPreferences pref;
            final CheckBox viaServer = (CheckBox) rootView
                    .findViewById(R.id.viaServerStatus);
            final CheckBox viaDevice = (CheckBox) rootView
                    .findViewById(R.id.viaDeviceStatus);
            final CheckBox proximity = (CheckBox) rootView
                    .findViewById(R.id.proximity);

            pref = getActivity().getSharedPreferences("dataPathSetting",
                    Context.MODE_PRIVATE);
            Log.d("WIFI", pref.getString("dataPath", null) + " ");
            if (TextUtils.equals(pref.getString("dataPath", null), "DEVICE")) {
                viaDevice.setChecked(true);
            } else if (TextUtils.equals(pref.getString("dataPath", null),
                    "SERVER")) {
                viaServer.setChecked(true);
            } else {
                Log.d("WIFI", "nothing fromshared pref default = DEVICE");
                // No path selected default to Device
                sPref.putString("dataPath", "DEVICE");
                viaDevice.setChecked(true);
            }

            if (TextUtils.equals(pref.getString("PROXIMITY", null), "ENABLED")) {
                proximity.setChecked(true);
            } else
                proximity.setChecked(false);


            proximity.setOnCheckedChangeListener(new OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView,
                                             boolean isChecked) {
                    if (isChecked) {
                        PushService.PROXIMITY_ENABLED = true;
                        sPref.putString("PROXIMITY", "ENABLED");
                        sPref.commit();
                        if (PushService.PROXIMITY_ENABLED) {
                            Log.d("WIFI", "PROXIMITY ENABLED");
                        }
                    } else {
                        PushService.PROXIMITY_ENABLED = false;
                        sPref.putString("PROXIMITY", "DISABLED");
                        sPref.commit();
                        if (!PushService.PROXIMITY_ENABLED) {
                            Log.d("WIFI", "PROXIMITY DISABLED");
                        }
                    }

                }
            });

            viaServer.setOnCheckedChangeListener(new OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView,
                                             boolean isChecked) {

                    if (isChecked) {

                        viaDevice.setChecked(false);
                        //tcp.writeToSocket(MainActivity.VIA_SERVER);
                        Log.d("WIFI", "Writing " + MainActivity.VIA_SERVER);

                        sPref.putString("dataPath", "SERVER");
                        sPref.commit();
                    }

                }
            });
            viaDevice.setOnCheckedChangeListener(new OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView,
                                             boolean isChecked) {

                    if (isChecked) {

                        viaServer.setChecked(false);
                        //tcp.writeToSocket(MainActivity.VIA_DEVICE);
                        Log.d("WIFI", "Writing " + MainActivity.VIA_DEVICE);

                        sPref.putString("dataPath", "DEVICE");
                        sPref.commit();
                    }

                }
            });



        } else {
            if (br.isOrderedBroadcast())
                getActivity().unregisterReceiver(br);
            rootView = inflater.inflate(R.layout.fragment_main, container,
                    false);
            TextView textView = (TextView) rootView
                    .findViewById(R.id.section_label);
            textView.setText(Integer.toString(getArguments().getInt(
                    ARG_SECTION_NUMBER)));

        }

        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        //getActivity().unregisterReceiver(br);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        //DISABLE MULTICAST LOCK for add device section
        if(menuNo == 2){
            udpStack.disableMultiCastLock();
            udpStack.closeSocket();
            Log.d("SERVER","Mulicast Lock released");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("WIFI", "Menu No:"+menuNo);


        if (menuNo == 3) {

            Log.d("WIFI", "Trying to update Alarm list");
            dataBaseStack = new DatabaseStack(getActivity());

            list = new ArrayList<String>();

            list = dataBaseStack.getAlarm();

            adapter = new StableArrayAdapter(getActivity(),
                    android.R.layout.simple_list_item_1, list);
            alarmList.setAdapter(adapter);

            // adapter.clear();
            //list = dataBaseStack.getAlarm();
            //adapter.notifyDataSetChanged();

        }

        //getActivity().registerReceiver(br,new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(getArguments().getInt(
                ARG_SECTION_NUMBER));
    }

    class Data {
        String NAME;
        String MAC;
        String IP;

        @Override
        public String toString() {
            return NAME;
        }
    }

    class WifiReceiver extends BroadcastReceiver {
        public void onReceive(Context c, Intent intent) {


            wifiList = mainWifi.getScanResults();
            ArrayList<String> list = new ArrayList<String>();
            @SuppressWarnings("unused")
            String toMatch;

            for (int i = 0; i < wifiList.size(); i++) {
                if (wifiList.get(i).SSID == "") {
                    continue;
                }
                toMatch = wifiList.get(i).SSID.substring(0, 3);
                mac[i] = wifiList.get(i).BSSID;
                // if( TextUtils.equals(toMatch, SMART_SSID))
                list.add(wifiList.get(i).SSID);
            }
            try {
                lv.setAdapter(new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_list_item_1, list));
                progressBar = (ProgressBar) getActivity().findViewById(
                        R.id.loading);
                progressBar.setVisibility(View.GONE);
            } catch (Exception e) {

                // Toast.makeText(getActivity(), e.toString(),
                // Toast.LENGTH_LONG).show();
                Log.d("WIFI", e.toString());
            }

        }
    }
}


class StableArrayAdapter extends ArrayAdapter<String> {

    HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

    public StableArrayAdapter(Context context, int textViewResourceId,
                              List<String> objects) {
        super(context, textViewResourceId, objects);
        for (int i = 0; i < objects.size(); ++i) {
            mIdMap.put(objects.get(i), i);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        return super.getView(position, convertView, parent);
    }

    @Override
    public long getItemId(int position) {
        String item = getItem(position);
        return mIdMap.get(item);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

}
