package in.tagbin.smartdevice;


import java.util.ArrayList;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DatabaseStack extends SQLiteOpenHelper {


	public DatabaseStack(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}
	private static final int DATABASE_VERSION = 1;
	
	// Database Name
	private static final String DATABASE_NAME = "tag_plug";
	
	//Table Name
	private static final String TABLE_DEVICE = "devices";
	
	// Contacts Table Columns names
	private static final String KEY_ID = "id";
	private static final String KEY_NAME = "name";
	private static final String KEY_TYPE = "type";
	private static final String KEY_LOC = "location";
	private static final String KEY_MAC = "mac";
	private static final String KEY_STATE = "state";
	private static final String KEY_NETWORK_STATE = "network_state";
	private static final String CREATE_DEVICE_TABLE = "CREATE TABLE " + TABLE_DEVICE + "("
			+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
			+ KEY_TYPE + " TEXT," + KEY_LOC + " TEXT, " + KEY_MAC + " TEXT UNIQUE, " + KEY_STATE + " INT, " + KEY_NETWORK_STATE + " INT " + ")";

	//private static final String _MODIFY = "ALTER TABLE " + TABLE_DEVICE + " ADD " + KEY_STATE + " INTEGER " ;
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		//db.execSQL("DROP TABLE devices");
		db.execSQL(CREATE_DEVICE_TABLE);
				
	}



	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		//db.execSQL("DROP TABLE IF EXISTS " + TABLE_DEVICE);
		//db.execSQL(_MODIFY);

		// Create tables again
		onCreate(db);
		
	}
	
	public void addDevice(String name, String type,String location, String MAC) {
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name); 
        values.put(KEY_TYPE, type); 
        values.put(KEY_LOC, location);
        values.put(KEY_STATE,0);
        values.put(KEY_MAC, MAC);
        
        // Inserting Row
        db.insert(TABLE_DEVICE, null, values);
        db.close(); // Closing database connection
      
    }
	
    public int getDeviceCount() {
        String countQuery = "SELECT  * FROM " + TABLE_DEVICE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        return cursor.getCount();
    }
 

    public void deleteAll(){
    	SQLiteDatabase database = this.getReadableDatabase();
    	database.delete(TABLE_DEVICE, null, null);
    	
    }
    
    public void delete(){
    	SQLiteDatabase database = this.getReadableDatabase();
    	database.delete(TABLE_DEVICE, null, null);
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
    	String col[] = new String[]{KEY_ID,KEY_NAME,KEY_LOC};
    	SQLiteDatabase database = this.getReadableDatabase();
    	Cursor c = database.query(TABLE_DEVICE, col, null, null, null, null, null);
    	String result = "";
    	int iID = c.getColumnIndex(KEY_ID);
    	int iName = c.getColumnIndex(KEY_NAME);
    	int iLoc = c.getColumnIndex(KEY_LOC);
    	
    	for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()){
    		result = result + c.getString(iID) +" "+ c.getString(iName)+ " " + c.getString(iLoc)+ "\n";
    	}
    	
    	return result;
    }
    public ArrayList<String> getDevice(String t){
    	ArrayList<String> list = new ArrayList<String>();
    	String col[] = new String[]{KEY_ID ,KEY_NAME,KEY_MAC};
    	SQLiteDatabase database = this.getReadableDatabase();
    	Cursor c = database.query(TABLE_DEVICE, col,KEY_LOC+"='"+t+"'", null, null, null, null);
    	
    	int iName = c.getColumnIndex(KEY_NAME);
    	//title = new String[]{ c.getString( 0 ), c.getString( 1 ) };
    	
	    	for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()){
	    		list.add(c.getString(iName));
	    	}
    	
    	return list;
    	
    }
  
}
    
 
