package project.wardenclyffe.Hub;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class MainActivity extends Activity {
    private final boolean DEBUG = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Check if it's the first time opening the app
        SharedPreferences Preferences = this.getSharedPreferences("project.wardenclyffe.Hub", 0);

        Set<String> MacAddress;

        MacAddress = Preferences.getStringSet("Device_MAC", null);
        Log.i("MACADDRESS_STORED", String.valueOf(MacAddress));

        boolean Setup = Preferences.getBoolean("Setup", true);

        if(DEBUG){
            Intent debug = new Intent(getApplicationContext(), Debug.class);
            startActivity(debug);
            finish();
        }

        else{
            //If true will show the setup view
            if (Setup) {
                Intent show_Setup = new Intent(getApplicationContext(), Setup.class);
                startActivity(show_Setup);
                finish();

            } else {
                Intent show_Hub = new Intent(getApplicationContext(), Hub.class);
                startActivity(show_Hub);
                finish();
            }
        }
    }
}
