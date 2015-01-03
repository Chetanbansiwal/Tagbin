package com.tagplug.app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity implements NavigationDrawerCallbacks {

    private Toolbar mToolbar;
    private NavigationDrawerFragment mNavigationDrawerFragment;

    Intent pushService;

    /* GLOBAL VARIABLES */
    public static String SHARED_PREF = "SHARED_PREF";
    public static String SERVER_CONN_STATE = "SERVER_CONN_STATE";
    public static String KEEP_ALIVE = "_ALIVE";
    public static String  HLK_COMMAND= "hlkATat";
    public static String DELIMITER = "\n";
    public static int ON = 1;
    public static int OFF = 0 ;
    public static String _ON = MainActivity.HLK_COMMAND+"_T_1"+MainActivity.DELIMITER;
    public static String _OFF = MainActivity.HLK_COMMAND+"_T_0"+MainActivity.DELIMITER;
    public static String VIA_DEVICE = "^PATH^null^null^0"+MainActivity.DELIMITER;
    public static String VIA_SERVER = "";
    public static String BYPASS = "_BYPASS"+MainActivity.DELIMITER;
    public static String _PAYLOAD = null;
    private static String PAYLOAD_DATA = "PAYLOAD_DATA";
    public static String TAG = "UDP";
    public static String SSID = null;
    public static String SSID_PASS = null;

    boolean isBound = false;
    private CharSequence mTitle;
    TextView headerText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.fragment_drawer);
        mNavigationDrawerFragment.setup(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawer), mToolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        switch (position) {
            case 0:
                FragmentManager f = getSupportFragmentManager();
                f.beginTransaction()
                        .replace(R.id.container,
                                DeviceFragment.newInstance(position + 1),
                                DeviceFragment.TAG).commit();
                break;
            default:
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager
                        .beginTransaction()
                        .replace(R.id.container,
                                PlaceholderFragment.newInstance(position + 1))
                        .commit();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (mNavigationDrawerFragment.isDrawerOpen())
            mNavigationDrawerFragment.closeDrawer();
        else
            super.onBackPressed();
    }

    public void onSectionAttached(int number) {

        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
            case 4:
                mTitle = getString(R.string.title_section4);
                break;
            case 5:
                mTitle = getString(R.string.title_section5);
                break;
        }
    }

}
