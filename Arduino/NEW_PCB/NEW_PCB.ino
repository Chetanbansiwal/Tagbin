#include <Tagplug.h>

Tagplug tagPlug = Tagplug();
void setup(){
tagPlug.init();  
}

void loop(){

       tagPlug.serialEvent();
       
            /* MAIN FUNCTIONALITY STARTS HERE */
            if(tagPlug.stringComplete){
                tagPlug.dataValidation();        
            } 
             
             // Alram timestamp Check 
            tagPlug.alarmCheck();        
          
            tagPlug.stringComplete = false; 
            /* MAIN FUNCTIONALITY STARTS HERE */
 
}













