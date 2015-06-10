package pt.Andr3Carvalh0.Lights;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Check if it's the first time opening the app
        SharedPreferences Preferences = this.getSharedPreferences("pt.Andr3Carvalh0.Lights", 0);

        boolean Setup = Preferences.getBoolean("Setup", true);

        //If true will show the setup view
        if(Setup){
            Intent show_Setup = new Intent(getApplicationContext(), Setup.class);
            startActivity(show_Setup);
        }
    }
}
