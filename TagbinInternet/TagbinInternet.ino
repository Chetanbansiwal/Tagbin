void setup() {
  // put your setup code here, to run once:
Serial1.begin(115200);
Serial.begin(115200);
delay(6000);
pinMode(21,OUTPUT);
digitalWrite(21,LOW);
delay(3000);
digitalWrite(21,HIGH);
delay(1000);
Serial1.print("at+netmode=?\r\n");
}

void loop() {
  // put your main code here, to run repeatedly:
  if (Serial1.available()) {
    int inByte = Serial1.read();
    Serial.write(inByte);
  }
}
