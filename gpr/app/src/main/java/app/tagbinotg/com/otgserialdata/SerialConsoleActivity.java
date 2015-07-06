package app.tagbinotg.com.otgserialdata;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import app.tagbinotg.com.otgserialdata.driver.UsbSerialPort;
import app.tagbinotg.com.otgserialdata.util.HexDump;
import app.tagbinotg.com.otgserialdata.util.SerialInputOutputManager;

/* Copyright 2011-2013 Google Inc.
        * Copyright 2013 mike wakerly <opensource@hoho.com>
        *
        * This library is free software; you can redistribute it and/or
        * modify it under the terms of the GNU Lesser General Public
        * License as published by the Free Software Foundation; either
        * version 2.1 of the License, or (at your option) any later version.
        *
        * This library is distributed in the hope that it will be useful,
        * but WITHOUT ANY WARRANTY; without even the implied warranty of
        * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
        * Lesser General Public License for more details.
        *
        * You should have received a copy of the GNU Lesser General Public
        * License along with this library; if not, write to the Free Software
        * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
        * USA.
        *
        * Project home page: https://github.com/mik3y/usb-serial-for-android
        */


/**
 * Monitors a single {@link UsbSerialPort} instance, showing all data
 * received.
 *
 * @author mike wakerly (opensource@hoho.com)
 */
public class SerialConsoleActivity extends Activity {

    String s;

    private final String TAG = SerialConsoleActivity.class.getSimpleName();
    private final int lengthTime=20;


    private int amountScroll=(int)3060/150;

    private final int firstImageStart=200;
    private final int firstImageEnd=425;

    private final int secondImageStart=600;
    private final int secondImageEnd=900;

    private final int thirdImageStart=990;
    private final int thirdImageEnd=1190;

    private final int fourthImageStart=1250;
    private final int fourthImageEnd=1550;

    private final int fifthImageStart=1700;
    private final int fifthImageEnd=2050;

    private final int sixthImageStart=2200;
    private final int sixthImageEnd=2450;

    private final int seventhImageStart=2700;
    private final int seventhImageEnd=2900;

    private final int eigthImageStart=3100;
    private final int eigthImageEnd=3600;

    private final int ninthImageStart=3900;
    private final int ninthImageEnd=4050;

    private int offset;

    private final int startPoint=3;

    /**
     * Driver instance, passed in statically via
     * {@link #show(Context, UsbSerialPort)}.
     *
     * <p/>
     * This is a devious hack; it'd be cleaner to re-create the driver using
     * arguments passed in with the {@link #startActivity(Intent)} intent. We
     * can get away with it because both activities will run in the same
     * process, and this is a simple demo.
     */
    //private static UsbSerialPort sPort = null;
    private static UsbSerialPort sPort = null;



    // private TextView mTitleTextView;
    private HorizontalScrollView mImageSlider;
    private TextView mDumpTextView;
    private int i=0;
    private int j=0;
    private int dataDistance;
    private boolean isInteger;
    private long time;
    private boolean istime=true;
    private long unixtime;
    private boolean isunixtime=true;
    private int distance;
    private int[] constDistance=new int[lengthTime];
    private  int counter=0;
    private boolean runExpandImage=false;

    public int[] dataArray=new int[15];
    public int indx=1;
    //private TextView mDumpTextView2;
    //private TextView mDumpTextView3;


    private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();

    private SerialInputOutputManager mSerialIoManager;

