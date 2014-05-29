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

package in.tagbin.smartdevice;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import android.os.AsyncTask;
import android.util.Log;

public class TcpStack {

	// Connection type
	public String connType = null;
	
	//Message to be sent
	public String MESSAGE = null;
	public char RECEVIED_MESSAGE;

	// IP Address for server and device(TagPlug)
	public String SERVER_IP = "23.23.209.78";
	public String DEVICE_IP = "192.168.16.254";
	public String IP = null;

	// PORT for server and device(TagPlug)
	public int SERVER_PORT = 5280;
	public int DEVICE_PORT = 8080;
	public int PORT;

	// Streams
	DataOutputStream dataOutputStream = null;
	DataInputStream dataInputStream = null;

	// Socket
	Socket socket;
	
	//TAG for debuggng
	public String TAG = "WIFI";
	
	
	public TcpStack(String connType,String message){
		
		this.connType = connType;
		this.MESSAGE = message;
		this.socket = null;
	}

	public void connectToSocket() {
		
		Log.d(TAG,"Creating socket");
		new AsyncTask<Void, Void, String>(){
			
			

			@Override
			protected void onPostExecute(String result) {
				Log.d(TAG,"Socket has been created");
				writeToSocket();
				super.onPostExecute(result);
			}

			@Override
			protected String doInBackground(Void... params) {
				if (connType == "SERVER") {
					IP = SERVER_IP;
					PORT = SERVER_PORT;
				} else if(connType == "DEVICE"){
					IP = DEVICE_IP;
					PORT = DEVICE_PORT;
				}else{
					Log.d(TAG,"ConnType not mentioned");
				}

				try {
					socket = new Socket(IP, PORT);
					dataOutputStream = new DataOutputStream(
							socket.getOutputStream());
					dataInputStream = new DataInputStream(
							socket.getInputStream());
				} catch (UnknownHostException e) {
					e.printStackTrace();
					Log.d(TAG, e.getMessage());
				} catch (IOException e) {
					e.printStackTrace();
					Log.d(TAG, e.getMessage());
				}
				return null;
			}
			
		}.execute(null,null,null);

	}
	
	public void writeToSocket(){
		if(socket != null){
			Log.d(TAG,"Socket exist..writing to socket");
			new AsyncTask<Void,Void,String>(){
				
				

				@Override
				protected void onPostExecute(String result) {
					Log.d(TAG,"OnPostExecute Called()");
					Thread thread = new Thread(new Runnable(){

						@Override
						public void run() {
							try {
								RECEVIED_MESSAGE = dataInputStream.readChar();
								Log.d(TAG,"Received from device"+RECEVIED_MESSAGE);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}					
						}
						
					});
					thread.start();
					
					super.onPostExecute(result);
				}

				@Override
				protected String doInBackground(Void... params) {
					Thread thread = new Thread(new Runnable() {
						
						@Override
						public void run() {
							try {
								dataOutputStream.writeBytes(MESSAGE);
							} catch (IOException e) {
								e.printStackTrace();
								Log.d(TAG,e.getMessage());
							}
							
						}
					});
					thread.start();
					
					return null;
				}
				
			}.execute(null,null,null);
			
		}else{
			Log.d(TAG,"Socket does not exist pre-exist...creating...");
			this.connectToSocket();
		}
	}

}
