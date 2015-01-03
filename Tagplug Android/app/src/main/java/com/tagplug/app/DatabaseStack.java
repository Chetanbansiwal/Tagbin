package com.tagplug.app;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class DatabaseStack extends SQLiteOpenHelper {

	Context context;

	public DatabaseStack(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
	}
	private static final int DATABASE_VERSION = 18;

	// Database Name
	private static final String DATABASE_NAME = "tag_plug";

	//Table Name
	private static final String TABLE_DEVICE = "devices";
	private static final String TABLE_ALARM = "alarm";
	private static final String TABLE_ENERGY = "energy";

	/* DEVICE TABLE */
	private static final String KEY_ID = "id";
	private static final String KEY_NAME = "name";
	private static final String KEY_TYPE = "type";
	private static final String KEY_LOC = "location";
	private static final String KEY_MAC = "mac";
	private static final String KEY_STATE = "state";
	private static final String KEY_NETWORK_STATE = "network_state";
    private static final String KEY_AUTH_KEY = "auth_key";
	private static final String CREATE_DEVICE_TABLE = "CREATE TABLE " + TABLE_DEVICE + "("
			+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " varchar(10),"
			+ KEY_TYPE + " varchar(10)," + KEY_LOC + " varchar(20), " + KEY_MAC + " varchar(50) UNIQUE, " + KEY_STATE + " INT, " + KEY_NETWORK_STATE + " INT, " + KEY_AUTH_KEY +" TEXT  )";
	
	
	/* ENERGY TABLE */
    private static final String KEY_ENERGY_ID = "id";
    private static final String KEY_FROM_TIMESTAMP = "from_timestamp";
    private static final String KEY_TO_TIMESTAMP = "to_timestamp";
    private static final String KEY_DEVICE_ID = "device_id";
    private static final String KEY_CURRENT = "current";
    private static final String KEY_VOLTAGE = "voltage";
    private static final String CREATE_ENERGY_TABLE = "CREATE TABLE "+TABLE_ENERGY+ " ("+KEY_ENERGY_ID+" INTEGER PRIMARY KEY, "+KEY_DEVICE_ID+" INTEGER,"+KEY_FROM_TIMESTAMP+" INTEGER , "+KEY_TO_TIMESTAMP+" INTEGER ,"+KEY_CURRENT+" FLOAT(5), "+KEY_VOLTAGE+" FLOAT(5) )";

    /* ALARM TABLE */
    private static final String KEY_ALARM_ID = "id";
    private static final String KEY_ALARM_TIMESTAMP = "timestamp";
    private static final String KEY_ALARM_STATUS = "state";
    private static final String CREATE_ALARM_TABLE = "CREATE TABLE "+TABLE_ALARM+ " ("+KEY_ALARM_ID+" INTEGER PRIMARY KEY, "+KEY_ALARM_TIMESTAMP+" INTEGER UNIQUE, "+KEY_ALARM_STATUS+" INT)";

    /* MODIFY STATEMENT */
    private static final String _MODIFY = "ALTER TABLE " + TABLE_ALARM + " ADD " + KEY_DEVICE_ID + " INTEGER " ;
    private static final String _REMOVE = "ALTER TABLE " + TABLE_DEVICE + " DROP INDEX '" + KEY_MAC+"'" ;


    @Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_DEVICE);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALARM);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ENERGY);
		db.execSQL(CREATE_DEVICE_TABLE);
		db.execSQL(CREATE_ALARM_TABLE);
        db.execSQL(CREATE_ENERGY_TABLE);
        Log.d("DATABASE","Database onCreate called()");

	}



	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_DEVICE);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALARM);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_ENERGY);
		db.execSQL(CREATE_DEVICE_TABLE);
		db.execSQL(CREATE_ALARM_TABLE);
		db.execSQL(CREATE_ENERGY_TABLE);
		//db.execSQL(_REMOVE);
		// Create tables again
		onCreate(db);
        Log.d("DATABASE","Database onUpgrade called()");

	}

	public void addDevice(String name, String type,String location, String MAC,String authKey) {
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name); 
        values.put(KEY_TYPE, type); 
        values.put(KEY_LOC, location);
        values.put(KEY_STATE,0);
        values.put(KEY_MAC, MAC);
        values.put(KEY_AUTH_KEY, authKey);

        // Inserting Row
        db.insert(TABLE_DEVICE, null, values);
        db.close(); // Closing database connection
      
    }

	public void addAlarm(long timestamp) {
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put(KEY_ALARM_TIMESTAMP, timestamp); 
        values.put(KEY_ALARM_STATUS, "1"); 
        
        // Inserting Row
        db.insert(TABLE_ALARM, null, values);
        db.close(); // Closing database connection
      
    }

    public int getDeviceCount() {
        String countQuery = "SELECT  * FROM " + TABLE_DEVICE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        return cursor.getCount();
    }
    
    public void updateState(int state,boolean external) {
        String countQuery = "UPDATE " + TABLE_DEVICE + " SET "+ KEY_STATE + " = "+ state ;
        Log.d("WIFI",countQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL(countQuery);
        
        if(external){
        Intent i = new Intent("stateChanged");
        i.putExtra("state", state);

        context.sendBroadcast(i);
        }
        
    }
 

    public void deleteAll(){
    	SQLiteDatabase database = this.getReadableDatabase();
    	database.delete(TABLE_DEVICE, null, null);
    	
    }
    
    public void delete(String mac){
    	SQLiteDatabase database = this.getReadableDatabase();
    	database.delete(TABLE_DEVICE, KEY_MAC+"="+mac, null);
    }
    
    public void toggleSwitch(Integer i, Integer WHERE_ID){
    	SQLiteDatabase database = this.getReadableDatabase();
    	database.execSQL("UPDATE " + TABLE_DEVICE + " SET " + KEY_STATE + " = " + i + " WHERE " + " KEY_ID = "+ WHERE_ID);

    }
    public void setNetworkState(Integer i, Integer WHERE_ID){
    	SQLiteDatabase database = this.getReadableDatabase();
    	//Log.d("WIFI","UPDATE " + TABLE_DEVICE + " SET " + KEY_NETWORK_STATE + " = " + i + " WHERE " +  KEY_ID +"= "+ WHERE_ID);
    	database.execSQL("UPDATE " + TABLE_DEVICE + " SET " + KEY_NETWORK_STATE + " = " + i + " WHERE " +  KEY_ID +"= "+ WHERE_ID);

    }
    public int getNetworkState(Integer id){
    	String countQuery = "SELECT  * FROM " + TABLE_DEVICE + " WHERE "+KEY_NETWORK_STATE + " = "+ id;
    	//Log.d("WIFI",countQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        return cursor.getCount();
    }
    public String getData(){
    	String col[] = new String[]{KEY_ID,KEY_NAME,KEY_LOC,KEY_MAC};
    	SQLiteDatabase database = this.getReadableDatabase();
    	Cursor c = database.query(TABLE_DEVICE, col, null, null, null, null, null);
    	String result = "";
    	int iID = c.getColumnIndex(KEY_ID);
    	int iName = c.getColumnIndex(KEY_NAME);
    	int iLoc = c.getColumnIndex(KEY_LOC);
        int iMac = c.getColumnIndex(KEY_MAC);
    	
    	for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()){
    		result = result+" "+c.getString(iMac)+" " + c.getString(iID) +" "+ c.getString(iName)+ " " + c.getString(iLoc)+ "\n";
    	}
    	
    	return result;
    }
    public ArrayList<String> getDevice(String t){
    	ArrayList<String> list = new ArrayList<String>();
    	String col[] = new String[]{KEY_ID ,KEY_NAME,KEY_MAC,KEY_TYPE,KEY_STATE};
    	SQLiteDatabase database = this.getReadableDatabase();
    	Cursor c = database.query(TABLE_DEVICE, col,KEY_LOC+"='"+t+"'", null, null, null, null);
    	
    	int iName = c.getColumnIndex(KEY_TYPE);
    	int iStatus = c.getColumnIndex(KEY_STATE);
    	//title = new String[]{ c.getString( 0 ), c.getString( 1 ) };
    	
	    	for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()){
	    		list.add(c.getString(iName)+"_"+c.getShort(iStatus));
	    	}
    	
    	return list;
    	
    }
    
    public ArrayList<String> getAlarm(){
    	ArrayList<String> list = new ArrayList<String>();
    	String col[] = new String[]{KEY_ALARM_TIMESTAMP,KEY_ALARM_STATUS};
    	SQLiteDatabase database = this.getReadableDatabase();
    	Cursor c = database.query(TABLE_ALARM, col,null, null, null, null, null);
    	
    	int iName = c.getColumnIndex(KEY_ALARM_TIMESTAMP);
    	//title = new String[]{ c.getString( 0 ), c.getString( 1 ) };
    	
	    	for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()){
	    		
	    		long unixTime = System.currentTimeMillis() / 1000L;
	    		long alarm = Long.parseLong((c.getString(iName)));
	    		if(alarm > unixTime ){
		    		Date date = new Date(Long.parseLong(c.getString(iName))*1000L); // *1000 is to convert seconds to milliseconds
					SimpleDateFormat sdf = new SimpleDateFormat("EEEE hh:mm a"); // the format of your date
					String formattedDate = sdf.format(date);
					
				
		    		list.add(formattedDate);
	    		}
	    	}
    	
    	return list;
    	
    }
    
    
    public boolean notPresent(String mac){
    	String col[] = new String[]{KEY_ID,KEY_NAME,KEY_LOC};
    	boolean x=false;
    	SQLiteDatabase database = this.getReadableDatabase();
    	Cursor c = database.query(TABLE_DEVICE, col, KEY_MAC+"='"+mac+"'", null, null, null, null);
    	int i=c.getCount();
    	if(i==0)
    	x=true;
    	return x;
    }
    
}
    