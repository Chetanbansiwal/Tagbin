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
Serial1.print("at+netmode=2\r\n");
Serial1.print("at+wifi_conf=Tagbin,wpawpa2_tkip,!@tagbin12\r\n");
Serial1.print("at+dhcpc=1\r\n");
//Serial1.print("at+remoteip=192.168.1.9\r\n");
//Serial1.print("at+remoteport=8080\r\n");
Serial1.print("at+remotepro=tcp\r\n");
Serial1.print("at+timeout=0\r\n");
Serial1.print("at+mode=server\r\n");
Serial1.print("at+uart=115200,8,n,1\r\n");
Serial1.print("at+uartpacklen=64\r\n");
Serial1.print("at+uartpacktimeout=10\r\n");
delay(500);
Serial1.print("at+net_commit=1\r\n");
delay(500);
Serial1.print("at+reconn=1\r\n");
}

void loop() {
  // put your main code here, to run repeatedly:
  if (Serial1.available()) {
    int inByte = Serial1.read();
    Serial.write(inByte);
  }
  if (Serial.available()) {
    int inByte = Serial.read();
    Serial1.write(inByte);
  }
}
