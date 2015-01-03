#include <SoftwareSerial.h>

SoftwareSerial mySerial(10, 11); // RX, TX

//Validate at server
boolean isLoggedIn = false;

//Set up timestamp 
unsigned long  int timestamp = 1402652077;

//Set up alarm at
unsigned long int alarmAt;


//For calculation
unsigned long int secondsLifeTime = 0;

//For EEMPROM address
//int addr = 0;
//byte value;

//InputString
String inputString = "";
boolean stringComplete = false;  // check if the string is complete

//Tagplug ONN/OFF Status
boolean on = false;
boolean off= false;

//Relay Pin 
int relayPin = A3;



void setup(){
  Serial.begin(115200);
  
  //WIFI PIN
  pinMode(8,OUTPUT);
  digitalWrite(8,HIGH);
  
  //RELAY PIN
  pinMode(relayPin,OUTPUT);
  
  //Get Alarm Value
  //value = EEPROM.read(0);
  //Serial.println(value, DEC);
  
  mySerial.begin(115200);
  
  
}

void loop(){
  
   //Take care of validation at TCP SERVER
  if(!isLoggedIn){
      mySerial.print("T");
      delay(1000);
    }
  
  serialEvent();
  if(stringComplete){
   String data = getValue(inputString, '_', 2);
    if(inputString.startsWith("_A")){
       
       //Serial.println("Alarm Setup for "+data);
       //alarmAt = data.toInt();
           
     /* EEPROM.write(addr, data);  
      addr += 1;
      if (addr == 512)
      addr = 0;
      delay(100);
      */
      
        
    }else if(inputString.startsWith("_T")){ //_TURN turn on/off Tagplug
             if(data == "1" )
                {
                  digitalWrite(relayPin,HIGH);
                  //Serial.println("on");
                  on = true;
                  off = false;
                }
            else if(data == "0" )
            {
              digitalWrite(relayPin,LOW);
              //Serial.println("off");
              on = false;
              off = true;
            }
    }else if(inputString.startsWith("_S")){// _SYNCHRONIZE synchronize tagpluck clock
      //timestamp = data.toInt();
      mySerial.println("Synchronized");
    }else if(inputString.startsWith("_OK")){ //OK after aithentication
      isLoggedIn = true;
       mySerial.println("Logged In");
    }else if(inputString.startsWith("_BYPASS")){  //_BYPASS authentication for device connection
      isLoggedIn = true;
       mySerial.println("Bypassed");
    }else if(inputString.startsWith("_DEVICE_TIME")){  //_DEVICE_TIME  Tagplug time clock
      mySerial.println(timestamp);
    }else if(inputString.startsWith("_PATH")){  //_DEVICE_TIME  Tagplug time clock
       
       String _SSID = getValue(inputString, '_', 2); 
       String _PASS = getValue(inputString, '_', 3); 
       String _MODE = getValue(inputString, '_', 4); 
       //changeMode(_SSID,_PASS,_MODE);
       //Serial.println(_MODE);
    }
    
    stringComplete = false;   
    inputString = "";
    

  }
  if(millis() - secondsLifeTime >= 1000 ){
      timestamp++;
      secondsLifeTime += 1000;
      if(timestamp == alarmAt){
            if(on){
              digitalWrite(relayPin,LOW);
                //Serial.println("off");
                on = false;
                off = true;
          }else{
            digitalWrite(relayPin,HIGH);
                //Serial.println("on");
                on = true;
                off = false;
          }
      }
  }
  
}

void serialEvent() {
  
   char inChar = (char)mySerial.read(); 
  while (inChar != 0){
     
    if (inChar != '@' && inChar != '\r' && inChar != '\n') {
       inputString += inChar;
    } else{
        stringComplete = true; 
    }
  }

}

 String getValue(String data, char separator, int index)
{
  int found = 0;
  int strIndex[] = {0, -1};
  int maxIndex = data.length()-1;

  for(int i=0; i<=maxIndex && found<=index; i++){
    if(data.charAt(i)==separator || i==maxIndex){
        found++;
        strIndex[0] = strIndex[1]+1;
        strIndex[1] = (i == maxIndex) ? i+1 : i;
    }
  }

  return found>index ? data.substring(strIndex[0], strIndex[1]) : "";
}

/*
void changeMode(String _SSID, String _PASS, String _MODE){
  
  if(_MODE == "1"){
  
    digitalWrite(8,LOW);
    delay(3000);
    digitalWrite(8,HIGH);
    delay(1000);
   
    Serial.print("at+netmode=2\r\n");
    Serial.print("at+wifi_conf=belkin.3f87,wpa2_aes,fe88968b\r\n");
    Serial.print("at+dhcpc=1\r\n");
    Serial.print("at+mode=client\r\n");
    Serial.print("at+remoteip=23.23.209.78\r\n");
    Serial.print("at+remoteport=2626\r\n");
    Serial.print("at+remotepro=tcp\r\n");
    Serial.print("at+timeout=0\r\n");
    Serial.print("at+uart=115200,8,n,1\r\n");
    Serial.print("at+uartpacklen=64\r\n");
    Serial.print("at+uartpacktimeout=10\r\n");
    Serial.print("at+net_commit=1\r\n");
    Serial.print("at+reconn=1\r\n");
    
  }else if(_MODE == "0"){
    digitalWrite(8,LOW);
    delay(7000);
    digitalWrite(8,HIGH);
    delay(1000);
  }

}


*/
