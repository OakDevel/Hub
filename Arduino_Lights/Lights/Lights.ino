#define LED_R 9
#define LED_G 11
#define LED_B 12

const double increment = 400; 

int estado_lamp;

float diference_R;
float diference_G;
float diference_B;

//For the LEDs
float RGB_start[] = {0, 0, 0}; //[0] --> Red, [1] --> Green,  //[2] --> Blue
float RGB_end[] = {0, 0, 0};   //[0] --> Red, [1] --> Green,  //[2] --> Blue

//Stores data from Serial
String data;

void setup() {
  //Comunication Speed
  Serial.begin(9600);
  
  //Self-Explanatory
  pinMode(LED_R, OUTPUT);
  pinMode(LED_G, OUTPUT);
  pinMode(LED_B, OUTPUT);
  
  //Start our "start" array with 0 value
  for(int i = 0; i < 3; i++){
    RGB_start[i] = 0;
  }

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
      RGB_end[i] = Separador(data, i+1).toInt();
    }
    
    //Make corrections
    //If the input is greater than 255
    if(RGB_end[0] > 255)RGB_end[0] = 255;
    if(RGB_end[1] > 255)RGB_end[1] = 255;
    if(RGB_end[2] > 255)RGB_end[2] = 255;
    
    //If the input is lower than 0
    if(RGB_end[0] < 0)RGB_end[0] = 0;
    if(RGB_end[1] < 0)RGB_end[1] = 0;
    if(RGB_end[2] < 0)RGB_end[2] = 0;
        
    //change states if state is 1
    if(estado_lamp == 1){
      //Calculate an incremento to do the LED fading
      diference_R = (RGB_end[0] - RGB_start[0]) / increment;
      diference_G = (RGB_end[1] - RGB_start[1]) / increment;
      diference_B = (RGB_end[2] - RGB_start[2]) / increment;

      for(int i = 0; i <= increment; i++){
        RGB_start[0] += diference_R;
	RGB_start[1] += diference_G;
	RGB_start[2] += diference_B;

	//Turn On
      	analogWrite(LED_R, RGB_start[0]);
      	analogWrite(LED_G, RGB_start[1]);
      	analogWrite(LED_B, RGB_start[2]);
	}
    }
    else{
      //Turn Off
      analogWrite(LED_R, 0);
      analogWrite(LED_G, 0);
      analogWrite(LED_B, 0);
    }
    //Just to be sure, we copy the values that we received to our "start" array
    for(int i = 0; i < 3; i++){
      RGB_start[i] = RGB_end[i];
    }
  }
}

String Separador(String data, int indice){
  int j = 0;
  String parcial = "";
  
  for(int i = 0; i < data.length() && j <= indice; i++){
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
