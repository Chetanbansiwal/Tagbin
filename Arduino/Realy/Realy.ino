const int relayPin = A3;
const int RED = 11;

/* AT COMMAND MODE PIN */
const int AT = 8;

void setup(){
  pinMode(relayPin,OUTPUT);
  pinMode(RED,OUTPUT);
   pinMode(AT,OUTPUT);
  digitalWrite(AT,HIGH);

}

void loop(){

  digitalWrite(relayPin,HIGH);
  digitalWrite(RED,HIGH);
  delay(200);
  digitalWrite(relayPin,LOW);
  digitalWrite(RED,LOW);
  delay(200);
}