    private final SerialInputOutputManager.Listener mListener =
            new SerialInputOutputManager.Listener() {

                @Override
                public void onRunError(Exception e) {
                    Log.d(TAG, "Runner stopped.");
                }

                @Override
                public void onNewData(final byte[] data) {
                    SerialConsoleActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            SerialConsoleActivity.this.updateReceivedData(data);
                        }
                    });
                }
            };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.serial_console_3);
        // mTitleTextView = (TextView) findViewById(R.id.demoTitle);
        mImageSlider=(HorizontalScrollView) findViewById(R.id.horizontalScrollView);
        mDumpTextView = (TextView) findViewById(R.id.consoleText);
        //mDumpTextView2 = (TextView) findViewById(R.id.textView);
        //mDumpTextView3 = (TextView) findViewById(R.id.textView3);

        mImageSlider.setSmoothScrollingEnabled(true);
        mImageSlider.setHorizontalScrollBarEnabled(false);


        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        offset=(int)((float)metrics.widthPixels/2);
        //Toast.makeText(this,"offset "+amountScroll,Toast.LENGTH_SHORT).show();

        // mScrollView = (ScrollView) findViewById(R.id.demoScroller);
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopIoManager();
        if (sPort != null) {
            try {
                sPort.close();
            } catch (IOException e) {
                // Ignore.
            }
            sPort = null;
        }
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "Resumed, port=" + sPort);
        if (sPort == null) {
            //mTitleTextView.setText("No serial device.");
        } else {
            final UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);

            UsbDeviceConnection connection = usbManager.openDevice(sPort.getDriver().getDevice());
            if (connection == null) {
                //mTitleTextView.setText("Opening device failed");
                return;
            }

            try {
                sPort.open(connection);
                sPort.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
                //sPort.setParameters(9600, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
            } catch (IOException e) {
                Log.e(TAG, "Error setting up device: " + e.getMessage(), e);
                //mTitleTextView.setText("Error opening device: " + e.getMessage());
                try {
                    sPort.close();
                } catch (IOException e2) {
                    // Ignore.
                }
                sPort = null;
                return;
            }
            //mTitleTextView.setText("Serial device: " + sPort.getClass().getSimpleName());
        }
        onDeviceStateChange();
    }

    private void stopIoManager() {
        if (mSerialIoManager != null) {
            Log.i(TAG, "Stopping io manager ..");
            mSerialIoManager.stop();
            mSerialIoManager = null;
        }
    }

    private void startIoManager() {
        if (sPort != null) {
            Log.i(TAG, "Starting io manager ..");
            mSerialIoManager = new SerialInputOutputManager(sPort, mListener);
            mExecutor.submit(mSerialIoManager);
        }
    }

    private void onDeviceStateChange() {
        stopIoManager();
        startIoManager();
    }

    //float dataAvg;

    private void updateReceivedData(byte[] data) {


        /*final String message = "Read " + data.length + " bytes: \n"
                + HexDump.dumpHexString(data) + "\n\n";

        //mDumpTextView.append(message);
       // mScrollView.smoothScrollTo(0, mDumpTextView.getBottom());*/

        mDumpTextView.setText("");
        //mDumpTextView2.setText("");
        //mDumpTextView3.setText("");

        //mDumpTextView2.setText(HexDump.dumpHexString(data)+"/n"+data.length);

        if(data.length==8 || data.length==9){//&& data[2]=='\n') {

            int scrollAmount=0;
            s = "";
            s = new String(data);
            //Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT).show();
            // mDumpTextView.setText(message);
            //mDumpTextView2.setText(HexDump.dumpHexString(data));

            //int temp;

            /*try {
                temp=s.replace("Tick #","");
            }catch (Exception e)
            {
                temp="0";
                e.printStackTrace();
            }*/

            i=Integer.parseInt(s.replaceAll("[\\D]", ""));

            mDumpTextView.setText(i+"cm");

            //Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT).show();

            //i = 0;


            /*try {
                i = Integer.parseInt(temp);
                isInteger = true;
            } catch (Exception e) {
                isInteger = false;
            }*/

            //mDumpTextView2.setText(""+i);

        /*if(i!=0 && isInteger && i!=j && !(Math.abs(i-j)>=3))
        {
            //mImageSlider.smoothScrollTo(i*50,0);
            mImageSlider.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //replace this line to scroll up or down
                    //mScrollView.fullScroll(ScrollView.FOCUS_RIGHT);
                    //mScrollView.smoothScrollBy(0,100);
                    //mScrollView.scrollBy(10, 10);
                    mImageSlider.smoothScrollTo(i*20, 0);

                }
            }, 200L);


        }

        j=i;*/


            //Toast.makeText(getApplicationContext()," "+firstImageStart+"_"+firstImageEnd,Toast.LENGTH_SHORT).show();


            if (istime){ //&& isInteger) {
                time = System.currentTimeMillis();
                unixtime = System.currentTimeMillis();
                dataDistance = i;
                dataArray[0] = i;
                istime = false;
            }

            if (i != 0 && Math.abs(i - j) <= 3 )//&& isInteger && Math.abs(i - dataDistance) != 1)//((j%10)!=i) && ((j/10)!=i) &&(j%100)!=i && (j/100)!=i &&&& Math.abs(distance-i)<=15)
            {
                //dataDistance=i;
                AddSample(i);
                dataDistance = (int) Average();
                //dataAvg=Average();

            }

        /*if ((System.currentTimeMillis()-unixtime)==500)
        {
            distance=dataDistance;
        }*/

           // Toast.makeText(getApplicationContext(),"  "+mImageSlider.getMaxScrollAmount(),Toast.LENGTH_SHORT).show();

            //if (((System.currentTimeMillis()-time)>250L))
            if (((System.currentTimeMillis() - time) > 250L)) {
                //Toast.makeText(getApplicationContext(),dataDistance,Toast.LENGTH_SHORT).show();
                //Toast.makeText(getApplicationContext(),"hello",Toast.LENGTH_SHORT).show();
                //mImageSlider.smoothScrollTo(dataDistance*20, 0);
                //ObjectAnimator animator=ObjectAnimator.ofInt(mImageSlider, "scrollX",dataDistance*29 );
                ObjectAnimator animator = ObjectAnimator.ofInt(mImageSlider, "scrollX", (dataDistance-startPoint) * amountScroll);
                animator.setDuration(250);
                animator.start();
                scrollAmount=offset+(dataDistance-startPoint) * amountScroll;
                constDistance[counter]=dataDistance;
                counter++;
                //istime=true;
                time = System.currentTimeMillis();
            }

        if(counter==lengthTime)
        {
            Toast.makeText(getApplicationContext(),"running pop up",Toast.LENGTH_SHORT).show();

            int temp=constDistance[0];

            while(counter>0)
            {
                if (temp==constDistance[counter-1] || Math.abs(temp-constDistance[counter-1])==1 ||Math.abs(temp-constDistance[counter-1])==2)
                {
                    runExpandImage=true;
                }
                else
                {
                    runExpandImage=false;
                    break;
                }
                counter--;
            }
            //runExpandImage=true;

            this.counter=0;
            constDistance=new int[lengthTime];
        }

        if (runExpandImage)
        {

            Toast.makeText(getApplicationContext()," "+scrollAmount,Toast.LENGTH_SHORT).show();

            //Toast.makeText(getApplicationContext(),"running pop up",Toast.LENGTH_SHORT).show();
            if(scrollAmount>=firstImageStart && scrollAmount<=firstImageEnd)
            {
                //ExpandImageActivity.photoId=1;
                Toast.makeText(getApplicationContext(),"1st pop up",Toast.LENGTH_SHORT).show();
            }
            else if (scrollAmount>=secondImageStart && scrollAmount<=secondImageEnd)
            {
                //ExpandImageActivity.photoId=2;
                Toast.makeText(getApplicationContext(),"2nd pop up",Toast.LENGTH_SHORT).show();

            }
            else if (scrollAmount>=thirdImageStart && scrollAmount<=thirdImageEnd)
            {
                //ExpandImageActivity.photoId=3;
                Toast.makeText(getApplicationContext(),"3rd pop up",Toast.LENGTH_SHORT).show();

            }
            else if (scrollAmount>=fourthImageStart && scrollAmount<=fourthImageEnd)
            {
                //ExpandImageActivity.photoId=4;
                Toast.makeText(getApplicationContext(),"4th pop up",Toast.LENGTH_SHORT).show();

            }
            else if (scrollAmount>=fifthImageStart && scrollAmount<=fifthImageEnd)
            {
                //ExpandImageActivity.photoId=5;
                Toast.makeText(getApplicationContext(),"5th pop up",Toast.LENGTH_SHORT).show();

            }
            else if (scrollAmount>=sixthImageStart && scrollAmount<=sixthImageEnd)
            {
                //ExpandImageActivity.photoId=6;
                Toast.makeText(getApplicationContext(),"6th pop up",Toast.LENGTH_SHORT).show();

            }

            else if (scrollAmount>=seventhImageStart && scrollAmount<=seventhImageEnd)
            {
                //ExpandImageActivity.photoId=6;
                Toast.makeText(getApplicationContext(),"7th pop up",Toast.LENGTH_SHORT).show();

            }

            else if (scrollAmount>=eigthImageStart && scrollAmount<=eigthImageEnd)
            {
                //ExpandImageActivity.photoId=6;
                Toast.makeText(getApplicationContext(),"8th pop up",Toast.LENGTH_SHORT).show();

            }


            else if (scrollAmount>=ninthImageStart && scrollAmount<=ninthImageEnd)
            {
                //ExpandImageActivity.photoId=6;
                Toast.makeText(getApplicationContext(),"9th pop up",Toast.LENGTH_SHORT).show();

            }



            runExpandImage=false;
            //ExpandImageActivity.show(getApplicationContext(),sPort);
        }

            j = i;
        }

    }


    static public class MyDialogFragment extends DialogFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setStyle(DialogFragment.STYLE_NORMAL, R.style.MY_DIALOG);
        }

        @Override
        public void onStart() {
            super.onStart();
            Dialog d = getDialog();
            if (d!=null){
                int width = ViewGroup.LayoutParams.MATCH_PARENT;
                int height = ViewGroup.LayoutParams.MATCH_PARENT;
                d.getWindow().setLayout(width, height);
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View root = inflater.inflate(R.layout.dialog_fragment, container, false);
            return root;
        }

    }

    public float Average()
    {
        float total=0;
        for (int i=0; i<dataArray.length; i++)
        {
            total+=dataArray[i];
        }

        return total/dataArray.length;
    }

    public void AddSample(int val)
    {
        dataArray[indx] = val;

        if (++indx == dataArray.length)
        {
            indx = 0;
        }
    }


    /**
     * Starts the activity, using the supplied driver instance.
     *
     * @param context
     @param driver
     */
    public static void show(Context context, UsbSerialPort port) {
        sPort = port;
        final Intent intent = new Intent(context, SerialConsoleActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
        context.startActivity(intent);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
            setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        }
    }

}