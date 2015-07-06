package app.tagbinotg.com.otgserialdata;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import app.tagbinotg.com.otgserialdata.driver.UsbSerialPort;

public class ExpandImageActivity extends Activity {

    private long currTime;
    public static UsbSerialPort usbPort;
    public static int photoId;
    public static Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expand_image);
        currTime=System.currentTimeMillis();

        ImageView expandableImage=(ImageView) findViewById(R.id.imageZoomView);

        ImageButton imageCloseButton=(ImageButton) findViewById(R.id.imageCloseButton);
        ImageButton imageHomeButton=(ImageButton) findViewById(R.id.imageHomeButton);

        imageCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SerialConsoleActivity.show(getApplicationContext(), usbPort);
            }
        });

        imageHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SerialConsoleActivity.show(getApplicationContext(), usbPort);
            }
        });


        switch(photoId) {
            case 1:
                expandableImage.setImageResource(R.drawable.image_1);
                break;
            case 2:
                expandableImage.setImageResource(R.drawable.image_2);
                break;
            case 3:
                expandableImage.setImageResource(R.drawable.image_3);
                break;
            case 4:
                expandableImage.setImageResource(R.drawable.image_4);
                break;
            case 5:
                expandableImage.setImageResource(R.drawable.image_5);
                break;
            case 6:
                expandableImage.setImageResource(R.drawable.image_6);
                break;

            default:
                Toast.makeText(mContext, "ERROR", Toast.LENGTH_SHORT).show();
                break;

        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        super.onResume();
        if(System.currentTimeMillis()-currTime>=1000)
        {
            SerialConsoleActivity.show(getApplicationContext(), usbPort);
        }
    }

    public static void show(Context context, UsbSerialPort port) {
        usbPort = port;
        mContext=context;
        final Intent intent = new Intent(context, ExpandImageActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
        context.startActivity(intent);
    }
}
