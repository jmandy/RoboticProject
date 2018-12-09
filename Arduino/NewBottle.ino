#include <LiquidCrystal.h>
LiquidCrystal lcd(12,11,10,9,8,7);

int thermometer = A0;
int heatingPad = 13;
int heatingLED = 2;

int maxTemp = 50;
int minTemp = 20; //may be variable

bool activated = true;

float R = 9870;
float Rzero = 103;
float Tzero = 298.15;
float B = 3435;
float c1 = 1.009249522e-03, c2 = 2.378405444e-04, c3 = 2.019202697e-07;

void setup() {
  // put your setup code here, to run once:
  Serial.begin(57600);
  pinMode(thermometer, INPUT);
  pinMode(heatingPad, OUTPUT);
  pinMode(heatingLED, OUTPUT);
  digitalWrite(heatingPad, LOW);
  digitalWrite(heatingLED, LOW);
  lcd.begin(16,2);
}

void loop() {
  String message = "";
  
  message.concat("~M");
  //1. read minimal temperature to change 'minTemp'
  if(Serial.available() > 0){
    long tempChange = Serial.parseInt();

    if(tempChange >= 400){
        activated = false;
    }else if(tempChange >= maxTemp || tempChange < 0){
      message.concat("inv"); 
    }else if(tempChange == 0){
      //this area is only available RIGHT AFTER 'activated' has changed into false
      //
    }else{
      //If valid minimal-temperature parameter is entered,
      //restore 'activated' into true & change minimal temperature
      activated = true;
      minTemp = (int) tempChange;
      message.concat("cgm");
    }
  }

  //2. read resistore value from thermometer and display current temperature IF 'activated' is true
  int val = analogRead(thermometer);
  float currentTemp = getTemp(val);
  
  if(activated == false){
    lcd.print("Deactivated");
    digitalWrite(heatingPad, LOW);
    digitalWrite(heatingLED, LOW);
    message.concat("dac");
    message.concat("#stp_");
    message.concat(currentTemp);
  }
  else{
    //activate or deactivate heating pad
    if(currentTemp < minTemp){
      lcd.print("Heating...");
      digitalWrite(heatingPad, HIGH);
      digitalWrite(heatingLED, HIGH);
      message.concat("#run_");
      message.concat(currentTemp);
    }else{
      lcd.print("Standby");
      digitalWrite(heatingPad, LOW);
      digitalWrite(heatingLED, LOW);
      message.concat("#stp_");
      message.concat(currentTemp);
    }
  }
  lcd.setCursor(0,1);
  lcd.print(currentTemp);
  lcd.print(" C");
  
  message.concat("[");
  message.concat(minTemp);
  message.concat("]");

  Serial.println(message);
  delay(1000);
  Serial.flush(); //flush buffer memory
  lcd.clear();
}


float getTemp(int val){
  float Rt = R * (1023.0 / (float)val - 1.0);
  float logRt = log(Rt);
  float C =  ( 1.0 / (c1 + c2*logRt + c3*logRt*logRt*logRt ) ) - 273.15;

  return C;
}
