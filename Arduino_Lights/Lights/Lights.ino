#define LEFT_R 11
#define LEFT_G 10
#define LEFT_B 9

#define RIGHT_R 8
#define RIGHT_G 7
#define RIGHT_B 6

const double increment = 400; 

int estado_lamp;

String type = "Light";

//For the LEDs
int LEFT_RGB[] = {0, 0, 0}; //[0] --> Red, [1] --> Green,  //[2] --> Blue
int RIGHT_RGB[] = {0, 0, 0}; //[0] --> Red, [1] --> Green,  //[2] --> Blue

int LEFT_Clone[] = {0, 0, 0}; //[0] --> Red, [1] --> Green,  //[2] --> Blue
int RIGHT_Clone[] = {0, 0, 0}; //[0] --> Red, [1] --> Green,  //[2] --> Blue

//Fading LEDs when changing color
float diff_left_R;
float diff_left_G;
float diff_left_B;

float diff_right_R;
float diff_right_G;
float diff_right_B;

//Stores data from Serial
String data;

void setup() {
  //Comunication Speed -- DEBUG
  Serial.begin(9600);
  
  //Self-Explanatory
  //Left side
  pinMode(LEFT_R, OUTPUT);
  pinMode(LEFT_G, OUTPUT);
  pinMode(LEFT_B, OUTPUT);
  
  //Right side
  pinMode(RIGHT_R, OUTPUT);
  pinMode(RIGHT_G, OUTPUT);
  pinMode(RIGHT_B, OUTPUT);
    
  //Start with the lamp turn off
  analogWrite(LEFT_R, 0);
  analogWrite(LEFT_G, 0);
  analogWrite(LEFT_B, 0);
  
  analogWrite(RIGHT_R, 0);
  analogWrite(RIGHT_G, 0);
  analogWrite(RIGHT_B, 0);

}

void loop() {
  //Read serial console
  if(Serial.available() > 0){
        
    //Data will be sent in this format:
    //  x:xxx:xxx:xxx:xxx:xxx&
    //  state : LEFT_RED : RIGHT_RED : LEFT_GREEN : RIGHT_GREEN : LEFT_BLUE : RIGHT_BLUE
    data = Serial.readStringUntil('&');
        
    //We need to break the string into various parts
    estado_lamp = Separador(data, 0).toInt();
     
    int left_aux = 0;
    int right_aux = 0;
    
    for(int i = 1; i <= 6; i++){
      //If it's a even number we add it to the right side
      if(i%2 == 0){
        RIGHT_RGB[right_aux] = Separador(data, i).toInt();
        right_aux++;

      }else{
        LEFT_RGB[left_aux] = Separador(data, i).toInt();
        left_aux++;
      } 
    }
    
    switch(estado_lamp){
      case 0:
        //Turn off our device
        analogWrite(LEFT_R, 0);
        analogWrite(LEFT_G, 0);
        analogWrite(LEFT_B, 0);
      
        analogWrite(RIGHT_R, 0);
        analogWrite(RIGHT_G, 0);
        analogWrite(RIGHT_B, 0);

        break;

      case 1:

      	calculateFading();

        //Turn on our device
        while(LEFT_Clone[0] != LEFT_RGB[0] || LEFT_Clone[1] != LEFT_RGB[1] || LEFT_Clone[2] != LEFT_RGB[2] || RIGHT_Clone[0] != RIGHT_RGB[0] || RIGHT_Clone[1] != RIGHT_RGB[1] || RIGHT_Clone[2] != RIGHT_RGB[2]){
          LEFT_Clone[0] = LEFT_Clone[0] + diff_left_R;
		      LEFT_Clone[1] = LEFT_Clone[1] + diff_left_G;
		      LEFT_Clone[2] = LEFT_Clone[2] + diff_left_B;

		      RIGHT_Clone[0] = RIGHT_Clone[0] + diff_right_R;
		      RIGHT_Clone[1] = RIGHT_Clone[1] + diff_right_G;
		      RIGHT_Clone[2] = RIGHT_Clone[2] + diff_right_B;

		      analogWrite(LEFT_R, LEFT_Clone[0]);
          analogWrite(LEFT_G, LEFT_Clone[1]);
          analogWrite(LEFT_B, LEFT_Clone[2]);
        
          analogWrite(RIGHT_R, RIGHT_Clone[0]);
          analogWrite(RIGHT_G, RIGHT_Clone[1]);
          analogWrite(RIGHT_B, RIGHT_Clone[2]);
                 
          delay(200);
        }
 
        break;
        
      case 2:
        //Send the type of this device to confirm that is supported by our android companion app.
        Serial.println(type);

        //Need a delay so we can be a sure that our android device got it.
        delay(500);
        
        Serial.read();
        break;
    }
    
    //Just to be sure, we copy the values that we received to our "start" array
   // for(int i = 0; i < 3; i++){
     // RGB_start[i] = RGB_end[i];
    //}
  }
}



void calculateFading(){
  diff_left_R = LEFT_RGB[0] / increment;
  diff_left_G = LEFT_RGB[1] / increment;
  diff_left_B = LEFT_RGB[2] / increment;

  diff_right_R = RIGHT_RGB[0] / increment;
  diff_right_G = RIGHT_RGB[1] / increment;
  diff_right_B = RIGHT_RGB[2] / increment;

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
