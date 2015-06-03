#define LED_R 9
#define LED_G 11
#define LED_B 10

int estado;

#define Leitura  0
#define Processamento 1

//Strobe Light - 
int estado_brigthness;
int estado_lamp;
int strobe;
#define estado_low  0
#define estado_high 1

//For the LEDs
int RGB[] = {0, 0, 0}; //[0] --> Red, [1] --> Green,  //[2] --> Blue

//Times
int time = 10;          //Delay 
double tempo_inicial;   //Time for the cronometer 

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
  
  strobe = 0;
  estado = Leitura;
  estado_brigthness = estado_low;
  
}

void loop() {
  switch(estado){
  
    case Leitura:
      //Read serial console
      if(Serial.available() > 0){
        //Data will be sent in this format:
        //  x:x:xxx:xxx:xxx&
        //  state : strobe mode : RED : GREEN : BLUE : To know the end of the string
        data = Serial.readStringUntil('&');
        //We need to break the string into various parts
        estado_lamp = Separador(data, 0).toInt();
        strobe = Separador(data, 1).toInt();
        
        for(int i = 0; i < 3; i++){
          RGB[i] = Separador(data, i+2).toInt();
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
            //Normal Mode
            analogWrite(LED_R, RGB[0]);
            analogWrite(LED_G, RGB[1]);
            analogWrite(LED_B, RGB[2]);
        }
        else{
          //Turn Off
          analogWrite(LED_R, 0);
          analogWrite(LED_G, 0);
          analogWrite(LED_B, 0);
        }
      }
    break;  
    
    case Processamento:
      //Start Timer
      tempo_inicial = micros();
      
      //Read serial console
      if(Serial.available() > 0){
        estado = Leitura;
      }
      else{
        ControlBrightness(RGB[0], 10, LED_R);
        ControlBrightness(RGB[1], 5, LED_G);
        ControlBrightness(RGB[2], 5, LED_B);
      }
    break;  
     
   
   
  } 
}

void Strobe(int frequencia, float dutycycle, int Pin){
  
//    ------    ------       <--- valor_estado_maximo:Tempo que demora do estado maximo para o estado minimo
//    |    |    |    |
//  ---    ------    ---     <--- valor_estado_minimo:Tempo que demora do estado minimo para o estado maximo
 
  double periodo = (1e6 / frequencia);
  double valor_estado_maximo = ((periodo * dutycycle) / 100);
  double valor_estado_minimo = (periodo - valor_estado_maximo);
  
  switch(estado_brigthness){
    case (estado_low):
      digitalWrite(Pin, LOW);
        if ((micros() - tempo_inicial) >= valor_estado_minimo){
          estado_brigthness = estado_high;
          tempo_inicial = micros();
      } 
    break;
    
    case (estado_high):
      digitalWrite(Pin, HIGH);
        if ((micros() - tempo_inicial) >= valor_estado_maximo){
          estado_brigthness = estado_low;
          tempo_inicial = micros();
      }
    break;
  
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
