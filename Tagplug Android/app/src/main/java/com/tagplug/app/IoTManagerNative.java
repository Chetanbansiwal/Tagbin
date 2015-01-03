package com.tagplug.app;



public class IoTManagerNative {
    static {
        System.loadLibrary("IoT_manager_jni");
    }

    public IoTManagerNative()
    {

    }

    /**
     * Start SmartConnection with Home AP
     *
     * @SSID : SSID of Home AP
     * @Password : Password of Home AP
     * @Auth : Auth of Home AP
     */
    public native int StartSmartConnection(String SSID, String Password, byte Auth);

    /**
     * Stop SmartConnection by user
     *
     */

    public native int StopSmartConnection();

    /*
     * initization Control Server
     *
     * return : 0 : success
     *          1 : failed
     */
    public native int InitControlServer(String IPAddr, int ServType);

	/*
	 * Query all client info of connected device
	 *
	 */

    public native ClientInfo[] QueryClientInfo(int ServType);

    public native int CtrlClientOffline(int ClientID);

    public native int SetGPIO(int ClientID, int GPIOList, int GPIOValue);

    public native int[] GetGPIO(int ClientID);

    public native int SetUARTData(int ClientID, String pData, int Len);

    public native String GetUARTData(int ClientID);

    public native int SetPWM(int ClientID, short Red, short Green, short Blue);

    public native int[] GetPWM(int ClientID);

    public native int SetCtrlPassword(String pPassword);

    public native int AddFriend(String pFriendID);

    public native int InitCtrlPassword(String pPassword);
}