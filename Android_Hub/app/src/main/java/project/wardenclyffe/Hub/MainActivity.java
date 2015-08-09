package project.wardenclyffe.Hub;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Check if it's the first time opening the app
        SharedPreferences Preferences = this.getSharedPreferences("project.wardenclyffe.Hub", 0);

        boolean Setup = Preferences.getBoolean("Setup", true);

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
