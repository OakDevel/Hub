#define LED_R 9
#define LED_G 11
#define LED_B 12

int estado_lamp;

//For the LEDs
int RGB[] = {0, 0, 0}; //[0] --> Red, [1] --> Green,  //[2] --> Blue


//Stores data from Serial
String data;

void setup() {
  //Comunication Speed
  Serial.begin(9600);
  
  //Self-Explanatory
  pinMode(LED_R, OUTPUT);
  pinMode(LED_G, OUTPUT);
  pinMode(LED_B, OUTPUT);
  
  //Start with the lamp turn off
  analogWrite(LED_R, 0);
  analogWrite(LED_G, 0);
  analogWrite(LED_B, 0);
  
}

void loop() {
  //Read serial console
  if(Serial.available() > 0){
        
    //Data will be sent in this format:
    //  x:xxx:xxx:xxx&
    //  state : RED : GREEN : BLUE : To know the end of the string
    data = Serial.readStringUntil('&');
        
    //We need to break the string into various parts
    estado_lamp = Separador(data, 0).toInt();
        
    for(int i = 0; i < 3; i++){
      RGB[i] = Separador(data, i+1).toInt();
    }
    
    //Make corrections
    //If the input is greater than 255
    if(RGB[0] > 255)RGB[0] = 255;
    if(RGB[1] > 255)RGB[1] = 255;
    if(RGB[2] > 255)RGB[2] = 255;
    
    //If the input is lower than 0
    if(RGB[0] < 0)RGB[0] = 0;
    if(RGB[1] < 0)RGB[1] = 0;
    if(RGB[2] < 0)RGB[2] = 0;
        
    //change states if state is 1
    if(estado_lamp == 1){
      //Turn On
      analogWrite(LED_R, RGB[0]);
      analogWrite(LED_G, RGB[1]);
      analogWrite(LED_B, RGB[2]);
    }else{
      //Turn Off
      analogWrite(LED_R, 0);
      analogWrite(LED_G, 0);
      analogWrite(LED_B, 0);
    }
  }
}


String Separador(String data, int indice){
  int j = 0;
  String parcial = "";
  
  for(int i = 0; i <= data.length()-1 && j <= indice; i++){
      parcial.concat(data[i]);

      if(data[i] == ':'){
        j++;

        if(j > indice){
          parcial.trim();
          return parcial;
        }    

        parcial = "";    
    }  
  }  
}
