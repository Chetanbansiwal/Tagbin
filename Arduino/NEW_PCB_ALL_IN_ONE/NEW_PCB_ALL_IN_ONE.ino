/* LED PIN   */
const int RED =  11;
const int GREEN = 9;
const int BLUE =  10;

/* AT COMMAND MODE PIN */
const int AT = 8;

/* RELAY PIN */
const int RELAY = A3;

/* VOLTAGE/CURRENT PIN */
const int VOLTAGE = 1;
const int CURRENT = 2;

/* RX/TX FROM WIFI */
const int RX = 0;
const int TX = 1;

/* ONBOARD SWITCH */
const int SWITCH = 7;

/* INPUT STRING FRO UART */
String inputString = "";

/* SERIAL RELATED VARIABLES */
boolean stringComplete = false;

/* Validate at server */
boolean isLoggedIn = false;

/* Set up timestamp */
unsigned long  int timestamp = 1402652077;

/* Set up alarm at */
unsigned long int alarmAt;

/* For calculation */
unsigned long int secondsLifeTime = 0;

/* Tagplug ONN/OFF Status */
boolean on = false;
boolean off = false;


void setup() {
  
 initialize();

}

void loop() {
    
          /* MAIN FUNCTIONALITY STARTS HERE */
            if(stringComplete){
                dataValidation();  
              inputString = "";
              stringComplete = false;      
            } 
             
             // Alram timestamp Check 
            alarmCheck();                 

}
































/* INITIALISATIONS*/
void initialize(){
  
  pinMode(AT,OUTPUT);
  pinMode(RED,OUTPUT);
  pinMode(GREEN,OUTPUT);
  pinMode(BLUE,OUTPUT);
  pinMode(SWITCH,INPUT);  
  pinMode(RELAY,OUTPUT);
  pinMode(VOLTAGE,INPUT);
  pinMode(CURRENT,INPUT);
  
  digitalWrite(AT,HIGH);
  Serial.begin(9600);
  Serial.println("BOOTING UP TAGPLUG");
  digitalWrite(BLUE,HIGH);
  
}



/* SERAIL PORT READER */
void serialEvent() {
  while (Serial.available()) {
    // get the new byte:
    char inChar = (char)Serial.read();
    // add it to the inputString:
    inputString += inChar;
    // if the incoming character is a newline, set a flag
    // so the main loop can do something about it:
    if (inChar == '\n') {
      stringComplete = true;
    }
  }
}

/* GET VALUE FROM DATA */
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

/* CHECK ALARM */
void alarmCheck(){
   if(millis() - secondsLifeTime >= 1000 ){
      timestamp++;
      secondsLifeTime += 1000;
      if(timestamp == alarmAt){
            if(on){
              digitalWrite(RELAY,LOW);
                Serial.println("ON_OK");
                on = false;
                off = true;
          }else{
            digitalWrite(RELAY,HIGH);
                Serial.println("OFF_OK");
                on = true;
                off = false;
          }
      }
  }
}

/* VALIDATE FOR CORRECT INPUT */
void dataValidation(){
   
  String data = getValue(inputString, '_', 2);
 
          if(inputString.startsWith("at_A")){
            alarmAt = data.toInt();
          }else if(inputString.startsWith("at_T")){ //_TURN turn on/off Tagplug
            
                   if(data.toInt() == 1 )
                      {
                        digitalWrite(RELAY,HIGH);
                        Serial.println("ON_OK");
                        on = true;
                        off = false;
                                               
                      }
                  else if(data.toInt() == 0 )
                  {
                    digitalWrite(RELAY,LOW);
                    Serial.println("OFF_OK");
                    on = false;
                    off = true;
                   
                  }
          }else if(inputString.startsWith("at_S")){// _SYNCHRONIZE synchronize tagpluck clock
            timestamp = data.toInt();
            Serial.println("Synchronized");
          }else if(inputString.startsWith("at_OK")){ //OK after aithentication
            isLoggedIn = true;
             Serial.println("Logged In");
          }else if(inputString.startsWith("at_BYPASS")){  //_BYPASS authentication for device connection
            isLoggedIn = true;
             Serial.println("Bypassed");
          }else if(inputString.startsWith("at_DEVICE_TIME")){  //_DEVICE_TIME  Tagplug time clock
            Serial.println(timestamp);
          } 
}

/* READ VOLTAGE CURVE FROM DEVICE */
void readVoltage(){
  float voltageCurve;
  voltageCurve = analogRead(VOLTAGE);
  Serial.println(voltageCurve);
}

/* READ CURRENT CURVE FROM DEVICE */
void readCurrent(){
  float currentCurve;
  currentCurve = analogRead(CURRENT);
  Serial.println(currentCurve);
}

