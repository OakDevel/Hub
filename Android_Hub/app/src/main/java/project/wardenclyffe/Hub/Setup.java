package project.wardenclyffe.Hub;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class Setup extends Activity {
    private String TAG = "SETUP";

    private RecyclerView Paired_Devices;
    private RecycleView_Adapter Paired_Devices_Layout;

    //Selected device MAC Address
    private String device_address;

    //Every device to display
    List<RecycleView_Element> data = new ArrayList<>();

    //To list every device that we have paired
    Set<BluetoothDevice> connected;

    private BluetoothAdapter Bluetooth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        //Initialize our RecycleView
        Paired_Devices = (RecyclerView) findViewById(R.id.RecycleView);

        //Setup our layout
        Paired_Devices_Layout = new RecycleView_Adapter(getApplicationContext(), data);
        Paired_Devices.setAdapter(Paired_Devices_Layout);
        Paired_Devices.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        //Add touch listener to every element.
        Paired_Devices.addOnItemTouchListener(new RecycleTouchListener(getApplicationContext(), Paired_Devices, new ClickListener() {
            @Override
            public void OnClick(View v, int position) {
                int i = 0;

                for(RecycleView_Element aux : data){
                    if(i == position){
                        Log.i(TAG + " - Device Selected", aux.Device_Address);
                        setAddress(aux.Device_Address);
                    }
                    i++;
                }

                Intent calling_security = new Intent(getApplicationContext(), Connecting.class);
                calling_security.putExtra("Address", getAddress());

                //Bundle animation = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.slide_in_left, R.anim.slide_out_left).toBundle();
                //startActivity(calling_security, animation);
                startActivity(calling_security);

                finish();

            }

            @Override
            public void OnLongClick(View v, int position) {
               //We dont do anything.We dont need the long click
            }
        }));

    }

    public void onResume() {
        super.onResume();

        //Check bluetooth state
        CheckBluetoothStatus();

        //"House" cleaning.Remove every element from our recycle view element list.
        data.clear();

        //Prepare our device
        Bluetooth = BluetoothAdapter.getDefaultAdapter();

        //Get our paired devices list and add it to the Paired_Device list
        connected = Bluetooth.getBondedDevices();

        SharedPreferences Reader = getSharedPreferences("project.wardenclyffe.Hub", 0);

        //Try to read our previous connected devices
        Set<String> Address = null;
        try {
            Address = Reader.getStringSet("Blacklist", null);
        } catch (NullPointerException e) {}

        //If we had already connected to a device previously
        if (Address != null) {
            //Check if we have any other devices paired
            if (Address.size() < connected.size()) {
                setupRecycleView();
            } else {
                displayMessage();
            }

            //This is our first time(Setup)
        }else{
            if(connected.size() > 0){
                setupRecycleView();
            }
            else{
                displayMessage();
            }
        }
    }

    /**
     * Check if device support bluetooth.
     * If so, check is it's On or Off.
     * If Off, ask user to turn it On.
     */
    private void CheckBluetoothStatus() {
        Bluetooth = BluetoothAdapter.getDefaultAdapter();

        //Check if device has bluetooth
        if (Bluetooth == null) {
            Log.i(TAG, "Device doesn't have bluetooth");
            Toast.makeText(getBaseContext(), "Device doesn't have bluetooth", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            if (!Bluetooth.isEnabled()) {
                //Ask user to turn on bluetooth
                Log.i(TAG, "Asking user to turn on bluetooth");
                Intent enable_Bluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enable_Bluetooth, 1);
            }
        }
    }

    /**
     * Check if device has been setup before
     */
    private boolean hasBeenConnected(String MAC){
        SharedPreferences Reader = getSharedPreferences("project.wardenclyffe.Hub", 0);
        Set<String> MacAddress = Reader.getStringSet("Blacklist", null);

        try{
            for(String adr : MacAddress){
                if(MAC.equals(adr)){
                    return true;
                }
            }
        }catch (NullPointerException e){

        }
        return false;
    }

    public void setupRecycleView(){
        for (BluetoothDevice device : connected) {

            //Only add devices that never had been connected.
            if (!hasBeenConnected(device.getAddress())) {

                RecycleView_Element current = new RecycleView_Element();
                current.Device_Name = device.getName();
                current.Device_Type = R.drawable.ic_device_toys;
                current.Device_Address = device.getAddress();

                data.add(current);
            }
        }
    }

    public void displayMessage(){
        Log.i(TAG, "No devices paired");

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set title
        alertDialogBuilder.setTitle("No devices paired");

        // set dialog message
        alertDialogBuilder.setMessage("Please pair a new device, from the Settings app.");

        alertDialogBuilder.setCancelable(false);

        alertDialogBuilder.setPositiveButton("Go!", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.android.settings");
                startActivity(launchIntent);
            }
        });
        alertDialogBuilder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    public void setAddress(String address) {
        device_address = address;
    }

    public String getAddress() {
        return device_address;
    }

    class RecycleTouchListener implements RecyclerView.OnItemTouchListener{

        private GestureDetector gestureDetector;
        private ClickListener cl;
        public RecycleTouchListener(Context context, RecyclerView rv, final ClickListener cl){
            this.cl = cl;

            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener(){
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;

                }

                @Override
                public void onLongPress(MotionEvent e){
                   View child = Paired_Devices.findChildViewUnder(e.getX(), e.getY());

                    if(child != null && cl !=null){
                        cl.OnLongClick(child, Paired_Devices.getChildAdapterPosition(child));

                    }
                }
            });

        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());

            if(child != null && cl !=null && gestureDetector.onTouchEvent(e)){
                cl.OnClick(child, Paired_Devices.getChildAdapterPosition(child));
            }

            return false;
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    public static interface ClickListener{
        public void OnClick(View v, int position);
        public void OnLongClick(View v, int position);
    }
}