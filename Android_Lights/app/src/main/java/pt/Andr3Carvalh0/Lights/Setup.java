package pt.Andr3Carvalh0.Lights;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class Setup extends Activity {
    ListView Paired_Devices;

    private BluetoothAdapter Bluetooth;
    private ArrayAdapter<String> Paired_Devices_Elements;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        //Initialize our arrayAdapter
        Paired_Devices_Elements = new ArrayAdapter<String>(this, R.layout.bluetooth_devices);

        //Connect "pointer" to "object"
        Paired_Devices = (ListView) findViewById(R.id.paired_devices);

        Paired_Devices.setAdapter(Paired_Devices_Elements);

    }

    /**
     * Check if device support bluetooth.
     * If so, check is it's On or Off.
     * If Off, ask user to turn it On.
     */
    private void CheckBluetoothStatus(){

        Bluetooth = BluetoothAdapter.getDefaultAdapter();

        //Check if device has bluetooth
        if(Bluetooth == null){
            Toast.makeText(getBaseContext(), "Device doesn't have bluetooth", Toast.LENGTH_SHORT);
            finish();
        }
        else{
            if(!Bluetooth.isEnabled()) {
                //Ask user to turn on bluetooth
                Intent enable_Bluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enable_Bluetooth, 1);
            }
        }
    }
}
