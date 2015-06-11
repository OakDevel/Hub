package pt.Andr3Carvalh0.Lights;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class Setup extends Activity {
    private RecyclerView Paired_Devices;
    private RecycleView_Adapter Paired_Devices_Layout;

    List<RecycleView_Element> data = new ArrayList<>();
    //int[] device_Types = {R.drawable.ic_DEVICE_TYPE_UNKNOWN, R.drawable.ic_DEVICE_TYPE_CLASSIC, R.drawable.ic_DEVICE_TYPE_LE, R.drawable.ic_DEVICE_TYPE_DUAL};

    private BluetoothAdapter Bluetooth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        //Initialize our RecycleView
        Paired_Devices = (RecyclerView) findViewById(R.id.RecycleView);
        Paired_Devices.setHasFixedSize(true);

    }

    public void onResume(){
        super.onResume();

        //Check bluetooth state
        CheckBluetoothStatus();

        //We need our layoutManager if not RecycleView will crash our app.
        Paired_Devices_Layout = new RecycleView_Adapter(getApplicationContext(), getData());
        Paired_Devices.setAdapter(Paired_Devices_Layout);
        Paired_Devices.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        //"House" cleaning
        data.clear();

        //Prepare our device
        Bluetooth = BluetoothAdapter.getDefaultAdapter();

        //Get our paired devices list and add it to the Paired_Device list
        Set<BluetoothDevice> connect = Bluetooth.getBondedDevices();

        if(connect.size() > 0){
            for (BluetoothDevice device : connect) {
                RecycleView_Element current = new RecycleView_Element();
                current.Device_Name = device.getName();
                //current.Device_Type = device_Types[device.getType()];
                data.add(current);
            }
        }

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

    //Getter
    public List<RecycleView_Element> getData() {
        return data;
    }
}
