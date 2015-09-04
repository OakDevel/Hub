package project.wardenclyffe.Hub;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import java.util.HashSet;
import java.util.Set;

public class Connecting extends Activity {

    //30 seconds.
    final private long timeout = 30000;

    private long starting_time;

    private boolean connected = false;

    private String MAC;

    private int helper = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connecting);

        //Get starting time
        starting_time = System.currentTimeMillis();

        //Progress bar related
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.VISIBLE);

        //Getting MAC address to test
        Intent i = getIntent();
        MAC = i.getStringExtra("Address");

        //This will handle our connection to the device
        Device device = new Device(BluetoothAdapter.getDefaultAdapter().getRemoteDevice(MAC));

        //Send request to get type
        device.mConnectedThread.write("02&");

        //We will end it if:
        // - The device respondes its type
        // - Or we timeout.
        while(device.getType().equals("Nope") || System.currentTimeMillis() - starting_time <= timeout){

            //We will send request every 10 seconds
            //10 secs mark
            if(System.currentTimeMillis() - starting_time >= timeout/3 && helper == 1){
                device.mConnectedThread.write("02&");
                helper++;
            }
            //20 secs mark
            if(System.currentTimeMillis() - starting_time >= timeout/3 && helper == 2){
                device.mConnectedThread.write("02&");
                helper++;
            }

            //Since the timeout is 30 seconds send another at 25
            if(System.currentTimeMillis() - starting_time >= timeout - 5000 && helper == 3){
                device.mConnectedThread.write("02&");
            }
        }

        //Ok this device is eligible, finish the setup and show Hub UI.
        if(!device.getType().equals("Nope")){
            SaveDevice(device.getAddress());

        }else{
            GoingBack(device.getAddress());

        }
    }

    private void SaveDevice(String address){
        SharedPreferences.Editor Editor = getSharedPreferences("project.wardenclyffe.Hub", 0).edit();

        Editor.putString("Device", address);
        Editor.apply();

        //We have finished the setup
        Editor.putBoolean("Setup", false);

        Editor.commit();

        Intent show_Hub = new Intent(getApplicationContext(), Hub.class);
        startActivity(show_Hub);
        finish();
    }

    private void GoingBack(String address){

        //Add device to our blacklist
        Set<String> MacAddress = null;
        Set<String> New_Mac_Address = new HashSet<String>();

        SharedPreferences Reader = getSharedPreferences("project.wardenclyffe.Hub", 0);
        SharedPreferences.Editor Editor = getSharedPreferences("project.wardenclyffe.Hub", 0).edit();

        try {
            MacAddress = Reader.getStringSet("Blacklist", null);
        }catch (Exception e){

        }
        if(MacAddress != null) {
            for (String aux : MacAddress) {
                New_Mac_Address.add(aux);
            }
        }
        New_Mac_Address.add(address);

        //Show Setup again
        Intent show_Hub = new Intent(getApplicationContext(), Hub.class);
        startActivity(show_Hub);
        finish();
    }
}